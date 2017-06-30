package com.example.yls.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yls.Adapter.RecyclerViewAdapter;
import com.example.yls.Date.Note;
import com.example.yls.notepaddemo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends Activity {
    private Button btnAdd;
    private RecyclerView NoteRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Note> mNoteList = new ArrayList<>();
    private long clickTime = 0;
    private LinearLayoutManager mLayoutManager;
    private int lastVisibleItem = -1;
    private EditText edt_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FindViews();
        initNoteData();

    }

    //查询数据，并显示在RecyclerView里
    private void initNoteData() {
        BmobQuery<Note> query = new BmobQuery<Note>();
        //查询playerName叫“比目”的数据
//        query.addWhereEqualTo("playerName", "比目");
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(50);
        //执行查询方法
        query.findObjects(new FindListener<Note>() {
            public void done(List<Note> object, BmobException e) {
                if(e==null){
                    mNoteList.clear();
//                    Toast.makeText(MainActivity.this, "查询成功：共"+object.size()+"条数据。", Toast.LENGTH_SHORT).show();
                    for (Note note : object) {
//                        //获得playerName的信息
//                        note.getPlayerName();
                        //获得数据的objectId信息
                        note.getObjectId();
                        //保存数据的objectId
                        note.setBMOB_ID(note.getObjectId());
                        //获取Note数据
                        mNoteList.add(note);
                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
                        note.getCreatedAt();
                        Collections.reverse(mNoteList);
                        mAdapter.changData(mNoteList);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    Toast.makeText(MainActivity.this,"Select defaut",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FindViews() {
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        NoteRecyclerView = (RecyclerView) findViewById(R.id.Note_RecyclerView);
//        NoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //👆线性布局 👇瀑布流布局 下面设置下拉刷新的时候又设置了一次布局
//        NoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,  StaggeredGridLayoutManager.VERTICAL));
        NoteRecyclerView.setAdapter(mAdapter = new RecyclerViewAdapter(mNoteList));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.SwipeRefresh_2,R.color.SwipeRefresh_3,R.color.SwipeRefresh_1);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initNoteData();
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                mAdapter.changData(mNoteList);
            }
        });

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        //以下注释是下拉刷新事件
//        NoteRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE
//                        && lastVisibleItem + 1 == mAdapter.getItemCount()) {
//                    Toast.makeText(MainActivity.this, "下拉刷新", Toast.LENGTH_SHORT).show();
//                    mSwipeRefreshLayout.setRefreshing(true);
//                    Handler handler = new Handler();
//                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
////                    handler.sendEmptyMessageDelayed(0, 3000);
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//            }
//        });

        NoteRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        NoteRecyclerView.setLayoutManager(mLayoutManager);
        NoteRecyclerView.setItemAnimator(new DefaultItemAnimator());

        edt_index = (EditText) findViewById(R.id.edt_index);
        edt_index.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm.isActive()){
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );
                        String Search = edt_index.getText().toString();

                        BmobQuery<Note> query = new BmobQuery<Note>();
                        //查询title叫“search_content”的数据
                        String SEARCH_CONTENT = edt_index.getText().toString();
                        if(SEARCH_CONTENT.equals("日记") ||
                                SEARCH_CONTENT.equals("随手记") ||
                                SEARCH_CONTENT.equals("学习")){
                            query.addWhereEqualTo("NoteClass", SEARCH_CONTENT);
                        }else if(SEARCH_CONTENT.indexOf("年月日：")!=-1){
                            query.addWhereEqualTo("Date_Time", SEARCH_CONTENT);
                        }else{
                            query.addWhereEqualTo("title",SEARCH_CONTENT);
                        }
                        //返回50条数据，如果不加上这条语句，默认返回10条数据
                        query.setLimit(50);
                        //执行查询方法
                        query.findObjects(new FindListener<Note>() {
                            @Override
                            public void done(List<Note> object, BmobException e) {
                                if(e==null){
//                                    toast("查询成功：共"+object.size()+"条数据。");
                                    mNoteList.clear();
                                    for (Note note : object) {
                                        //获得title的信息
                                        note.getTitle();
                                        //获得数据的objectId信息
                                        note.getObjectId();
                                        //获得createdAt数据创建时间（注意是：createdAt，不是createAt）
                                        note.getCreatedAt();
                                        mNoteList.add(note);
                                        mAdapter.changData(mNoteList);
                                    }
                                }else{
                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                }
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        initNoteData();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (SystemClock.uptimeMillis() - clickTime <= 1500) {
                finish();
            } else {
                //当前系统时间的毫秒值
                clickTime = SystemClock.uptimeMillis();
                Toast.makeText(this, "再次返回退出程序", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
