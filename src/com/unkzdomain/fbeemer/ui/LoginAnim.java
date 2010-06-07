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
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unkzdomain.fbeemer.R;
import com.unkzdomain.fbeemer.service.LoginAsyncTask;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;
import com.unkzdomain.fbeemer.utils.AbstractManagedActivity;

// TODO: Auto-generated Javadoc
/**
 * The Class LoginAnim.
 */
public class LoginAnim extends AbstractManagedActivity {

	/**
	 * The listener interface for receiving click events. The class that is
	 * interested in processing a click event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's <code>addClickListener<code> method. When
	 * the click event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see ClickEvent
	 */
	private class ClickListener implements OnClickListener {

		/**
		 * Instantiates a new click listener.
		 */
		ClickListener() {
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		public void onClick(final View v) {
			if (v == mCancelBt) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		}
	}

	/**
	 * The Class LoginServiceConnection.
	 */
	private class LoginServiceConnection implements ServiceConnection {

		/**
		 * Instantiates a new login service connection.
		 */
		public LoginServiceConnection() {
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		public void onServiceConnected(final ComponentName name,
				final IBinder service) {
			mXmppFacade = IXmppFacade.Stub.asInterface(service);
			if (mTask.getStatus() == AsyncTask.Status.PENDING) {
				mTask = mTask.execute(mXmppFacade);
			}
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		public void onServiceDisconnected(final ComponentName name) {
			mXmppFacade = null;
		}
	}

	/**
	 * The Class LoginTask.
	 */
	private class LoginTask extends LoginAsyncTask {

		/**
		 * Instantiates a new login task.
		 */
		LoginTask() {
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */

		@Override
		protected void onCancelled() {
			super.onCancelled();
			stopService(LoginAnim.SERVICE_INTENT);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */

		@Override
		protected void onPostExecute(final Boolean result) {

			if (result == null || !result) { // Task cancelled or exception
				if (!result) {
					final Intent i = new Intent();
					i.putExtra("message", getErrorMessage());
					LoginAnim.this.setResult(Activity.RESULT_CANCELED, i);
				} else {
					LoginAnim.this.setResult(Activity.RESULT_CANCELED);
				}
				finish();
			} else {
				mCancelBt.setEnabled(false);
				startService(LoginAnim.SERVICE_INTENT);
				LoginAnim.this.setResult(Activity.RESULT_OK);
				finish();
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(final Integer... values) {
			mLoginState.setText(getResources().getStringArray(
					R.array.loganim_state)[values[0]]);
		}

	}

	/** The Constant SERVICE_INTENT. */
	private static final Intent							SERVICE_INTENT	= new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("com.unkzdomain.fbeemer",
				"com.unkzdomain.fbeemer.AppService"));
	}
	
	/** The m logo. */
	private ImageView									mLogo;
	
	/** The m rotate anim. */
	private Animation									mRotateAnim;
	
	/** The m serv conn. */
	private final ServiceConnection						mServConn		= new LoginServiceConnection();
	
	/** The m xmpp facade. */
	private IXmppFacade									mXmppFacade;

	/** The m task. */
	private AsyncTask<IXmppFacade, Integer, Boolean>	mTask;

	/** The m cancel bt. */
	private Button										mCancelBt;

	/** The m login state. */
	private TextView									mLoginState;

	/**
	 * Instantiates a new login anim.
	 */
	public LoginAnim() {
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_anim);
		mLoginState = (TextView) findViewById(R.id.loginanim_status_text);
		mLogo = (ImageView) findViewById(R.id.loginanim_logo_anim);
		mRotateAnim = AnimationUtils.loadAnimation(this,
				R.anim.rotate_and_scale);
		mCancelBt = (Button) findViewById(R.id.loginanim_cancel_button);
		mCancelBt.setOnClickListener(new ClickListener());
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		// TODO use onBackPressed on Eclair (2.0)
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mTask.getStatus() != AsyncTask.Status.FINISHED) {
			setResult(Activity.RESULT_CANCELED);
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */

	@Override
	public void onPause() {
		super.onPause();
		if (mXmppFacade != null) { // and async task not en cours
			unbindService(mServConn);
			mXmppFacade = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */

	@Override
	public void onResume() {
		super.onResume();
		if (mTask == null) {
			mTask = new LoginTask();
		}
		if (mXmppFacade == null) {
			bindService(LoginAnim.SERVICE_INTENT, mServConn, BIND_AUTO_CREATE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */

	@Override
	public void onStart() {
		super.onStart();
		mLogo.startAnimation(mRotateAnim);
	}
}
