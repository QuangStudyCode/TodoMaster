package com.example.todomaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.todomaster.R;

import java.util.HashMap;
import java.util.List;

public class ExpandleListViewAdapter extends BaseExpandableListAdapter {

    private Context context;

    //    tiêu đề của nhóm
    private List<String> questionsList;

    //    nhóm
    HashMap<String, List<String>> questionGroup;


    public ExpandleListViewAdapter(Context context, List<String> questionsList, HashMap<String, List<String>> questionGroup) {
        this.context = context;
        this.questionsList = questionsList;
        this.questionGroup = questionGroup;
    }

    @Override
    public int getGroupCount() {
        return questionsList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.questionGroup.get(this.questionsList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.questionsList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.questionGroup.get(this.questionsList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String QuestionTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.question_list, null);
        }
        TextView questionTv = view.findViewById(R.id.tvQuestionList);
        questionTv.setText(QuestionTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String detailQuestionTitle = (String) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutInflater.inflate(R.layout.detail_question, null);
        }
        TextView detailQuestionTv = view.findViewById(R.id.tvDetailQuestions);
        detailQuestionTv.setText(detailQuestionTitle);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
