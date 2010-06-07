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

import com.medialets.android.analytics.ManagedActivity;
import com.unkzdomain.fbeemer.R;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractManagedActivity.
 */
public abstract class AbstractManagedActivity extends ManagedActivity {

	/*
	 * (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#getAppVersion()
	 */
	@Override
	public String getAppVersion() {
		// try {
		// return this.getPackageManager().getActivityInfo(
		// new ComponentName(this.getBaseContext(),
		// getString(R.string.package_name)), 0).packageName;
		// } catch (NameNotFoundException e) {
		// mManager.trackEvent(new MMEvent("Error getting app version"));
		// return null;
		// }
		return getString(R.string.medialets_app_version);
	}

	/*
	 * (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#getMMAppID()
	 */
	@Override
	public String getMMAppID() {
		return getString(R.string.app_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#useLocation()
	 */
	@Override
	public boolean useLocation() {
		return false;
	}

}
