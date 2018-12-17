package com.seoul.ddroad.diary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seoul.ddroad.R;

import java.util.ArrayList;

public class SpinnerAdapter  extends ArrayAdapter<String> {
    Context mcontext; int mresId;  ArrayList<String>
    mlist;
    public SpinnerAdapter(Context context, int resId, ArrayList<String>
            list){
        super(context,resId,list);
        this.mcontext=context;
        this.mresId = resId;
        this.mlist=list;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        return initRow(position, convertView, parent);

    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }

    public View initRow (int position, View convertView, ViewGroup
            parent){
        ViewHolder holder ;

        LayoutInflater inflater = (LayoutInflater) mcontext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView;
        // For reuse
        if (convertView == null) {
            row = inflater.inflate(mresId, null);
            holder = new ViewHolder();
            holder.textView1 = (TextView) row.findViewById(R.id.weathertext);
            holder.imageView1 = (ImageView)row.findViewById(R.id.weatherimageView);
            holder.item_holder = row.findViewById(R.id.item_holder);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }

        //xml weather_item_array 랑 갯수 마춰야합니다
        for (int i=0; i < 5 ; i++){
            if(position == 1 ){
                holder.imageView1.setImageResource(R.drawable.bichon1);
            }else if(position == 2){
                holder.imageView1.setImageResource(R.drawable.bichon2);
            }else if(position == 3){
                holder.imageView1.setImageResource(R.drawable.bichon3);
            }else if(position == 4){
                holder.imageView1.setImageResource(R.drawable.bichon4);
            }else if(position == 5){
                holder.imageView1.setImageResource(R.drawable.bichon5);
            }else if(position == 6){
                holder.imageView1.setImageResource(R.drawable.sun);
            }else if(position == 7){
                holder.imageView1.setImageResource(R.drawable.clouds);
            }else if(position == 8){
                holder.imageView1.setImageResource(R.drawable.cludysun);
            }else if(position == 9){
                holder.imageView1.setImageResource(R.drawable.drop);
            }else if(position == 10){
                holder.imageView1.setImageResource(R.drawable.flash);
            }else if(position == 11){
                holder.imageView1.setImageResource(R.drawable.snowflake);
            }else if(position == 12){
                holder.imageView1.setImageResource(R.drawable.hospital);
            }else if(position == 13){
                holder.imageView1.setImageResource(R.drawable.pills);
            }else if(position == 14){
                holder.imageView1.setImageResource(R.drawable.diary_walk);
            }else{
                holder.imageView1.setImageResource(0);
            }
        }

        holder.textView1.setText(mlist.get(position));


            if (position == 0) {
                holder.imageView1.setVisibility(View.GONE);
            } else {
                holder.imageView1.setVisibility(View.VISIBLE);
            }

        return row;
    }

    private class ViewHolder {
        TextView textView1 = null;
        ImageView imageView1 = null;
        LinearLayout item_holder = null;
    }
}
