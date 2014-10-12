
package com.ajimitei.fukuon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evixar.eaw_utilities.EAWSDK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SendActivity extends Activity implements OnClickListener {

    private EAWSDK eaw;
    private boolean finishEawInit;
    private boolean eawIsRunning;

    private EawResultHandler eawResultHandler = new EawResultHandler();
    private EawErrorHandler eawErrorHandler = new EawErrorHandler();

    TextView title;
    TextView status;
    ImageView user_photo;
    Bitmap user_picture;
    Context context;

    @SuppressLint("HandlerLeak")
    private class EawResultHandler extends Handler {

        private void postUserInfo(int i) {

            eawIsRunning = false;
            eaw.stopDetecting();

            switch (i) {
                case 1:
                    title.setText(R.string.sending_message_title_gacchiri);
                    break;
                case 2:
                    title.setText(R.string.sending_message_title_keion);
                    break;
                case 3:
                    title.setText(R.string.sending_message_title_fushigi);
                    break;
                default:
                    title.setText(R.string.sending_message_title_gacchiri);
                    break;
            }
            title.setText(R.string.sending_message_title_gacchiri);

            status.setText(R.string.sending_message_status_on);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Long)
            {
                long wmV = ((Long) msg.obj).longValue();
                // mEawTextLog.append(wmV + "\n");
                Log.v("hogehoge", String.valueOf(wmV));
                if (wmV == 1) {
                    postUserInfo(1);
                } else if (wmV == 2) {
                    postUserInfo(2);
                } else if (wmV == 3) {
                    postUserInfo(3);
                }
            }
            else if (msg.obj instanceof String)
            {
                String wmV = (String) msg.obj;
                Log.v("hogehoge", wmV);
            }
            else if (msg.obj == null)
            {
                Log.v("hogehoge", "hagehage");
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

        context = this;

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
    public void onClick(View arg0) {

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

        if (eawIsRunning)
            touchEawButton();
        if (finishEawInit) {
            eaw.release();
            finishEawInit = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (eawIsRunning)
            touchEawButton();
        if (finishEawInit) {
            eaw.release();
            finishEawInit = false;
        }
    }

}
