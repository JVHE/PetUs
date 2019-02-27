package com.example.jvhe.petus.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jvhe.petus.R;

import java.util.ArrayList;

public class ListViewAdapterFormat extends BaseAdapter {

    private static final String TAG = "ListViewAdapterFormat";

    Context context;

    // 리스트뷰에 담을 데이터 리스트.
    // 선언 필요
    ArrayList<String> data;

    // 뷰
    // 선언 필요
    TextView tv_text;

    public ListViewAdapterFormat(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_basic, null);
        }

        // 뷰와 데이터를 연결해 준다.
        // 선언 필요
        tv_text = ViewHolder.get(convertView, R.id.tv_text);
        String text = position + "번째 아이템";
        tv_text.setText(text);

        return convertView;
    }

    public static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
