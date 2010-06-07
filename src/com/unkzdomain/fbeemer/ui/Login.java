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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.unkzdomain.fbeemer.MainApplication;
import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.utils.AbstractManagedActivity;
import com.unkzdomain.fbeemer.utils.AppConnectivity;

// TODO: Auto-generated Javadoc
/**
 * The Class Login.
 */
public class Login extends AbstractManagedActivity { // NO_UCD

	/** The Constant LOGIN_REQUEST_CODE. */
	private static final int	LOGIN_REQUEST_CODE	= 1;

	/** The m text view. */
	private TextView			mTextView;

	/** The m is result. */
	private boolean				mIsResult;

	/** The m main application. */
	private MainApplication		mMainApplication;

	/**
	 * Instantiates a new login.
	 */
	public Login() {
	}

	/**
	 * Creates the about dialog.
	 */
	private void createAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String versionname;
		try {
			final PackageManager pm = getPackageManager();
			final PackageInfo pi = pm.getPackageInfo("com.unkzdomain.fbeemer",
					0);
			versionname = pi.versionName;
		} catch (final PackageManager.NameNotFoundException e) {
			versionname = "";
		}
		final String title = getString(R.string.login_about_title, versionname);
		builder.setTitle(title).setMessage(R.string.login_about_msg)
				.setCancelable(false);
		builder.setNeutralButton(R.string.login_about_button,
				new DialogInterface.OnClickListener() {

					public void onClick(final DialogInterface dialog,
							final int whichButton) {
						dialog.cancel();
					}
				});
		final AlertDialog aboutDialog = builder.create();
		aboutDialog.show();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (requestCode == LOGIN_REQUEST_CODE) {
			mIsResult = true;
			if (resultCode == Activity.RESULT_OK) {
				startActivity(new Intent(this, ContactList.class));
				finish();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				if (data != null) {
					final String tmp = data.getExtras().getString("message");
					Toast.makeText(Login.this, tmp, Toast.LENGTH_SHORT).show();
					mTextView.setText(tmp);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.medialets.android.analytics.ManagedActivity#onCreate(android.os.Bundle
	 * )
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Application app = getApplication();
		if (app instanceof MainApplication) {
			mMainApplication = (MainApplication) app;
			if (mMainApplication.isConnected()) {
				startActivity(new Intent(this, ContactList.class));
				finish();
			} else if (!mMainApplication.isAccountConfigured()) {
				startActivity(new Intent(this, Wizard.class));
				finish();
			}
		}
		setContentView(R.layout.login);
		mTextView = (TextView) findViewById(R.id.log_as_msg);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
			case R.id.login_menu_settings:
				mTextView.setText("");
				startActivity(new Intent(Login.this, Settings.class));
				result = true;
				break;
			case R.id.login_menu_about:
				createAboutDialog();
				result = true;
				break;
			case R.id.login_menu_login:
				if (mMainApplication.isAccountConfigured()) {
					final Intent i = new Intent(this, LoginAnim.class);
					startActivityForResult(i, LOGIN_REQUEST_CODE);
				}
				result = true;
				break;
			default:
				result = false;
				break;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (mMainApplication.isAccountConfigured() && !mIsResult
				&& AppConnectivity.isConnected(getApplicationContext())) {
			mTextView.setText("");
			final Intent i = new Intent(this, LoginAnim.class);
			startActivityForResult(i, LOGIN_REQUEST_CODE);
			mIsResult = false;
		} else {
			mTextView.setText(R.string.login_start_msg);
		}
	}
}
