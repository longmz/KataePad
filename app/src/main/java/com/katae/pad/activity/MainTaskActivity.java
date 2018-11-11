package com.katae.pad.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.katae.pad.R;
import com.katae.pad.fragment.CompTaskFragment;
import com.katae.pad.fragment.TodoTaskFragment;

/**
 * An activity representing a single User task screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainTaskActivity}.
 */
public class MainTaskActivity extends BaseActivity {

    private static final String TODO_TAB = "TODO_TASK_FRAGMENT";
    private static final String COMP_TAB = "COMP_TASK_FRAGMENT";

    private static TabHost mTabHostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.mLayoutView = R.layout.activity_main_task;
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(mAppName);

        mTabHostView = findViewById(R.id.user_task_tabhost);
        mTabHostView.setup();

        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        mTabHostView.addTab(mTabHostView.newTabSpec("tabTodo").setIndicator(getString(R.string.title_todo_task)).setContent(R.id.todo_task_container));
        mTabHostView.addTab(mTabHostView.newTabSpec("tabComp").setIndicator(getString(R.string.title_comp_task)).setContent(R.id.comp_task_container));

        // 修改标签字体样式
        TextView tabTextView = mTabHostView.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tabTextView.setTextSize(20);
        tabTextView = mTabHostView.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tabTextView.setTextSize(20);

        mTabHostView.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabHostView.setCurrentTab(0);
            }
        });

        mTabHostView.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabHostView.setCurrentTab(1);
            }
        });

        if(savedInstanceState == null) {
            Bundle argments = new Bundle();
            argments.putString(BaseActivity.ARG_APP_ID, mAppId);
            argments.putString(BaseActivity.ARG_APP_NAME, mAppName);

            TodoTaskFragment todoFragment = new TodoTaskFragment();
            todoFragment.setArguments(argments);
            CompTaskFragment compFragment = new CompTaskFragment();
            compFragment.setArguments(argments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.todo_task_container, todoFragment, TODO_TAB)
                    .add(R.id.comp_task_container, compFragment, COMP_TAB)
                    .commit();
        }
    }
}
