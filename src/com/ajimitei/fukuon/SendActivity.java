
package com.ajimitei.fukuon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.evixar.eaw_utilities.EAWSDK;

public class SendActivity extends Activity implements OnClickListener {

    private EAWSDK eaw;
    private boolean finishEawInit;
    private boolean eawIsRunning;

    private EawResultHandler eawResultHandler = new EawResultHandler();
    private EawErrorHandler eawErrorHandler = new EawErrorHandler();

    TextView title;
    TextView status;
    ImageView icon;

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
            icon.setImageResource(R.drawable.oreimo);
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
                // mEawTextLog.append(wmV + "\n");
                Log.v("hogehoge", wmV);
            }
            else if (msg.obj == null)
            {
                // mEawTextLog.append("not detected\n");
                Log.v("hogehoge", "hagehage");
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class EawErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // mEawTextLog.append("\nerror\n");
            // mEawTextLog.append("errcode: " + msg.obj + "\n");
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
        setContentView(R.layout.haishin_fukuon);

        title = (TextView) findViewById(R.id.textView1);
        status = (TextView) findViewById(R.id.textView2);
        icon = (ImageView) findViewById(R.id.imageView1);

        finishEawInit = false;
        init();

        eawIsRunning = true;
        // start
        eaw.startDetecting();

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
