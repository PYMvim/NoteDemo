package com.example.yls.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yls.Date.MyUser;
import com.example.yls.notepaddemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_phone;
    private EditText edt_code;
    private Button btn_getcode;
    private Button btn_sumbit;
    private Button btn_linshitongdao;
    private Handler handler;
    private Runnable runnable;
    private int num = 60;
    private MyUser MYUSER = new MyUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //第一：默认初始化
        Bmob.initialize(this, "fdaefcb7f1ffd30116e3773545e9a71a");

        FindViews();

        init();

        initHandler();
    }

    private void init() {
        QueryUser();
        //判断一天内是否登录过
        initUserIsLogin();
    }

    private void initHandler() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                btn_getcode.setText(String.valueOf("("+num+"s)"+"重新获取"));
                btn_getcode.setClickable(false);
                num --;
                if(num>0){
                    handler.postDelayed(runnable,1000);
                }else{
                    btn_getcode.setText("获取验证码");
                    btn_getcode.setClickable(true);
                }
            }
        };
    }

    private void coundeDown(){
        handler.post(runnable);
    }

    private void FindViews() {
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        btn_getcode = (Button) findViewById(R.id.btn_GetCode);
        btn_sumbit = (Button) findViewById(R.id.btn_sumbit);

        btn_getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edt_phone.getText().toString();
                if(isMobileNO(phone)){
                    BmobSMS.requestSMSCode(phone,"模板名称", new QueryListener<Integer>() {

                        @Override
                        public void done(Integer smsId,BmobException ex) {
                            if(ex==null){//验证码发送成功
                                Toast.makeText(LoginActivity.this, "coundDowen send success!", Toast.LENGTH_SHORT).show();
                                Log.i("smile", "短信id："+smsId);//用于后续的查询本次短信发送状态
                            }
                        }
                    });
                    coundeDown();
                }else{
                    Toast.makeText(LoginActivity.this, "Phone is error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = edt_phone.getText().toString();
                String code = edt_code.getText().toString();
                final MyUser user1 = new MyUser();
                BmobUser.signOrLoginByMobilePhone(phone, code, new LogInListener<MyUser>() {

                    @Override
                    public void done(MyUser user, BmobException e) {
                        if(user!=null){
                            Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                            user1.setUsername(phone);
                            user1.setLogin(true);
                            SimpleDateFormat DATE_formatter = new SimpleDateFormat("yyyy.MM[dd] HH:mm:ss");
                            Date DATE_curDate = new Date(System.currentTimeMillis());//获取当前日期
                            String Now_DateTime = DATE_formatter.format(DATE_curDate);
                            user1.setLastLoginTime(Now_DateTime);
                            user1.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if(e==null){
                                        Log.e("创建数据成功：","objectId: " + objectId);
                                    }else{
                                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                    }
                                }
                            });
                            gotoMainAcitivity();
                            Log.i("smile","用户登陆成功");
                        }else{
                            user1.setLogin(false);
                            Toast.makeText(LoginActivity.this, "验证码错误 , Login Defeat!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //临时通道-----临时通道-----临时通道-----临时通道-----临时通道-----临时通道-----临时通道-----临时通道-----
        btn_linshitongdao = (Button) findViewById(R.id.btn_linshitongdao);
        btn_linshitongdao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMainAcitivity();
            }
        });
    }

    private boolean isMobileNO(String phone) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private void gotoMainAcitivity(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void initUserIsLogin() {

    }

    private void QueryUser() {
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        //查询playerName叫“比目”的数据
//        query.addWhereEqualTo("playerName", "比目");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<MyUser>() {
            public void done(List<MyUser> object, BmobException e) {
                if(e==null){
                    Log.e("Save_Result", "查询成功：共"+object.size()+"条数据。");
                    for (MyUser user : object) {
//                        //获得playerName的信息
//                        note.getPlayerName();
                        //获得数据的objectId信息
                        user.getObjectId();
                        //获取user数据
                        MYUSER = user;
                        Log.e("asdadsadad",MYUSER.getUsername());
                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
                        user.getCreatedAt();
                    }
                }else{
                    Log.e("Save_Result","查询登录状态失败");
                }
            }
        });
    }
}
