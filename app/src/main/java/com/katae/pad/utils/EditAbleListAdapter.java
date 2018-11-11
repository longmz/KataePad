package com.katae.pad.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.katae.pad.bean.BaseInspect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.katae.pad.R;

/**
 * Created by longmz
 * 2018/6/10
 */
public class EditAbleListAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<BaseInspect> mDatas = new ArrayList<>();

    public void refreshDatas(List<BaseInspect> items, String groupId) {
        mDatas.clear();
        if(groupId == null || groupId.length() == 0) {
            mDatas.addAll(items);
        } else {
            for (BaseInspect item : items) {
                if(item.mGroupId.equals(groupId)) {
                    mDatas.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface EditAbleListAdapterListener{
        void onEditTextChanged(int position, String value);
        void onInspectChecked(int position);
        void onClickComment(int position);
        void onSelectPic(int position);
    }

    private EditAbleListAdapterListener mListener;
    public EditAbleListAdapter(Context context, EditAbleListAdapterListener listener){
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EditAbleListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.inspect_list_content, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EditAbleListViewHolder)holder).setContent(position, mDatas.get(position));
        if (position % 2 == 1) {
            holder.itemView.setBackgroundResource(R.color.colorListItem);
        } else {
            holder.itemView.setBackgroundResource(R.color.colorWhite);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class EditAbleListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvInspectName;
        private TextView tvInspectDesc;
        private EditText etInspectValue;

        private RadioGroup rgInspectCheck;
        private RadioButton rbCheck0;
        private RadioButton rbCheck1;

        private RadioGroup rgInspectSelect;
        private RadioButton rbSelect0;
        private RadioButton rbSelect1;
        private RadioButton rbSelect2;

        private LinearLayout layoutButton;
        private ImageView ivComment;
        private ImageView ivCamera;

        private TxtWatcher mTxtWatcher;

        public EditAbleListViewHolder(View itemView) {
            super(itemView);

            tvInspectName = itemView.findViewById(R.id.inspect_name);
            tvInspectDesc = itemView.findViewById(R.id.inspect_desc);
            etInspectValue = itemView.findViewById(R.id.inspect_value);

            rgInspectCheck = itemView.findViewById(R.id.inspect_check);
            rbCheck0 = itemView.findViewById(R.id.radio_check0);
            rbCheck1 = itemView.findViewById(R.id.radio_check1);

            rgInspectSelect = itemView.findViewById(R.id.inspect_select);
            rbSelect0 = itemView.findViewById(R.id.radio_select0);
            rbSelect1 = itemView.findViewById(R.id.radio_select1);
            rbSelect2 = itemView.findViewById(R.id.radio_select2);

            layoutButton = itemView.findViewById(R.id.layout_button);
            ivComment = itemView.findViewById(R.id.img_comment);
            ivCamera = itemView.findViewById(R.id.img_camera);

            mTxtWatcher = new TxtWatcher();
        }

        public void setContent(final int position, final BaseInspect data){
            tvInspectName.setText(data.mInspectName);

            if(data.mInspectDesc == null || data.mInspectDesc.trim().length() == 0 ||
                    data.mInspectDesc.trim().toLowerCase().equals("null")) {
                tvInspectDesc.setVisibility(View.GONE);
            } else {
                tvInspectDesc.setText(data.mInspectDesc);
            }

            switch (data.mValueType) {
                case "EditText":
                    etInspectValue.setVisibility(View.VISIBLE);
                    etInspectValue.setText(data.mInspectResult);

                    if(data.mShowButton) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(etInspectValue.getLayoutParams());
                        lp.weight = 2.0f;
                        lp.setMargins(8, 8, 8, 8);
                        lp.gravity = Gravity.CENTER_VERTICAL;
                        etInspectValue.setLayoutParams(lp);
                    }

                    mTxtWatcher.buildWatcher(position);
                    etInspectValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus){
                                etInspectValue.addTextChangedListener(mTxtWatcher);
                            } else {
                                etInspectValue.removeTextChangedListener(mTxtWatcher);
                            }
                        }
                    });
                    break;
                case "Check":
                    rgInspectCheck.setVisibility(View.VISIBLE);
                    rgInspectCheck.setOnCheckedChangeListener(null);

                    if(data.mInspectResult != null) {
                        if (data.mInspectResult.equals("0")) {
                            rbCheck0.setChecked(true);
                        } else if (data.mInspectResult.equals("1")) {
                            rbCheck1.setChecked(true);
                        } else {
                            rgInspectCheck.clearCheck();
                        }
                    } else {
                        rgInspectCheck.clearCheck();
                    }

                    rgInspectCheck.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == rbCheck0.getId()) {
                                data.mInspectResult = "0";
                            } else if (checkedId == rbCheck1.getId()) {
                                data.mInspectResult = "1";
                            }
                        }
                    });

                    rbCheck1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onInspectChecked(position);
                        }
                    });
                    break;
                case "Select":
                    rgInspectSelect.setVisibility(View.VISIBLE);
                    rgInspectSelect.setOnCheckedChangeListener(null);

                    if(data.mInspectResult != null) {
                        if (data.mInspectResult.equals("0")) {
                            rbSelect0.setChecked(true);
                        } else if (data.mInspectResult.equals("1")) {
                            rbSelect1.setChecked(true);
                        } else if(data.mInspectResult.equals("2")){
                            rbSelect2.setChecked(true);
                        } else {
                            rgInspectSelect.clearCheck();
                        }
                    } else {
                        rgInspectSelect.clearCheck();
                    }

                    rgInspectSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == rbSelect0.getId()) {
                                data.mInspectResult = "0";
                            } else if (checkedId == rbSelect1.getId()) {
                                data.mInspectResult = "1";
                            } else if (checkedId == rbSelect2.getId()) {
                                data.mInspectResult = "2";
                            }
                        }
                    });
                    break;
                default:
                    break;
            }

            /*if(data.mComment != null && data.mComment.length() > 0) {
                ivComment.setBackgroundResource(R.color.colorAccent);
            }
            if(data.mPictures != null && data.mPictures.length() > 0) {
                ivCamera.setBackgroundResource(R.color.colorAccent);
            }*/

            if(data.mShowButton) {
                layoutButton.setVisibility(View.VISIBLE);

                if(data.mComment != null && data.mComment.length() > 0) {
                    ivComment.setBackgroundResource(R.color.colorPrimary);
                }
                if(data.mPictures != null && data.mPictures.length() > 0) {
                    ivCamera.setBackgroundResource(R.color.colorPrimary);
                }

                ivComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (mListener != null) {
                            mListener.onClickComment(position);
                        }
                    }
                });

                ivCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (mListener != null) {
                            mListener.onSelectPic(position);
                        }
                    }
                });
            }
        }
    }

    public class TxtWatcher implements TextWatcher{

        private int mPosition;

        public void buildWatcher(int position){
            this.mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0) {
                if(mListener != null) {
                    mListener.onEditTextChanged(mPosition, s.toString());
                }
            } else {
                if(mListener != null) {
                    mListener.onEditTextChanged(mPosition,"");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}