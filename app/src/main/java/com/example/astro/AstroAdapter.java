package com.example.astro;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AstroAdapter extends RecyclerView.Adapter<AstroAdapter.aa> {
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
            textView2.setText(item.getLocdate());
            textView3.setText(item.getAstroTime());
        }
    }

}
