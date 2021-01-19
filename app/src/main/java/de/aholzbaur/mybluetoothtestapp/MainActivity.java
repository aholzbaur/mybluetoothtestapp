package de.aholzbaur.mybluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

import de.aholzbaur.mybluetoothtestapp.dialogs.CloseAppOnErrorDialog;

public class MainActivity extends AppCompatActivity {
    private static final int INTENT_ACTION_ENABLE_BLUETOOTH = 1;
    BluetoothAdapter bluetoothAdapter = null;
    private BroadcastReceiver bluetoothStateReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.checkBluetoothOnStart();

        this.configBluetoothStateReceiver();

        this.fillList();
    }

    private void fillList() {
        ArrayAdapter<String> devicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_list_item);
        ListView devicesListView = (ListView) this.findViewById(R.id.devices_list_view);
        devicesListView.setAdapter(devicesArrayAdapter);

        Set<BluetoothDevice> devicesList = this.bluetoothAdapter.getBondedDevices();

        if (devicesList.size() > 0) {
            for (BluetoothDevice d : devicesList) {
                devicesArrayAdapter.add(d.getName().toString());
            }
        } else {
            devicesArrayAdapter.add("No devices bonded");
        }
    }

    private void checkBluetoothOnStart() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            CloseAppOnErrorDialog d = new CloseAppOnErrorDialog(getResources().getString(R.string.dialog_close_app_bt_not_supported), this);
            d.show(getSupportFragmentManager(), null);
        } else {
            this.bluetoothAdapter = bluetoothAdapter;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // show dialog to user to turn on BT
            this.startActivityForResult(enableBluetoothIntent, INTENT_ACTION_ENABLE_BLUETOOTH);
        } else {
            // immediately check BT status and change text
            this.checkBluetoothStatus();
        }
    }

    public void checkBluetoothStatus() {
        TextView textViewBluetoothStatusValue = (TextView) this.findViewById(R.id.textViewBluetoothStatusValue);
        if (this.bluetoothAdapter.isEnabled()) {
            textViewBluetoothStatusValue.setText(this.getResources().getString(R.string.text_bt_status_on));
        } else {
            textViewBluetoothStatusValue.setText(this.getResources().getString(R.string.text_bt_status_off));
        }
    }

    private void configBluetoothStateReceiver() {
        this.bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    checkBluetoothStatus();
                }
            }
        };

        this.registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case INTENT_ACTION_ENABLE_BLUETOOTH: {
                // check BT status and change text after user enabled (or not) BT
                this.checkBluetoothStatus();
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}