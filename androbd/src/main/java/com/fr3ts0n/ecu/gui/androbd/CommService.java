/*
 * (C) Copyright 2015 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 *
 */

package com.fr3ts0n.ecu.gui.androbd;
import android.content.Context;

import com.fr3ts0n.ecu.prot.obd.ElmProt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract communication service
 */
public abstract class CommService
{
	/** communication media */
	public enum MEDIUM
	{
		BLUETOOTH,    ///< Bluetooth device
		USB,          ///< USB device
		NETWORK       ///< Network/WIFI device
	}

	/** media type selection */
	static MEDIUM medium = MEDIUM.BLUETOOTH;

	/** Constants that indicate the current connection state */
	public enum STATE
	{
		NONE,           ///< we're doing nothing
		LISTEN,         ///< listening for incoming connections
		CONNECTING,     ///< initiating an outgoing connection
		CONNECTED,      ///< connected to a remote device
		OFFLINE         ///< we are offline
	}

	// Debugging
	private static final String TAG = "CommService";

	static final Logger log = Logger.getLogger(TAG);

	public static final ElmProt elm = new ElmProt();

	Context mContext;
	private CommServiceCallback callback = null;
	STATE mState;

	/**
	 * Constructor. Prepares a new Communication session.
	 */
	CommService()
	{
		super();
		// mAdapter = MainActivity.mBluetoothAdapter;
		mState = STATE.NONE;
	}

	/**
	 * Constructor. Prepares a new Communication session.
	 *
	 * @param callback A CommServiceCallback to send messages back to the UI Activity
	 */
	private CommService(CommServiceCallback callback)
	{
		this();
		this.callback = callback;
	}

	/**
	 * Constructor. Prepares a new Bluetooth Communication session.
	 *
	 * @param context The UI Activity Context
	 * @param callback A CommServiceCallback to send messages back to the UI Activity
	 */
	CommService(Context context, CommServiceCallback callback)
	{
		this(callback);
		mContext = context;
	}

	/**
	 * Set the current state of the chat connection
	 *
	 * @param state An integer defining the current connection state
	 */
	synchronized void setState(STATE state)
	{
		log.log(Level.FINE, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the CommServiceCallback so the UI Activity can update
		callback.onStateChange(state);
	}
	
	/**
	 * Start the chat service. Specifically start AcceptThread to begin a session
	 * in listening (server) mode. Called by the Activity onResume()
	 */
	protected abstract void start();

	/**
	 * Stop all threads
	 */
	public abstract void stop();

	/**
	 * Write to the output device in an un-synchronized manner
	 *
	 * @param out The bytes to write
	 */
	public abstract void write(byte[] out);

	/**
	 * start connection to specified device
	 *
	 * @param device The device to connect
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	public abstract void connect(Object device, boolean secure);

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	void connectionFailed()
	{
		stop();
		// set new state offline
		setState(STATE.OFFLINE);
		// Send a failure message back to the Activity
		callback.onFail(mContext.getString(R.string.unabletoconnect));
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	void connectionLost()
	{
		stop();
		// set new state offline
		setState(STATE.OFFLINE);
		// Send a failure message back to the Activity
		callback.onFail(mContext.getString(R.string.connectionlost));
	}

	/**
	 * Indicate that the connection was established and notify the UI Activity.
	 */
	void connectionEstablished(String deviceName)
	{
		// Send the name of the connectionEstablished device back to the UI Activity
		callback.onFetchedDeviceName(deviceName);

		// set state to connected
		setState(STATE.CONNECTED);
	}
}
