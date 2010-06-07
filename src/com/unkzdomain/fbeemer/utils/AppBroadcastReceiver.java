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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.unkzdomain.fbeemer.AppService;
import com.unkzdomain.fbeemer.R;

// TODO: Auto-generated Javadoc
/**
 * The Class AppBroadcastReceiver.
 */
public class AppBroadcastReceiver extends BroadcastReceiver {

	/** The Constant CONNECTION_CLOSED. */
	public static final String	CONNECTION_CLOSED	= "FBeemerConnectionClosed";

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final String intentAction = intent.getAction();
		if (intentAction.equals(CONNECTION_CLOSED)) {
			final CharSequence message = intent.getCharSequenceExtra("message");
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

			if (context instanceof Activity) {
				final Activity act = (Activity) context;
				act.finish();
				// The service will be unbinded in the destroy of the activity.
			}
		} else if (intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION)
				&& intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
			Toast
					.makeText(
							context,
							context
									.getString(R.string.FBeemerBroadcastReceiverDisconnect),
							Toast.LENGTH_SHORT).show();
			context.stopService(new Intent(context, AppService.class));
		}
	}
}
