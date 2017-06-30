package com.example.yls.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by yls on 2017/5/11.
 */
public class MyUser extends BmobObject {
    private String username;
    private boolean IsLogin;
    private String lastLoginTime;

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLogin() {
        return IsLogin;
    }

    public void setLogin(boolean login) {
        IsLogin = login;
    }
}
