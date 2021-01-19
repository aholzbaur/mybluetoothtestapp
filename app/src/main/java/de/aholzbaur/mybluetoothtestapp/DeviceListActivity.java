package de.aholzbaur.mybluetoothtestapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class DeviceListActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_device_list);
    }
}
