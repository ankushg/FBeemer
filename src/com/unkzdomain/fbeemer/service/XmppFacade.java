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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import android.os.RemoteException;

import com.unkzdomain.fbeemer.service.aidl.IChatManager;
import com.unkzdomain.fbeemer.service.aidl.IRoster;
import com.unkzdomain.fbeemer.service.aidl.IXmppConnection;
import com.unkzdomain.fbeemer.service.aidl.IXmppFacade;
import com.unkzdomain.fbeemer.utils.PresenceType;

// TODO: Auto-generated Javadoc
/**
 * The Class XmppFacade.
 */
public class XmppFacade extends IXmppFacade.Stub {

	/** The m connexion. */
	private final XmppConnectionAdapter	mConnexion;

	/**
	 * Instantiates a new xmpp facade.
	 * 
	 * @param connection
	 *            the connection
	 */
	public XmppFacade(final XmppConnectionAdapter connection) {
		mConnexion = connection;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.unkzdomain.fbeemer.service.aidl.IXmppFacade#call(java.lang.String )
	 */

	public void call(final String jid) throws RemoteException {
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#changeStatus(int, java.lang.String)
	 */

	public void changeStatus(final int status, final String msg) {
		mConnexion.changeStatus(status, msg);
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#connectAsync()
	 */

	public void connectAsync() throws RemoteException {
		mConnexion.connectAsync();
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#connectSync()
	 */

	public void connectSync() throws RemoteException {
		mConnexion.connectSync();
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#createConnection()
	 */

	public IXmppConnection createConnection() throws RemoteException {
		return mConnexion;
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#disconnect()
	 */

	public void disconnect() throws RemoteException {
		mConnexion.disconnect();
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#getChatManager()
	 */

	public IChatManager getChatManager() throws RemoteException {
		return mConnexion.getChatManager();
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#getRoster()
	 */

	public IRoster getRoster() throws RemoteException {
		return mConnexion.getRoster();
	}

	/*
	 * (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#getVcardAvatar(java
	 * .lang .String)
	 */

	public byte[] getVcardAvatar(final String jid) throws RemoteException {
		final VCard vcard = new VCard();

		try {
			vcard.load(mConnexion.getAdaptee(), jid);
			return vcard.getAvatar();
		} catch (final XMPPException e) {
			// TODO Report error
		}
		return new byte[0];
	}

	/* (non-Javadoc)
	 * @see com.unkzdomain.fbeemer.service.aidl.IXmppFacade#sendPresencePacket(com.unkzdomain.fbeemer.service.PresenceAdapter)
	 */
	public void sendPresencePacket(final PresenceAdapter presence)
			throws RemoteException {
		final Presence presence2 = new Presence(PresenceType
				.getPresenceTypeFrom(presence.getType()));
		presence2.setTo(presence.getTo());
		mConnexion.getAdaptee().sendPacket(presence2);
	}
}
