package com.katae.pad.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.katae.pad.R;
import com.katae.pad.activity.BaseActivity;
import com.katae.pad.activity.InspectExpandActivity;
import com.katae.pad.activity.InspectRecyclerActivity;
import com.katae.pad.bean.ApiResult;
import com.katae.pad.bean.LoginContent;
import com.katae.pad.bean.Task;
import com.katae.pad.bean.TaskContent;
import com.katae.pad.utils.NetWebApi;
import com.katae.pad.utils.NetworkCheck;
import com.katae.pad.utils.SysAction;
import com.katae.pad.utils.RecyclerViewHolder;
import java.util.List;

/**
 * A fragment representing a single Pte detail screen.
 * This fragment is either contained in a {@link com.katae.pad.activity.MainTaskActivity}
 * in two-pane mode (on tablets) or a {@link com.katae.pad.activity.MainTaskActivity}
 * on handsets.
 */
public class CompTaskFragment extends BaseFragment {

    private RecyclerViewAdapter mRecyclerViewAdapter;

    private HttpTask mHttpTask = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CompTaskFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.mLayoutView = R.layout.user_task;
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        mLocalDataSync = rootView.findViewById(R.id.fab);
        mLocalDataSync.setVisibility(View.GONE);

        RecyclerView recyclerView = rootView.findViewById(R.id.task_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mHttpTask == null) {
                    showProgress(true);
                    mHttpTask = new HttpTask(SysAction.refresh);
                    mHttpTask.execute((Void) null);
                }
            }
        });

        if(mHttpTask == null) {
            showProgress(true);
            mHttpTask = new HttpTask(SysAction.load);
            mHttpTask.execute((Void) null);
        }

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewAdapter = new RecyclerViewAdapter(TaskContent.COMP_TASK_LIST);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private final List<Task> mValues;

        RecyclerViewAdapter(List<Task> items) {
            mValues = items;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_task_content, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
            //setBackgroundColor(holder.mView, position);

            Task item = mValues.get(position);
            holder.setText(R.id.task_name, item.mTaskTypeName)
                    .setText(R.id.task_id, item.mTaskId);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tvTaskId = holder.mView.findViewById(R.id.task_id);
                    String taskId = tvTaskId.getText().toString();
                    Task task = TaskContent.COMP_TASK_MAP.get(taskId);

                    Context context = getContext();
                    Intent intent = task.getInspectIntent(context);
                    intent.putExtra(BaseActivity.ARG_APP_ID, mAppId);
                    intent.putExtra(BaseActivity.ARG_APP_NAME, mAppName);
                    intent.putExtra(BaseActivity.ARG_TASK_ID, taskId);
                    intent.putExtra(BaseActivity.ARG_TASK_NAME, task.mTaskTypeName);
                    intent.putExtra(BaseActivity.ARG_TASK_TYPE, task.mTaskTypeNo);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

    private class HttpTask extends AsyncTask<Void, Void, Boolean> {

        private final int mAction;
        private String mErrorMessage = "Error";

        HttpTask(int action) {
            mAction = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(mAction == SysAction.load || mAction == SysAction.refresh) {
                if(NetworkCheck.isNetworkConnected(getContext())) {
                    ApiResult<List<Task>> getResult = NetWebApi.getFinishedTasks(LoginContent.UserId, "2018-01-01", "2018-12-31");
                    if(getResult.mErrorCode == 0) {
                        TaskContent.initCompList(getResult.mResult);
                        return true;
                    } else {
                        mErrorMessage = getResult.mErrorMessage;
                    }
                } else {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mHttpTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            showProgress(false);

            if (success) {
                if(mAction == SysAction.load || mAction == SysAction.refresh) {
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    if (mAction == SysAction.refresh) {
                        Toast.makeText(getContext(), getString(R.string.prompt_success_refresh), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), mErrorMessage, Toast.LENGTH_LONG).show();
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
