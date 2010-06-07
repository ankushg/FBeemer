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
package com.unkzdomain.fbeemer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class AppConnectivity.
 */
public final class AppConnectivity {

	/**
	 * Checks if is connected.
	 * 
	 * @param ctx
	 *            the ctx
	 * @return true, if is connected
	 */
	public static boolean isConnected(final Context ctx) {
		final ConnectivityManager connManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}

}
