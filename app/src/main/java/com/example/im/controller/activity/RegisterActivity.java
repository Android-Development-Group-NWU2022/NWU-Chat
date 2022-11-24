package com.example.im.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_login_name;
    private EditText et_login_pwd;
    private EditText et_login_pwd_dup;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_login_name=(EditText) findViewById(R.id.user_input);
        et_login_pwd = (EditText) findViewById(R.id.password_input);
        et_login_pwd_dup = (EditText) findViewById(R.id.duplicate_password_input);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button register_btn = findViewById(R.id.register_btn);


        //注册按钮的点击事件处理
        register_btn.setOnClickListener(v -> regist());
    }
    //注册的业务逻辑处理
    private void regist() {
        // 获取输入的用户名和密码
        String registName=et_login_name.getText().toString();
        String registPwd = et_login_pwd.getText().toString();
        String registPwdDup = et_login_pwd_dup.getText().toString();

        //2 校验输入的用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)||TextUtils.isEmpty(registPwdDup)) {
            Toast.makeText(RegisterActivity.this,"输入的用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //3 校验输入的密码
        if (!registPwd.equals(registPwdDup)) {
            Toast.makeText(RegisterActivity.this,"输入的密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        //4 去服务器注册
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //去环信服务器注册账号
                    EMClient.getInstance().createAccount(registName,registPwd);

                    //更新页面显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,"注册失败"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}