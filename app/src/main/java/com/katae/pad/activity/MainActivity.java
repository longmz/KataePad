package com.katae.pad.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.katae.pad.R;
import com.katae.pad.bean.AppInfo;
import com.katae.pad.bean.InspectContent;
import com.katae.pad.bean.LoginContent;
import com.katae.pad.bean.TaskContent;

/**
 * An activity representing a single User task screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class MainActivity extends BaseActivity {

    private HttpTask mHttpTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.mLayoutView = R.layout.activity_main;
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int ibheight = displayMetrics.widthPixels;
        if (ibheight > displayMetrics.heightPixels) {
            ibheight = displayMetrics.heightPixels;
        }

        // 每行APP图标数量，竖屏：2；横屏：3
        int lineAppNum = 2;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lineAppNum = 3;
        }

        int line = 0;
        ImageButton button;
        LinearLayout layout = null;
        LinearLayout.LayoutParams layoutParams = null;
        LinearLayout appLayout = findViewById(R.id.app_layout);

        for (final AppInfo app : LoginContent.APP_LIST) {
            if(app.mAppType.equals("1")) {
                if (line % lineAppNum == 0) {
                    layout = new LinearLayout(this);
                    layout.setDividerDrawable(getResources().getDrawable(R.drawable.sharp_line));
                    layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    appLayout.addView(layout);
                }

                if(layout != null) {
                    button = new ImageButton(this);
                    button.setImageResource(app.getImageDrawableId());
                    layout.addView(button);

                    if (line == 0) {
                        layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
                        layoutParams.height = ibheight * 3 / 8;
                        layoutParams.weight = 1.0f;
                    }

                    button.setLayoutParams(layoutParams);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Context context = view.getContext();
                            Intent intent = app.getAppIntent(context);
                            if (intent != null) {
                                intent.putExtra(BaseActivity.ARG_APP_ID, app.mAppId);
                                intent.putExtra(BaseActivity.ARG_APP_NAME, app.mAppName);
                                context.startActivity(intent);
                            }
                        }
                    });
                }

                line++;
            }
        }

        if(layout != null && layoutParams != null) {
            while (line % lineAppNum > 0) {
                button = new ImageButton(this);
                button.setImageResource(R.drawable.blank);
                button.setLayoutParams(layoutParams);
                layout.addView(button);
                line++;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            showExitConfirm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showExitConfirm();
    }

    private void showExitConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getString(R.string.prompt_exit_title));
        builder.setMessage(getString(R.string.prompt_exit_message));

        //监听下方button点击事件
        builder.setPositiveButton(getString(R.string.prompt_button_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(mHttpTask == null) {
                    showProgress(true);
                    mHttpTask = new HttpTask(LoginContent.Token);
                    mHttpTask.execute((Void) null);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.prompt_button_cancel), null);

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private class HttpTask extends AsyncTask<Void, Void, Boolean> {

        private final String mToken;
        private String mErrorMessage = "Error";

        HttpTask(String token) {
            mToken = token;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //清除缓存中的数据
            LoginContent.clearList();
            TaskContent.clearTodoList();
            TaskContent.clearCompList();
            InspectContent.clearList();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mHttpTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                Toast.makeText(MainActivity.this, mErrorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mHttpTask = null;
            showProgress(false);
        }
    }
}
