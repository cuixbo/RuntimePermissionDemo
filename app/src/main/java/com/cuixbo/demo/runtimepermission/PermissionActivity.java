package com.cuixbo.demo.runtimepermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author xiaobocui
 */
public class PermissionActivity extends AppCompatActivity {


    private final static int PERMISSION_REQ_CODE = 10;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        mContext = this;
        checkPermission();
    }

    private void doActions() {
        accessExternalCacheDir();
        accessExternalStorageDirectory();
    }

    /**
     * 1.检查权限
     * 2.申请权限(异步)
     * 3.处理权限申请的回调
     * 可以封装一下,处理回调
     */
    private void checkPermission() {
//        PermissionChecker.checkSelfPermission()
        //检查权限
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有授权
            Log.e("xbc", "还没有授权");
            //result=false，用户勾选了不再提醒，或者用户已经授予了权限
            boolean result = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!result) {
                //TODO 这里需要 给予用户提醒，比如Toast或者对话框，引导用户去系统设置-应用管理里把相关权限打开
                Toast.makeText(mContext, "sorry,您已勾选了不再提醒，去系统设置-应用管理里把相关权限打开吧，亲~~",
                        Toast.LENGTH_LONG).show();
            } else {
                //下一次权限申请，依然可以继续弹出ui，那我们就开始申请一次吧
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                        },
                        PERMISSION_REQ_CODE
                );
            }
            Log.e("xbc", "Whether you can show permission rationale UI:" + result);
        } else {
            //已经授权了
            Log.e("xbc", "已经授权了");
            //干你该干的事情去吧
            doActions();
        }

        //申请权限(异步)
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQ_CODE);

        //处理权限申请的回调

    }

    /**
     * 是否展示权限申请的理由描述UI（权限弹窗）
     * Whether you can show permission rationale UI
     * 它应该是在没有获得权限时使用
     */
    private void checkShouldShow() {
        boolean result = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!result) {
            //result=false，用户勾选了不再提醒，或者用户已经授予了权限
            //TODO 这里需要 给予用户提醒，比如Toast或者对话框，引导用户去系统设置-应用管理里把相关权限打开
            Toast.makeText(mContext, "sorry,您已勾选了不再提醒，去系统设置-应用管理里把相关权限打开吧，亲~~",
                    Toast.LENGTH_LONG).show();
        } else {
            //下一次权限申请，依然可以继续弹出ui
        }
        Log.e("xbc", "Whether you can show permission rationale UI:" + result);
    }


    private void accessExternalCacheDir() {
        File file = getExternalCacheDir();
        // api<19时如果清单没有配，设置权限中不展示权限，则返回null取不到。如果清单配置了，则返回目录，且设置权限中展示存储权限。
        if (file != null) {
            Log.e("xbc", "getExternalCacheDir:" + file.getAbsolutePath());

            File f = new File(file.getAbsolutePath() + File.separator + "test");
            try {
                boolean createResult = f.createNewFile();
                Log.e("xbc", "getExternalCacheDir:" + createResult);
                Log.e("xbc", "getExternalCacheDir:" + f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("xbc", "getExternalCacheDir:null");
        }
    }

    @SuppressWarnings("deprecation")
    private void accessExternalStorageDirectory() {
        File file = Environment.getExternalStorageDirectory();
        if (file != null) {
            Log.e("xbc", "getExternalStorageDirectory:" + file.getAbsolutePath());
            File f = new File(file.getAbsolutePath() + File.separator + "test");
            try {
                boolean createResult = f.createNewFile();
                Log.e("xbc", "getExternalStorageDirectory:" + createResult);
                Log.e("xbc", "getExternalStorageDirectory:" + f.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("xbc", "getExternalStorageDirectory:null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("xbc", "0,被授权了");
                } else {
                    Log.e("xbc", "0,授权被拒了");
                }
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("xbc", "1,被授权了");
                } else {
                    Log.e("xbc", "1,授权被拒了");
                }
                Log.e("xbc", "" + Arrays.toString(permissions));
            } else {
                Log.e("xbc", "授权被拒了");
            }
        }
    }


}
