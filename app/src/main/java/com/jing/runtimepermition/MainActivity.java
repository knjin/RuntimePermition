package com.jing.runtimepermition;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.knjin.runtimepermition.R;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private static final int RC_CAMERA = 10000;

    TextView tv;
    Button btn ,btnEasyPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        btnEasyPermission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                requestPermissionCamera();
            }
        });
    }

    /**
     * 传统方式
     * 获取相机的运行权限
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请相机权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
                            }
                        }).show();

            }else {
                //申请相机权限
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA,},1);
            }

        }else {
            tv.setText("相机权限已经申请");
            tv.setTextColor(Color.GREEN);
        }
    }

    private void initData() {
        tv  = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.btn_camera_per);
        btnEasyPermission = (Button) findViewById(R.id.btn_easycamera);
    }

    @AfterPermissionGranted(RC_CAMERA)
    private void requestPermissionCamera(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//授权成功
            openCamera();
        }else {
            EasyPermissions.requestPermissions(this, "请求相机权限", RC_CAMERA, perms);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 授权成功
     * @param requestCode
     * @param perms
     */
    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode != RC_CAMERA) {
            return;
        }
        openCamera();
    }

    private void openCamera() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
              tv.setText("相机权限已经申请");
            }
        });
    }

    /**
     * 授权失败
     * @param requestCode
     * @param perms
     */
    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode != RC_CAMERA) {
            return;
        }
        for (int i = 0; i < perms.size(); i++) {
            if (perms.get(i).equals(Manifest.permission.CAMERA)) {
                Log.e(TAG, "onPermissionsDenied: " + "读取存储拒绝");
            }
        }

        //如果有一些权限被永久的拒绝, 就需要转跳到 设置-->应用-->对应的App下去开启权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                .setTitle("权限已经被您拒绝")
                .setRationale("如果不打开权限则无法使用该功能,点击确定去打开权限")
                .setRequestCode(10001)//用于onActivityResult回调做其它对应相关的操作
                .build()
                .show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Toast.makeText(this, "从开启权限的页面转跳回来", Toast.LENGTH_SHORT).show();
        }
    }
}
