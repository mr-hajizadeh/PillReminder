package com.example.pillreminder.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.pillreminder.Database.ReminderViewModel;
import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.R;
import com.example.pillreminder.databinding.ActivityAddBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class AddActivity extends AppCompatActivity {

    private String[] audioTypes;
    private String fileName = null;
    private ActivityAddBinding binding;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean startRecording = true;
    private boolean isEdit = false;
    private SeekBar seekBar;
    private ImageView playBtn;
    private AutoCompleteTextView audioTypeCtv;
    private Timer timer;
    private PersianDate startDate;
    private Reminder reminder;
    private ReminderViewModel reminderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        audioTypes = new String[]{
                getString(R.string.default_audio),
                getString(R.string.sound_audio),
                getString(R.string.record_voice),
        };

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        reminderViewModel.getReminderById(
                        Objects.requireNonNull(getIntent().getExtras()).getInt("id"))
                .observe(this, reminder -> {
                    if (reminder != null) {
                        this.reminder = reminder;
                        isEdit = true;
                    } else
                        this.reminder = new Reminder();

                    initViews();
                });
        fileName = Objects.requireNonNull(getExternalCacheDir()).getAbsolutePath();
        fileName += "/audioRecord.3gp";

    }

    private void initViews() {

        binding.backBtn.setOnClickListener(v -> finish());

        TextView startTimeTxt, startDateTxt, reminderTimeTxt, limitDayCounterTxt;
        TextInputEditText medicineNameEdt, descriptionEdt;
        MaterialButton recorderBtn;
        LinearLayout playerView, recorderView, limitView;
        RadioButton continuousRd, limitedRd;
        SeekBar limitedDaySk;

        startTimeTxt = binding.startTimeReminderTxt;
        startDateTxt = binding.startDateReminderTxt;
        reminderTimeTxt = binding.reminderTimeTxt;
        medicineNameEdt = binding.medicineNameEdt;
        descriptionEdt = binding.descriptionEdt;
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

        medicineNameEdt.setText(reminder.getTitle() != null ? reminder.getTitle() : "");
        descriptionEdt.setText(reminder.getDescription() != null ? reminder.getDescription() : "");
        medicineNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        descriptionEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        long date;
        if (reminder.getStartMilliSecond() > 0)
            date = reminder.getStartMilliSecond();
        else
            date = Objects.requireNonNull(getIntent().getExtras()).getLong("date");
        startDate = new PersianDate(new Date(date));
        startDateTxt.setText(PersianDateFormat.format(startDate, "Y/m/d"));

        startTimeTxt.setText(clockFormat(reminder.getStartReminderHour()));
        startTimeTxt.setOnClickListener(v -> timePicker(startTimeTxt));
        startTimeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setStartReminderHour(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reminderTimeTxt.setText(clockFormat(reminder.getReminderHour()));
        reminderTimeTxt.setOnClickListener(v -> timePicker(reminderTimeTxt));
        reminderTimeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reminder.setReminderHour(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recorderBtn.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                audioPermission.launch(Manifest.permission.RECORD_AUDIO);
            } else {
                onRecord(startRecording);
                if (startRecording) {
                    recorderBtn.setText(getString(R.string.stop_recording));
                } else {
                    recorderBtn.setText(getString(R.string.record_voice));
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
        audioTypeCtv.setText(audioTypes[(reminder.getAudioType() != 0 ? reminder.getAudioType() : 0)], false);
        audioTypeCtv.setOnItemClickListener((parent, view, position, id) -> {
            reminder.setAudioType(position);
            switch (position) {
                case 0:
                    recorderView.setVisibility(View.GONE);
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

        reminder.setDays(reminder.getDays() != 0 ? reminder.getDays() : 90);
        limitedDaySk.setProgress(reminder.getDays());
        limitedDaySk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    reminder.setDays(progress);
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
            if (reminder.isLimitedDay()) {
                reminder.setLimitedDay(false);
                reminder.setDays(reminder.getDays() < 0 ? 1 : reminder.getDays());
                limitView.setVisibility(View.VISIBLE);
            }
        });

        limitedRd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!reminder.isLimitedDay()) {
                reminder.setLimitedDay(true);
                reminder.setDays(reminder.getDays() < 0 ? 90 : reminder.getDays());
                limitView.setVisibility(View.VISIBLE);
            }
        });

        binding.submitBtn.setOnClickListener(v -> {

            if (reminder.getTitle() != null && !reminder.getTitle().isEmpty()) {
                if (reminder.getDescription() != null && !reminder.getDescription().isEmpty()) {

                    String[] startTime = reminder.getStartReminderHour().split(":");

                    Calendar startTimeCalendar = Calendar.getInstance();
                    startTimeCalendar.set(Calendar.YEAR, startDate.getGrgYear());
                    startTimeCalendar.set(Calendar.MONTH, startDate.getGrgMonth() - 1);
                    startTimeCalendar.set(Calendar.DAY_OF_MONTH, startDate.getGrgDay());
                    startTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                    startTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(startTime[1]));
                    startTimeCalendar.set(Calendar.SECOND, 0);

                    Calendar endTimeCalendar = Calendar.getInstance();
                    endTimeCalendar.setTimeInMillis(
                            startTimeCalendar.getTimeInMillis() + (reminder.getDays() * 86400L) * 1000);

                    reminder.setStartMilliSecond(startTimeCalendar.getTimeInMillis());
                    reminder.setEndMilliSecond(endTimeCalendar.getTimeInMillis());

                    String[] splitRepeatTime = reminder.getReminderHour().split(":");
                    long hour = (Integer.parseInt(splitRepeatTime[0]) * 60 * 60 * 1000L);
                    long minute = (Integer.parseInt(splitRepeatTime[1]) * 60 * 1000L);

                    int hourCount = (24 * reminder.getDays()) / Integer.parseInt(splitRepeatTime[0]);
                    List<Long> hours = new ArrayList<>();
                    for (int i = 0; i <= hourCount; i++) {
                        long eachTimeMillis = startTimeCalendar.getTimeInMillis() + ((i * (hour + minute)));
                        if (eachTimeMillis <= reminder.getEndMilliSecond())
                            hours.add(eachTimeMillis);
                    }
                    reminder.setHours(hours);

                    if (isEdit)
                        reminderViewModel.updateReminder(reminder);
                    else
                        reminderViewModel.insertReminder(reminder);

                    finish();
//            AlarmHelper alarmHelper = new AlarmHelper(this);
//            alarmHelper.setRepeatingAlarm(reminder);

                } else
                    Toast.makeText(this, "empty description", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "empty title", Toast.LENGTH_SHORT).show();

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


    private String clockFormat(String time) {

        int hour, minute;
        if (time != null && !time.isEmpty()) {
            String[] timeSpilt = time.split(":");
            hour = Integer.parseInt(timeSpilt[0]);
            minute = Integer.parseInt(timeSpilt[1]);
        } else {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

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
                view.setText(clockFormat(timePicker.getHour() + ":" + timePicker.getMinute()))
        );

        timePicker.show(getSupportFragmentManager(), "timePicker");

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

    ActivityResultLauncher<String> audioPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> onRecord(true)
    );

    ActivityResultLauncher<String> audioLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
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
                } else
                    audioTypeCtv.setText(audioTypes[0], false);
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