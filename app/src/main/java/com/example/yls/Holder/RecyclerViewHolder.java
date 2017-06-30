package com.example.yls.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.yls.notepaddemo.R;

/**
 * Created by Min on 2017-06-25.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView Note_Title;
    public TextView Note_DateTime;
    public TextView Note_Content;
    public TextView Note_Class;
    public TextView Note_Weather;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        Note_Title = (TextView) itemView.findViewById(R.id.Note_title);
        Note_DateTime = (TextView) itemView.findViewById(R.id.Note_DateTime);
        Note_Content = (TextView) itemView.findViewById(R.id.Note_content);
        Note_Class = (TextView) itemView.findViewById(R.id.Note_Class);
        Note_Weather = (TextView) itemView.findViewById(R.id.Note_Weather);
    }

}
