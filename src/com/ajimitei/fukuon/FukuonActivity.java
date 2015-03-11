
package com.ajimitei.fukuon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FukuonActivity extends Activity implements OnClickListener {

    private byte[] pic_data;

    private EditText editText_ipaddress;
    private EditText editText_name;
    private EditText editText_description;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREF_KEY = "fukuonpreferences";
    private static final String KEY_TEXT = "fukuonip";

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fukuon);

        pref = getSharedPreferences(PREF_KEY, Activity.MODE_PRIVATE);

        SurfaceView mySurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        SurfaceHolder holder = mySurfaceView.getHolder();
        holder.addCallback(mSurfaceListener);
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        editText_name = (EditText) this.findViewById(R.id.editText1);
        editText_description = (EditText) this.findViewById(R.id.editText2);
        editText_ipaddress = (EditText) this.findViewById(R.id.editText3);

        ImageButton btn = (ImageButton) findViewById(R.id.button1);
        btn.setOnClickListener(this);

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

        if (editText_ipaddress != null) {
            editor = pref.edit();
            editor.putString(KEY_TEXT, editText_ipaddress.getText().toString());
            editor.commit();
        }

        Intent intent = new Intent(FukuonActivity.this, SendActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("pic_data", pic_data);
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
                        pic_data = data;
                        Bitmap user_picture = BitmapFactory.decodeByteArray(data, 0, data.length);

                        BitmapRegionDecoder regionDecoder = null;
                        try {
                            regionDecoder = BitmapRegionDecoder.newInstance(data, 0, data.length,
                                    false);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        Rect rect = new Rect(0, 400, 800, 680);
                        Bitmap bitmap = regionDecoder.decodeRegion(rect, null);

                        // user_picture =
                        // Bitmap.createScaledBitmap(user_picture, 400, 140,
                        // false);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 140, false);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // user_picture.compress(CompressFormat.JPEG, 100,
                        // baos);
                        bitmap.compress(CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        FileOutputStream myFOS = null;
                        try {
                            myFOS = new FileOutputStream("/sdcard/Download/user_picture.jpg");
                            myFOS.write(bytes);
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
