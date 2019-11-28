package com.cuixbo.demo.runtimepermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

/**
 * @author xiaobocui
 * 1.权限申请，按组怎么申请
 * 2.一次申请多个怎么申请，回调怎么处理
 * 3.不同厂商rom权限怎么适配
 * 4.ContextCompat.checkSelfPermission始终返回0，PERMISSION_GRANTED
 * 5.target=23前后的处理
 */
public class PermissionActivity extends AppCompatActivity {


    private final static int PERMISSION_REQ_CODE = 10;
    private Context mContext;
    private Button mBtnAccess;
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        mContext = this;
        initView();
        initListener();
    }

    private void initView() {
        mBtnAccess = findViewById(R.id.btn_access);
    }

    private void initListener() {
        mBtnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 1.检查权限
                 * 2.申请权限
                 * 3.处理权限申请的回调
                 * 4.告知用户，权限的重要性，需要权限。
                 */
                if (checkPermissions(mContext, permissions)) {
                    //已经授权了
                    Log.e("xbc", "已经授权了");
                    showToast("checkPermissions:已经授权了");
                    //干你该干的事情去吧
                    doActions();
                } else {
                    /*
                     * 如果target<23,部分厂商如小米，可能出现checkPermissions=false情况；
                     * 上面这种情况有可能是厂商系统导致，也可以是用户主动在权限管理里关闭的。
                     * 这里就需要提醒用户主动去开启权限才行，而且用户开启的权限也不一定立即奏效，有可能需要杀掉进程重启才会奏效。
                     * 结论，所以尽量还是要将target>=23,现在市场都要求target>=26了，最合适，否则这里会由于各种厂商、系统的不同
                     * 带来各种意想不到的变化，需要去处理兼容。
                     */
                    //没有授权
                    Log.e("xbc", "还没有授权");
                    showToast("checkPermissions:还没有授权");
                    //那就调用权限申请吧
                    ActivityCompat.requestPermissions((Activity) mContext, permissions,
                            PERMISSION_REQ_CODE);
                }
            }
        });

        mBtnAccess.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                gotoAppDetailActivity();
                return false;
            }
        });
    }


    private boolean checkPermissions(Context context, @NonNull String... permissions) {
        //当前运行设备的系统版本
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            for (String permission : permissions) {
                // target<23&api>=23时ContextCompat.checkSelfPermission可能一直返回0
                if (PermissionChecker.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void doActions() {
        accessExternalCacheDir();
        accessExternalStorageDirectory();
    }

    /**
     * API 19开始，访问该目录不需要存储权限了
     */
    private void accessExternalCacheDir() {
        File file = getExternalCacheDir();
        // api<19 时如果清单没有配，设置权限中不展示权限，则返回null取不到。如果清单配置了，则返回目录，且设置权限中展示存储权限。
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

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    private void accessExternalStorageDirectory() {
        File file = Environment.getExternalStorageDirectory();
        if (file != null) {
            Log.e("xbc", "getExternalStorageDirectory:" + file.getAbsolutePath());
            File f = new File(file.getAbsolutePath() + File.separator + "test");
            try {
                f.delete();
                boolean createResult = f.createNewFile();
                Log.e("xbc", "getExternalStorageDirectory:" + createResult);
                Log.e("xbc", "getExternalStorageDirectory:" + f.getAbsolutePath());
                if (createResult) {
                    showToast("成功访问，" + f.getAbsolutePath());
                } else {
                    showToast("访问失败，" + f.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("访问异常，" + f.getAbsolutePath() + "," + e.getLocalizedMessage());
            }
        } else {
            Log.e("xbc", "getExternalStorageDirectory:null");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0) {
                //下面只拿第一个权限做演示
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("xbc", "0,被授权了");
                    showToast("onRequestPermissionsResult:被授权了");
                    doActions();
                } else {
                    Log.e("xbc", "0,授权被拒了");
                    showToast("onRequestPermissionsResult:授权被拒了");
                    //这里要注意了，要检查是“被拒绝一次”还是“被拒绝，且不再提示”
                    boolean result = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                    /*
                     * result==true，用户只是拒绝了一次，并没有勾选不再提醒
                     * result==false，用户拒绝了，并且勾选了不再提醒
                     */
                    if (result) {
                        //还好，用户只是这次拒绝了你，下次你还是可以请求的。
                        //这里你要告知用户该权限的重要性，没有这个权限不行的，引导他再次进行授权。
                        showPermissionRationaleDialog(permissions);
                    } else {
                        //用户很在意这个权限，直接拒绝了你，而且不再提醒。
                        //此时此刻，你需要做事情了，要告诉用户你需要这个权限，并且可以引导他去开启这个权限。
                        //这里需要 给予用户提醒，比如Toast或者对话框，引导用户去系统设置-应用管理里把相关权限打开
                        showToast("Sorry，您已勾选了不再提醒，但是应用没有这些权限就无法正常运行，还请去系统设置-应用管理里把相关权限打开吧，亲~~");
                        showPermissionSettingGuideDialog();
                    }
                }
                Log.e("xbc", "" + Arrays.toString(permissions));
            } else {
                Log.e("xbc", "授权被拒了");
                Log.e("xbc", Arrays.toString(permissions));
                showToast("onRequestPermissionsResult:授权被拒了");

            }
        }
    }

    /**
     * 向用户具体解释为什么需要这些权限，应用的正常运行离不开这些权限。
     *
     * @param permissions 需要再次申请的权限
     */
    private void showPermissionRationaleDialog(@NonNull final String... permissions) {
        //这里你要告知用户该权限的重要性，没有这个权限不行的，告知需要他再次进行授权。
        new AlertDialog.Builder(this)
                .setTitle("权限申请提示")
                .setMessage("亲，应用必须需要这些权限才能够正常运行呦~~")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //下一次权限申请，依然可以继续弹出ui，那我们就开始申请一次吧
                        ActivityCompat.requestPermissions(
                                (Activity) mContext,
                                permissions,
                                PERMISSION_REQ_CODE
                        );
                    }
                })
                .create()
                .show();
    }

    /**
     * 用户拒绝了权限，而且还勾选了“不再提示”，接下来不管你再怎么调权限申请API，系统也都只会是直接拒绝你了。
     * 所以这里需要郑重的告知用户这些权限的重要性和必要性，没有这些权限，应用不能正常运行。
     * 还需要引导用户去权限设置页，手动开启权限。
     */
    private void showPermissionSettingGuideDialog() {
        //用户很在意这个权限，直接拒绝了你，而且不再提醒。
        //此时此刻，你需要做事情了，要告诉用户你需要这个权限，并且可以引导他去开启这个权限。
        //这里需要 给予用户提醒，比如Toast或者对话框，引导用户去系统设置-应用管理里把相关权限打开
        showToast("Sorry，您已勾选了不再提醒，但是应用没有这些权限就无法正常运行，还请去系统设置-应用管理里把相关权限打开吧，亲~~");
        new AlertDialog.Builder(this)
                .setTitle("权限申请提示")
                .setMessage("sorry，亲，您已勾选了不再提醒，但是应用没有这些权限就无法正常运行，去“权限管理”里把相关权限打开吧，亲~~")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导去应用信息页面设置权限
                        gotoAppDetailActivity();
                    }
                })
                .create()
                .show();
    }

    /**
     * 跳转到应用详情界面
     */
    public void gotoAppDetailActivity() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }


}
