package com.example.yls.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yls.Date.Note;
import com.example.yls.notepaddemo.R;
import com.example.yls.utils.PathGetter;
import com.example.yls.utils.ThreadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CreateNoteActivity extends Activity {
    private static final int CAMERA_SUCCESS = 1001;
    private static final int PHOTO_SUCCESS = 1002;
    private EditText edtNoteTitle;
    private Button btnNoteUpdateDate;
    private Button btnNoteUpdateTime;
    private Spinner sp_weather;
    private Spinner sp_noteclass;
    private EditText edtNoteContent;
    private Button btn_OK;
    private TextView Txt_No;
    private ImageButton btn_back;
    private ImageButton btn_delete;
    private ImageButton btn_photo;
    private ImageButton btn_music;
    private ImageButton btn_video;
    private ImageButton btn_erweima;
    private Calendar calendar;
    private List<String> NoteClass_list = new ArrayList<String>();
    private List<String> weather_list = new ArrayList<String>();
    private Note note;
    private boolean IsUpdate = false;
    private Uri originalUri;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        FindViews();
        init();
    }

    private void init() {
        //第一：默认初始化
        Bmob.initialize(this, "fdaefcb7f1ffd30116e3773545e9a71a");

        //初始化记事本
        note = new Note();

        SimpleDateFormat DATE_formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date DATE_curDate = new Date(System.currentTimeMillis());//获取当前日期
        String DATE_nowDate = DATE_formatter.format(DATE_curDate);
        btnNoteUpdateDate.setText(DATE_nowDate);

        SimpleDateFormat TIME_formatter = new SimpleDateFormat("HH:mm:ss");
        Date TIME_curDate = new Date(System.currentTimeMillis());//获取当前时间
        String TIME_nowDate = TIME_formatter.format(TIME_curDate);
        btnNoteUpdateTime.setText(TIME_nowDate);

        //初始化日历
        calendar = Calendar.getInstance();

        //点击某个记事时，加载数据
        String NOTE_TITLE = getIntent().getStringExtra("Note_title");
        String NOTE_CONTENT = getIntent().getStringExtra("Note_content");
        String NOTE_DATE_TIME = getIntent().getStringExtra("Note_DateTime");
        String NOTE_WEATHER = getIntent().getStringExtra("Note_weather");
        String NOTE_CLASS = getIntent().getStringExtra("Note_class");
        String NOTE_BMOB_ID = getIntent().getStringExtra("Note_BmobId");
        note.setBMOB_ID(NOTE_BMOB_ID);
        IsUpdate = getIntent().getBooleanExtra("IsUpdate",false);

        if (!TextUtils.isEmpty(NOTE_TITLE)) {
            edtNoteTitle.setText(NOTE_TITLE);
        }
        if (!TextUtils.isEmpty(NOTE_CONTENT)) {
            edtNoteContent.setText(NOTE_CONTENT);
        }

        //这里是获取点击的记事本的时间，不过此处不获取，将当前时间替换之前的时间作为修改时间
//        if (!TextUtils.isEmpty(NOTE_DATE_TIME)) {
//            btnNoteUpdateDate.setText(NOTE_DATE_TIME.substring(0, 11));
//            btnNoteUpdateTime.setText(NOTE_DATE_TIME.substring(11));
//        }

        if (!TextUtils.isEmpty(NOTE_WEATHER)) {
            if (WEATHER_IN_LIST_NO(NOTE_WEATHER) != -1) {
                sp_weather.setSelection(WEATHER_IN_LIST_NO(NOTE_WEATHER), true);
                //如果无修改，就继续保存原来的天气
                note.setWeather(weather_list.get(WEATHER_IN_LIST_NO(NOTE_WEATHER)));
            }
        }
        if (!TextUtils.isEmpty(NOTE_CLASS)) {
            if (NOTECLASS_IN_LIST_NO(NOTE_CLASS) != -1) {
                sp_noteclass.setSelection(NOTECLASS_IN_LIST_NO(NOTE_CLASS), true);
                //如果无修改，就继续保存原来的记事类型
                note.setNoteClass(NoteClass_list.get(NOTECLASS_IN_LIST_NO(NOTE_CLASS)));
            }
        }
    }

//计算Intent传送过来的字段在数组Noteclass_list里的位置
    private int NOTECLASS_IN_LIST_NO(String NoteClass) {
        for (int i = 0; i < NoteClass_list.size(); i++) {
            if (NoteClass_list.get(i).equals(NoteClass)) {
                return i;
            }
        }
        return -1;
    }

    //计算Intent传送过来的字段在数组weather_list里的位置
    private int WEATHER_IN_LIST_NO(String weather) {
        for (int i = 0; i < weather_list.size(); i++) {
            if (weather_list.get(i).equals(weather)) {
                return i;
            }
        }
        return -1;
    }

    private void FindViews() {
        edtNoteTitle = (EditText) findViewById(R.id.edt_noteTitle);
        btnNoteUpdateDate = (Button) findViewById(R.id.btn_noteUpdateDate);
        btnNoteUpdateTime = (Button) findViewById(R.id.btn_noteUpdateTime);
        edtNoteContent = (EditText) findViewById(R.id.edt_noteContent);
        sp_noteclass = (Spinner) findViewById(R.id.Sp_noteClass);
        sp_weather = (Spinner) findViewById(R.id.Sp_weather);
        Txt_No = (TextView) findViewById(R.id.txt_StrNo);
        btn_OK = (Button) findViewById(R.id.btn_OK);
        btn_back = (ImageButton) findViewById(R.id.CreateNote_back);
        btn_delete = (ImageButton) findViewById(R.id.CreateNote_Delete);
        btn_photo = (ImageButton) findViewById(R.id.btn_photo);
        btn_music = (ImageButton) findViewById(R.id.btn_music);
        btn_video = (ImageButton) findViewById(R.id.btn_video);
        btn_erweima = (ImageButton) findViewById(R.id.btn_erweima);
        ViewListener();

    }

    //View的监听事件
    private void ViewListener() {
        sp_noteclass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //点击时就保存记事类型
                note.setNoteClass(NoteClass_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_weather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //点击时就保存天气
                note.setWeather(weather_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnNoteUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });

        btnNoteUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });

        edtNoteContent.addTextChangedListener(new EditChangedListener());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dlg = new AlertDialog.Builder(CreateNoteActivity.this)
                        .setTitle("警告").setMessage("确认删除该记事？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //如果是修改记事时，方可点击删除
                                if (IsUpdate) {
                                    note.delete(note.getBMOB_ID(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(CreateNoteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(CreateNoteActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                            }
                                            finish();
                                        }
                                    });
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dlg.show();
            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是更新还是创建
                if (IsUpdate) {
                    note.setTitle(edtNoteTitle.getText().toString());
                    note.setContent(edtNoteContent.getText().toString());
                    note.setDate_Time(btnNoteUpdateDate.getText().toString() + " " + btnNoteUpdateTime.getText().toString());
                    note.setMulti_Media(null);
                    Note_Send_Bitmap_InBmob();
                    note.setFile(file);
                    note.update(note.getBMOB_ID(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(CreateNoteActivity.this, "更新记事成功", Toast.LENGTH_SHORT).show();
                                Log.i("bmob","更新记事成功");
                                finish();
                            }else{
                                Log.i("bmob","更新记事失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                } else {
                    note.setTitle(edtNoteTitle.getText().toString());
                    note.setContent(edtNoteContent.getText().toString());
                    note.setDate_Time(btnNoteUpdateDate.getText().toString() + " " + btnNoteUpdateTime.getText().toString());
                    note.setMulti_Media(null);
                    Note_Send_Bitmap_InBmob();
                    note.setFile(file);
                    note.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                Note_Send_Bitmap_InBmob();
                                Toast.makeText(CreateNoteActivity.this, "创建记事成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CreateNoteActivity.this, "创建记事失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(CreateNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        Weather_spinner();
        NoteClass_spinner();

        //添加图片时选择拍照或者图库中选择
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final CharSequence[] items = { "手机相册", "相机拍摄" };
                AlertDialog dlg = new AlertDialog.Builder(CreateNoteActivity.this).setTitle("选择图片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int item) {
                                //这里item是根据选择的方式,
                                //在items数组里面定义了两种方式, 拍照的下标为1所以就调用拍照方法
                                if(item==1){
                                    Intent getImageByCamera= new Intent("android.media.action.IMAGE_CAPTURE");
                                    startActivityForResult(getImageByCamera, CAMERA_SUCCESS);
                                }else{
                                    Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                    getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                    getImage.setType("image/*");
                                    startActivityForResult(getImage, PHOTO_SUCCESS);
                                }
                            }
                        }).create();
                dlg.show();
//                e.insertDrawable(R.drawable.easy);
            }
        });
    }

    private void showTime() {
        //选择时间Dialog
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(CreateNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Random random = new Random();
                String hour = Integer.toString(hourOfDay);
                String min = Integer.toString(minute);
                //获取秒
                SimpleDateFormat TIME_formatter = new SimpleDateFormat("ss");
                Date S_curDate = new Date(System.currentTimeMillis());//获取当前秒
                String S_nowDate = TIME_formatter.format(S_curDate);
                if (min.length() < 2) {
                    min = "0" + min;
                }
                String TIME = hour + ":" + min + ":" + S_nowDate;
                Log.d("测试", TIME);
                btnNoteUpdateTime.setText(TIME);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showDate() {
        //选择日期Dialog
        DatePickerDialog datePickerDialog;

        datePickerDialog = new DatePickerDialog(
                CreateNoteActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //monthOfYear 得到的月份会减1所以我们要加1
                String DATE = String.valueOf(year) + "年" + String.valueOf(monthOfYear + 1) + "月" + Integer.toString(dayOfMonth) + "日";
                Log.d("测试", DATE);
                btnNoteUpdateDate.setText(DATE);
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        //自动弹出键盘问题解决
        datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void NoteClass_spinner() {
        //数据
        ArrayAdapter<String> arr_adapter;
        NoteClass_list = new ArrayList<String>();
        NoteClass_list.add("日记");
        NoteClass_list.add("随手记");
        NoteClass_list.add("学习");
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NoteClass_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_noteclass.setAdapter(arr_adapter);
    }

    private void Weather_spinner() {
        //数据
        ArrayAdapter<String> arr_adapter;
        weather_list = new ArrayList<String>();
        weather_list.add("晴");
        weather_list.add("雨");
        weather_list.add("阴");
        weather_list.add("雪");
        weather_list.add("雨雪");
        weather_list.add("冰雹");
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weather_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        sp_weather.setAdapter(arr_adapter);
    }

    //拍照和图库的对应的事件
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_SUCCESS:
                    //获得图片的uri
                    originalUri = intent.getData();
                    Bitmap bitmap = null;
                    try {
                        Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));
                        bitmap = resizeImage(originalBitmap, 200, 200);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(bitmap != null){
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(CreateNoteActivity.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        //edittext下的文本就是new SpannableString里的字符，
                        // 在下面spannableString.setSpan替换时会通过这个字符来判断，所以需要字符一致
                        SpannableString spannableString = new SpannableString("[local]"+1+"[/local]");
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, "[local]1[local]".length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置
                        int index = edtNoteContent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = edtNoteContent.getEditableText();
                        if(index <0 || index >= edit_text.length()){
                            edit_text.append(spannableString);
                        }else{
                            edit_text.insert(index, spannableString);
                        }
                    }else{
                        Toast.makeText(CreateNoteActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CAMERA_SUCCESS:
                    Bundle extras = intent.getExtras();
                    Bitmap originalBitmap1 = (Bitmap) extras.get("data");
                    if(originalBitmap1 != null){
                        bitmap = resizeImage(originalBitmap1, 200, 200);
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(CreateNoteActivity.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        SpannableString spannableString = new SpannableString("[local]"+1+"[/local]");
                        //  用ImageSpan对象替换face
                        spannableString.setSpan(imageSpan, 0, "[local]1[local]".length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置
                        int index = edtNoteContent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = edtNoteContent.getEditableText();
                        if(index <0 || index >= edit_text.length()){
                            edit_text.append(spannableString);
                        }else{
                            edit_text.insert(index, spannableString);
                        }
                    }else{
                        Toast.makeText(CreateNoteActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 图片缩放
     * @param originalBitmap 原始的Bitmap
     * @param newWidth 自定义宽度
     * @return 缩放后的Bitmap
     */
    //选中图片后的缩放
    private Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight){
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        //定义欲转换成的宽、高
            newWidth = this.getWindowManager().getDefaultDisplay().getWidth();
            newHeight = 500;
        //计算宽、高缩放率
        float scanleWidth = (float)newWidth/width;
        float scanleHeight = (float)newHeight/height;
        //创建操作图片用的matrix对象 Matrix
        Matrix matrix = new Matrix();
        // 缩放图片动作
        matrix.postScale(scanleWidth,scanleHeight);
        //旋转图片 动作
        //matrix.postRotate(45);
        // 创建新的图片Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap,0,0,width,height,matrix,true);
        return resizedBitmap;
    }


    private void Note_Send_Bitmap_InBmob() {
        if(originalUri!=null) {
            file = new File(PathGetter.getPath(this, originalUri));
            Log.e("This is read photo","读取图片路径成功");
        }
    }

    private void JieXi_file(){

    }

    class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            new ThreadUtils().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    int length = s.length();
                    Txt_No.setText("当前已输入" + length + "个字符");
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
