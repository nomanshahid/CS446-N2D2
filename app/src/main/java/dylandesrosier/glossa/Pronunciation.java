package dylandesrosier.glossa;

import android.Manifest;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONObject;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import omrecorder.AudioChunk;
import omrecorder.AudioRecordConfig;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.PullableSource;
import omrecorder.Recorder;
import omrecorder.WriteAction;

public class Pronunciation extends AppCompatActivity {
    private Integer score;
    private String letter;
    private String pronunciation;

    // Recording Audio

    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToStoreAccepted = false;
    private boolean permissionToReadAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private ImageButton playButton = null;
    private MediaPlayer   player = null;

    private omrecorder.Recorder wavRecorder = null;

    // Request
//    private String url = "https://phoneme-recognition.herokuapp.com/getScore";
    private String url = "https://ae53e8bd.ngrok.io/getScore";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronunciation);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION );
        }

        letter = getIntent().getStringExtra("letter");
        pronunciation = getIntent().getStringExtra("pronunciation");

        TextView character = findViewById(R.id.character_text);
        character.setText(letter);
        TextView pronun = findViewById(R.id.pronunciation);
        pronun.setText(pronunciation);

//        fileName = getExternalCacheDir().getAbsolutePath();
//        fileName += "/recording.wav";

        fileName = "recording.wav";

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        onRecord(true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        onRecord(false);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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

        wavRecorder = OmRecorder.wav(
                new PullTransport.Noise(mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        },
                        new WriteAction.Default(),
                        new Recorder.OnSilenceListener() {
                            @Override public void onSilence(long silenceTime) {
                                Log.e("silenceTime", String.valueOf(silenceTime));
                            }
                        }, 200
                ), file()
        );
    }

    @NonNull private File file() {
        return new File(Environment.getExternalStorageDirectory(), fileName);
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private PullableSource mic() {
        return new PullableSource.NoiseSuppressor(
                new PullableSource.Default(
                        new AudioRecordConfig.Default(
                                MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                                AudioFormat.CHANNEL_IN_MONO, 44100
                        )
                )
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToStoreAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                permissionToReadAccepted  = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                break;

        }
        if (!permissionToRecordAccepted || !permissionToStoreAccepted || !permissionToReadAccepted) finish();
    }

    private void onRecord(boolean start) throws IOException {
        if (start) {
//            startRecording();
            wavRecorder.startRecording();
        } else {
//            stopRecording();
            wavRecorder.stopRecording();
            getScore();
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

//    private void startRecording() {
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//
//        recorder.setOutputFile(fileName);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//        try {
//            recorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        TextView r = findViewById(R.id.recordingIcon);
//        r.setVisibility(View.VISIBLE);
//
//        recorder.start();
//    }
//
//    private void stopRecording() {
//        TextView r = findViewById(R.id.recordingIcon);
//        r.setVisibility(View.INVISIBLE);
//
//        recorder.stop();
//        recorder.release();
//        recorder = null;
//
//        getScore();
//    }

    private void getScore() {
        // Build request

        File myFile = new File(Environment.getExternalStorageDirectory(), fileName);
        RequestParams params = new RequestParams();

        params.put("pronunciation", pronunciation);
        params.put("letter", letter);
        try {
            params.put("audio", myFile);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, this.getExternalFilesDir(null).getAbsolutePath());
        Log.d(LOG_TAG, String.format("audio %b", params.has("audio")));
        Log.d(LOG_TAG, String.format("pronunciation %b", params.has("pronunciation")));
        Log.d(LOG_TAG, String.format("letter %b", params.has("letter")));

        // send request
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
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



//    @Override
//    public void onStop() {
//        super.onStop();
//        if (recorder != null) {
//            recorder.release();
//            recorder = null;
//        }
//
//        if (player != null) {
//            player.release();
//            player = null;
//        }
//    }


}
