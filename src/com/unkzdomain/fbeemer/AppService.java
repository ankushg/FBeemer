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
package com.unkzdomain.fbeemer;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.proxy.ProxyInfo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.unkzdomain.fbeemer.service.XmppConnectionAdapter;
import com.unkzdomain.fbeemer.service.XmppFacade;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;
import com.unkzdomain.fbeemer.utils.AppBroadcastReceiver;
import com.unkzdomain.fbeemer.utils.AppConnectivity;
import com.unkzdomain.fbeemer.utils.Status;

// TODO: Auto-generated Javadoc
/**
 * The Class AppService.
 */
public class AppService extends Service {

	/** The Constant NOTIFICATION_STATUS_ID. */
	public static final int						NOTIFICATION_STATUS_ID	= 100;

	/** The Constant DEFAULT_XMPP_PORT. */
	private static final int					DEFAULT_XMPP_PORT		= 5222;

	/** The m notification manager. */
	private NotificationManager					mNotificationManager;

	/** The m connection. */
	private XmppConnectionAdapter				mConnection;

	/** The m settings. */
	private SharedPreferences					mSettings;

	/** The m login. */
	private String								mLogin;

	/** The m password. */
	private String								mPassword;

	/** The m host. */
	private String								mHost;

	/** The m service. */
	private String								mService;

	/** The m port. */
	private int									mPort;

	/** The m connection configuration. */
	private ConnectionConfiguration				mConnectionConfiguration;

	/** The m proxy info. */
	private ProxyInfo							mProxyInfo;

	/** The m bind. */
	private IXmppFacade.Stub					mBind;

	/** The m receiver. */
	private final AppBroadcastReceiver			mReceiver				= new AppBroadcastReceiver();

	/** The m on off receiver. */
	private final AppServiceBroadcastReceiver	mOnOffReceiver			= new AppServiceBroadcastReceiver();

	/** The m preference listener. */
	private final AppServicePreferenceListener	mPreferenceListener		= new AppServicePreferenceListener();

	/** The m on off receiver is registered. */
	private boolean								mOnOffReceiverIsRegistered;

	/**
	 * Instantiates a new app service.
	 */
	public AppService() {
	}

	/**
	 * Inits the connection config.
	 */
	private void initConnectionConfig() {
		mProxyInfo = ProxyInfo.forNoProxy();
		mConnectionConfiguration = new ConnectionConfiguration(mHost, mPort,
				mService, mProxyInfo);

		if (mSettings.getBoolean("settings_key_xmpp_tls_use", false)
				|| mSettings.getBoolean("settings_key_gmail", false)) {
			mConnectionConfiguration.setSecurityMode(SecurityMode.required);
		}
		mConnectionConfiguration.setDebuggerEnabled(false);
		mConnectionConfiguration.setSendPresence(true);
		// maybe not the universal path, but it works on most devices (Samsung
		// Galaxy, Google Nexus One)
		mConnectionConfiguration.setTruststoreType("BKS");
		mConnectionConfiguration
				.setTruststorePath("/system/etc/security/cacerts.bks");
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBind;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		if (!mConnection.getAdaptee().isConnected()) {
			this.stopSelf();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(mReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mSettings.registerOnSharedPreferenceChangeListener(mPreferenceListener);
		if (mSettings.getBoolean("settings_away_chk", false)) {
			mOnOffReceiverIsRegistered = true;
			registerReceiver(mOnOffReceiver, new IntentFilter(
					Intent.ACTION_SCREEN_OFF));
			registerReceiver(mOnOffReceiver, new IntentFilter(
					Intent.ACTION_SCREEN_ON));
		}
		final String tmpJid = mSettings.getString(
				MainApplication.ACCOUNT_USERNAME_KEY, "");
		mLogin = tmpJid;
		mPassword = mSettings.getString(MainApplication.ACCOUNT_PASSWORD_KEY,
				"");
		mPort = DEFAULT_XMPP_PORT;
		mService = "chat.facebook.com";
		mHost = mService;

		mHost = "chat.facebook.com";
		mPort = 5222;

		initConnectionConfig();
		configure(ProviderManager.getInstance());

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mConnection = new XmppConnectionAdapter(mConnectionConfiguration,
				mLogin, mPassword, this);

		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
		mBind = new XmppFacade(mConnection);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		resetStatus();
		mNotificationManager.cancelAll();
		unregisterReceiver(mReceiver);
		mSettings
				.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
		if (mOnOffReceiverIsRegistered) {
			unregisterReceiver(mOnOffReceiver);
		}
		if (mConnection.isAuthentificated()
				&& AppConnectivity.isConnected(this)) {
			mConnection.disconnect();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			mConnection.connectAsync();
		} catch (final RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send notification.
	 * 
	 * @param id
	 *            the id
	 * @param notif
	 *            the notif
	 */
	public void sendNotification(int id, Notification notif) {
		if (mSettings
				.getBoolean(MainApplication.NOTIFICATION_VIBRATE_KEY, true)) {
			notif.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notif.defaults |= Notification.DEFAULT_LIGHTS;
		final String ringtoneStr = mSettings.getString(
				MainApplication.NOTIFICATION_SOUND_KEY, "");
		notif.sound = Uri.parse(ringtoneStr);
		mNotificationManager.notify(id, notif);
	}

	/**
	 * Delete notification.
	 * 
	 * @param id
	 *            the id
	 */
	public void deleteNotification(int id) {
		mNotificationManager.cancel(id);
	}

	/**
	 * Reset status.
	 */
	public void resetStatus() {
		final Editor edit = mSettings.edit();
		edit.putInt(MainApplication.STATUS_KEY, 1);
		edit.commit();
	}

	/**
	 * Inits the jingle.
	 * 
	 * @param adaptee
	 *            the adaptee
	 */
	public void initJingle(XMPPConnection adaptee) {
	}

	/**
	 * Gets the bind.
	 * 
	 * @return the bind
	 */
	public IXmppFacade getBind() {
		return mBind;
	}

	/**
	 * Gets the service preference.
	 * 
	 * @return the service preference
	 */
	public SharedPreferences getServicePreference() {
		return mSettings;
	}

	/**
	 * Gets the notification manager.
	 * 
	 * @return the notification manager
	 */
	public NotificationManager getNotificationManager() {
		return mNotificationManager;
	}

	/**
	 * Configure.
	 * 
	 * @param pm
	 *            the pm
	 */
	private void configure(ProviderManager pm) {
		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	}

	/**
	 * The listener interface for receiving appServicePreference events. The
	 * class that is interested in processing a appServicePreference event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addAppServicePreferenceListener<code> method. When
	 * the appServicePreference event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see AppServicePreferenceEvent
	 */
	private class AppServicePreferenceListener implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		/**
		 * Instantiates a new app service preference listener.
		 */
		public AppServicePreferenceListener() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * android.content.SharedPreferences.OnSharedPreferenceChangeListener
		 * #onSharedPreferenceChanged(android.content.SharedPreferences,
		 * java.lang.String)
		 */
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if ("settings_away_chk".equals(key)) {
				if (sharedPreferences.getBoolean("settings_away_chk", false)) {
					mOnOffReceiverIsRegistered = true;
					registerReceiver(mOnOffReceiver, new IntentFilter(
							Intent.ACTION_SCREEN_OFF));
					registerReceiver(mOnOffReceiver, new IntentFilter(
							Intent.ACTION_SCREEN_ON));
				} else {
					mOnOffReceiverIsRegistered = false;
					unregisterReceiver(mOnOffReceiver);
				}
			}
		}
	}

	/**
	 * The Class AppServiceBroadcastReceiver.
	 */
	private class AppServiceBroadcastReceiver extends BroadcastReceiver {

		/** The m old status. */
		private String	mOldStatus;

		/** The m old mode. */
		private int		mOldMode;

		/**
		 * Instantiates a new app service broadcast receiver.
		 */
		public AppServiceBroadcastReceiver() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String intentAction = intent.getAction();
			if (intentAction.equals(Intent.ACTION_SCREEN_OFF)) {
				mOldMode = mConnection.getPreviousMode();
				mOldStatus = mConnection.getPreviousStatus();
				if (mConnection.isAuthentificated()) {
					mConnection.changeStatus(Status.AWAY, mSettings.getString(
							"settings_away_message", "Away"));
				}
			} else if (intentAction.equals(Intent.ACTION_SCREEN_ON)) {
				if (mConnection.isAuthentificated()) {
					mConnection.changeStatus(mOldMode, mOldStatus);
				}
			}
		}
	}
}
