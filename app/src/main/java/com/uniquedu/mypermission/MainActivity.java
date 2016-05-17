package com.uniquedu.mypermission;

import android.Manifest;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.button_camera)
    Button buttonCamera;
    @InjectView(R.id.button_call)
    Button buttonCall;
    @InjectView(R.id.button_open)
    Button buttonOpen;
    @InjectView(R.id.button_reopen)
    Button buttonReopen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.button_camera, R.id.button_call, R.id.button_open, R.id.button_reopen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_camera:
                Camera camera = Camera.open();
                break;
            case R.id.button_call:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:110"));
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
                startActivity(intent);
                break;
            case R.id.button_open:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "没有权限，开始申请权限", Toast.LENGTH_SHORT).show();
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //为什么申请该权限
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("必须同意该权限")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x23);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                        return;
                    }
                    //检查权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x23);
                    //申请权限
                    return;
                } else {
                    write2sdcard();
                    Toast.makeText(MainActivity.this, "已经拥有权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_reopen:
                Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent2.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent2);
                break;
        }
    }

    private void write2sdcard() {
        File file = new File(Environment.getExternalStorageDirectory() + "/11.txt");
        try {
            FileOutputStream os = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write("这是一个新的内容");
            writer.flush();
            writer.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0x23) {
            //申请权限成功
            Toast.makeText(MainActivity.this, "申请读写sdcard的权限成功", Toast.LENGTH_SHORT).show();
            write2sdcard();
        }
    }
}
