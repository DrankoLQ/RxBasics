package com.daniellq.rxbasics.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.daniellq.rxbasics.R

/**
 * Created by dani on 2/11/17.
 */

class BluetoothDevicesAdapter(val arrayList: ArrayList<String>) : RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_device_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.txtDeviceName)

        fun bindItems(items: String) {
            textView.text = items
        }
    }
}