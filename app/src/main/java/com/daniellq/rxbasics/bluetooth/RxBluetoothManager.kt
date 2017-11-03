package com.daniellq.rxbasics.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import io.reactivex.Observable
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by dani on 2/11/17.
 */

class RxBluetoothManager(bluetoothAdapter: BluetoothAdapter) : IRxBluetoothManager {
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null
    private var scannedDevices: ArrayList<BluetoothDevice>? = null

    init {
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scannedDevices = ArrayList()
    }

    override fun scanDevices(): Observable<BluetoothDevice> {
        return Observable.create { subscriber ->
            scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    if (!scannedDevices!!.contains(result!!.device)) {
                        scannedDevices!!.add(result.device)
                        subscriber.onNext(result.device)
                    }
                }
            }
            bluetoothLeScanner!!.startScan(scanCallback)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    bluetoothLeScanner!!.stopScan(scanCallback)
                    subscriber.onComplete()
                }
            }, 5000)
        }
    }

}