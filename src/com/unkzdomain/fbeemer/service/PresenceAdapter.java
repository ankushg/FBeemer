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

import org.jivesoftware.smack.packet.Presence;

import android.os.Parcel;
import android.os.Parcelable;

import com.unkzdomain.fbeemer.utils.PresenceType;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class PresenceAdapter.
 */
public class PresenceAdapter implements Parcelable {

	/** The Constant CREATOR. */
	public static final Parcelable.Creator<PresenceAdapter>	CREATOR	= new Parcelable.Creator<PresenceAdapter>() {

																		public PresenceAdapter createFromParcel(
																				final Parcel source) {
																			return new PresenceAdapter(
																					source);
																		}

																		public PresenceAdapter[] newArray(
																				final int size) {
																			return new PresenceAdapter[size];
																		}
																	};

	/** The m type. */
	private int												mType;

	/** The m status. */
	private int												mStatus;

	/** The m to. */
	private String											mTo;

	/** The m from. */
	private String											mFrom;

	/** The m status text. */
	private String											mStatusText;

	/**
	 * Instantiates a new presence adapter.
	 * 
	 * @param source
	 *            the source
	 */
	private PresenceAdapter(final Parcel source) {
		mType = source.readInt();
		mStatus = source.readInt();
		mTo = source.readString();
		mFrom = source.readString();
		mStatusText = source.readString();
	}

	/**
	 * Instantiates a new presence adapter.
	 * 
	 * @param presence
	 *            the presence
	 */
	protected PresenceAdapter(final Presence presence) {
		mType = PresenceType.getPresenceType(presence);
		mStatus = Status.getStatusFromPresence(presence);
		mTo = presence.getTo();
		mFrom = presence.getFrom();
		mStatusText = presence.getStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public String getFrom() {
		return mFrom;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return mStatus;
	}

	/**
	 * Gets the status text.
	 * 
	 * @return the status text
	 */
	public String getStatusText() {
		return mStatusText;
	}

	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public String getTo() {
		return mTo;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public int getType() {
		return mType;
	}

	/**
	 * Sets the from.
	 * 
	 * @param from
	 *            the new from
	 */
	public void setFrom(final String from) {
		mFrom = from;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(final int status) {
		mStatus = status;
	}

	/**
	 * Sets the status text.
	 * 
	 * @param statusText
	 *            the new status text
	 */
	public void setStatusText(final String statusText) {
		mStatusText = statusText;
	}

	/**
	 * Sets the to.
	 * 
	 * @param to
	 *            the new to
	 */
	public void setTo(final String to) {
		mTo = to;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(final int type) {
		mType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */

	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(mType);
		dest.writeInt(mStatus);
		dest.writeString(mTo);
		dest.writeString(mFrom);
		dest.writeString(mStatusText);
	}
}
