package com.katae.pad.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.katae.pad.R;
import com.katae.pad.activity.BaseActivity;

/**
 * A fragment representing a single Pte detail screen.
 * This fragment is either contained in a {@link com.katae.pad.activity.MainActivity}
 * in two-pane mode (on tablets) or a {@link com.katae.pad.activity.MainActivity}
 * on handsets.
 */
public class BaseFragment extends Fragment {

    protected String mAppId;
    protected String mAppName;
    protected String mTaskId;
    protected String mTaskName;

    protected int mLayoutView;
    protected View mProgressView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected View mLocalDataSync;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BaseFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(BaseActivity.ARG_APP_ID)) {
            mAppId = getArguments().getString(BaseActivity.ARG_APP_ID);
        } else {
            mAppId = "";
        }

        if (getArguments().containsKey(BaseActivity.ARG_APP_NAME)) {
            mAppName = getArguments().getString(BaseActivity.ARG_APP_NAME);
        } else {
            mAppName = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(this.mLayoutView, container, false);

        mProgressView = rootView.findViewById(R.id.load_progress);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        }

        return rootView;
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
}
