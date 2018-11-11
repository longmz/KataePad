package com.katae.pad.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.katae.pad.R;
import com.katae.pad.bean.BaseInspect;
import com.katae.pad.bean.InspectItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longmz
 * 2018/6/10
 */
public class EditAbleExpandAdapter extends BaseExpandableListAdapter{

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

    public interface EditAbleExpandAdapterListener{
        void onEditTextChanged(int groupPosition, int childPosition, String value);
        void onInspectChecked(int groupPosition, int childPosition);
        void onClickComment(int groupPosition, int childPosition);
        void onSelectPic(int groupPosition, int childPosition);
    }

    private EditAbleExpandAdapterListener mListener;
    public EditAbleExpandAdapter(Context context, EditAbleExpandAdapterListener listener){
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public int getGroupCount()
    {
        return mDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDatas.get(groupPosition).mItems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDatas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDatas.get(groupPosition).mItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     *
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded 该组是展开状态还是伸缩状态
     * @param convertView 重用已有的视图对象
     * @param parent 返回的视图对象始终依附于的视图组
     * @return convertView
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, View,
     *      ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inspect_group_content, null);
            groupHolder = new GroupHolder();
            groupHolder.mView = convertView.findViewById(R.id.group_layout);
            groupHolder.mInspectName = convertView.findViewById(R.id.inspect_name);
            groupHolder.mInspectDesc = convertView.findViewById(R.id.inspect_desc);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder)convertView.getTag();
        }

        if (!isExpanded) {
            if (groupPosition % 2 == 1) {
                groupHolder.mView.setBackgroundResource(R.color.colorListItem);
            } else {
                groupHolder.mView.setBackgroundResource(R.color.colorWhite);
            }
            groupHolder.mInspectName.setTextColor(Color.DKGRAY);
            groupHolder.mInspectDesc.setTextColor(Color.DKGRAY);
        } else {
            groupHolder.mView.setBackgroundResource(R.color.colorPrimary);
            groupHolder.mInspectName.setTextColor(Color.WHITE);
            groupHolder.mInspectDesc.setTextColor(Color.WHITE);
        }

        BaseInspect item = mDatas.get(groupPosition);
        groupHolder.mInspectName.setText(item.mInspectName);
        groupHolder.mInspectDesc.setText(item.mInspectDesc);

        return convertView;
    }

    /**
     *
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild 子元素是否处于组中的最后一个
     * @param convertView 重用已有的视图(View)对象
     * @param parent 返回的视图(View)对象始终依附于的视图组
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, View,
     *      ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        InspectItem item = mDatas.get(groupPosition).mItems.get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inspect_item_content, null);
            itemHolder = new ItemHolder(convertView);
            itemHolder.mView = convertView.findViewById(R.id.item_layout);
            itemHolder.setContent(groupPosition, childPosition, item);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder)convertView.getTag();
            itemHolder.setContent(groupPosition, childPosition, item);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class GroupHolder {
        View mView;
        private TextView mInspectName;
        private TextView mInspectDesc;
    }

    public class ItemHolder {
        View mView;
        private TextView tvItemName;
        private EditText etItemValue;
        private TextView tvItemUnit;

        private RadioGroup rgInspectCheck;
        private RadioButton rbCheck0;
        private RadioButton rbCheck1;

        private LinearLayout layoutButton;
        private ImageView ivComment;
        private ImageView ivCamera;

        private TxtWatcher mTxtWatcher;

        public ItemHolder(View itemView) {
            tvItemName = itemView.findViewById(R.id.item_name);
            etItemValue = itemView.findViewById(R.id.item_value);
            tvItemUnit = itemView.findViewById(R.id.item_unit);

            rgInspectCheck = itemView.findViewById(R.id.inspect_check);
            rbCheck0 = itemView.findViewById(R.id.radio_check0);
            rbCheck1 = itemView.findViewById(R.id.radio_check1);

            layoutButton = itemView.findViewById(R.id.layout_button);
            ivComment = itemView.findViewById(R.id.img_comment);
            ivCamera = itemView.findViewById(R.id.img_camera);

            mTxtWatcher = new TxtWatcher();
        }

        public void setContent(final int groupPosition, final int childPosition, final InspectItem data){
            tvItemName.setText(data.mItemName);

            if(data.mItemUnit == null || data.mItemUnit.trim().length() == 0 ||
                    data.mItemUnit.trim().toLowerCase().equals("null")) {
                tvItemUnit.setVisibility(View.GONE);
            } else {
                tvItemUnit.setText(data.mItemUnit);
            }

            BaseInspect groupData = mDatas.get(groupPosition);

            switch (groupData.mValueType) {
                case "EditText":
                    etItemValue.setVisibility(View.VISIBLE);
                    etItemValue.setText(data.mInspectResult);
                    etItemValue.setHint(data.mValueHint);

                    mTxtWatcher.buildWatcher(groupPosition, childPosition);
                    etItemValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus){
                                etItemValue.addTextChangedListener(mTxtWatcher);
                            } else {
                                etItemValue.removeTextChangedListener(mTxtWatcher);
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
                            mListener.onInspectChecked(groupPosition, childPosition);
                        }
                    });
                    break;
                default:
                    break;
            }

            if(groupData.mShowButton) {
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
                            mListener.onClickComment(groupPosition, childPosition);
                        }
                    }
                });

                ivCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (mListener != null) {
                            mListener.onSelectPic(groupPosition, childPosition);
                        }
                    }
                });
            }
        }
    }


    public class TxtWatcher implements TextWatcher{

        private int mGroupPosition;
        private int mChildPosition;

        public void buildWatcher(int groupPosition, int childPosition){
            this.mGroupPosition = groupPosition;
            this.mChildPosition = childPosition;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0) {
                if(mListener != null) {
                    mListener.onEditTextChanged(mGroupPosition, mChildPosition, s.toString());
                }
            } else {
                if(mListener != null) {
                    mListener.onEditTextChanged(mGroupPosition, mChildPosition,"");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}