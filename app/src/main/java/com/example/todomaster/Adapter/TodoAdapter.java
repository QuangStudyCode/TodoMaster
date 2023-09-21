package com.example.todomaster.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomaster.MainActivity;
import com.example.todomaster.Model.TodoModel;
import com.example.todomaster.R;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private MainActivity context;
    private ArrayList<TodoModel> todoModels;

    public TodoAdapter(MainActivity context, ArrayList<TodoModel> todoModels) {
        this.context = context;
        this.todoModels = todoModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View todo = layoutInflater.inflate(R.layout.task_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(todo);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoModel todoModel = todoModels.get(position);
        holder.cbTask.setText(todoModel.getTask());
        holder.cbTask.setChecked(toBoolean(todoModel.getStatus()));

//        here
        boolean isCheck = toBoolean(todoModel.getStatus());
        if (isCheck) {
            holder.cbTask.setPaintFlags(holder.cbTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.cbTask.setPaintFlags(holder.cbTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.cbTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//              get current status and set new status in fuc (AC Main)
                todoModel.setStatus(toInt(b));
                if (b) {
                    holder.cbTask.setPaintFlags(holder.cbTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.cbTask.setPaintFlags(holder.cbTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                context.UpdateStatusForTask(todoModel.getId(), toInt(b));
                context.MakeSoundForTask(b);
            }
        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    private int toInt(boolean value) {
        return value ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return todoModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTask = itemView.findViewById(R.id.cbTask);
        }
    }
}
