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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.unkzdomain.fbeemer.service.aidl.IChat;
import com.unkzdomain.fbeemer.service.aidl.IMessageListener;

// TODO: Auto-generated Javadoc
/**
 * The Class ChatAdapter.
 */
public class ChatAdapter extends IChat.Stub { // NO_UCD

	/**
	 * The listener interface for receiving msg events. The class that is
	 * interested in processing a msg event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addMsgListener<code> method. When
	 * the msg event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see MsgEvent
	 */
	private class MsgListener implements ChatStateListener {

		/**
		 * Instantiates a new msg listener.
		 */
		public MsgListener() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.MessageListener#processMessage(org.jivesoftware
		 * .smack.Chat, org.jivesoftware.smack.packet.Message)
		 */
		public void processMessage(final Chat chat,
				final org.jivesoftware.smack.packet.Message message) {
			final Message msg = new Message(message);
			addMessage(msg);
			final int n = mRemoteListeners.beginBroadcast();
			for (int i = 0; i < n; i++) {
				final IMessageListener listener = mRemoteListeners
						.getBroadcastItem(i);
				try {
					if (listener != null) {
						listener.processMessage(ChatAdapter.this, msg);
					}
				} catch (final RemoteException e) {
					// TODO Report Error
				}
			}
			mRemoteListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smackx.ChatStateListener#stateChanged(org.jivesoftware
		 * .smack.Chat, org.jivesoftware.smackx.ChatState)
		 */
		public void stateChanged(final Chat chat, final ChatState state) {
			mState = state.name();
			final int n = mRemoteListeners.beginBroadcast();

			for (int i = 0; i < n; i++) {
				final IMessageListener listener = mRemoteListeners
						.getBroadcastItem(i);
				try {
					listener.stateChanged(ChatAdapter.this);
				} catch (final RemoteException e) {
					// TODO Report error
				}
			}
			mRemoteListeners.finishBroadcast();
		}
	}

	/** The Constant HISTORY_MAX_SIZE. */
	private static final int							HISTORY_MAX_SIZE	= 50;

	/** The m adaptee. */
	private final Chat									mAdaptee;

	/** The m participant. */
	private final Contact								mParticipant;

	/** The m state. */
	private String										mState;

	/** The m is open. */
	private boolean										mIsOpen;

	/** The m messages. */
	private final List<Message>							mMessages;

	/** The m remote listeners. */
	private final RemoteCallbackList<IMessageListener>	mRemoteListeners	= new RemoteCallbackList<IMessageListener>();

	/** The m msg listener. */
	private final MsgListener							mMsgListener		= new MsgListener();

	/**
	 * Instantiates a new chat adapter.
	 * 
	 * @param chat
	 *            the chat
	 */
	protected ChatAdapter(final Chat chat) {
		mAdaptee = chat;
		mParticipant = new Contact(chat.getParticipant());
		mMessages = new LinkedList<Message>();
		mAdaptee.addMessageListener(mMsgListener);
	}

	/**
	 * Adds the message.
	 * 
	 * @param msg
	 *            the msg
	 */
	private void addMessage(final Message msg) {
		if (mMessages.size() == HISTORY_MAX_SIZE) {
			mMessages.remove(0);
		}
		mMessages.add(msg);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IChat#addMessageListener(com.unkzdomain
	 * .fbeemer.service.aidl.IMessageListener)
	 */
	public void addMessageListener(final IMessageListener listen) {
		if (listen != null) {
			mRemoteListeners.register(listen);
		}
	}

	/**
	 * Gets the adaptee.
	 * 
	 * @return the adaptee
	 */
	public Chat getAdaptee() {
		return mAdaptee;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#getMessages()
	 */
	public List<Message> getMessages() throws RemoteException {
		return Collections.unmodifiableList(mMessages);
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#getParticipant()
	 */
	public Contact getParticipant() throws RemoteException {
		return mParticipant;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#getState()
	 */
	public String getState() throws RemoteException {
		return mState;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#isOpen()
	 */
	public boolean isOpen() {
		return mIsOpen;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.unkzdomain.fbeemer.service.aidl.IChat#removeMessageListener(com.
	 * unkzdomain.fbeemer.service.aidl.IMessageListener)
	 */
	public void removeMessageListener(final IMessageListener listen) {
		if (listen != null) {
			mRemoteListeners.unregister(listen);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IChat#sendMessage(com.unkzdomain.
	 * fbeemer.service.Message)
	 */
	public void sendMessage(final com.unkzdomain.fbeemer.service.Message message)
			throws RemoteException {
		final org.jivesoftware.smack.packet.Message send = new org.jivesoftware.smack.packet.Message();
		send.setTo(message.getTo());
		send.setBody(message.getBody());
		send.setThread(message.getThread());
		send.setSubject(message.getSubject());
		send.setType(org.jivesoftware.smack.packet.Message.Type.chat);
		try {
			mAdaptee.sendMessage(send);
			mMessages.add(message);
		} catch (final XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#setOpen(boolean)
	 */
	public void setOpen(final boolean isOpen) {
		mIsOpen = isOpen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IChat#setState(java.lang.String)
	 */
	public void setState(final String state) throws RemoteException {
		mState = state;
	}
}
