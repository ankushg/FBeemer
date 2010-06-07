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

import org.jivesoftware.smack.packet.XMPPError;

import android.os.Parcel;
import android.os.Parcelable;

// TODO: Auto-generated Javadoc
/**
 * The Class Message.
 */
public class Message implements Parcelable {

	/** The Constant MSG_TYPE_CHAT. */
	public static final int							MSG_TYPE_CHAT	= 200;

	/** The Constant MSG_TYPE_ERROR. */
	public static final int							MSG_TYPE_ERROR	= 400;

	/** The Constant CREATOR. */
	public static final Parcelable.Creator<Message>	CREATOR			= new Parcelable.Creator<Message>() {

																		public Message createFromParcel(
																				final Parcel source) {
																			return new Message(
																					source);
																		}

																		public Message[] newArray(
																				final int size) {
																			return new Message[size];
																		}
																	};

	/** The type. */
	private int										type;
	
	/** The body. */
	private String									body;
	
	/** The subject. */
	private String									subject;
	
	/** The to. */
	private String									to;
	
	/** The from. */
	private String									from;
	
	/** The thread. */
	private String									thread;

	/**
	 * Instantiates a new message.
	 * 
	 * @param smackMsg
	 *            the smack msg
	 */
	protected Message(final org.jivesoftware.smack.packet.Message smackMsg) {
		this(smackMsg.getTo());
		if (smackMsg.getType().equals(
				org.jivesoftware.smack.packet.Message.Type.error)) {
			type = MSG_TYPE_ERROR;
		} else {
			type = MSG_TYPE_CHAT;
		}
		from = smackMsg.getFrom();
		if (type == MSG_TYPE_ERROR) {
			final XMPPError er = smackMsg.getError();
			final String msg = er.getMessage();
			if (msg != null) {
				body = msg;
			} else {
				body = er.getCondition();
			}
		} else {
			body = smackMsg.getBody();
			subject = smackMsg.getSubject();
			thread = smackMsg.getThread();
		}
	}

	/**
	 * Instantiates a new message.
	 * 
	 * @param in
	 *            the in
	 */
	private Message(final Parcel in) {
		type = in.readInt();
		to = in.readString();
		body = in.readString();
		subject = in.readString();
		thread = in.readString();
		from = in.readString();
	}

	/**
	 * Instantiates a new message.
	 * 
	 * @param to
	 *            the to
	 */
	private Message(final String to) {
		this(to, MSG_TYPE_CHAT);
	}

	/**
	 * Instantiates a new message.
	 * 
	 * @param to
	 *            the to
	 * @param type
	 *            the type
	 */
	public Message(final String to, final int type) {
		this.to = to;
		this.type = type;
		body = "";
		subject = "";
		thread = "";
		from = null;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Gets the thread.
	 * 
	 * @return the thread
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the body.
	 * 
	 * @param body
	 *            the new body
	 */
	public void setBody(final String body) {
		this.body = body;
	}

	/**
	 * Sets the from.
	 * 
	 * @param from
	 *            the new from
	 */
	public void setFrom(final String from) {
		this.from = from;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Sets the thread.
	 * 
	 * @param thread
	 *            the new thread
	 */
	public void setThread(final String thread) {
		this.thread = thread;
	}

	/**
	 * Sets the to.
	 * 
	 * @param to
	 *            the new to
	 */
	public void setTo(final String to) {
		this.to = to;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(final int type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(type);
		dest.writeString(to);
		dest.writeString(body);
		dest.writeString(subject);
		dest.writeString(thread);
		dest.writeString(from);
	}

}
