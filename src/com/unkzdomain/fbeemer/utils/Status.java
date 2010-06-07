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
import org.jivesoftware.smack.packet.Presence.Mode;

// TODO: Auto-generated Javadoc
/**
 * The Class Status.
 */
public final class Status {

	/** The Constant DISCONNECT. */
	public static final int	DISCONNECT			= 100;

	/** The Constant UNAVAILABLE. */
	public static final int	UNAVAILABLE			= 200;

	/** The Constant AWAY. */
	public static final int	AWAY				= 300;

	/** The Constant BUSY. */
	public static final int	BUSY				= 400;

	/** The Constant AVAILABLE. */
	public static final int	AVAILABLE			= 500;

	/** The Constant AVAILABLE_FOR_CHAT. */
	public static final int	AVAILABLE_FOR_CHAT	= 600;

	/**
	 * Gets the presence mode from status.
	 * 
	 * @param status
	 *            the status
	 * @return the presence mode from status
	 */
	public static Presence.Mode getPresenceModeFromStatus(final int status) {
		Presence.Mode res;
		switch (status) {
			case AVAILABLE:
				res = Presence.Mode.available;
				break;
			case AVAILABLE_FOR_CHAT:
				res = Presence.Mode.chat;
				break;
			case AWAY:
				res = Presence.Mode.away;
				break;
			case BUSY:
				res = Presence.Mode.dnd;
				break;
			case UNAVAILABLE:
				res = Presence.Mode.xa;
				break;
			default:
				res = null;
		}
		return res;
	}

	/**
	 * Gets the status from presence.
	 * 
	 * @param presence
	 *            the presence
	 * @return the status from presence
	 */
	public static int getStatusFromPresence(final Presence presence) {
		int res = Status.DISCONNECT;
		if (presence.getType().equals(Presence.Type.unavailable)) {
			res = Status.DISCONNECT;
		} else {
			final Mode mode = presence.getMode();
			if (mode == null) {
				res = Status.AVAILABLE;
			} else {
				switch (mode) {
					case available:
						res = Status.AVAILABLE;
						break;
					case away:
						res = Status.AWAY;
						break;
					case chat:
						res = Status.AVAILABLE_FOR_CHAT;
						break;
					case dnd:
						res = Status.BUSY;
						break;
					case xa:
						res = Status.UNAVAILABLE;
						break;
					default:
						res = Status.DISCONNECT;
						break;
				}
			}
		}
		return res;
	}

	/**
	 * Status online.
	 * 
	 * @param status
	 *            the status
	 * @return true, if successful
	 */
	public static boolean statusOnline(final int status) {
		return status != Status.DISCONNECT;
	}

	/**
	 * Instantiates a new status.
	 */
	private Status() {
	}

}