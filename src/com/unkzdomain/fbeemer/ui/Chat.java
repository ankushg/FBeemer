/*
    FBeemer is a Facebook chat implementation for Android over XMPP

    Copyright (C) 2010  Ankush Gupta (UnkzDomain) unk@unkzdomain.com

    Originally based on BEEM Copyright (C) 2009 by
                          Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of FBeemer.

    FBeemer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FBeemer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FBeemer.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.unkzdomain.fbeemer.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.util.StringUtils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.Contact;
import com.unkzdomain.fbeemer.service.Message;
import com.unkzdomain.fbeemer.service.PresenceAdapter;
import com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener;
import com.unkzdomain.fbeemer.service.aidl.IChat;
import com.unkzdomain.fbeemer.service.aidl.IChatManager;
import com.unkzdomain.fbeemer.service.aidl.IChatManagerListener;
import com.unkzdomain.fbeemer.service.aidl.IMessageListener;
import com.unkzdomain.fbeemer.service.aidl.IRoster;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;
import com.unkzdomain.fbeemer.utils.AbstractManagedActivity;
import com.unkzdomain.fbeemer.utils.AppBroadcastReceiver;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class Chat.
 */
public class Chat extends AbstractManagedActivity implements OnKeyListener {

	/**
	 * The listener interface for receiving chatManager events. The class that
	 * is interested in processing a chatManager event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addChatManagerListener<code> method. When
	 * the chatManager event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see ChatManagerEvent
	 */
	private class ChatManagerListener extends IChatManagerListener.Stub {

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IChatManagerListener#chatCreated(com.unkzdomain.fbeemer.service.aidl.IChat, boolean)
		 */
		public void chatCreated(final IChat chat, final boolean locally) {
			if (locally) {
				return;
			}
			try {
				final String contactJid = contact.getJIDWithRes();
				final String chatJid = chat.getParticipant().getJIDWithRes();
				if (chatJid.equals(contactJid)) {
					// This should not be happened but to be sure
					if (mChat != null) {
						mChat.setOpen(false);
						mChat.removeMessageListener(mMessageListener);
					}
					mChat = chat;
					mChat.setOpen(true);
					mChat.addMessageListener(mMessageListener);
					mChatManager.deleteChatNotification(mChat);
				}
			} catch (final RemoteException ex) {
				// TODO Report error
			}
		}
	}

	/**
	 * The listener interface for receiving FBeemerRoster events. The class that
	 * is interested in processing a FBeemerRoster event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addFBeemerRosterListener<code> method. When
	 * the FBeemerRoster event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see FBeemerRosterEvent
	 */
	private class FBeemerRosterListener extends IBeemRosterListener.Stub {

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesAdded(java.util.List)
		 */
		public void onEntriesAdded(final List<String> addresses)
				throws RemoteException {
			// Do nothing
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesDeleted(java.util.List)
		 */
		public void onEntriesDeleted(final List<String> addresses)
				throws RemoteException {
			// Do nothing
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesUpdated(java.util.List)
		 */
		public void onEntriesUpdated(final List<String> addresses)
				throws RemoteException {
			// Do nothing
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onPresenceChanged(com.unkzdomain.fbeemer.service.PresenceAdapter)
		 */
		public void onPresenceChanged(final PresenceAdapter presence)
				throws RemoteException {
			if (contact.getJID().equals(
					StringUtils.parseBareAddress(presence.getFrom()))) {
				handler.post(new Runnable() {

					public void run() {
						contact.setStatus(presence.getStatus());
						contact.setMsgState(presence.getStatusText());
						updateContactInformations();
						updateContactStatusIcon();
					}
				});
			}
		}
	}

	/**
	 * The Class FBeemerServiceConnection.
	 */
	private final class FBeemerServiceConnection implements ServiceConnection {

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		public void onServiceConnected(final ComponentName name,
				final IBinder service) {
			mXmppFacade = IXmppFacade.Stub.asInterface(service);
			try {
				roster = mXmppFacade.getRoster();
				if (roster != null) {
					roster.addRosterListener(mFBeemerRosterListener);
				}
				mChatManager = mXmppFacade.getChatManager();
				if (mChatManager != null) {
					mChatManager.addChatCreationListener(mChatManagerListener);
					changeCurrentChat(contact);
				}
			} catch (final RemoteException e) {
				// TODO Report error
			}
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */

		public void onServiceDisconnected(final ComponentName name) {
			mXmppFacade = null;
			try {
				roster.removeRosterListener(mFBeemerRosterListener);
				mChatManager.removeChatCreationListener(mChatManagerListener);
			} catch (final RemoteException e) {
				// TODO Report error
			}
		}
	}

	/**
	 * The Class MessagesListAdapter.
	 */
	private class MessagesListAdapter extends BaseAdapter {

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */

		public int getCount() {
			return mListMessages.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */

		public Object getItem(final int position) {
			return mListMessages.get(position);
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */

		public long getItemId(final int position) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(final int position, final View convertView,
				final ViewGroup parent) {
			View sv;
			if (convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				sv = inflater.inflate(R.layout.chat_msg_row, null);
			} else {
				sv = convertView;
			}
			final MessageText msg = mListMessages.get(position);
			final TextView msgName = (TextView) sv
					.findViewById(R.id.chatmessagename);
			msgName.setText(msg.getName());
			msgName.setError(null);
			final TextView msgText = (TextView) sv
					.findViewById(R.id.chatmessagetext);
			msgText.setText(msg.getMessage());
			if (msg.isError()) {
				final String err = getString(R.string.chat_error);
				msgName.setText(err);
				msgName.setTextColor(Color.RED);
				msgName.setError(err);
			}
			return sv;
		}
	}

	/**
	 * The Class MessageText.
	 */
	private class MessageText {
		
		/** The m bare jid. */
		private transient String		mBareJid;
		
		/** The m name. */
		private transient String		mName;
		
		/** The m message. */
		private transient String		mMessage;
		
		/** The m is error. */
		private transient final boolean	mIsError;

		/**
		 * Instantiates a new message text.
		 * 
		 * @param bareJid
		 *            the bare jid
		 * @param name
		 *            the name
		 * @param message
		 *            the message
		 */
		public MessageText(final String bareJid, final String name,
				final String message) {
			mBareJid = bareJid;
			mName = name;
			mMessage = message;
			mIsError = false;
		}

		/**
		 * Instantiates a new message text.
		 * 
		 * @param bareJid
		 *            the bare jid
		 * @param name
		 *            the name
		 * @param message
		 *            the message
		 * @param isError
		 *            the is error
		 */
		public MessageText(final String bareJid, final String name,
				final String message, final boolean isError) {
			mBareJid = bareJid;
			mName = name;
			mMessage = message;
			mIsError = isError;
		}

		/**
		 * Gets the bare jid.
		 * 
		 * @return the bare jid
		 */
		public String getBareJid() {
			return mBareJid;
		}

		/**
		 * Gets the message.
		 * 
		 * @return the message
		 */
		public String getMessage() {
			return mMessage;
		}

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return mName;
		}

		/**
		 * Checks if is error.
		 * 
		 * @return true, if is error
		 */
		public boolean isError() {
			return mIsError;
		}

		/**
		 * Sets the bare jid.
		 * 
		 * @param bareJid
		 *            the new bare jid
		 */
		@SuppressWarnings("unused")
		public void setBareJid(final String bareJid) {
			mBareJid = bareJid;
		}

		/**
		 * Sets the message.
		 * 
		 * @param message
		 *            the new message
		 */
		public void setMessage(final String message) {
			mMessage = message;
		}

		/**
		 * Sets the name.
		 * 
		 * @param name
		 *            the new name
		 */
		@SuppressWarnings("unused")
		public void setName(final String name) {
			mName = name;
		}
	}

	/**
	 * The listener interface for receiving onMessage events. The class that is
	 * interested in processing a onMessage event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addOnMessageListener<code> method. When
	 * the onMessage event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see OnMessageEvent
	 */
	private class OnMessageListener extends IMessageListener.Stub {

		/**
		 * Instantiates a new on message listener.
		 */
		public OnMessageListener() {
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IMessageListener#processMessage(com.unkzdomain.fbeemer.service.aidl.IChat, com.unkzdomain.fbeemer.service.Message)
		 */

		public void processMessage(final IChat chat, final Message msg)
				throws RemoteException {
			final String fromBareJid = StringUtils.parseBareAddress(msg
					.getFrom());

			if (contact.getJID().equals(fromBareJid)) {
				handler.post(new Runnable() {

					public void run() {
						if (msg.getType() == Message.MSG_TYPE_ERROR) {
							mListMessages.add(new MessageText(fromBareJid,
									contact.getName(), msg.getBody(), true));
							mMessagesListAdapter.notifyDataSetChanged();
						} else if (msg.getBody() != null) {
							MessageText lastMessage = null;
							if (mListMessages.size() != 0) {
								lastMessage = mListMessages.get(mListMessages
										.size() - 1);
							}

							if (lastMessage != null
									&& lastMessage.getBareJid().equals(
											fromBareJid)) {
								lastMessage.setMessage(lastMessage.getMessage()
										.concat("\n" + msg.getBody()));
								mListMessages.set(mListMessages.size() - 1,
										lastMessage);
							} else if (msg.getBody() != null) {
								mListMessages.add(new MessageText(fromBareJid,
										contact.getName(), msg.getBody()));
							}
							mMessagesListAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IMessageListener#stateChanged(com.unkzdomain.fbeemer.service.aidl.IChat)
		 */

		public void stateChanged(final IChat chat) throws RemoteException {
			final String state = chat.getState();
			handler.post(new Runnable() {

				public void run() {
					String text = null;
					if ("active".equals(state)) {
						text = Chat.this.getString(R.string.chat_state_active);
					} else if ("composing".equals(state)) {
						text = Chat.this
								.getString(R.string.chat_state_composing);
					} else if ("gone".equals(state)) {
						text = Chat.this.getString(R.string.chat_state_gone);
					} else if ("inactive".equals(state)) {
						text = Chat.this
								.getString(R.string.chat_state_inactive);
					} else if ("paused".equals(state)) {
						text = Chat.this.getString(R.string.chat_state_active);
					}
					contactChatState.setText(text);
				}
			});

		}
	}

	/** The Constant SERVICE_INTENT. */
	private static final Intent						SERVICE_INTENT			= new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("com.unkzdomain.fbeemer",
				"com.unkzdomain.fbeemer.AppService"));
	}
	
	/** The handler. */
	private transient final Handler					handler					= new Handler();
	
	/** The roster. */
	private transient IRoster						roster;
	
	/** The contact. */
	private transient Contact						contact;
	
	/** The contact name view. */
	private transient TextView						contactNameView;

	/** The contact chat state. */
	private transient TextView						contactChatState;

	/** The contact status icon. */
	private transient ImageView						contactStatusIcon;
	
	/** The messages list view. */
	private transient ListView						messagesListView;
	
	/** The input field. */
	private transient EditText						inputField;
	
	/** The send button. */
	private transient Button						sendButton;
	
	/** The status icons map. */
	private final Map<Integer, Bitmap>				statusIconsMap			= new HashMap<Integer, Bitmap>();

	/** The m list messages. */
	private final List<MessageText>					mListMessages			= new ArrayList<MessageText>();
	
	/** The m chat. */
	private IChat									mChat;
	
	/** The m chat manager. */
	private IChatManager							mChatManager;
	
	/** The m message listener. */
	private final IMessageListener					mMessageListener		= new OnMessageListener();
	
	/** The m chat manager listener. */
	private transient final IChatManagerListener	mChatManagerListener	= new ChatManagerListener();

	/** The m messages list adapter. */
	private transient final MessagesListAdapter		mMessagesListAdapter	= new MessagesListAdapter();

	/** The m conn. */
	private final ServiceConnection					mConn					= new FBeemerServiceConnection();

	/** The m broadcast receiver. */
	private transient final AppBroadcastReceiver	mBroadcastReceiver		= new AppBroadcastReceiver();

	/** The m f beemer roster listener. */
	private final FBeemerRosterListener				mFBeemerRosterListener	= new FBeemerRosterListener();

	/** The m xmpp facade. */
	private IXmppFacade								mXmppFacade;

	/** The m binded. */
	private boolean									mBinded;

	/**
	 * Instantiates a new chat.
	 */
	public Chat() {
		super();
	}

	/**
	 * Change current chat.
	 * 
	 * @param contact
	 *            the contact
	 * @throws RemoteException
	 *             the remote exception
	 */
	private void changeCurrentChat(final Contact contact)
			throws RemoteException {
		if (mChat != null) {
			mChat.setOpen(false);
			mChat.removeMessageListener(mMessageListener);
		}
		mChat = mChatManager.getChat(contact);
		if (mChat != null) {
			mChat.setOpen(true);
			mChat.addMessageListener(mMessageListener);
			mChatManager.deleteChatNotification(mChat);
		}
		this.contact = roster.getContact(contact.getJID());
		final String res = contact.getSelectedRes();
		if (this.contact == null) {
			this.contact = contact;
		}
		if (!"".equals(res)) {
			this.contact.setSelectedRes(res);
		}
		updateContactInformations();
		updateContactStatusIcon();

		playRegisteredTranscript();
	}

	/**
	 * Convert messages list.
	 * 
	 * @param chatMessages
	 *            the chat messages
	 * @return the list
	 */
	private List<MessageText> convertMessagesList(
			final List<Message> chatMessages) {
		final List<MessageText> result = new ArrayList<MessageText>(
				chatMessages.size());
		final String remoteName = contact.getName();
		final String localName = getString(R.string.chat_self);
		MessageText lastMessage = null;

		for (final Message m : chatMessages) {
			String name = remoteName;
			String fromBareJid = StringUtils.parseBareAddress(m.getFrom());
			if (m.getType() == Message.MSG_TYPE_ERROR) {
				lastMessage = null;
				result
						.add(new MessageText(fromBareJid, name, m.getBody(),
								true));
				continue;
			} else if (m.getType() == Message.MSG_TYPE_CHAT) {
				if (fromBareJid == null) { // nofrom or from == yours
					name = localName;
					fromBareJid = "";
				}

				if (m.getBody() != null) {
					if (lastMessage == null
							|| !fromBareJid.equals(lastMessage.getBareJid())) {
						lastMessage = new MessageText(fromBareJid, name, m
								.getBody());
						result.add(lastMessage);
					} else {
						lastMessage.setMessage(lastMessage.getMessage().concat(
								"\n" + m.getBody()));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Creates the chat switcher dialog.
	 * 
	 * @param openedChats
	 *            the opened chats
	 */
	private void createChatSwitcherDialog(final List<Contact> openedChats) {
		final CharSequence[] items = new CharSequence[openedChats.size()];

		int i = 0;
		for (final Contact c : openedChats) {
			items[i++] = c.getName();
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.chat_dialog_change_chat_title));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int item) {
				final Intent chatIntent = new Intent(getApplicationContext(),
						com.unkzdomain.fbeemer.ui.Chat.class);
				chatIntent.setData(openedChats.get(item).toUri());
				startActivity(chatIntent);
			}
		});
		final AlertDialog chatSwitcherDialog = builder.create();
		chatSwitcherDialog.show();
	}

	/**
	 * Creates the no active chats dialog.
	 */
	private void createNoActiveChatsDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.chat_no_more_chats));
		final AlertDialog noActiveChatsDialog = builder.create();
		noActiveChatsDialog.show();
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onCreate(android.os.Bundle)
	 */

	@Override
	public void onCreate(final Bundle savedBundle) {
		super.onCreate(savedBundle);
		setContentView(R.layout.chat);
		this.registerReceiver(mBroadcastReceiver, new IntentFilter(
				AppBroadcastReceiver.CONNECTION_CLOSED));

		// UI
		contactNameView = (TextView) findViewById(R.id.chat_contact_name);
		contactChatState = (TextView) findViewById(R.id.chat_contact_chat_state);
		contactStatusIcon = (ImageView) findViewById(R.id.chat_contact_status_icon);
		messagesListView = (ListView) findViewById(R.id.chat_messages);
		messagesListView.setAdapter(mMessagesListAdapter);
		inputField = (EditText) findViewById(R.id.chat_input);
		inputField.setOnKeyListener(this);
		sendButton = (Button) findViewById(R.id.chat_send_message);
		sendButton.setOnClickListener(new OnClickListener() {

			public void onClick(final View v) {
				sendMessage();
			}
		});

		prepareIconsStatus();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onDestroy()
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */

	public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				sendMessage();
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */

	@Override
	protected void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.chat_menu_contacts_list:
				final Intent contactListIntent = new Intent(this,
						ContactList.class);
				contactListIntent
						.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
								| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(contactListIntent);
				break;
			case R.id.chat_menu_change_chat:
				try {
					final List<Contact> openedChats = mChatManager
							.getOpenedChatList();
					if (openedChats.size() > 0) {
						createChatSwitcherDialog(openedChats);
					} else {
						createNoActiveChatsDialog();
					}
				} catch (final RemoteException e) {
					// TODO Report error
				}
				break;
			case R.id.chat_menu_close_chat:
				try {
					mChatManager.destroyChat(mChat);
				} catch (final RemoteException e) {
					// TODO Report error
				}
				finish();
				break;
			default:
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onPause()
	 */

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (mChat != null) {
				mChat.setOpen(false);
				mChat.removeMessageListener(mMessageListener);
			}
			if (roster != null) {
				roster.removeRosterListener(mFBeemerRosterListener);
			}
			if (mChatManager != null) {
				mChatManager.removeChatCreationListener(mChatManagerListener);
			}
		} catch (final RemoteException e) {
			// TODO Report error
		}
		if (mBinded) {
			unbindService(mConn);
			mBinded = false;
		}
		mXmppFacade = null;
		roster = null;
		mChat = null;
		mChatManager = null;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		contact = new Contact(getIntent().getData());
		if (!mBinded) {
			bindService(SERVICE_INTENT, mConn, BIND_AUTO_CREATE);
			mBinded = true;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */

	@Override
	protected void onSaveInstanceState(final Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Play registered transcript.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	private void playRegisteredTranscript() throws RemoteException {
		mListMessages.clear();
		if (mChat != null) {
			final List<MessageText> msgList = convertMessagesList(mChat
					.getMessages());
			mListMessages.addAll(msgList);
			mMessagesListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Prepare icons status.
	 */
	private void prepareIconsStatus() {
		statusIconsMap.put(Status.AVAILABLE, BitmapFactory.decodeResource(
				getResources(), android.R.drawable.presence_online));
		statusIconsMap.put(Status.AVAILABLE_FOR_CHAT, BitmapFactory
				.decodeResource(getResources(),
						android.R.drawable.presence_online));
		statusIconsMap.put(Status.AWAY, BitmapFactory.decodeResource(
				getResources(), android.R.drawable.presence_away));
		statusIconsMap.put(Status.BUSY, BitmapFactory.decodeResource(
				getResources(), android.R.drawable.presence_busy));
		statusIconsMap.put(Status.DISCONNECT, BitmapFactory.decodeResource(
				getResources(), android.R.drawable.presence_offline));
		statusIconsMap.put(Status.UNAVAILABLE, BitmapFactory.decodeResource(
				getResources(), R.drawable.status_requested));
	}

	/**
	 * Send message.
	 */
	private void sendMessage() {
		final String inputContent = inputField.getText().toString();

		if (!"".equals(inputContent)) {
			final Message msgToSend = new Message(contact.getJIDWithRes(),
					Message.MSG_TYPE_CHAT);
			msgToSend.setBody(inputContent);

			try {
				if (mChat == null) {
					mChat = mChatManager.createChat(contact, mMessageListener);
					mChat.setOpen(true);
				}
				mChat.sendMessage(msgToSend);
			} catch (final RemoteException e) {
				// TODO Report error
			}

			final String self = getString(R.string.chat_self);
			MessageText lastMessage = null;
			if (mListMessages.size() != 0) {
				lastMessage = mListMessages.get(mListMessages.size() - 1);
			}

			if (lastMessage != null && lastMessage.getName().equals(self)) {
				lastMessage.setMessage(lastMessage.getMessage().concat(
						"\n" + inputContent));
			} else {
				mListMessages.add(new MessageText(self, self, inputContent));
			}
			mMessagesListAdapter.notifyDataSetChanged();
			inputField.setText(null);
		}
	}

	/**
	 * Update contact informations.
	 */
	private void updateContactInformations() {
		// Check for a contact name update
		String name = contact.getName();
		final String res = contact.getSelectedRes();
		if (!"".equals(res)) {
			name += "(" + res + ")";
		}
		if (!contactNameView.getText().toString().equals(name)) {
			contactNameView.setText(name);
		}
	}

	/**
	 * Update contact status icon.
	 */
	private void updateContactStatusIcon() {
		contactStatusIcon.setImageLevel(contact.getStatus());
	}
}