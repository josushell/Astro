package com.example.astro;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AstroAdapter extends RecyclerView.Adapter<AstroAdapter.aa> {
    private static onAstroClickListener astroClickListener=null;
    // 커스텀 리스너 등록해서 mainActivity에서 처리
    public interface onAstroClickListener{
        void onItemClick(View v);
    }
    public void setOnAstroClickListener(onAstroClickListener listener){
        this.astroClickListener=listener;
    }

    ArrayList<AstroItem> items=new ArrayList<>();
    Context context;

    public AstroAdapter(ArrayList<AstroItem> items, Context context) {
        this.items = items;
        this.context=context;

    }

    @NonNull
    @Override
    public aa onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.astro_item,parent,false);
        return new aa(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull aa holder, int position) {
        AstroItem item=items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(AstroItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<AstroItem> _items) {
        this.items = _items;
    }

    public AstroItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, AstroItem item) {
        items.set(position, item);
    }

    static class aa extends RecyclerView.ViewHolder{
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;

        public aa(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(astroClickListener!=null){
                        astroClickListener.onItemClick(view);
                    }
                }
            });

            textView1=itemView.findViewById(R.id.textTitle);
            textView2=itemView.findViewById(R.id.textLocdate);
            textView3=itemView.findViewById(R.id.textTime);
            textView4=itemView.findViewById(R.id.textevent);
        }
        public void setItem(AstroItem item) {
            if(item.getAstroTitle()==null){
                textView1.setText(item.getAstroEvent());
                textView4.setText("");
            }
            else{
                textView1.setText(item.getAstroTitle());
                textView4.setText(item.getAstroEvent());
            }
            String s=item.getLocdate();
            String result=String.format("%s/%s/%s",s.substring(0,4),s.substring(4,6),s.substring(6,8));
            textView2.setText(result);
            textView3.setText(item.getAstroTime());
        }
    }

}
