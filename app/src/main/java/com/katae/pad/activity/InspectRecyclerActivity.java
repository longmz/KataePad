package com.katae.pad.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.katae.pad.R;
import com.katae.pad.bean.ApiResult;
import com.katae.pad.bean.BaseInspect;
import com.katae.pad.bean.InspectContent;
import com.katae.pad.bean.LoginContent;
import com.katae.pad.utils.EditAbleListAdapter;
import com.katae.pad.utils.NetWebApi;
import com.katae.pad.utils.NetworkCheck;
import com.katae.pad.utils.SpinnerOption;
import com.katae.pad.utils.SysAction;
import com.katae.pad.utils.UploadFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link InspectRecyclerActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class InspectRecyclerActivity extends BaseActivity implements View.OnClickListener,
        EditAbleListAdapter.EditAbleListAdapterListener, UploadFile.OnUploadProcessListener {

    private EditAbleListAdapter mRecyclerViewAdapter;

    private Button mBackButton;
    private Button mSaveButton;
    private Button mCommitButton;

    private HttpTask mHttpTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.mLayoutView = R.layout.activity_inspect_recycler;
        super.onCreate(savedInstanceState);

        InspectContent.clearList();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(mTaskName);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mHttpTask == null) {
                    mHttpTask = new HttpTask(SysAction.refresh, getInspectGroup());
                    mHttpTask.execute((Void) null);
                }
            }
        });

        mInspectGroupView.setSelection(0, true);
        mInspectGroupView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupId = ((SpinnerOption) parent.getItemAtPosition(position)).getValue();
                mRecyclerViewAdapter.refreshDatas(InspectContent.INSPECT_LIST, groupId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.inspect_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(this);
        mSaveButton = findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(this);
        mCommitButton = findViewById(R.id.commit_button);
        mCommitButton.setOnClickListener(this);

        //mUploadImageResult = findViewById(R.id.upload_result);
        mProgressBar = findViewById(R.id.load_progress);
        mProgressDialog = new ProgressDialog(this);

        if(mHttpTask == null) {
            showProgress(true);
            mHttpTask = new HttpTask(SysAction.load, "");
            mHttpTask.execute((Void) null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                if(mRecyclerViewAdapter.getItemCount() > 0) {
                    if (mHttpTask == null) {
                        showProgress(true);
                        mHttpTask = new HttpTask(SysAction.save, getInspectGroup());
                        mHttpTask.execute((Void) null);
                    }
                }
                break;
            case R.id.commit_button:
                if(mRecyclerViewAdapter.getItemCount() > 0) {
                    if (mHttpTask == null) {
                        mCommitButton.setEnabled(false);
                        showProgress(true);
                        mHttpTask = new HttpTask(SysAction.submit, getInspectGroup());
                        mHttpTask.execute((Void) null);
                    }
                }
                break;
            case R.id.back_button:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onEditTextChanged(int position, String value) {
        BaseInspect item = InspectContent.INSPECT_LIST.get(position);
        item.mInspectResult = value;
    }

    @Override
    public void onInspectChecked(int position) {
        final BaseInspect item = InspectContent.INSPECT_LIST.get(position);

        View viewDialog = View.inflate(this, R.layout.comment_input_layout, null);
        final EditText commentTextView = viewDialog.findViewById(R.id.input_comment);
        commentTextView.setText(item.mInconformityDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewDialog);

        //监听下方button点击事件
        builder.setPositiveButton(getString(R.string.prompt_button_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                item.mInconformityDesc = commentTextView.getText().toString();
            }
        });
        builder.setNegativeButton(getString(R.string.prompt_button_cancel), null);

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClickComment(int position) {
        final BaseInspect item = InspectContent.INSPECT_LIST.get(position);

        View viewDialog = View.inflate(this, R.layout.comment_input_layout, null);
        final EditText commentTextView = viewDialog.findViewById(R.id.input_comment);
        commentTextView.setText(item.mComment);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewDialog);

        //监听下方button点击事件
        builder.setPositiveButton(getString(R.string.prompt_button_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                item.mComment = commentTextView.getText().toString();
            }
        });
        builder.setNegativeButton(getString(R.string.prompt_button_cancel), null);

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSelectPic(int position) {
        mPicPosition = position;
        BaseInspect item = InspectContent.INSPECT_LIST.get(position);
        Intent intent = new Intent(this, SelectPicActivity.class);

        if(item.mPictures != null && item.mPictures.length() > 0) {
            intent.putExtra(SelectPicActivity.KEY_PHOTO_PATH, item.mPictures);
        }

        startActivityForResult(intent, TO_SELECT_PHOTO);
    }

    @Override
    public void onResume() {
        super.onResume();

        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerViewAdapter = new EditAbleListAdapter(this,this);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.refreshDatas(InspectContent.INSPECT_LIST, "");
    }

    private class HttpTask extends AsyncTask<Void, Void, Boolean> {

        private final Integer mAction;
        private final String mInspectGroup;
        private String mErrorMessage = "Error";

        HttpTask(Integer action, String inspectGroup) {
            mAction = action;
            mInspectGroup = inspectGroup;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(mAction == SysAction.load || mAction == SysAction.refresh) {
                ApiResult<List<BaseInspect>> result;

                if(NetworkCheck.isNetworkConnected(mBackButton.getContext())) {
                    result = NetWebApi.getInspectList(mTaskType, mTaskId);
                } else {
                    result = NetWebApi.getLocalInspectList(mTaskId, mBackButton.getContext());
                }
                if(result.mErrorCode == 0) {
                    InspectContent.initList(result.mResult);
                    return true;
                } else {
                    mErrorMessage = result.mErrorMessage;
                }
            } else if(mAction == SysAction.save) {
                //ApiResult<Integer> saveResult = NetWebApi.saveInspect(mTaskType, InspectContent.INSPECT_LIST, mInspectGroup);
                ApiResult<Integer> saveResult = NetWebApi.updateLocalInspectList(InspectContent.INSPECT_LIST, mSaveButton.getContext());
                if (saveResult.mErrorCode == 0) {
                    return true;
                } else {
                    this.mErrorMessage = saveResult.mErrorMessage;
                }
            } else if(mAction == SysAction.submit) {
                ApiResult<Integer> submitResult = NetWebApi.commitInspect(mTaskType, InspectContent.INSPECT_LIST, mInspectGroup);
                if (submitResult.mErrorCode == 0) {
                    ApiResult<Integer> deleteResult = NetWebApi.deleteLocalInspectList(InspectContent.INSPECT_LIST, mCommitButton.getContext());
                    /*if(deleteResult.mErrorCode == 0) {
                        return true;
                    } else {
                        this.mErrorMessage = deleteResult.mErrorMessage;
                    }*/
                    return true;
                } else {
                    this.mErrorMessage = submitResult.mErrorMessage;
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
                    mRecyclerViewAdapter.refreshDatas(InspectContent.INSPECT_LIST, "");
                    initInspectGroup(InspectContent.GROUP_LIST);
                    if(mAction == SysAction.refresh) {
                        Toast.makeText(InspectRecyclerActivity.this, getString(R.string.prompt_success_refresh), Toast.LENGTH_SHORT).show();
                    }
                } else if(mAction == SysAction.save) {
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    Toast.makeText(InspectRecyclerActivity.this, getString(R.string.prompt_success_save), Toast.LENGTH_SHORT).show();
                } else if(mAction == SysAction.submit) {
                    mCommitButton.setEnabled(true);
                    Toast.makeText(InspectRecyclerActivity.this, getString(R.string.prompt_success_submit), Toast.LENGTH_SHORT).show();

                    InspectContent.clearList();
                    //mRecyclerViewAdapter.notifyDataSetChanged();
                    mRecyclerViewAdapter.refreshDatas(InspectContent.INSPECT_LIST, "");
                }
            } else {
                Toast.makeText(InspectRecyclerActivity.this, this.mErrorMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mHttpTask = null;
            showProgress(false);
        }
    }


    // region 选择图片
    private static final String TAG = "uploadImage";

    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;

    private TextView mUploadImageResult;
    private ProgressBar mProgressBar;
    private String mPicPath = null;
    private int mPicPosition;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == TO_SELECT_PHOTO) {
            mPicPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Log.i(TAG, "最终选择的图片=" + mPicPath);

            BaseInspect item = InspectContent.INSPECT_LIST.get(mPicPosition);
            item.mPictures = mPicPath;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 上传服务器响应回调
     */
    @Override
    public void onUploadDone(int responseCode, String message) {
        mProgressDialog.dismiss();
        Message msg = Message.obtain();
        msg.what = UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    String requestURL = "";
    private void toUploadFile() {
        mUploadImageResult.setText("正在上传中...");
        mProgressDialog.setMessage("正在上传文件...");
        mProgressDialog.show();
        String fileKey = "pic";
        UploadFile uploadFile = UploadFile.getInstance();;
        uploadFile.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", "11111");
        uploadFile.uploadFile(mPicPath, fileKey, requestURL, params);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    toUploadFile();
                    break;
                case UPLOAD_INIT_PROCESS:
                    mProgressBar.setMax(msg.arg1);
                    break;
                case UPLOAD_IN_PROCESS:
                    mProgressBar.setProgress(msg.arg1);
                    break;
                case UPLOAD_FILE_DONE:
                    String result = "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj + "\n耗时：" +
                            UploadFile.getRequestTime() + "秒";
                    mUploadImageResult.setText(result);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        handler.sendMessage(msg );
    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        handler.sendMessage(msg );
    }

    // end region
}
