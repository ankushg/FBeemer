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

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// TODO: Auto-generated Javadoc
/**
 * The Class MainApplication.
 */
public class MainApplication extends Application {

	/**
	 * The listener interface for receiving preference events. The class that is
	 * interested in processing a preference event implements this interface,
	 * and the object created with that class is registered with a component
	 * using the component's <code>addPreferenceListener<code> method. When
	 * the preference event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see PreferenceEvent
	 */
	private class PreferenceListener implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		/*
		 * (non-Javadoc)
		 * @see
		 * android.content.SharedPreferences.OnSharedPreferenceChangeListener
		 * #onSharedPreferenceChanged(android.content.SharedPreferences,
		 * java.lang.String)
		 */
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (MainApplication.ACCOUNT_USERNAME_KEY.equals(key)
					|| MainApplication.ACCOUNT_PASSWORD_KEY.equals(key)) {
				final String login = settings.getString(
						MainApplication.ACCOUNT_USERNAME_KEY, "");
				final String password = settings.getString(
						MainApplication.ACCOUNT_PASSWORD_KEY, "");
				isAccountConfigured = !"".equals(login) && !"".equals(password);
			}
		}
	}

	/** The Constant ACCOUNT_USERNAME_KEY. */
	public static final String					ACCOUNT_USERNAME_KEY		= "account_username";

	/** The Constant ACCOUNT_PASSWORD_KEY. */
	public static final String					ACCOUNT_PASSWORD_KEY		= "account_password";

	/** The Constant STATUS_KEY. */
	protected static final String				STATUS_KEY					= "status";

	/** The Constant NOTIFICATION_VIBRATE_KEY. */
	protected static final String				NOTIFICATION_VIBRATE_KEY	= "notification_vibrate";

	/** The Constant NOTIFICATION_SOUND_KEY. */
	protected static final String				NOTIFICATION_SOUND_KEY		= "notification_sound";

	/** The is connected. */
	private transient boolean					isConnected;

	/** The is account configured. */
	private transient boolean					isAccountConfigured;

	/** The settings. */
	private transient SharedPreferences			settings;

	/** The preference listener. */
	private transient final PreferenceListener	preferenceListener			= new PreferenceListener();

	/**
	 * Instantiates a new main application.
	 */
	public MainApplication() {
	}

	/**
	 * Checks if is account configured.
	 * 
	 * @return true, if is account configured
	 */
	public boolean isAccountConfigured() {
		return isAccountConfigured;
	}

	/**
	 * Checks if is connected.
	 * 
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		final String login = settings.getString(
				MainApplication.ACCOUNT_USERNAME_KEY, "");
		final String password = settings.getString(
				MainApplication.ACCOUNT_PASSWORD_KEY, "");
		isAccountConfigured = !("".equals(login) || "".equals(password));
		settings.registerOnSharedPreferenceChangeListener(preferenceListener);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		settings.unregisterOnSharedPreferenceChangeListener(preferenceListener);
	}

	/**
	 * Sets the connected.
	 * 
	 * @param newIsConnected
	 *            the new connected
	 */
	public void setConnected(final boolean newIsConnected) {
		isConnected = newIsConnected;
	}
}