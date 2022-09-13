package com.example.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.controller.rest.RetrofitHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CodeBlock extends Fragment {
    private RecyclerView codeList;
    private Button front, back, right, left, start;
    private BlockListAdapter adapter;
    private List<String> codes;
    private RetrofitHelper helper;
    private Disposable disposable;

    public CodeBlock(RetrofitHelper helper){
        this.helper = helper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater
                .inflate(R.layout.fragment_code_block, container, false);

        init(viewGroup);
        setLayoutManager();

        front.setOnClickListener(view -> updateList(front));
        back.setOnClickListener(view -> updateList(back));
        right.setOnClickListener(view -> updateList(right));
        left.setOnClickListener(view -> updateList(left));

        disposable = getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> helper.codeBlockSend(query),
                        throwable -> Toast.makeText(getContext(), throwable.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show());

        return viewGroup;
    }

    private Observable<String> getObservable(){
        return Observable.create(emitter ->
            start.setOnClickListener(view -> {
                StringBuilder builder = new StringBuilder();
                codes.forEach(code -> builder.append(code.toLowerCase()));

                System.out.println(builder);
                emitter.onNext(builder.toString());
            })
        );
    }
    private void init(ViewGroup viewGroup){
        codeList = viewGroup.findViewById(R.id.codeList);
        front = viewGroup.findViewById(R.id.front);
        back = viewGroup.findViewById(R.id.back);
        right = viewGroup.findViewById(R.id.right);
        left = viewGroup.findViewById(R.id.left);
        start = viewGroup.findViewById(R.id.startBtn);

        codes = new ArrayList<>();
        adapter = new BlockListAdapter(codes);
    }

    private void setLayoutManager(){
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        codeList.setLayoutManager(manager);
        codeList.setAdapter(adapter);
    }

    private void updateList(Button button){
        codes.add(button.getText().toString().toUpperCase());
        adapter.notifyItemInserted(adapter.getItemCount());
        codeList.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}