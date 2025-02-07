package com.example.pillreminder.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillreminder.Utils.PreferencesManager;
import com.example.pillreminder.databinding.ActivitySettingBinding;
import com.google.android.material.textfield.TextInputLayout;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private PreferencesManager preferencesManager;
    private TextInputLayout inputLayout;
    private CheckBox sendSms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferencesManager = new PreferencesManager(this);
        initViews();

    }

    private void initViews() {

        binding.backBtn.setOnClickListener(v -> finish());
        binding.guidBtn.setOnClickListener(v -> startActivity(new Intent(this, GuidActivity.class)));
        binding.aboutUsBtn.setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));

        NumberPicker snoozePicker = binding.snoozePicker;
        snoozePicker.setMaxValue(15);
        snoozePicker.setMinValue(1);
        snoozePicker.setValue(preferencesManager.getIntegerValue("snooze"));
        snoozePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            new Handler().postDelayed(() ->
                    preferencesManager.saveIntegerValue("snooze", newVal), 1000);
        });

        EditText phoneNumberEdt = binding.phoneNumberEdt;
        inputLayout = binding.layoutPhoneNumber;
        sendSms = binding.sendSmsCh;
        sendSms.setChecked(preferencesManager.checkExistsKey("sendSms")
                && preferencesManager.getBooleanValue("sendSms"));

        sendSms.setChecked(preferencesManager.checkExistsKey("sendSms") &&
                preferencesManager.getBooleanValue("sendSms"));
        inputLayout.setVisibility(sendSms.isChecked() ? View.VISIBLE : View.GONE);

        sendSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                smsPermission.launch(Manifest.permission.SEND_SMS);
            else {
                preferencesManager.saveBooleanValue("sendSms", isChecked);
                inputLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        phoneNumberEdt.setText(preferencesManager.checkExistsKey("phone") ?
                preferencesManager.getStringValue("phone") : "");
        phoneNumberEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Handler().postDelayed(() ->
                        preferencesManager.saveStringValue("phone", s.toString()), 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    ActivityResultLauncher<String> smsPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission()
            , isGranted -> {
                preferencesManager.saveBooleanValue("sendSms", sendSms.isChecked());
                inputLayout.setVisibility(sendSms.isChecked() ? View.VISIBLE : View.GONE);
            });

}