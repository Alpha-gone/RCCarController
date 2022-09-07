package com.example.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.CodeItemHolder> {
    private ArrayList<String> codes;

    public BlockListAdapter(ArrayList<String> codes) {
        this.codes = codes;
    }

    @NonNull
    @Override
    public CodeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.codeitem, parent, false);

        return new CodeItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CodeItemHolder holder, int position) {
        String code = codes.get(position);

        holder.codeLine.setText(code);
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    class CodeItemHolder extends RecyclerView.ViewHolder{
        TextView codeLine;
        Button itemDeleteBtn;

        public CodeItemHolder(View view){
            super(view);
            codeLine = view.findViewById(R.id.codeLine);
            itemDeleteBtn = view.findViewById(R.id.itemDeleteBtn);
        }
    }
}
