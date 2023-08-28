package ru.mirea.chudnevtsevmr.audiorecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

import ru.mirea.chudnevtsevmr.audiorecorder.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 200;
    private boolean hasPermissions = false;
    private boolean recording = false;
    private boolean playing = false;

    private ActivityMainBinding binding;
    private String recordPath;
    private MediaRecorder recorder;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();

        recordPath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "/record.3gp").getAbsolutePath();

        binding.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    binding.record.setText("Start record\n25, БСБО-06-21");
                    binding.play.setEnabled(true);
                    stopRecording();
                } else {
                    binding.record.setText("Stop record\n25, БСБО-06-21");
                    binding.play.setEnabled(false);
                    startRecording();
                }
                recording = !recording;
            }
        });

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    binding.play.setText("Play");
                    binding.record.setEnabled(true);
                    stopPlaying();
                } else {
                    binding.play.setText("Stop");
                    binding.record.setEnabled(false);
                    startPlaying();
                }
                playing = !playing;
            }
        });
    }

    private void checkPermissions() {
        int recorderPermissionStatus = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (recorderPermissionStatus == PackageManager.PERMISSION_GRANTED &&
                storagePermissionStatus == PackageManager.PERMISSION_GRANTED) {
            hasPermissions = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE_PERMISSION);
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordPath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("MainActivity", "prepare() error");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordPath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("MainActivity", "prepare() error");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }
}