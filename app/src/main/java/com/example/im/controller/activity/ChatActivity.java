package com.example.im.controller.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.example.im.R;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.widget.EaseTitleBar;

//会话详情页面
public class ChatActivity extends FragmentActivity {

    private String mHxid;
    private EaseChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();

        initView();
    }

    private void initView() {
        //设置标题栏
        EaseTitleBar titleBar = findViewById(R.id.chat_title_bar);
        //设置标题为当前点击的用户
        Intent intent = getIntent();
        titleBar.setTitle(intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID));
        //设置右侧图标
        //titleBar.setRightImageResource(R.drawable.chat_user_info);
        //设置左侧图标
<<<<<<< HEAD
        titleBar.setLeftImageResource(R.drawable.ease_mm_title_back);
=======
        //titleBar.setLeftImageResource(R.drawable.ease_mm_title_back);

>>>>>>> 7b93d61ea86156112d27266527557b6604e53c1c
    }


    private void initData() {
        //创建一个会话的Fragment
        chatFragment = new EaseChatFragment();

        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);

        chatFragment.setArguments(getIntent().getExtras());

        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat, chatFragment).commit();
    }


}