package com.fr3ts0n.ecu.gui.androbd

interface CommServiceCallback {
    /**
     * Give the new state to the CommServiceCallback so the UI Activity can update
     */
    fun onStateChange(state: CommService.STATE)

    /**
     * Send a failure message back to the Activity
     */
    fun onFail(msgStr: String)

    /**
     * Send the name of the connectionEstablished device back to the UI Activity
     */
    fun onFetchedDeviceName(deviceName: String)
}