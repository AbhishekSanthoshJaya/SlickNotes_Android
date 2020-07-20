package com.aby.note_quasars_android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.aby.note_quasars_android.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioActivity extends AppCompatActivity {

    @BindView(R.id.recordBtn)
    Button recordBtn;

    @BindView(R.id.playBtn)
    Button playBtn;


    boolean isRecording;
    boolean isPlaying;

    String  fileName;

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private static final String LOG_TAG = "AudioRecordTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);



    }

    @OnClick(R.id.recordBtn)
    public void recordStartSop()  {
        if(!isRecording){
            recordBtn.setText("Stop");

            try {
                startRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            recordBtn.setText("Start");
            stopRecording();


        }
        isRecording = !isRecording;
    }


    @OnClick(R.id.playBtn)
    public void playStartSop()  {
        if(!isPlaying){
            playBtn.setText("Stop");
            startPlaying();

        }
        else{
            playBtn.setText("Start");
            stopPlaying();


        }
        isPlaying = !isPlaying;
    }


    private void startPlaying(){
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            System.out.println(player.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying(){
        player.release();
        player = null;
    }

    private void  stopRecording(){
        recorder.stop();
        recorder.reset();    // set state to idle
        recorder.release();
        recorder = null;
    }

    private String getFileName() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "/AUDIO_" + timeStamp + "_.3gp";

//        return Environment.getExternalStorageDirectory().getAbsolutePath() + audioFileName;
        return getApplicationContext().getFilesDir().getPath() + audioFileName;

        // Save a file: path for use with ACTION_VIEW intents
//        return sound.getAbsolutePath();

    }

    private void startRecording() throws IOException {


        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        String fileName = getFileName();
        this.fileName = fileName;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed" + e);
        }

        recorder.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

}