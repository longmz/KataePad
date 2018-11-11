package com.katae.pad.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.azhon.appupdate.utils.LogUtil;
import com.katae.pad.R;
import com.katae.pad.Service.BadgeService;
import com.katae.pad.bean.AccessToken;
import com.katae.pad.bean.ApiResult;
import com.katae.pad.bean.AppInfo;
import com.katae.pad.bean.LoginContent;
import com.katae.pad.bean.UpdateInfo;
import com.katae.pad.utils.APKVersionCodeUtils;
import com.katae.pad.utils.JellyInterpolator;
import com.katae.pad.utils.NetWebApi;

import java.io.File;
import java.util.ArrayList;
import android.os.Handler;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.katae.pad.utils.NetworkCheck;

/**
 * A login screen that offers login via account/password.
 */
public class LoginActivity extends AppCompatActivity implements OnDownloadListener, OnClickListener, OnButtonClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private TextView mBtnLogin;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mPsw;

    // UI references.
    private EditText mAccountView;
    private EditText mPasswordView;
    private String mIMEI;
    private NumberProgressBar progressBar;

    private Intent badgeServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //badgeServiceIntent = new Intent(LoginActivity.this, BadgeService.class);
        //startService(badgeServiceIntent);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        1000);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1001);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if(!Build.MODEL.contains("Android")) {
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
                    mIMEI = tm.getDeviceId();
                }
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(getString(R.string.prompt_alert_title));
                builder.setMessage(getString(R.string.prompt_security_alert));

                //监听下方button点击事件
                builder.setPositiveButton(getString(R.string.prompt_button_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        /*ApiResult<UpdateInfo> updateResult = NetWebApi.getUpdateInfo();
        if(updateResult.mErrorCode == 0) {
            int versionCode = APKVersionCodeUtils.getVersionCode(this);
            if(updateResult.mResult.mVersionCode > versionCode) {
                startUpdate3(updateResult.mResult.mApkUrl,
                    updateResult.mResult.mVersionCode,
                    updateResult.mResult.mVersionName,
                    updateResult.mResult.mApkSize,
                    updateResult.mResult.mApkDescription);
            }
        }*/

        initView();
    }

    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw = findViewById(R.id.input_layout_psw);
        mAccountView = findViewById(R.id.account);
        mPasswordView = findViewById(R.id.password);
        progressBar = findViewById(R.id.number_progress_bar);

        mBtnLogin.setOnClickListener(this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        attemptLogin();
        /*String account = mAccountView.getText().toString();

        try {
            String test = DefaultEncryptor.Encrypt3DES(account, "jacob929@qq.com");
            mAccountView.setText(test);
        } catch (Exception ex) {
            mAccountView.setText(ex.getMessage());
        }*/
    }

    private void startUpdate1() {
        new AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage("新版本消息提示")
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager manager = DownloadManager.getInstance(LoginActivity.this);
                        manager.setApkName("appupdate.apk")
                                .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .download();
                    }
                }).create().show();
    }

    private void startUpdate2() {
        DownloadManager manager = DownloadManager.getInstance(LoginActivity.this);
        manager.setApkName("appupdate.apk")
                .setApkUrl("https://raw.githubusercontent.com/azhon/AppUpdate/master/apk/appupdate.apk")
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setSmallIcon(R.mipmap.ic_launcher)
                .download();
    }

    private void startUpdate3(String apkUrl, int versionCode, String versionName, String apkSize, String apkDesc) {
        /*
         * 整个库允许配置的内容
         * 非必选
         */
        UpdateConfiguration configuration = new UpdateConfiguration()
                //输出错误日志
                .setEnableLog(true)
                //设置自定义的下载
                //.setHttpManager()
                //下载完成自动跳动安装页面
                .setJumpInstallPage(true)
                //支持断点下载
                .setBreakpointDownload(true)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置强制更新
                .setForcedUpgrade(false)
                //设置对话框按钮的点击监听
                .setButtonClickListener(this)
                //设置下载过程的监听
                .setOnDownloadListener(this);

        DownloadManager manager = DownloadManager.getInstance(this);
        manager.setApkName("KataePad.apk")
                .setApkUrl(apkUrl)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowNewerToast(true)
                .setConfiguration(configuration)
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setApkVersionCode(versionCode)
                .setApkVersionName(versionName)
                .setApkSize(apkSize)
                .setAuthorities(getPackageName())
                .setApkDescription(apkDesc)
                .download();
    }

    @Override
    public void start() {

    }

    @Override
    public void downloading(int max, int progress) {
        Message msg = new Message();
        msg.arg1 = max;
        msg.arg2 = progress;
        handler.sendMessage(msg);
    }

    @Override
    public void done(File apk) {

    }

    @Override
    public void error(Exception e) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressBar.setMax(msg.arg1);
            progressBar.setProgress(msg.arg2);
        }
    };

    @Override
    public void onButtonClick(int id) {
        LogUtil.e("TAG", id);
    }

    /**
     * 输入框的动画效果
     * @param view 控件
     * @param w 宽
     * @param h 高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationRepeat(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

                String account = mAccountView.getText().toString();
                String password = mPasswordView.getText().toString();
                mAuthTask = new UserLoginTask(account, password, mIMEI);
                mAuthTask.execute((Void) null);
            }
            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }

    /**
     * 出现进度动画
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
    }

    /**
     * 恢复初始状态
     */
    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid account, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid account.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // 计算出控件的高与宽
            mWidth = mBtnLogin.getMeasuredWidth();
            mHeight = mBtnLogin.getMeasuredHeight();
            // 隐藏输入框
            mName.setVisibility(View.INVISIBLE);
            mPsw.setVisibility(View.INVISIBLE);

            inputAnimator(mInputLayout, mWidth, mHeight);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 1001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAccount;
        private final String mPassword;
        private final String mIMEI;
        private ApiResult<AccessToken> mAccessToken;

        UserLoginTask(String account, String password, String imei) {
            mAccount = account;
            mPassword = password;
            mIMEI = imei;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = mBtnLogin.getContext();
            if(NetworkCheck.isNetworkConnected(context)) {
                mAccessToken = NetWebApi.getAccessToken(mAccount, mPassword, mIMEI);
                NetWebApi.saveLocalAccessToken(mAccessToken.mResult, mAccount, mPassword, context);
            } else {
                mAccessToken = NetWebApi.getLocalAccessToken(mAccount, mPassword, mIMEI, context);
            }
            return mAccessToken.mErrorCode == 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            recovery();


            if (success) {
                if(mAccessToken.mResult.mEmpId.length() > 0) {

                    LoginContent.Account = mAccount;
                    LoginContent.UserId = mAccessToken.mResult.mEmpId;
                    LoginContent.UserName = mAccessToken.mResult.mName;
                    LoginContent.Token = mAccessToken.mResult.mAccessToken;

                    if(mAccessToken.mResult.mApps == null) {
                        AppInfo app = new AppInfo("a01", "品管检查", "", "1", "1");
                        mAccessToken.mResult.mApps = new ArrayList<>();
                        mAccessToken.mResult.mApps.add(app);
                    }
                    if(mAccessToken.mResult.mApps != null) {
                        LoginContent.initList(mAccessToken.mResult.mApps);
                    }

                    Context context = mAccountView.getContext();
                    Intent intent;
                    if(mAccessToken.mResult.mApps == null || mAccessToken.mResult.mApps.size() <= 1) {
                        intent = new Intent(context, MainTaskActivity.class);
                        intent.putExtra(BaseActivity.ARG_APP_ID, "a01");
                        intent.putExtra(BaseActivity.ARG_APP_NAME, "品管检查");
                    } else {
                        intent = new Intent(context, MainActivity.class);
                    }
                    context.startActivity(intent);
                } else {
                    mAccountView.setError(mAccessToken.mErrorMessage);
                    mAccountView.requestFocus();
                }
            } else {
                Toast.makeText(LoginActivity.this, mAccessToken.mErrorMessage, Toast.LENGTH_LONG).show();
            }

            //finish();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            recovery();
        }
    }
}

