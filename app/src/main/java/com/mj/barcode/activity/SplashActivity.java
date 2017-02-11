package com.mj.barcode.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;


import com.mj.barcode.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 应用启动短暂白屏处理
 * 初始化权限请求
 *
 */
public class SplashActivity extends BasicActivity implements EasyPermissions.PermissionCallbacks{

    private static final int RC_LOCATION_STORAGE_PHONE = 110;
    private static final int RC_SETTING_SCREEN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //MainActivity.actionStart(this);
        initPermissions();
    }

    @AfterPermissionGranted(RC_LOCATION_STORAGE_PHONE)
    private void initPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //有权限 继续业务
            onNext();
        } else {
            //没权限，申请权限
            EasyPermissions.requestPermissions(this, getString(R.string.permission_init_alert),
                    RC_LOCATION_STORAGE_PHONE, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // Some permissions have been granted
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        // 判断是否有权限被永久禁止
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AlertDialog.Builder(this).setMessage(getString(R.string.permission_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.text_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivityForResult(intent, RC_SETTING_SCREEN);
                        }
                    })
                    .setNegativeButton(getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onNext();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } else {
            onNext();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onNext();
    }

    private void onNext() {
        MainActivity.actionStart(this);
    }
}
