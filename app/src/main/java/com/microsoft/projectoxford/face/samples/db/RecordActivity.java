package com.microsoft.projectoxford.face.samples.db;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.samples.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordActivity extends AppCompatActivity {
    private static String RECORDED_FILE; //요게 녹음파일 경로명

    MediaPlayer player;
    MediaRecorder recorder;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        File dir =new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"/evergreen/audio");

        if(!dir.exists())

            dir.mkdirs();

        long now = System.currentTimeMillis();
        Date date = new Date(now);


        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String getTime = mFormat.format(date);

        file = new File(dir, getTime+".mp3");
        Log.d("chae",file.getPath().toString());
        RECORDED_FILE = file.getAbsolutePath();

        // DB 녹음파일 저장.
        SharedPreferences insert = getSharedPreferences("Picture_info_Pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = insert.edit();
        editor.putString("record_path", file.getAbsolutePath()); //First라는 key값으로 infoFirst 데이터를 저장한다.
        editor.commit(); //완료한다.

        TextView recordBtn = (TextView) findViewById(R.id.recordBtn);
        TextView recordStopBtn = (TextView) findViewById(R.id.recordStopBtn);
        TextView playBtn = (TextView) findViewById(R.id.playBtn);
        TextView playStopBtn = (TextView) findViewById(R.id.playStopBtn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }

                recorder = new MediaRecorder();

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                recorder.setOutputFile(RECORDED_FILE);

                try {
                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();

                    recorder.prepare();
                    recorder.start();
                } catch (Exception ex) {
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder == null)
                    return;

                recorder.stop();
                recorder.release();

                recorder = null;

                Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }

                Toast.makeText(getApplicationContext(), "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();
                try {
                    player = new MediaPlayer();

                    player.setDataSource(RECORDED_FILE);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Log.e("SampleAudioRecorder", "Audio play failed.", e);
                }
            }
        });


        playStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player == null)
                    return;

                Toast.makeText(getApplicationContext(), "재생이 중지되었습니다.", Toast.LENGTH_LONG).show();

                player.stop();
                player.release();
                player = null;
            }
        });

    }

    protected void onPause() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        super.onPause();
    }

    public void recordFinish(View view) {
        finish();
    }
}
