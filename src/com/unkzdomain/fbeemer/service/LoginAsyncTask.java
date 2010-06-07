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

import android.os.AsyncTask;
import android.os.RemoteException;

import com.unkzdomain.fbeemer.service.aidl.IXmppConnection;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;

// TODO: Auto-generated Javadoc
/**
 * The Class LoginAsyncTask.
 */
public class LoginAsyncTask extends AsyncTask<IXmppFacade, Integer, Boolean> {

	/** The Constant STATE_CONNECTION_RUNNING. */
	private static final int	STATE_CONNECTION_RUNNING	= 0;
	
	/** The Constant STATE_LOGIN_RUNNING. */
	private static final int	STATE_LOGIN_RUNNING			= 1;
	
	/** The Constant STATE_LOGIN_SUCCESS. */
	private static final int	STATE_LOGIN_SUCCESS			= 2;
	
	/** The Constant STATE_LOGIN_FAILED. */
	private static final int	STATE_LOGIN_FAILED			= 3;

	/** The m connection. */
	private IXmppConnection		mConnection;
	
	/** The m error message. */
	private String				mErrorMessage;

	/**
	 * Instantiates a new login async task.
	 */
	public LoginAsyncTask() {
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		return mErrorMessage;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		try {
			if (mConnection != null && mConnection.isAuthentificated()) {
				mConnection.disconnect();
			}
		} catch (final RemoteException e) {
			// TODO Report Error
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(final IXmppFacade... params) { // NO_UCD
		boolean result = true;
		final IXmppFacade facade = params[0];
		try {
			publishProgress(STATE_CONNECTION_RUNNING);
			mConnection = facade.createConnection();
			if (!mConnection.connect()) {
				mErrorMessage = mConnection.getErrorMessage();
				return false;
			}
			publishProgress(STATE_LOGIN_RUNNING);

			if (!mConnection.login()) {
				mErrorMessage = mConnection.getErrorMessage();
				publishProgress(STATE_LOGIN_FAILED);
				return false;
			}
			publishProgress(STATE_LOGIN_SUCCESS);
		} catch (final RemoteException e) {
			mErrorMessage = "Exception during connection :" + e;
			result = false;
		}
		return result;
	}
}
