package com.katae.pad.fragment;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.katae.pad.R;
import com.katae.pad.Service.MessengerService;
import com.katae.pad.activity.BaseActivity;
import com.katae.pad.activity.InspectExpandActivity;
import com.katae.pad.activity.InspectRecyclerActivity;
import com.katae.pad.bean.ApiResult;
import com.katae.pad.bean.BaseInspect;
import com.katae.pad.bean.LoginContent;
import com.katae.pad.bean.Task;
import com.katae.pad.bean.TaskContent;
import com.katae.pad.utils.NetWebApi;
import com.katae.pad.utils.NetworkCheck;
import com.katae.pad.utils.SysAction;
import com.katae.pad.utils.RecyclerViewHolder;

import java.util.List;

/**
 * A fragment representing a user to-do list screen.
 * This fragment is either contained in a {@link com.katae.pad.activity.MainTaskActivity}
 * in two-pane mode (on tablets) or a {@link com.katae.pad.activity.MainTaskActivity}
 * on handsets.
 */
public class TodoTaskFragment extends BaseFragment {

    private RecyclerViewAdapter mRecyclerViewAdapter;

    private HttpTask mHttpTask = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoTaskFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.mLayoutView = R.layout.user_task;
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mTaskNumView = rootView.findViewById(R.id.textView);

        mLocalDataSync = rootView.findViewById(R.id.fab);
        mLocalDataSync.setVisibility(View.VISIBLE);
        mLocalDataSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle(getString(R.string.prompt_sync_title));
                builder.setMessage(getString(R.string.prompt_sync_message));

                //监听下方button点击事件
                builder.setPositiveButton(getString(R.string.prompt_button_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mHttpTask == null) {
                            showProgress(true);
                            mHttpTask = new HttpTask(SysAction.sync);
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
        });

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

        // bind service
        /*updateCount();
        mActivityMessenger = new Messenger(mMessengerHandler);
        intent = new Intent(getContext(), MessengerService.class);
        startMessengerMethod();*/

        return rootView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewAdapter = new RecyclerViewAdapter(TaskContent.TODO_TASK_LIST);
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
                    Task task = TaskContent.TODO_TASK_MAP.get(taskId);

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

    // region Messager

    private int mCount = 0;

    Intent intent;

    Messenger mServiceMessenger;
    Messenger mActivityMessenger;
    TextView mTaskNumView;

    private boolean isMessengerServiceConnected = false;


    private void startMessengerMethod() {
        getActivity().bindService(intent, messengerServiceConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 刷新数字
     */
    private void updateCount() {
        //由于从binder调用回来是在子线程里，需要post到主线程调用
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTaskNumView.setText(Integer.toString(mCount));
            }
        });
    }

    private void stopAll() {
        if (isMessengerServiceConnected) {
            getActivity().unbindService(messengerServiceConnection);
            isMessengerServiceConnected = false;
        }
    }

    @Override
    public void onDestroy() {
        stopAll();
        super.onDestroy();
    }

    //messenger使用
    private ServiceConnection messengerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isMessengerServiceConnected = true;

            mServiceMessenger = new Messenger(service);

            Message message = Message.obtain();
            message.replyTo = mActivityMessenger;

            try {
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Handler mMessengerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCount = msg.arg1;
            updateCount();
            super.handleMessage(msg);
        }
    };


    // endregion

    private class HttpTask extends AsyncTask<Void, Void, Boolean> {

        private final int mAction;
        private String mErrorMessage = "Error";

        HttpTask(int action) {
            mAction = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Context context = getContext();

            if(mAction == SysAction.load || mAction == SysAction.refresh) {
                ApiResult<List<Task>> getResult;
                if(NetworkCheck.isNetworkConnected(context)) {
                    getResult = NetWebApi.getUnfinishedTasks(LoginContent.UserId);
                } else {
                    getResult = NetWebApi.getLocalUnfinishedTasks(LoginContent.Account, "a01", context);
                }

                if(getResult.mErrorCode == 0) {
                    TaskContent.initTodoList(getResult.mResult);
                    return true;
                } else {
                    mErrorMessage = getResult.mErrorMessage;
                }
            } else if(mAction == SysAction.sync) {
                if(NetworkCheck.isNetworkConnected(context)) {
                    NetWebApi.saveLocalUnfinishedTasks(TaskContent.TODO_TASK_LIST, LoginContent.Account, "a01", context);

                    ApiResult<List<BaseInspect>> inspectResult;
                    for (Task item : TaskContent.TODO_TASK_LIST) {
                        inspectResult = NetWebApi.getInspectList(item.mTaskTypeNo, item.mTaskId);
                        if(inspectResult.mErrorCode == 0) {
                            NetWebApi.saveLocalInspectList(inspectResult.mResult, LoginContent.Account, "a01", item.mTaskId, context);
                        }
                    }
                    return true;
                } else {
                    mErrorMessage = getString(R.string.error_network_message);
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
                } else if(mAction == SysAction.sync) {
                    Toast.makeText(getContext(), getString(R.string.prompt_success_sync), Toast.LENGTH_SHORT).show();
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
