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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.unkzdomain.fbeemer.MainApplication;
import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.utils.AbstractManagedActivity;

// TODO: Auto-generated Javadoc
/**
 * The Class Account.
 */
public class Account extends AbstractManagedActivity implements
		OnClickListener { // NO_UCD

	/**
	 * The Class JidTextWatcher.
	 */
 private class JidTextWatcher implements TextWatcher {

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		public void afterTextChanged(final Editable s) {
			checkUsername(s.toString());
			nextButton.setEnabled(mValidJid && mValidPassword);
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
		 */
		public void beforeTextChanged(final CharSequence s, final int start,
				final int count, final int after) {
			// Do nothing
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
		 */
		public void onTextChanged(final CharSequence s, final int start,
				final int before, final int count) {
			// Do nothing
		}
	}

	/**
	 * The Class PasswordTextWatcher.
	 */
	private class PasswordTextWatcher implements TextWatcher {

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		public void afterTextChanged(final Editable s) {
			checkPassword(s.toString());
			nextButton.setEnabled(mValidPassword);
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
		 */
		public void beforeTextChanged(final CharSequence s, final int start,
				final int count, final int after) {
			// Do nothing

		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
		 */
		public void onTextChanged(final CharSequence s, final int start,
				final int before, final int count) {
			// Do nothing

		}
	}

	/** The Constant MANUAL_CONFIG. */
	private static transient final int			MANUAL_CONFIG	= 1;
	
	/** The next button. */
	private transient Button					nextButton;
	
	/** The account jid. */
	private transient EditText					accountJID;
	
	/** The account password. */
	private transient EditText					accountPassword;
	
	/** The jid text watcher. */
	private transient final JidTextWatcher		jidTextWatcher	= new JidTextWatcher();
	
	/** The pass text watcher. */
	private transient final PasswordTextWatcher	passTextWatcher	= new PasswordTextWatcher();

	/** The m valid jid. */
	private transient boolean					mValidJid;

	/** The m valid password. */
	private transient boolean					mValidPassword;

	/**
	 * Check password.
	 * 
	 * @param password
	 *            the password
	 */
	private void checkPassword(final String password) {
		if (password.length() > 0) {
			mValidPassword = true;
		} else {
			mValidPassword = false;
		}
	}

	/**
	 * Check username.
	 * 
	 * @param username
	 *            the username
	 */
	private void checkUsername(final String username) {
		final String name = username;
		if (TextUtils.isEmpty(name)) {
			mValidJid = false;
		} else {
			mValidJid = true;
		}
	}

	/**
	 * Configure account.
	 */
	private void configureAccount() {
		final SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor edit = settings.edit();
		edit.putString(MainApplication.ACCOUNT_USERNAME_KEY, accountJID
				.getText().toString());
		edit.putString(MainApplication.ACCOUNT_PASSWORD_KEY, accountPassword
				.getText().toString());
		edit.commit();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (requestCode == Account.MANUAL_CONFIG) {
			final SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			final String login = settings.getString(
					MainApplication.ACCOUNT_USERNAME_KEY, "");
			final String password = settings.getString(
					MainApplication.ACCOUNT_PASSWORD_KEY, "");
			accountJID.setText(login);
			accountPassword.setText(password);
			checkUsername(login);
			checkPassword(password);
			nextButton.setEnabled(mValidJid && mValidPassword);
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(final View v) {
		if (v == nextButton) {
			configureAccount();
			final Intent i = new Intent(this, Login.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see com.medialets.android.analytics.ManagedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wizard_account_configure);
		nextButton = (Button) findViewById(R.id.next);
		nextButton.setOnClickListener(this);
		accountJID = (EditText) findViewById(R.id.account_username);
		accountPassword = (EditText) findViewById(R.id.account_password);

		final InputFilter[] orgFilters = accountJID.getFilters();
		final InputFilter[] newFilters = new InputFilter[orgFilters.length + 1];
		int i;
		for (i = 0; i < orgFilters.length; i++) {
			newFilters[i] = orgFilters[i];
		}
		newFilters[i] = new LoginFilter.UsernameFilterGeneric();
		accountJID.setFilters(newFilters);
		accountJID.addTextChangedListener(jidTextWatcher);
		accountPassword.addTextChangedListener(passTextWatcher);
	}
}
