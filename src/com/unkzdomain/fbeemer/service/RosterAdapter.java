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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class RosterAdapter.
 */
public class RosterAdapter extends // NO_UCD
		com.unkzdomain.fbeemer.service.aidl.IRoster.Stub {

	/**
	 * The Class RosterListenerAdapter.
	 */
	private class RosterListenerAdapter implements RosterListener {

		/**
		 * Instantiates a new roster listener adapter.
		 */
		public RosterListenerAdapter() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.RosterListener#entriesAdded(java.util.Collection
		 * )
		 */

		public void entriesAdded(final Collection<String> addresses) {
			final int n = mRemoteRosListeners.beginBroadcast();

			final List<String> tab = new ArrayList<String>();
			tab.addAll(addresses);
			for (int i = 0; i < n; i++) {
				final IBeemRosterListener listener = mRemoteRosListeners
						.getBroadcastItem(i);
				try {
					listener.onEntriesAdded(tab);
				} catch (final RemoteException e) {
					// TODO Report Error
				}
			}
			mRemoteRosListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.RosterListener#entriesDeleted(java.util.Collection
		 * )
		 */

		public void entriesDeleted(final Collection<String> addresses) {
			final int n = mRemoteRosListeners.beginBroadcast();

			final List<String> tab = new ArrayList<String>();
			tab.addAll(addresses);
			for (int i = 0; i < n; i++) {
				final IBeemRosterListener listener = mRemoteRosListeners
						.getBroadcastItem(i);
				try {
					listener.onEntriesDeleted(tab);
				} catch (final RemoteException e) {
					// TODO Report Error
				}
			}
			mRemoteRosListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.RosterListener#entriesUpdated(java.util.Collection
		 * )
		 */

		public void entriesUpdated(final Collection<String> addresses) {
			final int n = mRemoteRosListeners.beginBroadcast();

			final List<String> tab = new ArrayList<String>();
			tab.addAll(addresses);
			for (int i = 0; i < n; i++) {
				final IBeemRosterListener listener = mRemoteRosListeners
						.getBroadcastItem(i);
				try {
					listener.onEntriesUpdated(tab);
				} catch (final RemoteException e) {
					// TODO Report Error
				}
			}
			mRemoteRosListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.RosterListener#presenceChanged(org.jivesoftware
		 * .smack.packet.Presence)
		 */

		public void presenceChanged(final Presence presence) {
			final int n = mRemoteRosListeners.beginBroadcast();

			for (int i = 0; i < n; i++) {
				final IBeemRosterListener listener = mRemoteRosListeners
						.getBroadcastItem(i);
				try {
					if (presence.getStatus() == null
							|| "".equals(presence.getStatus())) {
						presence.setStatus(mDefaultStatusMessages.get(Status
								.getStatusFromPresence(presence)));
					}
					listener.onPresenceChanged(new PresenceAdapter(presence));
				} catch (final RemoteException e) {
					// TODO Report Error
				}
			}
			mRemoteRosListeners.finishBroadcast();
		}
	}

	/** The m adaptee. */
	private final Roster									mAdaptee;

	/** The m remote ros listeners. */
	private final RemoteCallbackList<IBeemRosterListener>	mRemoteRosListeners	= new RemoteCallbackList<IBeemRosterListener>();

	/** The m default status messages. */
	private final Map<Integer, String>						mDefaultStatusMessages;

	/** The m roster listener. */
	private final RosterListenerAdapter						mRosterListener		= new RosterListenerAdapter();

	/**
	 * Instantiates a new roster adapter.
	 * 
	 * @param roster
	 *            the roster
	 * @param context
	 *            the context
	 */
	protected RosterAdapter(final Roster roster, final Context context) {
		mAdaptee = roster;
		roster.addRosterListener(mRosterListener);
		mDefaultStatusMessages = createDefaultStatusMessagesMap(context);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#addContact(java.lang.String,
	 * java.lang.String, java.lang.String[])
	 */

	public Contact addContact(final String user, final String name,
			final String[] groups) throws RemoteException {
		RosterEntry contact = mAdaptee.getEntry(user);
		try {
			mAdaptee.createEntry(user, name, groups);
			contact = mAdaptee.getEntry(user);
		} catch (final XMPPException e) {
			// TODO Report Error
			return null;
		}
		return getContactFromRosterEntry(contact);
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IRoster#addContactToGroup(java
	 * .lang.String, java.lang.String)
	 */

	public void addContactToGroup(final String groupName, final String jid)
			throws RemoteException {
		createGroup(groupName);
		final RosterGroup group = mAdaptee.getGroup(groupName);
		try {
			group.addEntry(mAdaptee.getEntry(jid));
		} catch (final XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#addRosterListener(com.unkzdomain
	 * .fbeemer.service.aidl.IBeemRosterListener)
	 */

	public void addRosterListener(final IBeemRosterListener listen)
			throws RemoteException {
		if (listen != null) {
			mRemoteRosListeners.register(listen);
		}
	}

	/**
	 * Creates the default status messages map.
	 * 
	 * @param context
	 *            the context
	 * @return the map
	 */
	private Map<Integer, String> createDefaultStatusMessagesMap(
			final Context context) {
		final Map<Integer, String> defaultStatusMessages = new HashMap<Integer, String>();
		defaultStatusMessages.put(Status.AVAILABLE, context
				.getString(R.string.contact_status_msg_available));
		defaultStatusMessages.put(Status.AVAILABLE_FOR_CHAT, context
				.getString(R.string.contact_status_msg_available_chat));
		defaultStatusMessages.put(Status.AWAY, context
				.getString(R.string.contact_status_msg_away));
		defaultStatusMessages.put(Status.BUSY, context
				.getString(R.string.contact_status_msg_dnd));
		defaultStatusMessages.put(Status.DISCONNECT, context
				.getString(R.string.contact_status_msg_offline));
		defaultStatusMessages.put(Status.UNAVAILABLE, context
				.getString(R.string.contact_status_msg_xa));

		return defaultStatusMessages;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#createGroup(java.lang.String)
	 */

	public void createGroup(final String groupname) throws RemoteException {
		if (mAdaptee.getGroup(groupname) == null) {
			mAdaptee.createGroup(groupname);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#deleteContact(com.unkzdomain
	 * .fbeemer.service.Contact)
	 */
	public void deleteContact(final Contact contact) throws RemoteException {
		try {
			final RosterEntry entry = mAdaptee.getEntry(contact.getJID());
			mAdaptee.removeEntry(entry);
		} catch (final XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#getContact(java.lang.String)
	 */

	public Contact getContact(final String jid) throws RemoteException {
		if (mAdaptee.contains(jid)) {
			return getContactFromRosterEntry(mAdaptee.getEntry(jid));
		}
		return null;
	}

	/**
	 * Gets the contact from roster entry.
	 * 
	 * @param entry
	 *            the entry
	 * @return the contact from roster entry
	 */
	private Contact getContactFromRosterEntry(final RosterEntry entry) {
		final String user = entry.getUser();
		final Contact c = new Contact(user);
		Presence p = mAdaptee.getPresence(user);

		if (p.getStatus() == null || "".equals(p.getStatus())) {
			p.setStatus(mDefaultStatusMessages.get(Status
					.getStatusFromPresence(p)));
		}
		c.setStatus(p);
		try {
			c.setGroups(entry.getGroups());
		} catch (final NullPointerException e) {
			// TODO Report Error
		}
		final Iterator<Presence> iPres = mAdaptee.getPresences(user);
		while (iPres.hasNext()) {
			p = iPres.next();
			if (!p.getType().equals(Presence.Type.unavailable)) {
				c.addRes(StringUtils.parseResource(p.getFrom()));
			}
		}
		c.setName(entry.getName());
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IRoster#getContactList()
	 */

	public List<Contact> getContactList() throws RemoteException {
		final Collection<RosterEntry> list = mAdaptee.getEntries();
		final List<Contact> coList = new ArrayList<Contact>(list.size());
		for (final RosterEntry entry : list) {
			coList.add(getContactFromRosterEntry(entry));
		}
		return coList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IRoster#getGroupsNames()
	 */

	public List<String> getGroupsNames() throws RemoteException {
		final Collection<RosterGroup> groups = mAdaptee.getGroups();
		final List<String> result = new ArrayList<String>(groups.size());
		for (final RosterGroup rosterGroup : groups) {
			result.add(rosterGroup.getName());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IRoster#getPresence(java.lang
	 * .String)
	 */

	public PresenceAdapter getPresence(final String jid) throws RemoteException {
		return new PresenceAdapter(mAdaptee.getPresence(jid));
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IRoster#removeContactFromGroup
	 * (java.lang.String, java.lang.String)
	 */

	public void removeContactFromGroup(final String groupName, final String jid)
			throws RemoteException {
		final RosterGroup group = mAdaptee.getGroup(groupName);
		try {
			group.removeEntry(mAdaptee.getEntry(jid));
		} catch (final XMPPException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#removeRosterListener(com.
	 * unkzdomain.fbeemer.service.aidl.IBeemRosterListener)
	 */

	public void removeRosterListener(final IBeemRosterListener listen)
			throws RemoteException {
		if (listen != null) {
			mRemoteRosListeners.unregister(listen);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IRoster#setContactName(java.lang.
	 * String, java.lang.String)
	 */

	public void setContactName(final String jid, final String name)
			throws RemoteException {
		mAdaptee.getEntry(jid).setName(name);
	}
}