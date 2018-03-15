package com.findmyelderly.findmyelderly;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeechActivity extends AppCompatActivity {

    public static final String TAG = "SpeechActivity";

    private static final int RECORD_REQUEST_CODE = 101;

    //@BindView(R.id.status) TextView status;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.textMessage)
    TextView textMessage;
    @BindView(R.id.listview)
    ListView listView;

    private List<String> stringList;
    private SpeechAPI speechAPI;
    private VoiceRecorder mVoiceRecorder;
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }

    };
    private ArrayAdapter<String> adapter;
    private final SpeechAPI.Listener mSpeechServiceListener =
            new SpeechAPI.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (textMessage != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    textMessage.setText(null);
                                    stringList.add(0,text);
                                    adapter.notifyDataSetChanged();
                                    Log.w(TAG, text);
                                    speechStrAnalyze(text);
                                } else {
                                    textMessage.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        ButterKnife.bind(this);
        speechAPI = new SpeechAPI(SpeechActivity.this);
        stringList = new ArrayList<>();
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        stopVoiceRecorder();

        // Stop Cloud Speech API
        try
        {
            speechAPI.addListener(mSpeechServiceListener);
            speechAPI.destroy();
            speechAPI = null;

        }catch (NullPointerException exception)
        {
            Log.w(TAG,exception);
            startActivity(new Intent(SpeechActivity.this, MainActivity.class));
        }
        catch (Exception exception){
            Log.w(TAG,exception);
            startActivity(new Intent(SpeechActivity.this, MainActivity.class));
        }
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else {
            makeRequest(Manifest.permission.RECORD_AUDIO);
        }

        try
        {
            speechAPI.addListener(mSpeechServiceListener);
        }catch (NullPointerException exception)
        {
            Log.w(TAG,exception);
            startActivity(new Intent(SpeechActivity.this, MainActivity.class));
        }
    }

    private int isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                startVoiceRecorder();
            }
        }
    }

    private void speechStrAnalyze(String inputStr){
        if(inputStr.contains("屋企")){
            Log.w(TAG,"have string : 屋企");
            startActivity(new Intent(SpeechActivity.this, MapsActivity.class));
        }else{
            if (inputStr.contains("回家")){
                Log.w(TAG,"have string : 回家");
                startActivity(new Intent(SpeechActivity.this, MapsActivity.class));
            }
        }
    }

}
