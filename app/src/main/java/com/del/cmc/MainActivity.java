package com.del.cmc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    List<String> pairedDeviceList;
    ListView lvDevices;
    ArrayAdapter<String> pairedDeviceAdapter;
    public TextView textInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvDevices = (ListView) findViewById(R.id.list);
        textInfo = (TextView) findViewById(R.id.textInfo);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "BLUETOOTH не поддерживается", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "BLUETOOTH отключен", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String stInfo = bluetoothAdapter.getName();
        textInfo.setText(String.format("Это устройство: %s", stInfo));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        setup();
    }

    private void setup() {
        Set<BluetoothDevice> dev = bluetoothAdapter.getBondedDevices();
        if (!dev.isEmpty()) {
            pairedDeviceList = new ArrayList<>();
            for (BluetoothDevice bd : dev) {
                pairedDeviceList.add(bd.getName() + "\n" + bd.getAddress());
            }
            pairedDeviceAdapter = new ArrayAdapter<>(this, simple_list_item_1, pairedDeviceList);
            lvDevices.setAdapter(pairedDeviceAdapter);
            lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemValue = (String) lvDevices.getItemAtPosition(position);
                    String MAC = itemValue.substring(itemValue.length() - 17);
                    BluetoothDevice dev2 = bluetoothAdapter.getRemoteDevice(MAC);
                    Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                    intent.putExtra(ControlActivity.BLUETOOTH_DEVICE, dev2);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            }
        } else {
            Toast.makeText(this, "BLUETOOTH не включен", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}