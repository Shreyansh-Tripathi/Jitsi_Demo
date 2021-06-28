package com.shreyansh.jitsidemo;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity{

    SpeechConfig speechConfig;
    Timer  timer;
    Thread thread;
    JitsiMeetActivity jitsiMeetActivity;

    JitsiMeetOngoingConferenceService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jitsiMeetActivity=new JitsiMeetActivity();

        service=new JitsiMeetOngoingConferenceService();

        service.
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setWelcomePageEnabled(true)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

         speechConfig = SpeechConfig.fromSubscription(
                "cbd0c6c5fb63406db3588b382a5d3728", "centralindia");



//        Looper looper=Looper.myLooper();
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//            //Background work here
//            int i=0;
//            while(i<11){
//                new Handler(looper).postDelayed(() -> {
//                    try {
//                        fromMic(speechConfig);
//                    }
//                    catch (InterruptedException | ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                },1000);
//                ++i;
//            }
//
//            handler.post(() -> {
//                Log.i("RECOGNIZED: Text=","Work");
//                Button create=findViewById(R.id.createbtn);
//                create.setOnClickListener(v -> createRoom());
//                //UI Thread work here
//            });
//        });
//
    }

    public void fromMic(SpeechConfig speechConfig) throws InterruptedException, ExecutionException {
        AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

        Future<SpeechRecognitionResult> task = recognizer.recognizeOnceAsync();
        SpeechRecognitionResult result = task.get();
        Log.i("RECOGNIZED: Text=" , result.getText());
        if(result.getText().contains("***"))
        {
            Intent intent=new Intent(this,AnyActivity.class);
            startActivity(intent);

            JitsiMeetActivity activity=new JitsiMeetActivity();
            activity.leave();

            timer.cancel();
            thread.interrupt();
//            JitsiMeetView view=new JitsiMeetView(activity);
//            view.leave();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timer!=null)
        timer.cancel();

    }

    public void createRoom(View view) {
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                backgroundTask();
            }
        };

         thread=new Thread(runnable);
        thread.start();

        EditText editText=findViewById(R.id.et_name);

        JitsiMeetUserInfo info=new JitsiMeetUserInfo();
        info.setDisplayName("Shreyansh");
        JitsiMeetConferenceOptions options = null;
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si/"))
                    .setRoom(editText.getText().toString())
                     .setUserInfo(info)
                    .setAudioOnly(true)
                    .setVideoMuted(true)
                    .setFeatureFlag("chat.enabled", false)
                    .setFeatureFlag("toolbox.enabled",true)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

       JitsiMeetActivity.launch(this, options);







//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(JitsiMeetActivity.ACTIVITY_SERVICE);
//        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

//        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                JitsiMeetView view1=new JitsiMeetView(MainActivity.this);
//                view1.leave();
//            }
//        },1200000);
   }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Pause","Pause");
    }

    public  void backgroundTask(){

        timer=new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    fromMic(speechConfig);
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        },10,1000);
   }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        JitsiMeetActivity activity=new JitsiMeetActivity();
//        if (activity.isInPictureInPictureMode()){
//            Log.i("Jitsi123","Leave");
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        JitsiMeetActivity activity=new JitsiMeetActivity();
//        activity.enterPictureInPictureMode();
//        if (activity.isInPictureInPictureMode()){
//            Log.i("Jitsi123","Leave");
//        }
//    }
}
//
//class SpeechAsyncc extends AsyncTask<Void,Void,Void>{
//
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        Looper looper=Looper.myLooper();
//        int i=0;
//        while(i<11){
//            new Handler(looper).postDelayed(() -> {
//                try {
//                    fromMic(speechConfig);
//                }
//                catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            },1000);
//            ++i;
//        }
//        return null;
//    }
//}
