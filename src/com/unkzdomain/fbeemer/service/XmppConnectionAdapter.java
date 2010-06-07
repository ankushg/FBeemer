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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.unkzdomain.fbeemer.AppService;
import com.unkzdomain.fbeemer.MainApplication;
import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.aidl.IBeemConnectionListener;
import com.unkzdomain.fbeemer.service.aidl.IChatManager;
import com.unkzdomain.fbeemer.service.aidl.IRoster;
import com.unkzdomain.fbeemer.service.aidl.IXmppConnection;
import com.unkzdomain.fbeemer.ui.ContactList;
import com.unkzdomain.fbeemer.utils.AppBroadcastReceiver;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class XmppConnectionAdapter.
 */
public class XmppConnectionAdapter extends IXmppConnection.Stub {

	/**
	 * The Class ConnexionListenerAdapter.
	 */
	private class ConnexionListenerAdapter implements ConnectionListener {

		/**
		 * Instantiates a new connexion listener adapter.
		 */
		public ConnexionListenerAdapter() {
		}

		/*
		 * (non-Javadoc)
		 * @see org.jivesoftware.smack.ConnectionListener#connectionClosed()
		 */

		public void connectionClosed() {
			mRoster = null;
			final Intent intent = new Intent(
					AppBroadcastReceiver.CONNECTION_CLOSED);
			intent.putExtra("message", mService
					.getString(R.string.FBeemerBroadcastReceiverDisconnect));
			intent.putExtra("normally", true);
			mService.sendBroadcast(intent);
			mService.stopSelf();
			mApplication.setConnected(false);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.ConnectionListener#connectionClosedOnError
		 * (java.lang.Exception)
		 */

		public void connectionClosedOnError(final Exception exception) {
			// TODO Report error
			mRoster = null;
			final Intent intent = new Intent(
					AppBroadcastReceiver.CONNECTION_CLOSED);
			intent.putExtra("message", exception.getMessage());
			mService.sendBroadcast(intent);
			mService.stopSelf();
			mApplication.setConnected(false);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jivesoftware.smack.ConnectionListener#reconnectingIn(int)
		 */

		public void reconnectingIn(final int arg0) {
			// TODO Report error
			final int n = mRemoteConnListeners.beginBroadcast();

			for (int i = 0; i < n; i++) {
				final IBeemConnectionListener listener = mRemoteConnListeners
						.getBroadcastItem(i);
				try {
					if (listener != null) {
						listener.reconnectingIn(arg0);
					}
				} catch (final RemoteException e) {
					// TODO Report error
				}
			}
			mRemoteConnListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.ConnectionListener#reconnectionFailed(java
		 * .lang.Exception)
		 */

		public void reconnectionFailed(final Exception arg0) {
			// TODO Report error
			final int r = mRemoteConnListeners.beginBroadcast();

			for (int i = 0; i < r; i++) {
				final IBeemConnectionListener listener = mRemoteConnListeners
						.getBroadcastItem(i);
				try {
					if (listener != null) {
						listener.reconnectionFailed();
					}
				} catch (final RemoteException e) {
					// TODO Report error
				}
			}
			mRemoteConnListeners.finishBroadcast();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.ConnectionListener#reconnectionSuccessful()
		 */

		public void reconnectionSuccessful() {
			mApplication.setConnected(true);
			final PacketFilter filter = new PacketFilter() {

				public boolean accept(final Packet packet) {
					if (packet instanceof Presence) {
						final Presence pres = (Presence) packet;
						if (pres.getType() == Presence.Type.subscribe) {
							return true;
						}
					}
					return false;
				}
			};

			mAdaptee.addPacketListener(new PacketListener() {

				public void processPacket(final Packet packet) {
					// String from = packet.getFrom();
					// Notification notif = new Notification(
					// android.R.drawable.stat_notify_more, mService
					// .getString(R.string.AcceptContactRequest,
					// from), System.currentTimeMillis());
					// notif.flags = Notification.FLAG_AUTO_CANCEL;
					// Intent intent = new Intent(mService,
					// Subscription.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(
					// "from", from);
					// notif.setLatestEventInfo(mService, from,
					// mService.getString(
					// R.string.AcceptContactRequestFrom, from),
					// PendingIntent.getActivity(mService, 0, intent,
					// PendingIntent.FLAG_ONE_SHOT));
					// int id = packet.hashCode();
					// mService.sendNotification(id, notif);
				}
			}, filter);

			final int n = mRemoteConnListeners.beginBroadcast();

			for (int i = 0; i < n; i++) {
				final IBeemConnectionListener listener = mRemoteConnListeners
						.getBroadcastItem(i);
				try {
					if (listener != null) {
						listener.reconnectionSuccessful();
					}
				} catch (final RemoteException e) {
					// TODO Report error
				}
			}
			mRemoteConnListeners.finishBroadcast();
		}
	}

	/**
	 * The listener interface for receiving subscribePacket events. The class
	 * that is interested in processing a subscribePacket event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addSubscribePacketListener<code> method. When
	 * the subscribePacket event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see SubscribePacketEvent
	 */
	private class SubscribePacketListener implements PacketListener {

		/**
		 * Instantiates a new subscribe packet listener.
		 */
		public SubscribePacketListener() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.jivesoftware.smack.PacketListener#processPacket(org.jivesoftware
		 * .smack.packet.Packet)
		 */
		public void processPacket(final Packet packet) {
			// if (!(packet instanceof Presence))
			// return;
			// Presence p = (Presence) packet;
			// if (p.getType() != Presence.Type.subscribe)
			// return;
			// String from = p.getFrom();
			// Notification notification = new Notification(
			// android.R.drawable.stat_notify_more, mService.getString(
			// R.string.AcceptContactRequest, from), System
			// .currentTimeMillis());
			// notification.flags = Notification.FLAG_AUTO_CANCEL;
			// Intent intent = new Intent(mService, Subscription.class);
			// intent.putExtra("from", from);
			// notification.setLatestEventInfo(mService, from,
			// mService.getString(
			// R.string.AcceptContactRequestFrom, from), PendingIntent
			// .getActivity(mService, 0, intent,
			// PendingIntent.FLAG_ONE_SHOT));
			// int id = p.hashCode();
			// mService.sendNotification(id, notification);
		}
	}

	/** The Constant SMACK_PRIORITY_MIN. */

	private static final int									SMACK_PRIORITY_MIN			= -128;

	/** The Constant SMACK_PRIORITY_MAX. */
	private static final int									SMACK_PRIORITY_MAX			= 128;

	/** The m adaptee. */
	private final XMPPConnection								mAdaptee;

	/** The m chat manager. */
	private IChatManager										mChatManager;

	/** The m login. */
	private final String										mLogin;

	/** The m password. */
	private final String										mPassword;

	/** The m resource. */
	private String												mResource;

	/** The m error msg. */
	private String												mErrorMsg;

	/** The m roster. */
	private RosterAdapter										mRoster;

	/** The m previous priority. */
	private int													mPreviousPriority;

	/** The m previous mode. */
	private int													mPreviousMode;

	/** The m previous status. */
	private String												mPreviousStatus;

	/** The m service. */
	private final AppService									mService;

	/** The m application. */
	private MainApplication										mApplication;

	/** The m remote conn listeners. */
	private final RemoteCallbackList<IBeemConnectionListener>	mRemoteConnListeners		= new RemoteCallbackList<IBeemConnectionListener>();

	/** The m subscribe packet listener. */
	private final SubscribePacketListener						mSubscribePacketListener	= new SubscribePacketListener();

	/** The m con listener. */
	private final ConnexionListenerAdapter						mConListener				= new ConnexionListenerAdapter();

	/**
	 * Instantiates a new xmpp connection adapter.
	 * 
	 * @param config
	 *            the config
	 * @param login
	 *            the login
	 * @param password
	 *            the password
	 * @param service
	 *            the service
	 */
	public XmppConnectionAdapter(final ConnectionConfiguration config,
			final String login, final String password, final AppService service) {
		this(new XMPPConnection(config), login, password, service);
	}

	/**
	 * Instantiates a new xmpp connection adapter.
	 * 
	 * @param con
	 *            the con
	 * @param login
	 *            the login
	 * @param password
	 *            the password
	 * @param service
	 *            the service
	 */
	private XmppConnectionAdapter(final XMPPConnection con, final String login,
			final String password, final AppService service) {
		mAdaptee = con;
		PrivacyListManager.getInstanceFor(mAdaptee);
		mLogin = login;
		mPassword = password;
		mService = service;
		final Context ctx = mService.getApplicationContext();
		if (ctx instanceof MainApplication) {
			mApplication = (MainApplication) ctx;
		}
		final SharedPreferences pref = mService.getServicePreference();
		try {
			mPreviousPriority = Integer.parseInt(pref.getString(
					"settings_key_priority", "0"));
			mResource = pref.getString("settings_key_resource", "FBeemer");
		} catch (final NumberFormatException ex) {
			mPreviousPriority = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#addConnectionListener
	 * (com.unkzdomain.fbeemer.service.aidl.IBeemConnectionListener)
	 */

	public void addConnectionListener(final IBeemConnectionListener listen)
			throws RemoteException {
		if (listen != null) {
			mRemoteConnListeners.register(listen);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#changeStatus(int,
	 * java.lang.String)
	 */

	public void changeStatus(final int status, final String msg) {
		changeStatusAndPriority(status, msg, mPreviousPriority);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#changeStatusAndPriority
	 * (int, java.lang.String, int)
	 */

	public void changeStatusAndPriority(final int status, final String msg,
			final int priority) {
		final Presence pres = new Presence(Presence.Type.available);
		String m;
		if (msg != null) {
			m = msg;
		} else {
			m = mPreviousStatus;
		}
		pres.setStatus(m);
		mPreviousStatus = m;
		final Presence.Mode mode = Status.getPresenceModeFromStatus(status);
		if (mode != null) {
			pres.setMode(mode);
			mPreviousMode = status;
		} else {
			pres.setMode(Status.getPresenceModeFromStatus(mPreviousMode));
		}
		int p = priority;
		if (priority < SMACK_PRIORITY_MIN) {
			p = SMACK_PRIORITY_MIN;
		}
		if (priority > SMACK_PRIORITY_MAX) {
			p = SMACK_PRIORITY_MAX;
		}
		mPreviousPriority = p;
		pres.setPriority(p);
		mAdaptee.sendPacket(pres);
		updateNotification(m);
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#connect()
	 */
	public boolean connect() throws RemoteException {
		if (mAdaptee.isConnected()) {
			return true;
		} else {
			try {
				mAdaptee.connect();
				mAdaptee.addConnectionListener(mConListener);
				return true;
			} catch (final XMPPException e) {
				// TODO Report error
				try {
					final String str = mService.getResources().getString(
							mService.getResources().getIdentifier(
									e.getXMPPError().getCondition().replace(
											"-", "_"), "string",
									"com.unkzdomain.fbeemer"));
					mErrorMsg = str;
				} catch (final NullPointerException e2) {
					if (!"".equals(e.getMessage())) {
						mErrorMsg = e.getMessage();
					} else {
						mErrorMsg = e.toString();
					}
				}
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#connectAsync()
	 */

	public final void connectAsync() throws RemoteException {
		if (mAdaptee.isConnected() || mAdaptee.isAuthenticated()) {
			return;
		}
		final Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					connectSync();
				} catch (final RemoteException e) {
					// TODO Report error
				}
			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#connectSync()
	 */

	public boolean connectSync() throws RemoteException {
		if (connect()) {
			return login();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#disconnect()
	 */

	public boolean disconnect() {
		if (mAdaptee != null && mAdaptee.isConnected()) {
			mAdaptee.disconnect();
		}
		return true;
	}

	/**
	 * Gets the adaptee.
	 * 
	 * @return the adaptee
	 */
	public XMPPConnection getAdaptee() {
		return mAdaptee;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#getChatManager()
	 */

	public IChatManager getChatManager() throws RemoteException {
		return mChatManager;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#getErrorMessage()
	 */

	public String getErrorMessage() {
		return mErrorMsg;
	}

	/**
	 * Gets the previous mode.
	 * 
	 * @return the previous mode
	 */
	public int getPreviousMode() {
		return mPreviousMode;
	}

	/**
	 * Gets the previous status.
	 * 
	 * @return the previous status
	 */
	public String getPreviousStatus() {
		return mPreviousStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#getRoster()
	 */

	public IRoster getRoster() throws RemoteException {
		if (mRoster != null) {
			return mRoster;
		}
		final Roster adap = mAdaptee.getRoster();
		if (adap == null) {
			return null;
		}
		mRoster = new RosterAdapter(adap, mService);
		return mRoster;
	}

	/**
	 * Inits the features.
	 */
	private void initFeatures() {
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager
				.getInstanceFor(mAdaptee);
		if (sdm == null) {
			sdm = new ServiceDiscoveryManager(mAdaptee);
		}
		sdm.addFeature("http://jabber.org/protocol/disco#info");
		sdm.addFeature("jabber:iq:privacy");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#isAuthentificated()
	 */
	public boolean isAuthentificated() {
		return mAdaptee.isAuthenticated();
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppConnection#login()
	 */
	public boolean login() throws RemoteException {
		if (mAdaptee.isAuthenticated()) {
			return true;
		}
		if (!mAdaptee.isConnected()) {
			return false;
		}
		try {
			mAdaptee.login(mLogin, mPassword, mResource);
			mChatManager = new AppChatManager(mAdaptee.getChatManager(),
					mService);

			initFeatures(); // pour declarer les features xmpp qu'on
			// supporte

			final PacketFilter filter = new PacketFilter() {

				public boolean accept(final Packet packet) {
					if (packet instanceof Presence) {
						final Presence pres = (Presence) packet;
						if (pres.getType() == Presence.Type.subscribe) {
							return true;
						}
					}
					return false;
				}
			};

			mAdaptee.addPacketListener(mSubscribePacketListener, filter);

			mService.resetStatus();
			mService.initJingle(mAdaptee);

			mApplication.setConnected(true);
			changeStatus(Status.AVAILABLE, mService.getServicePreference()
					.getString("status_text", ""));
			return true;
		} catch (final XMPPException e) {
			// TODO Report error
			mErrorMsg = mService.getString(R.string.error_login_authentication);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppConnection#removeConnectionListener
	 * (com.unkzdomain.fbeemer.service.aidl.IBeemConnectionListener)
	 */

	public void removeConnectionListener(final IBeemConnectionListener listen)
			throws RemoteException {
		if (listen != null) {
			mRemoteConnListeners.unregister(listen);
		}
	}

	/**
	 * Update notification.
	 * 
	 * @param text
	 *            the text
	 */
	private void updateNotification(final String text) {
		Notification mStatusNotification;
		mStatusNotification = new Notification(
				com.unkzdomain.fbeemer.R.drawable.notification_icon, text,
				System.currentTimeMillis());
		mStatusNotification.defaults = Notification.DEFAULT_LIGHTS;
		mStatusNotification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;

		mStatusNotification.setLatestEventInfo(mService, "FBeemer Active",
				text, PendingIntent.getActivity(mService, 0, new Intent(
						mService, ContactList.class), 0));
		mService.getNotificationManager().notify(
				AppService.NOTIFICATION_STATUS_ID, mStatusNotification);
	}

}