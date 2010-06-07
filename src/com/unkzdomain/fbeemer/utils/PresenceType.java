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

import org.jivesoftware.smack.packet.Presence;

// TODO: Auto-generated Javadoc
/**
 * The Class PresenceType.
 */
public final class PresenceType {

	/** The Constant AVAILABLE. */
	private static final int	AVAILABLE		= 100;

	/** The Constant UNAVAILABLE. */
	private static final int	UNAVAILABLE		= 200;

	/** The Constant SUBSCRIBE. */

	private static final int	SUBSCRIBE		= 300;

	/** The Constant SUBSCRIBED. */
	private static final int	SUBSCRIBED		= 400;

	/** The Constant UNSUBSCRIBE. */
	private static final int	UNSUBSCRIBE		= 500;

	/** The Constant UNSUBSCRIBED. */
	private static final int	UNSUBSCRIBED	= 600;

	/** The Constant ERROR. */
	private static final int	ERROR			= 701;

	/**
	 * Gets the presence type.
	 * 
	 * @param presence
	 *            the presence
	 * @return the presence type
	 */
	public static int getPresenceType(final Presence presence) {
		int res = PresenceType.ERROR;
		switch (presence.getType()) {
			case available:
				res = PresenceType.AVAILABLE;
				break;
			case unavailable:
				res = PresenceType.UNAVAILABLE;
				break;
			case subscribe:
				res = PresenceType.SUBSCRIBE;
				break;
			case subscribed:
				res = PresenceType.SUBSCRIBED;
				break;
			case unsubscribe:
				res = PresenceType.UNSUBSCRIBE;
				break;
			case unsubscribed:
				res = PresenceType.UNSUBSCRIBED;
				break;
			case error:
			default:
				res = PresenceType.ERROR;
		}
		return res;
	}

	/**
	 * Gets the presence type from.
	 * 
	 * @param type
	 *            the type
	 * @return the presence type from
	 */
	public static Presence.Type getPresenceTypeFrom(final int type) {
		Presence.Type res;
		switch (type) {
			case AVAILABLE:
				res = Presence.Type.available;
				break;
			case UNAVAILABLE:
				res = Presence.Type.unavailable;
				break;
			case SUBSCRIBE:
				res = Presence.Type.subscribe;
				break;
			case SUBSCRIBED:
				res = Presence.Type.subscribed;
				break;
			case UNSUBSCRIBE:
				res = Presence.Type.unsubscribe;
				break;
			case UNSUBSCRIBED:
				res = Presence.Type.unsubscribed;
				break;
			case ERROR:
				res = Presence.Type.error;
				break;
			default:
				res = null;
		}
		return res;
	}

	/**
	 * Instantiates a new presence type.
	 */
	private PresenceType() {
	}
}
