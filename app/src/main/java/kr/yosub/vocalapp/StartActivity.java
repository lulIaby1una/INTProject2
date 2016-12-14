package kr.yosub.vocalapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartActivity extends Activity{
    // Media
    static final String RECORDED_FILE = "/data/data/kr.yosub.vocalapp/recorded.mp4";
    static final String RECORDED_FILE2 = "/data/data/kr.yosub.vocalapp/recorded2.mp3";

    MediaPlayer player;
    MediaRecorder recorder;

    int playbackPosition = 0;

    //File Download
    // declare the dialog as a member field of your activity
    android.app.ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Download Files
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(StartActivity.this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);


        //Media
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button recordBtn = (Button) findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
        Button playBtn = (Button) findViewById(R.id.playBtn);
        Button playStopBtn = (Button) findViewById(R.id.playStopBtn);
       // Button testPlay = (Button) findViewById(R.id.testPlay);
        Button btDownload = (Button) findViewById(R.id.btDownload);
       // Button btMusic = (Button) findViewById(R.id.btMusic);

      /*  testPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder != null){
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }// TODO Auto-generated method stub
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(RECORDED_FILE2);
                try{
                    Toast.makeText(getApplicationContext(),
                            "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                    recorder.prepare();
                    recorder.start();

                }catch (Exception ex){
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });

        */
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder != null){
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }// TODO Auto-generated method stub

                //Media
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setOutputFile(RECORDED_FILE);
                try{
                    Toast.makeText(getApplicationContext(),
                            "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                    playAudio(RECORDED_FILE2);
                    recorder.prepare();
                    recorder.start();

                }catch (Exception ex){
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder == null)
                    return;

                recorder.stop();
                recorder.release();
                recorder = null;

                Toast.makeText(getApplicationContext(),
                        "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
                // TODO Auto-generated method stub

            }
        });




        playBtn.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                try{
                    playAudio(RECORDED_FILE);

                    Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨.", 2000).show();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        playStopBtn.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                if(player != null){
                    playbackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.",2000).show();
                }
            }
        });

        btDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // execute this when the downloader must be fired
                final DownloadFilesTask downloadTask = new DownloadFilesTask(StartActivity.this);
                downloadTask.execute("http://cfile1.uf.tistory.com/media/234A0B33583C114C13C6CE");

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                }
           });
            }
        });

        /*
        btMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    playAudio(RECORDED_FILE2);

                    Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨.", 2000).show();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        */






    }

    private void playAudio(String url) throws Exception{
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if(player != null){
            try {
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    protected void onPause(){
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        if (player != null){
            player.release();
            player = null;
        }

        super.onPause();

    }

    //Downloads Class
    public class DownloadFilesTask extends AsyncTask<String, Integer, String> {



        private android.content.Context context;
        private android.os.PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/data/data/kr.yosub.vocalapp/recorded2.mp3");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (java.io.IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}


