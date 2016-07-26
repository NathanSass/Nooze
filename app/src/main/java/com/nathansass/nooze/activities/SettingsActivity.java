package com.nathansass.nooze.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nathansass.nooze.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSave)
    public void btnSaveClicked() {
        Toast.makeText(this, "Save clicked", Toast.LENGTH_LONG).show();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
