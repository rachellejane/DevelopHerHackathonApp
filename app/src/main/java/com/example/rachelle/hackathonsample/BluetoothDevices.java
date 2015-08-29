package com.example.rachelle.hackathonsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.List;
import java.util.Set;

/**
 * Created by rachelle on 8/29/15.
 */
public class BluetoothDevices {

    public static final int REQUEST_ENABLE_BT = 1;
    public static final String NAME_BLUETOOTH_MODULE="HC-06";
    public static final String UUID_SERIAL_PORT_PROFILE = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothAdapter myBluetoothAdapter;
    public static Set<BluetoothDevice> pairedDevices;
    public static List<BluetoothDevice> pairedDevicesList;
    public static BluetoothDevice fiddleBluetoothDevice;
    public static BluetoothSocket bluetoothSocket;
}
