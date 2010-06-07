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
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class Contact.
 */
public class Contact implements Parcelable {

	/** The Constant CREATOR. */
	public static final Parcelable.Creator<Contact>	CREATOR	= new Parcelable.Creator<Contact>() {

																public Contact createFromParcel(
																		final Parcel source) {
																	return new Contact(
																			source);
																}

																public Contact[] newArray(
																		final int size) {
																	return new Contact[size];
																}
															};

	/** The m id. */
	private int										mID;

	/** The m status. */
	private int										mStatus;

	/** The m jid. */
	private final String							mJID;

	/** The m selected res. */
	private String									mSelectedRes;

	/** The m msg state. */
	private String									mMsgState;

	/** The m res. */
	private List<String>							mRes;

	/** The m groups. */
	private final List<String>						mGroups	= new ArrayList<String>();

	/** The m name. */
	private String									mName;

	/**
	 * Instantiates a new contact.
	 * 
	 * @param in
	 *            the in
	 */
	private Contact(final Parcel in) {
		mID = in.readInt();
		mStatus = in.readInt();
		mJID = in.readString();
		mSelectedRes = in.readString();
		mName = in.readString();
		mMsgState = in.readString();
		mRes = new ArrayList<String>();
		in.readStringList(mRes);
		in.readStringList(mGroups);
	}

	/**
	 * Instantiates a new contact.
	 * 
	 * @param jid
	 *            the jid
	 */
	public Contact(final String jid) {
		mJID = StringUtils.parseBareAddress(jid);
		mName = mJID;
		mStatus = Status.DISCONNECT;
		mMsgState = null;
		mRes = new ArrayList<String>();
		final String res = StringUtils.parseResource(jid);
		mSelectedRes = res;
		if (!"".equals(res)) {
			mRes.add(res);
		}
	}

	/**
	 * Instantiates a new contact.
	 * 
	 * @param uri
	 *            the uri
	 */
	public Contact(final Uri uri) {
		if (!"xmpp".equals(uri.getScheme())) {
			throw new IllegalArgumentException();
		}
		final String enduri = uri.getEncodedSchemeSpecificPart();
		mJID = StringUtils.parseBareAddress(enduri);
		mName = mJID;
		mStatus = Status.DISCONNECT;
		mMsgState = null;
		mRes = new ArrayList<String>();
		final String res = StringUtils.parseResource(enduri);
		mSelectedRes = res;
		mRes.add(res);
	}

	/**
	 * Adds the res.
	 * 
	 * @param res
	 *            the res
	 */
	protected void addRes(final String res) {
		if (!mRes.contains(res)) {
			mRes.add(res);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Contact)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		final Contact c = (Contact) other;
		return c.getJID().equals(getJID());
	}

	/**
	 * Gets the groups.
	 * 
	 * @return the groups
	 */
	public List<String> getGroups() {
		return mGroups;
	}

	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 */
	public int getID() {
		return mID;
	}

	/**
	 * Gets the jID.
	 * 
	 * @return the jID
	 */
	public String getJID() {
		return mJID;
	}

	/**
	 * Gets the jID with res.
	 * 
	 * @return the jID with res
	 */
	public String getJIDWithRes() {
		final StringBuilder build = new StringBuilder(mJID);
		if (!"".equals(mSelectedRes)) {
			build.append('/').append(mSelectedRes);
		}
		return build.toString();
	}

	/**
	 * Gets the m res.
	 * 
	 * @return the m res
	 */
	public List<String> getMRes() {
		return mRes;
	}

	/**
	 * Gets the msg state.
	 * 
	 * @return the msg state
	 */
	public String getMsgState() {
		return mMsgState;
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
	 * Gets the selected res.
	 * 
	 * @return the selected res
	 */
	public String getSelectedRes() {
		return mSelectedRes;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return mStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mJID.hashCode();
	}

	/**
	 * Sets the groups.
	 * 
	 * @param groups
	 *            the new groups
	 */
	public void setGroups(final Collection<RosterGroup> groups) {
		mGroups.clear();
		for (final RosterGroup rosterGroup : groups) {
			mGroups.add(rosterGroup.getName());
		}
	}

	/**
	 * Sets the groups.
	 * 
	 * @param groups
	 *            the new groups
	 */
	public void setGroups(final List<String> groups) {
		mGroups.clear();
		mGroups.addAll(groups);
	}

	/**
	 * Sets the iD.
	 * 
	 * @param mid
	 *            the new iD
	 */
	public void setID(final int mid) {
		mID = mid;
	}

	/**
	 * Sets the m res.
	 * 
	 * @param mRes
	 *            the new m res
	 */
	public void setMRes(final List<String> mRes) {
		this.mRes = mRes;
	}

	/**
	 * Sets the msg state.
	 * 
	 * @param msgState
	 *            the new msg state
	 */
	public void setMsgState(final String msgState) {
		mMsgState = msgState;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(final String name) {
		if (name == null || "".equals(name)) {
			mName = mJID;
			mName = StringUtils.parseName(mName);
			if (mName == null || "".equals(mName)) {
				mName = mJID;
			}
		} else {
			mName = name;
		}
	}

	/**
	 * Sets the selected res.
	 * 
	 * @param resource
	 *            the new selected res
	 */
	public void setSelectedRes(final String resource) {
		mSelectedRes = resource;
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
	 * Sets the status.
	 * 
	 * @param presence
	 *            the new status
	 */
	public void setStatus(final Presence presence) {
		mStatus = Status.getStatusFromPresence(presence);
		mMsgState = presence.getStatus();
	}

	/**
	 * Sets the status.
	 * 
	 * @param presence
	 *            the new status
	 */
	public void setStatus(final PresenceAdapter presence) {
		mStatus = presence.getStatus();
		mMsgState = presence.getStatusText();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (mJID != null) {
			return mJID;
		}
		return super.toString();
	}

	/**
	 * To uri.
	 * 
	 * @return the uri
	 */
	public Uri toUri() {
		final StringBuilder build = new StringBuilder("xmpp:");
		final String name = StringUtils.parseName(mJID);
		build.append(name);
		if (!"".equals(name)) {
			build.append('@');
		}
		build.append(StringUtils.parseServer(mJID));
		final Uri u = Uri.parse(build.toString());
		return u;
	}

	/**
	 * To uri.
	 * 
	 * @param resource
	 *            the resource
	 * @return the uri
	 */
	public Uri toUri(final String resource) {
		final StringBuilder build = new StringBuilder("xmpp:");
		final String name = StringUtils.parseName(mJID);
		build.append(name);
		if (!"".equals(name)) {
			build.append('@');
		}
		build.append(StringUtils.parseServer(mJID));
		if (!"".equals(resource)) {
			build.append('/');
			build.append(resource);
		}
		final Uri u = Uri.parse(build.toString());
		return u;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(mID);
		dest.writeInt(mStatus);
		dest.writeString(mJID);
		dest.writeString(mSelectedRes);
		dest.writeString(mName);
		dest.writeString(mMsgState);
		dest.writeStringList(getMRes());
		dest.writeStringList(getGroups());
	}

}