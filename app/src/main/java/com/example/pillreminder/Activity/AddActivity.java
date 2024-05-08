package com.example.pillreminder.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
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

import com.example.pillreminder.Database.DatabaseListener;
import com.example.pillreminder.Database.ReminderViewModel;
import com.example.pillreminder.Model.Reminder;
import com.example.pillreminder.R;
import com.example.pillreminder.Utils.AlarmHelper;
import com.example.pillreminder.databinding.ActivityAddBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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
                        fileName = reminder.getAudioPath();
                        isEdit = true;
                    } else {
                        this.reminder = new Reminder();
                        File fileDirectory = getFilesDir();
                        fileName = fileDirectory.getAbsolutePath() + "/" + System.currentTimeMillis() + "_audio.3gp";
                    }

                    initViews();
                });

    }

    @SuppressLint("SetTextI18n")
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
        reminder.setStartReminderHour(clockFormat(reminder.getStartReminderHour()));
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
        reminder.setReminderHour(clockFormat(reminder.getReminderHour()));
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

        playerView.setVisibility(reminder.getAudioType() == 2 ? View.VISIBLE : View.GONE);
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
                    Log.i("TAG", "initViews: " + fileName);
                    reminder.setAudioPath(fileName);
                    setupMediaPlayer();
                }
                startRecording = !startRecording;
            }
        });

        playBtn.setOnClickListener(v -> {
            if (player == null)
                setupMediaPlayer();
            else {
                if (player.isPlaying()) {
                    playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
                    player.pause();
                } else {
                    playBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
                    player.start();
                }
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
        recorderView.setVisibility(reminder.getAudioType() == 2 ? View.VISIBLE : View.GONE);
        audioTypeCtv.setOnItemClickListener((parent, view, position, id) -> {
            reminder.setAudioType(position);
            switch (position) {
                case 0:
                    reminder.setAudioPath(null);
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
        if (reminder.isLimitedDay()) {
            limitedRd.setChecked(true);
            limitedDaySk.setProgress(reminder.getDays());
            limitDayCounterTxt.setText(reminder.getDays() + " " + getString(R.string.day));
            limitView.setVisibility(View.VISIBLE);
        }
        limitedDaySk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        continuousRd.setOnClickListener(v -> {
            if (reminder.isLimitedDay()) {
                reminder.setLimitedDay(false);
                reminder.setDays(90);
                limitView.setVisibility(View.GONE);
            }
        });

        limitedRd.setOnClickListener(v -> {
            if (!reminder.isLimitedDay()) {
                reminder.setLimitedDay(true);
                reminder.setDays(1);
                limitView.setVisibility(View.VISIBLE);
            }
        });

        binding.submitBtn.setOnClickListener(v -> {

            String[] splitRepeatTime = reminder.getReminderHour().split(":");
            int repeatHour = Integer.parseInt(splitRepeatTime[0]);
            int repeatMinute = Integer.parseInt(splitRepeatTime[1]);

            String[] startTime = reminder.getStartReminderHour().split(":");
            int startHour = Integer.parseInt(startTime[0]);
            int startMinute = Integer.parseInt(startTime[1]);

            if (reminder.getTitle() != null && !reminder.getTitle().isEmpty()) {
                if (reminder.getDescription() != null && !reminder.getDescription().isEmpty()) {
                    if (repeatHour >= 1) {

                        Calendar startTimeCalendar = Calendar.getInstance();
                        startTimeCalendar.set(Calendar.YEAR, startDate.getGrgYear());
                        startTimeCalendar.set(Calendar.MONTH, startDate.getGrgMonth() - 1);
                        startTimeCalendar.set(Calendar.DAY_OF_MONTH, startDate.getGrgDay());
                        startTimeCalendar.set(Calendar.HOUR_OF_DAY, startHour);
                        startTimeCalendar.set(Calendar.MINUTE, startMinute);
                        startTimeCalendar.set(Calendar.SECOND, 0);

                        Calendar endTimeCalendar = Calendar.getInstance();
                        endTimeCalendar.setTimeInMillis(
                                startTimeCalendar.getTimeInMillis() + (reminder.getDays() * 86400L) * 1000);

                        reminder.setStartMilliSecond(startTimeCalendar.getTimeInMillis());
                        reminder.setEndMilliSecond(endTimeCalendar.getTimeInMillis());

                        long repeatMillis = repeatHour * 60 * 60 * 1000L + repeatMinute * 60 * 1000L;

                        List<Long> hours = new ArrayList<>();
                        for (long currentMillis = startTimeCalendar.getTimeInMillis();
                             currentMillis < endTimeCalendar.getTimeInMillis(); currentMillis += repeatMillis) {
                            hours.add(currentMillis);
                        }
                        reminder.setHours(hours);

                        AlarmHelper alarmHelper = new AlarmHelper(this);

                        if (isEdit) {
                            reminderViewModel.updateReminder(reminder);
                        } else {
                            reminderViewModel.insertReminder(reminder, id -> {
                                long firstUpcomingTime = -1;
                                for (long time : hours) {
                                    if (time > System.currentTimeMillis()) {
                                        firstUpcomingTime = time;
                                        break;
                                    }
                                }
                                reminder.setId((int) id);
                                Log.i("TAG", "initViews: " + reminder.getId());
                                alarmHelper.setAlarm(reminder, firstUpcomingTime);
                                finish();
                            });
                        }

                    } else
                        Toast.makeText(this, "زمان تکرار را نمی تواند کمتر از 1 ساعت باشد!", Toast.LENGTH_SHORT).show();
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
                            Log.i("TAG", "Audio file path: " + getPathFromUri(uri));
                            reminder.setAudioPath(getPathFromUri(uri));
                        }
                    }
                } else {
                    audioTypeCtv.setText(audioTypes[0], false);
                }
            }
    );

    private String getPathFromUri(Uri uri) {
        String filePath = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                File tempFile = createTempFile();
                if (tempFile != null) {
                    copyInputStreamToFile(inputStream, tempFile);
                    filePath = tempFile.getAbsolutePath();
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private File createTempFile() {
        try {
            return File.createTempFile("temp_audio", ".tmp", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

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