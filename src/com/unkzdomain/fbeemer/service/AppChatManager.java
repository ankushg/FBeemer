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
package com.unkzdomain.fbeemer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.unkzdomain.fbeemer.AppService;
import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.aidl.IChat;
import com.unkzdomain.fbeemer.service.aidl.IChatManager;
import com.unkzdomain.fbeemer.service.aidl.IChatManagerListener;
import com.unkzdomain.fbeemer.service.aidl.IMessageListener;
import com.unkzdomain.fbeemer.service.aidl.IRoster;

// TODO: Auto-generated Javadoc
/**
 * The Class AppChatManager.
 */
public class AppChatManager extends IChatManager.Stub {

	/**
	 * The listener interface for receiving chat events. The class that is
	 * interested in processing a chat event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addChatListener<code> method. When
	 * the chat event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see ChatEvent
	 */
	private class ChatListener extends IMessageListener.Stub implements
			ChatManagerListener {

		/**
		 * Instantiates a new chat listener.
		 */
		public ChatListener() {
		}

		/* (non-Javadoc)
		 * @see org.jivesoftware.smack.ChatManagerListener#chatCreated(org.jivesoftware.smack.Chat, boolean)
		 */
		public void chatCreated(final Chat chat, final boolean locally) {
			final IChat newchat = getChat(chat);
			try {
				newchat.addMessageListener(mChatListener);
				final int n = mRemoteChatCreationListeners.beginBroadcast();

				for (int i = 0; i < n; i++) {
					final IChatManagerListener listener = mRemoteChatCreationListeners
							.getBroadcastItem(i);
					listener.chatCreated(newchat, locally);
				}
				mRemoteChatCreationListeners.finishBroadcast();
			} catch (final RemoteException e) {
				// TODO Report Error
			}
		}

		/**
		 * Make chat intent.
		 * 
		 * @param chat
		 *            the chat
		 * @return the pending intent
		 */
		private PendingIntent makeChatIntent(final IChat chat) {
			final Intent chatIntent = new Intent(mService,
					com.unkzdomain.fbeemer.ui.Chat.class);
			chatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
					| Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				chatIntent.setData(chat.getParticipant().toUri());
			} catch (final RemoteException e) {
				// TODO Report Error
			}
			final PendingIntent contentIntent = PendingIntent.getActivity(
					mService, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			return contentIntent;
		}

		/**
		 * Notify new chat.
		 * 
		 * @param chat
		 *            the chat
		 */
		private void notifyNewChat(final IChat chat) {
			PreferenceManager.getDefaultSharedPreferences(mService);
			try {
				final CharSequence tickerText = mService.getBind().getRoster()
						.getContact(chat.getParticipant().getJID()).getName();
				final Notification notification = new Notification(
						android.R.drawable.sym_action_chat, tickerText, System
								.currentTimeMillis());
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(mService, tickerText, mService
						.getString(R.string.FBeemerChatManagerNewMessage),
						makeChatIntent(chat));
				mService.sendNotification(chat.getParticipant().getJID()
						.hashCode(), notification);
			} catch (final RemoteException e) {
				// TODO Report Error
			}
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IMessageListener#processMessage(com.unkzdomain.fbeemer.service.aidl.IChat, com.unkzdomain.fbeemer.service.Message)
		 */
		public void processMessage(final IChat chat, final Message message) {
			try {
				if (!chat.isOpen() && message.getBody() != null) {
					if (chat instanceof ChatAdapter) {
						mChats.put(chat.getParticipant().getJID(),
								(ChatAdapter) chat);
					}
					notifyNewChat(chat);
				}
			} catch (final RemoteException e) {
				// TODO Report Error
			}
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IMessageListener#stateChanged(com.unkzdomain.fbeemer.service.aidl.IChat)
		 */
		public void stateChanged(final IChat chat) {
		}
	}

	/** The m adaptee. */
	private final ChatManager								mAdaptee;
	
	/** The m chats. */
	private final Map<String, ChatAdapter>					mChats							= new HashMap<String, ChatAdapter>();
	
	/** The m chat listener. */
	private final ChatListener								mChatListener					= new ChatListener();
	
	/** The m remote chat creation listeners. */
	private final RemoteCallbackList<IChatManagerListener>	mRemoteChatCreationListeners	= new RemoteCallbackList<IChatManagerListener>();

	/** The m service. */
	private final AppService								mService;

	/**
	 * Instantiates a new app chat manager.
	 * 
	 * @param chatManager
	 *            the chat manager
	 * @param service
	 *            the service
	 */
	protected AppChatManager(final ChatManager chatManager,
			final AppService service) {
		mService = service;
		mAdaptee = chatManager;
		mAdaptee.addChatListener(mChatListener);
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#addChatCreationListener(com.unkzdomain.fbeemer.service.aidl.IChatManagerListener)
	 */
	public void addChatCreationListener(final IChatManagerListener listener)
			throws RemoteException {
		if (listener != null) {
			mRemoteChatCreationListeners.register(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#createChat(com.unkzdomain.fbeemer.service.Contact, com.unkzdomain.fbeemer.service.aidl.IMessageListener)
	 */
	public IChat createChat(final Contact contact,
			final IMessageListener listener) {
		final String jid = contact.getJIDWithRes();

		return createChat(jid, listener);
	}

	/**
	 * Creates the chat.
	 * 
	 * @param jid
	 *            the jid
	 * @param listener
	 *            the listener
	 * @return the i chat
	 */
	private IChat createChat(final String jid, final IMessageListener listener) {
		final String key = jid;
		ChatAdapter result;
		if (mChats.containsKey(key)) {
			result = mChats.get(key);
			result.addMessageListener(listener);
			return result;
		}
		final Chat c = mAdaptee.createChat(key, null);
		result = getChat(c);
		result.addMessageListener(listener);
		return result;
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#deleteChatNotification(com.unkzdomain.fbeemer.service.aidl.IChat)
	 */
	public void deleteChatNotification(final IChat chat) {
		try {
			mService.deleteNotification(chat.getParticipant().getJID()
					.hashCode());
		} catch (final RemoteException e) {
			// TODO Report Error
		}
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#destroyChat(com.unkzdomain.fbeemer.service.aidl.IChat)
	 */
	public void destroyChat(final IChat chat) throws RemoteException {
		if (chat == null) {
			return;
		}
		deleteChatNotification(chat);
		mChats.remove(chat.getParticipant().getJID());
	}

	/**
	 * Gets the chat.
	 * 
	 * @param chat
	 *            the chat
	 * @return the chat
	 */
	private ChatAdapter getChat(final Chat chat) {
		final String key = chat.getParticipant();
		if (mChats.containsKey(key)) {
			return mChats.get(key);
		}
		final ChatAdapter res = new ChatAdapter(chat);
		mChats.put(key, res);
		return res;
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#getChat(com.unkzdomain.fbeemer.service.Contact)
	 */
	public ChatAdapter getChat(final Contact contact) {
		final String key = contact.getJIDWithRes();
		return mChats.get(key);
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#getOpenedChatList()
	 */
	public List<Contact> getOpenedChatList() throws RemoteException {
		final List<Contact> openedChats = new ArrayList<Contact>();
		final IRoster mRoster = mService.getBind().getRoster();

		for (final ChatAdapter chat : mChats.values()) {
			if (chat.getMessages().size() > 0) {
				Contact t = mRoster.getContact(chat.getParticipant().getJID());
				if (t == null) {
					t = new Contact(chat.getParticipant().getJID());
				}
				openedChats.add(t);
			}
		}
		return openedChats;
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChatManager#removeChatCreationListener(com.unkzdomain.fbeemer.service.aidl.IChatManagerListener)
	 */
	public void removeChatCreationListener(final IChatManagerListener listener)
			throws RemoteException {
		if (listener != null) {
			mRemoteChatCreationListeners.unregister(listener);
		}
	}
}