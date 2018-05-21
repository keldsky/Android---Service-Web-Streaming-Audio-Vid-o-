package com.example.sample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.MusiqueStream.GestionMusiqueStreamPrx;
import com.example.sample.MusiqueStream.GestionMusiqueStreamPrxHelper;
import com.example.sample.model.MP3;
import com.example.sample.parsers.DataParser;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {
private final int SPEECH_RECOGNITION_CODE = 1;
private TextView txtOutput;
private ImageButton btnMicrophone;
private LibVLC libvlc;
private SurfaceHolder holder;

private MediaPlayer mediaPlayer = null;

TextView output;
ProgressBar pb;
List<MyTaskXML> tasks;
List<MyTaskJSON> tasksjson;

List<MP3> MP3List;
List<String> analyserList;

String command;
String song;
    String url = null;
    String text=null;
    Media media;
    GestionMusiqueStreamPrx test = null;
    Ice.ObjectPrx base = null;
    Ice.Communicator ic = null;

    @Override
protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtOutput = (TextView) findViewById(R.id.txt_output);
        tasks = new ArrayList<>();
        tasksjson = new ArrayList<>();
        ArrayList<String> options = new ArrayList<>();
        options.add("-vvv");
        libvlc = new LibVLC(this, options);


        try {
            ic = Ice.Util.initialize();
            base = ic.stringToProxy("SimplePrinter:tcp -h 192.168.42.239 -p 10001");
            test = GestionMusiqueStreamPrxHelper.checkedCast(base);
            if (test == null)
                throw new Error("Invalid Proxy");


        } catch (Exception e) {
            System.err.println(e.getMessage());

        }

        btnMicrophone = (ImageButton) findViewById(R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

}
/**
* Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
* */
private void startSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
        "Speak something...");

        try {

        startActivityForResult(intent, SPEECH_RECOGNITION_CODE);

        }
        catch (ActivityNotFoundException a) {

        Toast.makeText(getApplicationContext(),
        "Sorry! Speech recognition is not supported in this device.",
        Toast.LENGTH_SHORT).show();

        }
}

/**
 * Callback for speech recognition activity
 * */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {

        case SPEECH_RECOGNITION_CODE: {

            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                text = result.get(0);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();



                if (isOnline()) {

                    if(!text.equals("")){

                        //Récupérer l'ensemble des musiques pré enregistrées pour les comparer à la musique demandée
                        requestData("http://1-dot-projetanalyse200.appspot.com/rest/ar/xml");

                        //Passer la retranscription de la voix à l'analyseur de commande
                        requestAnalyzer("http://1-dot-projetanalyse200.appspot.com/rest/ar/" + text);
                    }

                } else {

                    Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();

                }


            }
            break;
        }


    }
}
    /**
     * RESTFULL Request to know the list of music the user can play
     * */
    private void requestData(String uri) {
        MyTaskXML task = new MyTaskXML();
        task.execute(uri);
    }

    /**
     * RESTFULL Request to know the command and the music user want
     * */
    private void requestAnalyzer(String uri) {
        MyTaskJSON task = new   MyTaskJSON();
        task.execute(uri);
    }

    protected void updateDisplay() {

        if(analyserList!=null)
        {
            Log.d("myApp", analyserList.get(0));
            Log.d("myApp", analyserList.get(1));
            command = analyserList.get(0);
            song =  analyserList.get(1);

        }

    }
    /**
     * Test if device android is connected to internet
     * */
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTaskXML extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            tasks.add(this);
        }
        /** will be executed in background **/
        @Override
        protected String doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }
        /**This method is called after doInBackground method completes processing,
         * Result from doInBackground is passed to this method
         */
        @Override
        protected void onPostExecute(String result) {

            MP3List = DataParser.parseFeedXml(result);

            tasks.remove(this);

        }

    }

    private class MyTaskJSON extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }
        /** will be executed in background **/
        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        /**This method is called after doInBackground method completes processing,
         * Result from doInBackground is passed to this method
          */
        @Override
        protected void onPostExecute(String result) {

            //get array of command and music
            analyserList = DataParser.parseFeedJson(result);

            updateDisplay();
            if (isOnline()) {

                int find = 0;

                if (MP3List != null) {

                    for (MP3 mp3 : MP3List) {

                        if (mp3.getTitre().equalsIgnoreCase(song) == true) {

                            find = 1;
                            break;

                        }

                    }
                }

                if  ((find == 0)&&(command != null) && (command.equals("play")))  {

                    Toast.makeText(MainActivity.this,"Aucune musique à ce nom disponible !", Toast.LENGTH_LONG).show();

                }

                if ((command != null) && (command.equals("pause"))) {
                   if(mediaPlayer!=null && mediaPlayer.isPlaying())
                        mediaPlayer.pause();

                }
                if ((command != null) && (command.equals("stop"))) {
                    if(mediaPlayer!=null && mediaPlayer.isPlaying())
                        mediaPlayer.stop();

                }

                if ((command != null) && (command.equals("start"))&&(mediaPlayer != null)) {

                    mediaPlayer.stop();

                    mediaPlayer.release();

                    mediaPlayer = new MediaPlayer(libvlc);

                    media = new Media(libvlc, Uri.parse(url));

                    mediaPlayer.setMedia(media);
                    mediaPlayer.play();

                }


                if ((command != null) && (command.equals("play"))) {


                    url = test.Stream(command, song);

                    if(mediaPlayer!=null) {
                    mediaPlayer.release();
                    }


                    mediaPlayer = new MediaPlayer(libvlc);

                    media = new Media(libvlc, Uri.parse(url));

                    mediaPlayer.setMedia(media);

                    mediaPlayer.play();


                } else if (command != null && command.equalsIgnoreCase("delete")) {


                        test.Delete(song);

                        Toast.makeText(MainActivity.this, "Musique supprimé", Toast.LENGTH_LONG).show();

                }
                else {
//Code
                }

            }

        }
    }

}