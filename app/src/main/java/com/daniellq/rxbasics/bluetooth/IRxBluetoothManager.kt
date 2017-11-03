package com.daniellq.rxbasics.bluetooth

import android.bluetooth.BluetoothDevice
import io.reactivex.Observable

/**
 * Created by dani on 2/11/17.
 */
interface IRxBluetoothManager {
    fun scanDevices(): Observable<BluetoothDevice>
}