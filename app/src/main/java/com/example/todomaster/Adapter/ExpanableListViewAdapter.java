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

public class ExpanableListViewAdapter extends BaseExpandableListAdapter {
    //    đại diện cho context của ứng dụng
    private Context context;

    //    đại diện cho List của ứng dụng
    private List<String> chapterList;

    //    đại diện cho nhóm (gồm tiêu đề và nội dung)
    private HashMap<String, List<String>> topicsList;

    public ExpanableListViewAdapter(Context context, List<String> chapterList, HashMap<String, List<String>> topicsList) {
        this.context = context;
        this.chapterList = chapterList;
        this.topicsList = topicsList;
    }

    //    lấy số phần tử của group
    @Override
    public int getGroupCount() {
        return chapterList.size();
    }


    //    số lượng con của mô nhóm cụ thể trong danh sách mở rộng
    @Override
    public int getChildrenCount(int i) {
        return this.topicsList.get(this.chapterList.get(i)).size();
    }

    //  trả về và lấy thông tin của header group . Lấy thông tin về một nhóm cụ thể trong danh sách mở rộng
    @Override
    public Object getGroup(int i) {
        return this.chapterList.get(i);
    }


    //    lấy thông tin về con cụ thể trong danh sách mở rộng
    @Override
    public Object getChild(int i, int i1) {
        return this.topicsList.get(this.chapterList.get(i)).get(i1);
    }

    //    phương thức này trả về id của group tại vị trí truyền vào
    @Override
    public long getGroupId(int i) {
        return i;
    }

    //   lấy id của con cụ thể được truyền vào danh sách
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    //  Phương thức này kiểm tra  ID con và nhóm có ổn định không khi thay đổi dữ liệu cơ bản.
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //  Trả về giao diện view cho mootj nhóm cụ thể trong danh sách mở rộng/ Nghĩa là khởi tạo header cho mỗi danh sách
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
//        các tham số:
//        i: đại diện cho vị trí index của nhóm trong danh sách mở rộng
//        b: trạng thái mở rộng của nhóm đó
//        view: là một đối tượng view để tái sử dụng view
//        viewGroup là một đối tượng viewGroup thường là expandleListView chứa danh sách mở rộng
//
        String chapterTitle = (String) getGroup(i);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.question_list, null);
        }
        TextView chapterTv = view.findViewById(R.id.tvQuestionList);
        chapterTv.setText(chapterTitle);
        return view;
    }

    //    trả về giao diện view cho một con cụ thể
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
//        giải thích các tham số
//        i (index) đại diện cho vị trí của nhóm cha trong danh sách mở rộng
//        i1(index) đại diện cho vị trí của nhóm con trong danh sách của nhóm cha
//        b chỉ định xem con có phải là con cuối cùng trong danh sách con của nhóm cha hay không
//        view : được dùng để tái sử dụng giao diện nếu đã được tạo trước đó
//        viewGroup là một đối tượng viewGroup thường là expandleListView chứa danh sách mở rộng

        String topicTitle = (String) getChild(i, i1);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.topics_list, null);
        }
        TextView textView = view.findViewById(R.id.tvtopPicList);
        textView.setText(topicTitle);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
