package com.katae.pad.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.katae.pad.R;
import com.katae.pad.bean.InspectGroup;
import com.katae.pad.bean.Task;
import com.katae.pad.bean.TaskContent;
import com.katae.pad.utils.SpinnerOption;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BaseActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BaseActivity extends AppCompatActivity {

    // *BaseActivity 考虑使用单独 layout*
    public static final String ARG_APP_ID     = "APP_ID";
    public static final String ARG_APP_NAME   = "APP_NAME";
    public static final String ARG_TASK_ID    = "TASK_ID";
    public static final String ARG_TASK_NAME  = "TASK_NAME";
    public static final String ARG_TASK_TYPE  = "TASK_TYPE";

    protected String mAppId;
    protected String mAppName;
    protected String mTaskId;
    protected String mTaskName;
    protected String mTaskType;

    protected int mLayoutView;
    protected Spinner mInspectGroupView;
    protected View mProgressView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.mLayoutView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(ARG_APP_ID)) {
            mAppId = getIntent().getStringExtra(ARG_APP_ID);
        } else {
            mAppId = "";
        }
        if(getIntent().hasExtra(ARG_APP_NAME)) {
            mAppName = getIntent().getStringExtra(ARG_APP_NAME);
        } else {
            mAppName = "";
        }

        if(getIntent().hasExtra(ARG_TASK_ID)) {
            mTaskId = getIntent().getStringExtra(ARG_TASK_ID);
        } else {
            mTaskId = "";
        }
        if(getIntent().hasExtra(ARG_TASK_NAME)) {
            mTaskName = getIntent().getStringExtra(ARG_TASK_NAME);
        } else {
            mTaskName = "";
        }
        if(getIntent().hasExtra(ARG_TASK_TYPE)) {
            mTaskType = getIntent().getStringExtra(ARG_TASK_TYPE);
        } else {
            mTaskType = "";
        }

        mProgressView = findViewById(R.id.load_progress);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        }

        mInspectGroupView = findViewById(R.id.inspect_group);
    }

    protected void initInspectGroup(List<InspectGroup> inspectGroups) {
        if(mInspectGroupView != null && inspectGroups != null) {
            ArrayList<SpinnerOption> igs = new ArrayList<>();
            igs.add(new SpinnerOption("", getString(R.string.inspect_group_0)));
            for (InspectGroup ig : inspectGroups) {
                igs.add(new SpinnerOption(ig.mGroupId, ig.mGroupName));
            }

            ArrayAdapter<SpinnerOption> igAdapter = new ArrayAdapter<>(BaseActivity.this, android.R.layout.simple_spinner_item, igs);
            igAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mInspectGroupView.setAdapter(igAdapter);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    protected void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    protected void setBackgroundColor(View view, int position) {
        if(view != null) {
            if (position % 2 == 1) {
                view.setBackgroundResource(R.color.colorListItem);
            } else {
                view.setBackgroundResource(R.color.colorWhite);
            }
        }
    }

    protected String getInspectGroup() {
        if(mInspectGroupView != null) {
            Object selectedItem = mInspectGroupView.getSelectedItem();
            if(selectedItem != null) {
                return ((SpinnerOption) selectedItem).getValue();
            }
        }

        return "";
    }
}