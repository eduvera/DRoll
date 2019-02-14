package com.viedmapp.timeddiceroller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Dice> dataSet;
    private int resource;

    RecyclerAdapter(ArrayList<Dice> dataSet, int resource) {
        this.dataSet = dataSet;
        this.resource = resource;
    }


    //Create new CardView
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.diceFaces.setText(String.valueOf(dataSet.get(i).getFACES()));
        viewHolder.roll.setText(String.format("Roll: %s", String.valueOf(dataSet.get(i).getVALUE())));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView diceFaces;
        TextView roll;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            diceFaces = itemView.findViewById(R.id.diceFace);
            roll = itemView.findViewById(R.id.diceVal);
        }
    }
}