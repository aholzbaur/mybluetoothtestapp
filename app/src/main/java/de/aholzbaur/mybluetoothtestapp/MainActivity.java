package de.aholzbaur.mybluetoothtestapp;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        this.configButton();
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

    private void showDeviceList() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivity(intent);
    }

    private void configButton() {
        Button openListButton = this.findViewById(R.id.button_open_list);
        openListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceList();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(bluetoothStateReceiver);
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