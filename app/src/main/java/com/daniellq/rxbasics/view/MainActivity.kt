package com.daniellq.rxbasics.view

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.daniellq.rxbasics.view.adapter.BluetoothDevicesAdapter
import com.daniellq.rxbasics.R
import com.daniellq.rxbasics.presenter.MainPresenter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity(), IMainView, LocationListener {
    @BindView(R.id.btnGetData)
    lateinit var btnGetData: Button
    @BindView(R.id.btnGetEmailFromUser)
    lateinit var btnGetEmailFromUse: Button
    @BindView(R.id.fetchStatus)
    lateinit var fetchStatus: TextView
    @BindView(R.id.btnScanBluetooth)
    lateinit var btnScanBluetooth: Button
    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    private val REQUEST_ENABLE_BT = 0
    private val REQUEST_LOCATION_PERMISSION = 0
    private val REQUEST_LOCATION_STATUS = 1

    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: BluetoothDevicesAdapter
    private var devices = ArrayList<String>()
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrentLocation: Location? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        val mBluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter

        if (!mBluetoothAdapter!!.isEnabled) {
            enableBluetooth()
        } else {
            setupActivity()
        }
    }

    private fun setupLocationApi() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build()

        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 10000
        mLocationRequest?.fastestInterval = 5000
        mLocationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private fun checkLocationStatus() {
        mGoogleApiClient?.connect()
        // The callback for the management of the user settings regarding location
        val mResultCallbackFromSettings = ResultCallback<LocationSettingsResult> { result ->
            val status = result.status
            val locationSettingsStates = result.locationSettingsStates
            if (locationSettingsStates.isLocationUsable || locationSettingsStates.isGpsUsable) {
                if (mCurrentLocation == null) {
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this@MainActivity)
                    } catch (se: SecurityException) {
                        Log.d("MachineFragment", "Exception: " + se)
                    }
                }
                mPresenter.scanBluetooth()
                mGoogleApiClient?.disconnect()
            } else {
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(this@MainActivity, REQUEST_LOCATION_STATUS)
                } catch (e: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
                mGoogleApiClient?.disconnect()
            }
        }

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build())
        result.setResultCallback(mResultCallbackFromSettings)
    }

    override fun onLocationChanged(p0: Location?) {
        mCurrentLocation = p0
    }

    private fun isLocationPermissionEnabled(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationStatus()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOCATION_STATUS && resultCode == RESULT_OK) {
            checkLocationStatus()
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            setupActivity()
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            enableBluetooth()
        }
    }

    private fun enableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    private fun setupActivity() {
        mPresenter = MainPresenter(this, mBluetoothAdapter!!)
        btnGetData.setOnClickListener {
            mPresenter.fetchData()
        }
        btnGetEmailFromUse.setOnClickListener({
            mPresenter.fetchUserFollowers()
        })
        btnScanBluetooth.setOnClickListener({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isLocationPermissionEnabled()) {
                    checkLocationStatus()
                }
            }
        })

        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        mAdapter = BluetoothDevicesAdapter(devices)
        mRecyclerView.adapter = mAdapter
        setupLocationApi()
    }

    /*
    * IMainView methods
    */
    override fun setStatus(status: String) {
        fetchStatus.text = status
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDeviceScanned(deviceAddres: String) {
        devices.add(deviceAddres)
        mAdapter.notifyDataSetChanged()
    }

    override fun clearRecyclerView() {
        devices.clear()
        mAdapter.notifyDataSetChanged()
    }

    override fun changeButtonState(state: String) {
        btnScanBluetooth.text = state
    }

    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBar.visibility = View.INVISIBLE
    }
}