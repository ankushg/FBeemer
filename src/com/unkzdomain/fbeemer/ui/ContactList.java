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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.util.StringUtils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.Contact;
import com.unkzdomain.fbeemer.service.PresenceAdapter;
import com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener;
import com.unkzdomain.fbeemer.service.aidl.IRoster;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;
import com.unkzdomain.fbeemer.utils.AbstractManagedActivity;
import com.unkzdomain.fbeemer.utils.AppBroadcastReceiver;
import com.unkzdomain.fbeemer.utils.SortedList;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class ContactList.
 */
public class ContactList extends AbstractManagedActivity {

	/**
	 * The Class ComparatorContactListByStatusAndName.
	 * 
	 * @param <T>
	 *            the generic type
	 */
	private static class ComparatorContactListByStatusAndName<T> implements
			Comparator<T> {
		
		/**
		 * Instantiates a new comparator contact list by status and name.
		 */
		public ComparatorContactListByStatusAndName() {
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(final T c1, final T c2) {
			if (((Contact) c1).getStatus() < ((Contact) c2).getStatus()) {
				return 1;
			} else if (((Contact) c1).getStatus() > ((Contact) c2).getStatus()) {
				return -1;
			} else {
				return ((Contact) c1).getName().compareToIgnoreCase(
						((Contact) c2).getName());
			}
		}
	}

	/**
	 * The Class FBeemerContactList.
	 */
	private class FBeemerContactList extends BaseAdapter {

		/**
		 * Instantiates a new f beemer contact list.
		 */
		public FBeemerContactList() {
		}

		/**
		 * Bind view.
		 * 
		 * @param view
		 *            the view
		 * @param curContact
		 *            the cur contact
		 */
		private void bindView(final View view, final Contact curContact) {

			if (curContact != null) {
				final TextView v = (TextView) view
						.findViewById(R.id.contactlistpseudo);
				final LevelListDrawable mStatusDrawable = (LevelListDrawable) getResources()
						.getDrawable(R.drawable.status_icon);
				mStatusDrawable.setLevel(curContact.getStatus());
				v.setCompoundDrawablesWithIntrinsicBounds(mStatusDrawable,
						null, null, null);
				v.setText(curContact.getName());
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */

		public int getCount() {
			return mListContact.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */

		public Object getItem(final int position) {
			return mListContact.get(position);
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
			View v = convertView;
			if (convertView == null) {
				v = mInflater.inflate(R.layout.contactlistcontact, null);
			}
			Contact c = mListContact.get(position);
			if (mRoster != null) {
				try {
					c = mRoster.getContact(c.getJID());
				} catch (final RemoteException e) {
					e.printStackTrace();
				}
			}
			bindView(v, c);
			return v;
		}
	}

	/**
	 * The Class FBeemerContactListOnClick.
	 */
	private class FBeemerContactListOnClick implements OnItemClickListener {
		
		/**
		 * Instantiates a new f beemer contact list on click.
		 */
		public FBeemerContactListOnClick() {
		}

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */

		public void onItemClick(final AdapterView<?> arg0, final View v,
				final int pos, final long lpos) {
			final Contact c = mListContact.get(pos);
			final Intent i = new Intent(ContactList.this, Chat.class);
			i.setData(c.toUri());
			startActivity(i);
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
		
		/**
		 * Instantiates a new f beemer roster listener.
		 */
		public FBeemerRosterListener() {
		}

		/**
		 * Adds the to special list.
		 * 
		 * @param contact
		 *            the contact
		 */
		private void addToSpecialList(final Contact contact) {
			final List<String> groups = contact.getGroups();
			List<Contact> list = mContactOnGroup
					.get(getString(R.string.contact_list_all_contact));
			if (list != mListContact) {
				list.add(contact);
			}
			list = mContactOnGroup
					.get(getString(R.string.contact_list_no_group));
			if (list != mListContact && groups.isEmpty()) {
				list.add(contact);
			}
		}

		/**
		 * Clean banner group.
		 * 
		 * @throws RemoteException
		 *             the remote exception
		 */
		private void cleanBannerGroup() throws RemoteException {
			final List<String> rosterGroups = mRoster.getGroupsNames();
			final List<String> realGroups = mListGroup.subList(1, mListContact
					.size() - 1);
			realGroups.retainAll(rosterGroups);
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesAdded(java.util.List)
		 */

		public void onEntriesAdded(final List<String> addresses)
				throws RemoteException {
			final boolean hideDisconnected = mSettings.getBoolean(
					SETTINGS_HIDDEN_CONTACT, true);
			for (final String newName : addresses) {
				final Contact contact = mRoster.getContact(newName);
				final boolean visible = !hideDisconnected
						|| Status.statusOnline(contact.getStatus());
				final List<String> groups = contact.getGroups();
				if (visible) {
					for (final String group : groups) {
						if (!mListGroup.contains(group)) {
							mListGroup.add(mListGroup.size() - 1, group);
							final List<Contact> tmplist = new SortedList<Contact>(
									new LinkedList<Contact>(), mComparator);
							mContactOnGroup.put(group, tmplist);
						}
						final List<Contact> contactByGroups = mContactOnGroup
								.get(group);
						if (contactByGroups == mListContact) {
							updateCurrentList(group, contact);
							continue;
						}
						contactByGroups.add(contact);
					}

					// add the contact to all and no groups
					addToSpecialList(contact);
				}
			}
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesDeleted(java.util.List)
		 */

		public void onEntriesDeleted(final List<String> addresses)
				throws RemoteException {
			for (final String cToDelete : addresses) {
				final Contact contact = new Contact(cToDelete);
				for (final Map.Entry<String, List<Contact>> entry : mContactOnGroup
						.entrySet()) {
					final List<Contact> contactByGroups = entry.getValue();
					if (contactByGroups == mListContact) {
						updateCurrentList(entry.getKey(), contact);
						continue;
					}
					contactByGroups.remove(contact);
				}
				cleanBannerGroup();
			}

			mHandler.post(new Runnable() {
				public void run() {
					mListContact = mContactOnGroup
							.get(getString(R.string.contact_list_all_contact));
					mAdapterContactList.notifyDataSetChanged();
				}
			});

		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onEntriesUpdated(java.util.List)
		 */

		public void onEntriesUpdated(final List<String> addresses)
				throws RemoteException {
			final boolean hideDisconnected = mSettings.getBoolean(
					SETTINGS_HIDDEN_CONTACT, true);
			for (final String adr : addresses) {
				final Contact contact = mRoster.getContact(adr);
				final boolean visible = !hideDisconnected
						|| Status.statusOnline(contact.getStatus());
				final List<String> groups = contact.getGroups();
				for (final Map.Entry<String, List<Contact>> entry : mContactOnGroup
						.entrySet()) {
					final List<Contact> contactByGroups = entry.getValue();
					if (contactByGroups == mListContact) {
						updateCurrentList(entry.getKey(), contact);
						continue;
					}
					contactByGroups.remove(contact);
					if (visible) {
						for (final String group : groups) {
							if (!mListGroup.contains(group)) {
								mListGroup.add(mListGroup.size() - 1, group);
								final List<Contact> tmplist = new SortedList<Contact>(
										new LinkedList<Contact>(), mComparator);
								mContactOnGroup.put(group, tmplist);
							}
							mContactOnGroup.get(group).remove(contact);
						}
					}

				}

				// add the contact to all and no groups
				if (visible) {
					addToSpecialList(contact);
				}
			}
			cleanBannerGroup();
		}

		/* (non-Javadoc)
		 * @see com.unkzdomain.fbeemer.service.aidl.IBeemRosterListener#onPresenceChanged(com.unkzdomain.fbeemer.service.PresenceAdapter)
		 */

		public void onPresenceChanged(final PresenceAdapter presence)
				throws RemoteException {
			final String from = presence.getFrom();
			final boolean hideDisconnected = mSettings.getBoolean(
					SETTINGS_HIDDEN_CONTACT, true);
			final Contact contact = mRoster.getContact(StringUtils
					.parseBareAddress(from));
			final boolean visible = !hideDisconnected
					|| Status.statusOnline(contact.getStatus());
			final List<String> groups = contact.getGroups();
			for (final Map.Entry<String, List<Contact>> entry : mContactOnGroup
					.entrySet()) {
				final List<Contact> contactByGroups = entry.getValue();
				if (contactByGroups == mListContact) {
					updateCurrentList(entry.getKey(), contact);
					continue;
				}
				contactByGroups.remove(contact);
				if (visible) {
					if (groups.contains(entry.getKey())) {
						contactByGroups.add(contact);
					}
				}
			}
			if (visible) {
				addToSpecialList(contact);
			}
		}

		/**
		 * Update current list.
		 * 
		 * @param listName
		 *            the list name
		 * @param contact
		 *            the contact
		 */
		private void updateCurrentList(final String listName,
				final Contact contact) {
			final boolean hideDisconnected = mSettings.getBoolean(
					SETTINGS_HIDDEN_CONTACT, true);
			final List<String> groups = contact.getGroups();
			final String noGroup = getString(R.string.contact_list_no_group);
			final String allGroup = getString(R.string.contact_list_all_contact);
			final boolean add = (!hideDisconnected || Status
					.statusOnline(contact.getStatus()))
					&& // must
					// show
					// and
					(listName.equals(noGroup) && groups.isEmpty() || // in no
							// group
							groups.contains(listName) || // or in current
					listName.equals(allGroup) // or in all
					);
			mHandler.post(new Runnable() {
				public void run() {
					mListContact.remove(contact);
					if (add) {
						mListContact.add(contact);
					}
					mAdapterContactList.notifyDataSetChanged();
				}
			});

		}

	}

	/**
	 * The Class FBeemerServiceConnection.
	 */
	private class FBeemerServiceConnection implements ServiceConnection {

		/**
		 * Instantiates a new f beemer service connection.
		 */
		public FBeemerServiceConnection() {
		}

		/**
		 * Assign contact to groups.
		 * 
		 * @param contacts
		 *            the contacts
		 * @param groupNames
		 *            the group names
		 */
		private void assignContactToGroups(final List<Contact> contacts,
				final List<String> groupNames) {
			final boolean hideDisconnected = mSettings.getBoolean(
					SETTINGS_HIDDEN_CONTACT, true);
			mContactOnGroup.clear();
			final List<Contact> all = new LinkedList<Contact>();
			final List<Contact> noGroups = new LinkedList<Contact>();
			for (final String group : groupNames) {
				mContactOnGroup.put(group, new LinkedList<Contact>());
			}
			for (final Contact c : contacts) {
				if (hideDisconnected && !Status.statusOnline(c.getStatus())) {
					continue;
				}
				all.add(c);
				final List<String> groups = c.getGroups();
				if (groups.isEmpty()) {
					noGroups.add(c);
				} else {
					for (final String currentGroup : groups) {
						final List<Contact> contactsByGroups = mContactOnGroup
								.get(currentGroup);
						contactsByGroups.add(c);
					}
				}
			}
			mContactOnGroup.put(getString(R.string.contact_list_no_group),
					noGroups);
			mContactOnGroup.put(getString(R.string.contact_list_all_contact),
					all);
		}

		/**
		 * Make sorted list.
		 * 
		 * @param map
		 *            the map
		 */
		private void makeSortedList(final Map<String, List<Contact>> map) {
			for (final Map.Entry<String, List<Contact>> entry : map.entrySet()) {
				final List<Contact> l = entry.getValue();
				entry.setValue(new SortedList<Contact>(l, mComparator));
			}
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		public void onServiceConnected(final ComponentName name,
				final IBinder service) {
			mXmppFacade = IXmppFacade.Stub.asInterface(service);
			try {
				mRoster = mXmppFacade.getRoster();
				if (mRoster != null) {
					mRoster.getContactList();
					final List<String> tmpGroupList = mRoster.getGroupsNames();
					Collections.sort(tmpGroupList);
					mListGroup.clear();
					mListGroup
							.add(getString(R.string.contact_list_all_contact));
					mListGroup.addAll(tmpGroupList);
					mListGroup.add(getString(R.string.contact_list_no_group));
					assignContactToGroups(mRoster.getContactList(),
							tmpGroupList);
					makeSortedList(mContactOnGroup);
					if (!mSettings
							.getBoolean("settings_key_hide_groups", false)) {
						showGroups();
					} else {
						hideGroups();
					}
					final String group = getString(R.string.contact_list_all_contact);
					buildContactList(group);
					mRoster.addRosterListener(mFBeemerRosterListener);
				}
			} catch (final RemoteException e) {
				e.printStackTrace();
			}
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		public void onServiceDisconnected(final ComponentName name) {
			try {
				mRoster.removeRosterListener(mFBeemerRosterListener);
			} catch (final RemoteException e) {
				e.printStackTrace();
			}
			mXmppFacade = null;
			mRoster = null;
			mListContact.clear();
			mListGroup.clear();
			mContactOnGroup.clear();
			mBinded = false;
		}
	}

	/** The Constant SERVICE_INTENT. */
	private static final Intent									SERVICE_INTENT			= new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("com.unkzdomain.fbeemer",
				"com.unkzdomain.fbeemer.AppService"));
	}
	
	/** The Constant SETTINGS_HIDDEN_CONTACT. */
	private static final String									SETTINGS_HIDDEN_CONTACT	= "settings_key_hidden_contact";
	
	/** The m adapter contact list. */
	private final FBeemerContactList							mAdapterContactList		= new FBeemerContactList();
	
	/** The m list group. */
	private final List<String>									mListGroup				= new ArrayList<String>();
	
	/** The m contact on group. */
	private final Map<String, List<Contact>>					mContactOnGroup			= new HashMap<String, List<Contact>>();
	
	/** The m on contact click. */
	private final FBeemerContactListOnClick						mOnContactClick			= new FBeemerContactListOnClick();
	
	/** The m handler. */
	private final Handler										mHandler				= new Handler();
	
	/** The m serv conn. */
	private final ServiceConnection								mServConn				= new FBeemerServiceConnection();
	
	/** The m receiver. */
	private final AppBroadcastReceiver							mReceiver				= new AppBroadcastReceiver();
	
	/** The m comparator. */
	private final ComparatorContactListByStatusAndName<Contact>	mComparator				= new ComparatorContactListByStatusAndName<Contact>();
	
	/** The m f beemer roster listener. */
	private final FBeemerRosterListener							mFBeemerRosterListener	= new FBeemerRosterListener();
	
	/** The m list contact. */
	private List<Contact>										mListContact;
	
	/** The m roster. */
	private IRoster												mRoster;

	/** The m selected contact. */
	private Contact												mSelectedContact;

	/** The m xmpp facade. */
	private IXmppFacade											mXmppFacade;

	/** The m settings. */
	private SharedPreferences									mSettings;

	/** The m inflater. */
	private LayoutInflater										mInflater;

	/** The m binded. */
	private boolean												mBinded;

	/**
	 * Instantiates a new contact list.
	 */
	public ContactList() {
	}

	/**
	 * Builds the contact list.
	 * 
	 * @param group
	 *            the group
	 */
	private void buildContactList(final String group) {
		mListContact = mContactOnGroup.get(group);
		mAdapterContactList.notifyDataSetChanged();
	}

	/**
	 * Hide groups.
	 */
	private void hideGroups() {
		// View v = findViewById(R.id.contactlist_groupstub);
		// if (v != null)
		// v.setVisibility(View.GONE);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		Intent in;
		boolean result;
		if (mSelectedContact != null) {
			switch (item.getItemId()) {
				case R.id.contact_list_context_menu_chat_item:
					final List<String> res = mSelectedContact.getMRes();
					if (res.isEmpty()) {
						result = false;
						break;
					}
					for (final String resv : res) {
						in = new Intent(this, Chat.class);
						in.setData(mSelectedContact.toUri(resv));
						item.getSubMenu().add(resv).setIntent(in);
					}
					result = true;
					break;
				case R.id.contact_list_context_menu_call_item:
					try {
						mXmppFacade.call(mSelectedContact.getJID() + "/psi");
						result = true;
					} catch (final RemoteException e) {
						e.printStackTrace();
					}
					result = true;
					break;
				case R.id.contact_list_context_menu_user_info:
					item.getSubMenu().setHeaderTitle(mSelectedContact.getJID());
					result = true;
					break;
				default:
					result = super.onContextItemSelected(item);
					break;
			}
			return result;
		}
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(final Bundle saveBundle) {
		super.onCreate(saveBundle);
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.contactlist);

		this.registerReceiver(mReceiver, new IntentFilter(
				AppBroadcastReceiver.CONNECTION_CLOSED));

		mInflater = getLayoutInflater();
		mListContact = new ArrayList<Contact>();
		final ListView listView = (ListView) findViewById(R.id.contactlist);
		listView.setOnItemClickListener(mOnContactClick);
		registerForContextMenu(listView);
		listView.setAdapter(mAdapterContactList);
		mAdapterContactList.notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v,
			final ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contactlist_context, menu);
		final AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		final Contact c = mListContact.get(info.position);
		try {
			mSelectedContact = mRoster.getContact(c.getJID());
		} catch (final RemoteException e) {
			e.printStackTrace();
		}
		menu.setHeaderTitle(mSelectedContact.getJID());
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_list, menu);

		return true;
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.contact_list_menu_settings:
				startActivity(new Intent(this, Settings.class));
				return true;
			case R.id.menu_disconnect:
				stopService(SERVICE_INTENT);
				finish();
				return true;
			default:
				return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onPause()
	 */

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (mRoster != null) {
				mRoster.removeRosterListener(mFBeemerRosterListener);
				mRoster = null;
			}
		} catch (final RemoteException e) {
			// TODO Report error
		}
		if (mBinded) {
			unbindService(mServConn);
			mBinded = false;
		}
		mXmppFacade = null;
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (!mBinded) {
			mBinded = bindService(SERVICE_INTENT, mServConn, BIND_AUTO_CREATE);
		}
		mAdapterContactList.notifyDataSetChanged();
	}

	/**
	 * Show groups.
	 */
	private void showGroups() {
		//
		// ViewStub stub = (ViewStub) findViewById(R.id.contactlist_stub);
		// if (stub != null) {
		// View v = stub.inflate();
		// Gallery g = (Gallery) v.findViewById(R.id.contactlist_banner);
		// g.setOnItemClickListener(new OnItemClickGroupName());
		// g.setAdapter(mAdapterBanner);
		// g.setSelection(0);
		// } else {
		// ((LinearLayout) findViewById(R.id.contactlist_groupstub))
		// .setVisibility(View.VISIBLE);
		// Gallery g = (Gallery) findViewById(R.id.contactlist_banner);
		// g.setSelection(0);
		// }
	}
}