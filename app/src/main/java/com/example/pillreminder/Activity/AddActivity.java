package com.example.pillreminder.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pillreminder.R;
import com.example.pillreminder.databinding.ActivityAddBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AddActivity extends AppCompatActivity {

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private final String[] audioTypes = {
            "پیشفرض",
            "آهنگ",
            "ضبط صدا"
    };
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private ActivityAddBinding binding;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean startRecording = true;
    private boolean limitedDay = false;
    private int days = 1;
    private SeekBar seekBar;
    private ImageView playBtn;
    private TextView limitDayCounterTxt;
    private AutoCompleteTextView audioTypeCtv;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

    }

    private void initViews() {

        binding.backBtn.setOnClickListener(v -> finish());

        TextView startTimeTxt, startDateTxt, reminderTimeTxt;
        TextInputEditText medicineNameEdt, descriptionEdt, dayCountEdt;
        MaterialButton recorderBtn;
        LinearLayout playerView, recorderView, limitView;
        RadioButton continuousRd, limitedRd;
        SeekBar limitedDaySk;

        startTimeTxt = binding.startTimeReminderTxt;
        startDateTxt = binding.startDateReminderTxt;
        reminderTimeTxt = binding.reminderTimeTxt;
        recorderBtn = binding.recorderBtn;
        playBtn = binding.playPauseBtn;
        playerView = binding.playerView;
        recorderView = binding.recorderView;
        seekBar = binding.seekBar;
        audioTypeCtv = binding.audioTypeCtv;
        limitDayCounterTxt = binding.limitDayCounterTxt;
        continuousRd = binding.continuousRd;
        limitedRd = binding.limitedRd;
        limitedDaySk = binding.limitDaySk;
        limitView = binding.limitView;

        startDateTxt.setText(Objects.requireNonNull(getIntent().getExtras()).getString("date"));

        Calendar calendar = Calendar.getInstance();

        reminderTimeTxt.setText(clockFormat(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        ));
        reminderTimeTxt.setOnClickListener(v -> timePicker(reminderTimeTxt));

        startTimeTxt.setOnClickListener(v -> timePicker(startTimeTxt));

        fileName = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        recorderBtn.setOnClickListener(v -> {
            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                onRecord(startRecording);
                if (startRecording) {
                    recorderBtn.setText("توقف ضبط");
                } else {
                    recorderBtn.setText("ضبط صدا");
                    playerView.setVisibility(View.VISIBLE);
                    setupMediaPlayer();
                }
                startRecording = !startRecording;
            }
        });

        playBtn.setOnClickListener(v -> {
            if (player.isPlaying()) {
                playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
                player.pause();
            } else {
                playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
                player.start();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    player.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                audioTypes
        );
        audioTypeCtv.setAdapter(arrayAdapter);
        audioTypeCtv.setText(audioTypes[0], false);
        audioTypeCtv.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    recorderView.setVisibility(View.GONE);
                    audioTypeCtv.setText(audioTypes[0], false);
                    if (player != null && player.isPlaying()) {
                        player.pause();
                        playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
                    }
                    break;
                case 1:
                    audioLauncher.launch("audio/*");
                    break;
                case 2:
                    recorderView.setVisibility(View.VISIBLE);
                    break;
            }
        });

        limitedDaySk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    days = progress;
                    limitDayCounterTxt.setText(progress + " " + getString(R.string.day));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        continuousRd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (limitedDay) {
                limitedDay = false;
                limitView.setVisibility(View.VISIBLE);
            }
        });

        limitedRd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!limitedDay) {
                limitedDay = true;
                limitView.setVisibility(View.GONE);
            }
        });

    }

    private void setupMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            seekBar.setMax(player.getDuration());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (player != null && player.isPlaying()) {
                        seekBar.setProgress(player.getCurrentPosition());
                    }
                }
            }, 0, 1000);

            player.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                player.seekTo(0);
                playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
            });

        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }


    private String clockFormat(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    private void timePicker(TextView view) {

        Calendar calendar = Calendar.getInstance();
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText(getString(R.string.time_picker_title))
                .setPositiveButtonText(getString(R.string.accept))
                .setNegativeButtonText(getString(R.string.cancel))
                .build();

        timePicker.addOnPositiveButtonClickListener(v1 ->
                view.setText(clockFormat(timePicker.getHour(), timePicker.getMinute()))
        );

        timePicker.show(getSupportFragmentManager(), "timePicker");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            onRecord(true);
        }

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    ActivityResultLauncher<String> audioLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range")
                        String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        audioTypeCtv.setText(displayName, false);
                        Log.i("TAG", "Audio file name: " + displayName);

//                        MediaPlayer player = new MediaPlayer();
//                        player.setAudioAttributes(new AudioAttributes.Builder()
//                                .setUsage(AudioAttributes.USAGE_MEDIA)
//                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                .build());
//
//                        try {
//                            player.setOnPreparedListener(mp -> {
//                                Log.i("TAG", "Audio file name: " + displayName);
//                                mp.start();
//                            });
//
//                            player.setOnErrorListener((mp, what, extra) -> {
//                                Log.e("TAG", "Error occurred while playing audio");
//                                return false;
//                            });
//
//                            player.setDataSource(getApplicationContext(), uri);
//                            player.prepareAsync();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                    }
                }
            }
    );

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
}