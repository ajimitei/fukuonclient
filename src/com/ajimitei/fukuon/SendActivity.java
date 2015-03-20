
package com.ajimitei.fukuon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evixar.eaw_utilities.EAWSDK;

import jp.tf_web.fukuon.SendAudioRunnable;
import jp.tf_web.fukuon.network.NetworkAsyncTask;
import jp.tf_web.fukuon.network.NetworkWork;
import jp.tf_web.fukuon.network.model.DeleteUserRequest;
import jp.tf_web.fukuon.network.model.PostUserRequest;
import jp.tf_web.fukuon.network.model.PostUserResponse;
import jp.tf_web.fukuon.network.model.Response;
import jp.tf_web.fukuon.network.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SendActivity extends Activity implements OnClickListener {
    private static final String LOG_TAG = "SendActivity";

    private EAWSDK eaw;
    private boolean finishEawInit;
    private boolean eawIsRunning;

    SharedPreferences pref;
    private static final String PREF_KEY = "fukuonpreferences";
    private static final String KEY_TEXT = "fukuonip";

    private EawResultHandler eawResultHandler = new EawResultHandler();
    private EawErrorHandler eawErrorHandler = new EawErrorHandler();

    TextView title;
    TextView status;
    ImageView user_photo;
    Bitmap user_picture;

    private SoundPool mSoundPool;
    private int mSoundIdA;
    private int mSoundIdB;
    private int mSoundIdC;
    private int mSoundIdD;
    private int mProgramId;

    ImageButton btn_a;
    ImageButton btn_b;
    ImageButton btn_c;
    ImageButton btn_d;

    private SendAudioRunnable sendAudioRunnable;

    @SuppressLint("HandlerLeak")
    private class EawResultHandler extends Handler {

        private void postUserInfo(int i) {

            eawIsRunning = false;
            eaw.stopDetecting();
            Log.v("postUserInfo", String.valueOf(i));

            switch (i) {
                case 1:
                    mProgramId = R.string.sending_message_title_gacchiri;
                    break;
                case 2:
                    mProgramId = R.string.sending_message_title_fushigi;
                    break;
                case 3:
                    mProgramId = R.string.sending_message_title_chubo;
                    break;
                case 4:
                    mProgramId = R.string.sending_message_title_locodol;
                    break;
                case 5:
                    mProgramId = R.string.sending_message_title_nihon;
                    break;
                default:
                    mProgramId = R.string.sending_message_title_gacchiri;
                    break;
            }

            title.setText(mProgramId);
            status.setText(R.string.sending_message_status_on);
            postToServer(i, getString(mProgramId));
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Long)
            {
                long wmV = ((Long) msg.obj).longValue();
                // mEawTextLog.append(wmV + "\n");
                Log.v(LOG_TAG, String.valueOf(wmV));
                if (wmV == 1) {
                    postUserInfo(1);
                } else if (wmV == 2) {
                    postUserInfo(2);
                } else if (wmV == 3) {
                    postUserInfo(3);
                } else if (wmV == 4) {
                    postUserInfo(4);
                } else if (wmV == 5) {
                    postUserInfo(5);
                }
            }
            else if (msg.obj instanceof String)
            {
                String wmV = (String) msg.obj;
                Log.v(LOG_TAG, wmV);
            }
            else if (msg.obj == null)
            {
                Log.v(LOG_TAG, "msg.obj is null");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class EawErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        }
    }

    private void init() {
        eawIsRunning = false;
        // eaw app key
        String eawAppKey = "pBQ9inKpnQ1+ZPStxgfWxkdThNysrWYV040Qw9exqMAc21oFFUv63vNXEK9wkgClDDm9xKACOQxk6JYJaC0CiQ==";
        Context context = getBaseContext().getApplicationContext();
        eaw = new EAWSDK(eawAppKey, context, eawResultHandler, eawErrorHandler);
        finishEawInit = true;
    }

    private void touchEawButton() {
        if (eawIsRunning) {
            eawIsRunning = false;
            // stop
            eaw.stopDetecting();
        }
        else {
            eawIsRunning = true;
            // start
            eaw.startDetecting();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.haishin_fukuon);

        pref = getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);

        title = (TextView) findViewById(R.id.textView1);
        status = (TextView) findViewById(R.id.textView2);
        user_photo = (ImageView) findViewById(R.id.imageView1);

        Toast.makeText(SendActivity.this, getUserName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(SendActivity.this, getUserDescription(), Toast.LENGTH_SHORT).show();

        byte[] bytes = getIntent().getExtras().getByteArray("pic_data");
        user_picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        user_picture = Bitmap.createScaledBitmap(user_picture, 800, 800, false);
        user_photo.setImageBitmap(user_picture);

        finishEawInit = false;
        init();

        eawIsRunning = true;
        // start
        eaw.startDetecting();

        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundIdA = mSoundPool.load(getApplicationContext(), R.raw.sound_a, 0);
        mSoundIdB = mSoundPool.load(getApplicationContext(), R.raw.sound_g, 0);
        mSoundIdC = mSoundPool.load(getApplicationContext(), R.raw.sound_p, 0);
        mSoundIdD = mSoundPool.load(getApplicationContext(), R.raw.sound_u, 0);

        btn_a = (ImageButton) findViewById(R.id.imageButton1);
        btn_a.setOnClickListener(this);
        btn_b = (ImageButton) findViewById(R.id.imageButton2);
        btn_b.setOnClickListener(this);
        btn_c = (ImageButton) findViewById(R.id.imageButton3);
        btn_c.setOnClickListener(this);
        btn_d = (ImageButton) findViewById(R.id.imageButton4);
        btn_d.setOnClickListener(this);

        // 送信先アドレスを設定する
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName((pref.getString(KEY_TEXT, "")));
            // addr = InetAddress.getByName("172.20.10.5");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.toString());
        }
        Log.e(LOG_TAG, "addr:" + addr);
        // オーディオ送信 Runnable
        sendAudioRunnable = new SendAudioRunnable(addr);
        Log.e(LOG_TAG, "sendAudioRunnable" + sendAudioRunnable);

    }

    private String getUserName() {

        File directory = Environment.getExternalStorageDirectory();
        String filepath = "/sdcard/Download/user_name.txt";
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            while (br.ready()) {
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            // System.out.println("ファイルが見つかりません。");
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println("入出力エラーです。");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // System.out.println("入出力エラーです。");
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    private String getUserDescription() {

        File directory = Environment.getExternalStorageDirectory();
        String filepath = "/sdcard/Download/user_description.txt";
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            while (br.ready()) {
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            // System.out.println("ファイルが見つかりません。");
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println("入出力エラーです。");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // System.out.println("入出力エラーです。");
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fukuon, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1:
                mSoundPool.play(mSoundIdA, 1.0F, 1.0F, 0, 0, 1.0F);
                break;
            case R.id.imageButton2:
                mSoundPool.play(mSoundIdB, 1.0F, 1.0F, 0, 0, 1.0F);
                break;
            case R.id.imageButton3:
                mSoundPool.play(mSoundIdC, 1.0F, 1.0F, 0, 0, 1.0F);
                break;
            case R.id.imageButton4:
                mSoundPool.play(mSoundIdD, 1.0F, 1.0F, 0, 0, 1.0F);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!finishEawInit)
            init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();

        if (sendAudioRunnable != null) {
            // 録音停止
            sendAudioRunnable.stopRecording();
        }

        if (eawIsRunning)
            touchEawButton();
        if (finishEawInit) {
            eaw.release();
            finishEawInit = false;
        }

        // ユーザー削除
        deleteUser();

    }

    private void postToServer(int programId, String programTitle) {
        // String server = "192.168.1.178";
        String server = "nodejs.moe.hm";
        User user = new User(getUserName(), "/sdcard/Download/user_picture.jpg", programTitle,
                programId, getUserDescription(), 0);
        PostUserRequest req = new PostUserRequest(server, user);

        NetworkWork resultWork = new NetworkWork() {
            @Override
            public void response(Response resp) {
                if (resp == null)
                    return;
                // ここで レスポンスからユーザー必要情報を取得する
                if (resp.getStatus().equals(Response.STATUS_SUCCESS)) {
                    PostUserResponse postUserResponse = (PostUserResponse) resp;
                    loginUser = postUserResponse.getUser();
                    Log.v("response", "set loginUser");

                    // オーディオ送信
                    Thread thrd = new Thread(sendAudioRunnable);
                    thrd.start();
                }
            }
        };

        // 非同期でRequestを実行
        NetworkAsyncTask task = new NetworkAsyncTask(resultWork);
        task.execute(req);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (sendAudioRunnable != null) {
            // 録音停止
            sendAudioRunnable.stopRecording();
        }

        if (eawIsRunning)
            touchEawButton();
        if (finishEawInit) {
            eaw.release();
            finishEawInit = false;
        }
        // ユーザー削除
        deleteUser();
    }

    // ログイン中のユーザー
    User loginUser = null;

    private void deleteUser() {
        if (loginUser == null) {
            return;
        }
        // String server = "192.168.1.178";
        String server = "nodejs.moe.hm";

        DeleteUserRequest req = new DeleteUserRequest(server, loginUser);
        NetworkWork resultWork = new NetworkWork() {
            @Override
            public void response(Response resp) {
                if (resp == null)
                    return;
                // ここで レスポンスからユーザー必要情報を取得する
                if (resp.getStatus().equals(Response.STATUS_SUCCESS)) {
                    //
                }
            }
        };

        // 非同期でRequestを実行
        NetworkAsyncTask task = new NetworkAsyncTask(resultWork);
        task.execute(req);
    }
}
