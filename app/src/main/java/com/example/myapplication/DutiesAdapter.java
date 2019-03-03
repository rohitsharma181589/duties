package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.interfacesPck.ItemClick;

import java.util.ArrayList;
import java.util.Locale;

public class DutiesAdapter extends RecyclerView.Adapter<DutiesAdapter.MyViewHolder> {


    private ArrayList<Integer> dutiesList;
    private ItemClick click;

    DutiesAdapter(ArrayList<Integer> dutiesList, ItemClick itemClick) {
        this.dutiesList = dutiesList;
        click = itemClick;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.duties_list_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final int id = dutiesList.get(i);
        myViewHolder.textView.setText(String.format(Locale.ENGLISH, "%d", id));
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClick(String.valueOf(id));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dutiesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_duties);
            parent = itemView.findViewById(R.id.ll_parent);
        }


    }
}
