package dylandesrosier.glossa;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.*;

import org.json.JSONObject;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class Pronunciation extends AppCompatActivity {
    private Integer score;
    private String letter;
    private String pronunciation;

    // Recording Audio
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private ImageButton playButton = null;
    private MediaPlayer player = null;

    // Request
    private String url = "https://phoneme-recognition.herokuapp.com/getScore";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronunciation);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        letter = getIntent().getStringExtra("letter");
        pronunciation = getIntent().getStringExtra("pronunciation");

        TextView character = findViewById(R.id.character_text);
        character.setText(letter);
        TextView pronun = findViewById(R.id.pronunciation);
        pronun.setText(pronunciation);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/recording.mpeg4";

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    onRecord(true);
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    onRecord(false);
                }
                return true;
            }
        });

        playButton = findViewById(R.id.play);
        playButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlay(true);
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    onPlay(false);
                }
                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        recorder.setOutputFile(fileName);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        TextView r = findViewById(R.id.recordingIcon);
        r.setVisibility(View.VISIBLE);

        recorder.start();
    }

    private void stopRecording() {
        TextView r = findViewById(R.id.recordingIcon);
        r.setVisibility(View.INVISIBLE);

        recorder.stop();
        recorder.release();
        recorder = null;

        getScore();
    }

    private void getScore() {
        // Build request
        File myFile = new File(fileName);
        RequestParams params = new RequestParams();
        try {
            params.put("audio", myFile);
            params.put("pronunciation", pronunciation);
            params.put("letter", letter);
        } catch (FileNotFoundException e) {
        }

        // send request
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.d(LOG_TAG, "sent");
                try {
                    JSONObject r = new JSONObject(new String(response));
                    Log.d(LOG_TAG, r.getString("score"));
                    TextView score = findViewById(R.id.score);
                    score.setText(String.format("%s/100", r.getString("score")));
                    score.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                TextView score = findViewById(R.id.score);
                score.setText("There was an issue scoring your audio");
                score.setVisibility(View.VISIBLE);
                Log.d(LOG_TAG, "failed");
            }
        });
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
