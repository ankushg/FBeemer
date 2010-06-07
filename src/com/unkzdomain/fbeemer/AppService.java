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

	/**
	 * The Class FBeemerServiceBroadcastReceiver.
	 */
	private class FBeemerServiceBroadcastReceiver extends BroadcastReceiver {

		/** The old status. */
		private String	oldStatus;
		
		/** The old mode. */
		private int		oldMode;

		/**
		 * Instantiates a new f beemer service broadcast receiver.
		 */
		public FBeemerServiceBroadcastReceiver() {
		}

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String intentAction = intent.getAction();
			if (intentAction.equals(Intent.ACTION_SCREEN_OFF)) {
				oldMode = connection.getPreviousMode();
				oldStatus = connection.getPreviousStatus();
				if (connection.isAuthentificated()) {
					connection.changeStatus(Status.AWAY, settings.getString(
							"settings_away_message", "Away"));
				}
			} else if (intentAction.equals(Intent.ACTION_SCREEN_ON)) {
				if (connection.isAuthentificated()) {
					connection.changeStatus(oldMode, oldStatus);
				}
			}
		}
	}

	/**
	 * The listener interface for receiving FBeemerServicePreference events. The
	 * class that is interested in processing a FBeemerServicePreference event
	 * implements this interface, and the object created with that class is
	 * registered with a component using the component's
	 * <code>addFBeemerServicePreferenceListener<code> method. When
	 * the FBeemerServicePreference event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see FBeemerServicePreferenceEvent
	 */
	private class FBeemerServicePreferenceListener implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		/**
		 * Instantiates a new f beemer service preference listener.
		 */
		public FBeemerServicePreferenceListener() {
		}

		/* (non-Javadoc)
		 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
		 */
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if ("settings_away_chk".equals(key)) {
				if (sharedPreferences.getBoolean("settings_away_chk", false)) {
					onOffReceiverIsRegistered = true;
					registerReceiver(onOffReceiver, new IntentFilter(
							Intent.ACTION_SCREEN_OFF));
					registerReceiver(onOffReceiver, new IntentFilter(
							Intent.ACTION_SCREEN_ON));
				} else {
					onOffReceiverIsRegistered = false;
					unregisterReceiver(onOffReceiver);
				}
			}
		}
	}

	/** The Constant NOTIFICATION_STATUS_ID. */
	public static final int							NOTIFICATION_STATUS_ID	= 100;

	/** The Constant DEFAULT_XMPP_PORT. */
	private static final int						DEFAULT_XMPP_PORT		= 5222;
	
	/** The notification manager. */
	private NotificationManager						notificationManager;
	
	/** The connection. */
	private XmppConnectionAdapter					connection;
	
	/** The settings. */
	private SharedPreferences						settings;

	/** The login. */
	private String									login;
	
	/** The password. */
	private String									password;
	
	/** The connection config. */
	private final ConnectionConfiguration			connectionConfig		= new ConnectionConfiguration(
																					"chat.facebook.com",
																					DEFAULT_XMPP_PORT,
																					"chat.facebook.com",
																					ProxyInfo
																							.forNoProxy());

	/** The bind. */
	private IXmppFacade.Stub						bind;
	
	/** The receiver. */
	private final AppBroadcastReceiver				receiver				= new AppBroadcastReceiver();

	/** The on off receiver. */
	private final FBeemerServiceBroadcastReceiver	onOffReceiver			= new FBeemerServiceBroadcastReceiver();

	/** The pref listener. */
	private final FBeemerServicePreferenceListener	prefListener			= new FBeemerServicePreferenceListener();

	/** The on off receiver is registered. */
	private boolean									onOffReceiverIsRegistered;

	/**
	 * Instantiates a new app service.
	 */
	public AppService() {
	}

	/**
	 * Configure.
	 * 
	 * @param pm
	 *            the pm
	 */
	private void configure(final ProviderManager pm) {
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
	}

	/**
	 * Delete notification.
	 * 
	 * @param id
	 *            the id
	 */
	public void deleteNotification(final int id) {
		notificationManager.cancel(id);
	}

	/**
	 * Gets the bind.
	 * 
	 * @return the bind
	 */
	public IXmppFacade getBind() {
		return bind;
	}

	/**
	 * Gets the notification manager.
	 * 
	 * @return the notification manager
	 */
	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	/**
	 * Gets the service preference.
	 * 
	 * @return the service preference
	 */
	public SharedPreferences getServicePreference() {
		return settings;
	}

	/**
	 * Inits the connection config.
	 */
	private void initConnectionConfig() {
		if (settings.getBoolean("settings_key_xmpp_tls_use", false)) {
			connectionConfig.setSecurityMode(SecurityMode.required);
		}
		connectionConfig.setDebuggerEnabled(false);
		connectionConfig.setSendPresence(true);
		connectionConfig.setTruststoreType("BKS");
		connectionConfig.setTruststorePath("/system/etc/security/cacerts.bks");
	}

	/**
	 * Inits the jingle.
	 * 
	 * @param adaptee
	 *            the adaptee
	 */
	public void initJingle(final XMPPConnection adaptee) {
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(final Intent intent) {
		return bind;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(receiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(prefListener);
		if (settings.getBoolean("settings_away_chk", false)) {
			onOffReceiverIsRegistered = true;
			registerReceiver(onOffReceiver, new IntentFilter(
					Intent.ACTION_SCREEN_OFF));
			registerReceiver(onOffReceiver, new IntentFilter(
					Intent.ACTION_SCREEN_ON));
		}
		final String tmpJid = settings.getString(
				MainApplication.ACCOUNT_USERNAME_KEY, "");
		login = tmpJid;
		password = settings.getString(MainApplication.ACCOUNT_PASSWORD_KEY, "");

		initConnectionConfig();
		configure(ProviderManager.getInstance());

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		connection = new XmppConnectionAdapter(connectionConfig, login,
				password, this);

		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
		bind = new XmppFacade(connection);
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		resetStatus();
		notificationManager.cancelAll();
		unregisterReceiver(receiver);
		settings.unregisterOnSharedPreferenceChangeListener(prefListener);
		if (onOffReceiverIsRegistered) {
			unregisterReceiver(onOffReceiver);
		}
		if (connection.isAuthentificated() && AppConnectivity.isConnected(this)) {
			connection.disconnect();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(final Intent intent, final int startId) {
		super.onStart(intent, startId);
		try {
			connection.connectAsync();
		} catch (final RemoteException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(final Intent intent) {
		if (!connection.getAdaptee().isConnected()) {
			this.stopSelf();
		}
		return true;
	}

	/**
	 * Reset status.
	 */
	public void resetStatus() {
		final Editor edit = settings.edit();
		edit.putInt(MainApplication.STATUS_KEY, 1);
		edit.commit();
	}

	/**
	 * Send notification.
	 * 
	 * @param id
	 *            the id
	 * @param notif
	 *            the notif
	 */
	public void sendNotification(final int id, final Notification notif) {
		if (settings.getBoolean(MainApplication.NOTIFICATION_VIBRATE_KEY, true)) {
			notif.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notif.defaults |= Notification.DEFAULT_LIGHTS;
		final String ringtoneStr = settings.getString(
				MainApplication.NOTIFICATION_SOUND_KEY, "");
		notif.sound = Uri.parse(ringtoneStr);
		notificationManager.notify(id, notif);
	}
}
