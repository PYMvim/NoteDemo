package com.example.yls.Adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yls.Activity.CreateNoteActivity;
import com.example.yls.Date.Note;
import com.example.yls.Holder.RecyclerViewHolder;
import com.example.yls.notepaddemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2017-06-25.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{
    private List<Note> mNoteList = new ArrayList<>();

    public RecyclerViewAdapter(List<Note> mNoteList){
        this.mNoteList = mNoteList;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Note note = mNoteList.get(position);

        if(TextUtils.isEmpty(note.getTitle())){
            holder.Note_Title.setText("(无标题)");
        }else {
            holder.Note_Title.setText(note.getTitle());
        }

        if(TextUtils.isEmpty(note.getContent())){
            holder.Note_Content.setText("(无内容)");
        }else{
            holder.Note_Content.setText(note.getContent());
        }

        holder.Note_DateTime.setText(note.getDate_Time());
        holder.Note_Class.setText("记事类型："+note.getNoteClass());
        holder.Note_Weather.setText("天气："+note.getWeather());

        //itemView点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(holder.itemView.getContext(), CreateNoteActivity.class);
                intent.putExtra("Note_title", note.getTitle());
                intent.putExtra("Note_content", note.getContent());
                intent.putExtra("Note_DateTime", note.getDate_Time());
                intent.putExtra("Note_class", note.getNoteClass());
                intent.putExtra("Note_weather", note.getWeather());
                intent.putExtra("IsUpdate", true);
                intent.putExtra("Note_BmobId",note.getBMOB_ID());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void changData(List<Note> mNoteList) {
        this.mNoteList = mNoteList;
        notifyDataSetChanged();
    }
}
