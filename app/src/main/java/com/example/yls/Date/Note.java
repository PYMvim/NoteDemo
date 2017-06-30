package com.example.yls.Date;

import java.io.File;

import cn.bmob.v3.BmobObject;

/**
 * Created by Min on 2017-06-24.
 */

public class Note extends BmobObject {
    private String title;
    private String Date_Time;
    private String weather;
    private String NoteClass;
    private String content;
    private String Multi_Media;
    private String BMOB_ID;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getBMOB_ID() {
        return BMOB_ID;
    }

    public void setBMOB_ID(String BMOB_ID) {
        this.BMOB_ID = BMOB_ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_Time() {
        return Date_Time;
    }

    public void setDate_Time(String date_Time) {
        Date_Time = date_Time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getNoteClass() {
        return NoteClass;
    }

    public void setNoteClass(String noteClass) {
        NoteClass = noteClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMulti_Media() {
        return Multi_Media;
    }

    public void setMulti_Media(String multi_Media) {
        Multi_Media = multi_Media;
    }
}
