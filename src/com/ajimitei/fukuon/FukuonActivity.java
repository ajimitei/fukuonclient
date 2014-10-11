
package com.ajimitei.fukuon;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;

public class FukuonActivity extends Activity implements OnClickListener {

    private EditText editText_name;
    private EditText editText_description;
    private Camera myCamera;
    private SurfaceHolder.Callback mSurfaceListener =
            new SurfaceHolder.Callback() {
                public void surfaceCreated(SurfaceHolder holder) {
                    // TODO Auto-generated method stub
                    myCamera = Camera.open(1);
                    try {
                        myCamera.setPreviewDisplay(holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void surfaceDestroyed(SurfaceHolder holder) {
                    // TODO Auto-generated method stub
                    myCamera.release();
                    myCamera = null;
                }

                public void surfaceChanged(SurfaceHolder holder, int format, int width,
                        int height) {
                    // TODO Auto-generated method stub
                    Camera.Parameters parameters = myCamera.getParameters();
                    // parameters.setPreviewSize(width, height);
                    parameters.setPreviewSize(640, 480);
                    parameters.setRotation(270);
                    myCamera.setDisplayOrientation(90);
                    myCamera.setParameters(parameters);
                    myCamera.startPreview();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fukuon);

        SurfaceView mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        SurfaceHolder holder = mySurfaceView.getHolder();
        holder.addCallback(mSurfaceListener);
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        editText_name = (EditText) this.findViewById(R.id.editText1);
        editText_description = (EditText) this.findViewById(R.id.editText2);

        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(this);

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
        if (editText_name != null) {
            FileOutputStream myFOS = null;
            try {
                myFOS = new FileOutputStream("/sdcard/Download/user_name.txt");
                myFOS.write(editText_name.getText().toString().getBytes());
                myFOS.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (editText_description != null) {
            FileOutputStream myFOS = null;
            try {
                myFOS = new FileOutputStream("/sdcard/Download/user_description.txt");
                myFOS.write(editText_description.getText().toString().getBytes());
                myFOS.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(FukuonActivity.this, SendActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);

    }

    // シャッターが押されたときに呼ばれるコールバック
    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                public void onShutter() {
                    // TODO Auto-generated method stub
                }
            };

    // JPEGイメージ生成後に呼ばれるコールバック
    private Camera.PictureCallback mPictureListener =
            new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    // SDカードにJPEGデータを保存する
                    if (data != null) {
                        FileOutputStream myFOS = null;
                        try {
                            myFOS = new FileOutputStream("/sdcard/Download/user_picture.jpg");
                            myFOS.write(data);
                            myFOS.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        camera.startPreview();
                    }
                }
            };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (myCamera != null) {
                myCamera.takePicture(mShutterListener, null, mPictureListener);
            }
        }
        return true;
    }

}
