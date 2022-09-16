package com.example.controller;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.controller.rest.RetrofitHelper;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeBlock extends Fragment {
    private RecyclerView codeList;
    private Button front, back, right, left, start;
    private BlockListAdapter adapter;
    private List<Direction> codeArr;
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


        return viewGroup;
    }

    private void init(ViewGroup viewGroup){
        codeList = viewGroup.findViewById(R.id.codeList);
        front = viewGroup.findViewById(R.id.front);
        back = viewGroup.findViewById(R.id.back);
        right = viewGroup.findViewById(R.id.right);
        left = viewGroup.findViewById(R.id.left);
        start = viewGroup.findViewById(R.id.startBtn);

        codeArr = new ArrayList<>();
        adapter = new BlockListAdapter(codeArr, getContext());
    }

    private void setLayoutManager(){
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        codeList.setLayoutManager(manager);
        codeList.setAdapter(adapter);
    }

    private void updateList(Button button){
        codeArr.add(getDirection(button.getId()));
        adapter.notifyItemInserted(adapter.getItemCount());
        codeList.scrollToPosition(adapter.getItemCount() - 1);
    }

    private Direction getDirection(int id){
        switch (id){
            case R.id.front: return Direction.FRONT;
            case R.id.left: return Direction.LEFT;
            case R.id.right: return Direction.RIGHT;
            case R.id.back: return Direction.BACK;
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        disposable = getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                   start.setEnabled(false);
                   start.setBackground(ContextCompat.
                            getDrawable(getContext(),R.drawable.ic_baseline_play_circle_filled_24));

                   Call<Void> call = helper.getCodeBlockCall(query);
                            System.out.println(call.request());
                   call.enqueue(new Callback<Void>() {
                       @Override
                       public void onResponse(Call<Void> call, Response<Void> response) {
                           if (response.isSuccessful()){
                               start.setEnabled(true);
                               start.setBackground(ContextCompat.
                                       getDrawable(getContext(),
                                               R.drawable.ic_baseline_play_circle_24));

                               adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
                               codeArr.clear();

                           }
                       }

                       @Override
                       public void onFailure(Call<Void> call, Throwable t) {

                       }
                   });
                        },
                        throwable -> System.out.println(throwable.getLocalizedMessage()));
    }

    private Observable<String> getObservable(){
        return Observable.create(emitter ->
            start.setOnClickListener(view -> {
                StringBuilder builder = new StringBuilder("I");
                codeArr.forEach(direction -> builder.append(direction.getQuery()));

                emitter.onNext(builder.toString());
            })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}