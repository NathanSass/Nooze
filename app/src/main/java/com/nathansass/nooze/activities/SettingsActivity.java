package com.nathansass.nooze.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.nathansass.nooze.R;
import com.nathansass.nooze.fragments.DatePickerFragment;
import com.nathansass.nooze.models.Settings;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    @BindView(R.id.etBeginDatePicker)
    EditText etBeginDatePicker;
    Settings settings;

    ArrayList<String> newsCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = new Settings();
        newsCategories = new ArrayList<>();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSave)
    public void btnSaveClicked() {

        // Get settings object and then send it
        settings.newsCategories = newsCategories;

        Intent i = new Intent();
        i.putExtra("settings", settings);
        i.putExtra("code", 200);

        setResult(RESULT_OK, i);
        finish();

        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @OnClick({R.id.rbNewest, R.id.rbOldest, R.id.rbNoSort})
    public void onRadioButtonClick(RadioButton radioButton) {
        if (radioButton.getId() == R.id.rbOldest) {
            settings.sortBy = "oldest";
        } else if (radioButton.getId() == R.id.rbNewest) {
            settings.sortBy = "newest";
        } else if (radioButton.getId() == R.id.rbNoSort) {
            settings.sortBy = null;
        }
    }

    @OnClick( {R.id.btnArts, R.id.btnFashion, R.id.btnSports, R.id.btnPolitics, R.id.btnTech, R.id.btnHealth} )
    public void optionButtonClick(Button btn) {

        String btnText = (String) btn.getText();

        if ( newsCategories.contains(btnText)) {

            newsCategories.remove(btnText);
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBackground));
        } else {
            newsCategories.add(btnText);

            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }

    @OnFocusChange(R.id.etBeginDatePicker)
    @OnClick(R.id.etBeginDatePicker)
    public void openBeginDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        LocalDate beginDate = new LocalDate(year, monthOfYear, dayOfMonth);
        settings.beginDate = beginDate;
//        etBeginDatePicker.setText(beginDate.getMonthOfYear() + "/" + beginDate.getDayOfMonth() + "/" + beginDate.getYear());
        etBeginDatePicker.setText(monthOfYear + "/" + dayOfMonth + "/" + year);



    }
}
