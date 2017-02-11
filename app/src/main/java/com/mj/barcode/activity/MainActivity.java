package com.mj.barcode.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.mj.barcode.R;
import com.mj.barcode.utils.ToastUtils;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BasicActivity implements EasyPermissions.PermissionCallbacks{
    private static final int RC_SCAN_BARCODE = 10;
    private static final int RC_SETTING_SCREEN = 11;
    private TextView tvResult;
    private long firstClick = 0;

    public static void actionStart(Activity act) {
        Intent intent = new Intent(act, MainActivity.class);
        act.startActivity(intent);
        act.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CardView cardView = (CardView) findViewById(R.id.card_view);
        tvResult = (TextView) findViewById(R.id.tv_result);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqPermission();

            }
        });

    }

    @AfterPermissionGranted(1)
    private void reqPermission() {
        String[] perms = {
                Manifest.permission.CAMERA
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            onNext();
        }else {
            EasyPermissions.requestPermissions(this, getString(R.string.perm_camera_rational),1,perms);
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
            new AlertDialog.Builder(this).setMessage(getString(R.string.perm_camera_not_granted))
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SCAN_BARCODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    tvResult.setText(result);
                }
                break;
            default:
                break;
        }
    }

    private void onNext() {
        //MipcaActivityCapture.actionStartForResult(MainActivity.this, RC_SCAN_BARCODE);
        Intent intent = new Intent(this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent,RC_SCAN_BARCODE);
    }

    //双击退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondClick = System.currentTimeMillis();
            if (secondClick - firstClick > 1000) {
                ToastUtils.showToast(this, "再次点击退出");
                firstClick = secondClick;
                return true;
            }else{
                //退出应用
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
