package com.example.im.controller.activity;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.im.R;
import com.example.im.controller.fragment.ChatFragment;
import com.example.im.controller.fragment.ContractListFragment;
import com.example.im.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {
    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContractListFragment contractListFragment;
    private SettingFragment settingFragment;
    private RadioButton rb_main_chat;
    private RadioButton rb_main_contact;
    private RadioButton rb_main_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initData();

        initListener();
    }

    private void initListener() {
        //RadioGroup的现在事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment=null;
                switch (checkedId) {
                    //会话列表页面
                    case R.id.rb_main_chat:
                        fragment = chatFragment;

                        break;
                    //联系人列表页面
                    case R.id.rb_main_contact:
                        fragment = contractListFragment;
                        break;
                    //设置页面
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }
                //实现fragment 的切换
                switchFragment(fragment);
            }
        });
        //默认会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }
    //实现fragment切换的方法
    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }

    private void initData() {
        //创建三个fragment对象
        chatFragment = new ChatFragment();
        contractListFragment = new ContractListFragment();
        settingFragment = new SettingFragment();
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        rg_main=(RadioGroup)findViewById(R.id.rg_main);
        rb_main_chat=findViewById(R.id.rb_main_chat);
        rb_main_contact=findViewById(R.id.rb_main_contact);
        rb_main_setting=findViewById(R.id.rb_main_setting);

        // 加载icon字体文件
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        rb_main_chat.setTypeface(iconfont);
        rb_main_contact.setTypeface(iconfont);
        rb_main_setting.setTypeface(iconfont);

    }
}
