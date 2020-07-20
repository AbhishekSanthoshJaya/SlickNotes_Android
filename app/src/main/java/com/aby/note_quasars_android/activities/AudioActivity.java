package com.aby.note_quasars_android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.widget.Button;

import com.aby.note_quasars_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioActivity extends AppCompatActivity {

    @BindView(R.id.recordBtn)
    Button recordBtn;

    boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);



    }

    @OnClick(R.id.recordBtn)
    public void recordStartSop(){
        if(!isRecording){
            recordBtn.setText("Stop");
        }
        else{
            recordBtn.setText("Start");


        }
        isRecording = !isRecording;
    }

}