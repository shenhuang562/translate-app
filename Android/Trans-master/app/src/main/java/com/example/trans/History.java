package com.example.trans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Button;
import androidx.fragment.app.FragmentTransaction;


import androidx.fragment.app.Fragment;

public class History extends Fragment {

    private SQLiteDatabase db;

    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_history, container, false);
        LinearLayout linearLayout = view.findViewById(R.id.historycontainer);

        Button clearButton = view.findViewById(R.id.clear);

        MainActivity mainActivity = (MainActivity) getActivity();

        // 获取数据库实例
        if (mainActivity != null) {
            db = mainActivity.getDatabaseInstance();
        } else {
            // 处理无法获取数据库实例的情况
        }
        // 假设你已经从数据库中获取了数据的列表 dataList
        Cursor cursor = db.query("history", null, null, null, null, null, "_id DESC", "50");
        displayHistoryData(cursor, inflater, linearLayout);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDatabase();
                LinearLayout linearLayout = getView().findViewById(R.id.historycontainer);
                linearLayout.removeAllViews();
            }
        });

        return view;
    }

    public void displayHistoryData(Cursor cursor, LayoutInflater inflater, LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String fromColumn = cursor.getString(cursor.getColumnIndexOrThrow("from_column"));
                String toColumn = cursor.getString(cursor.getColumnIndexOrThrow("to_column"));
                String fromText = cursor.getString(cursor.getColumnIndexOrThrow("fromtext"));
                String toText = cursor.getString(cursor.getColumnIndexOrThrow("totext"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time_column"));

                View itemLayout = inflater.inflate(R.layout.article, null);

                Button deleteButton = itemLayout.findViewById(R.id.deleteButton);
                deleteButton.setTag(id);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idToDelete = (int) v.getTag();
                        db.delete("history", "_id = ?", new String[]{String.valueOf(idToDelete)});
                        ViewGroup parentContainer = (ViewGroup) deleteButton.getParent();
                        ViewGroup parentOfParent = (ViewGroup) parentContainer.getParent();
                        parentOfParent.removeView(parentContainer);
                    }
                });

                String fromColumnData = "源语言: " + fromColumn;
                TextView sourceLanguageTextView = itemLayout.findViewById(R.id.sourceLanguageTextView);
                sourceLanguageTextView.setText(fromColumnData);

                String toColumnData = "目标语言: " + toColumn;
                TextView targetLanguageTextView = itemLayout.findViewById(R.id.targetLanguageTextView);
                targetLanguageTextView.setText(toColumnData);

                String fromTextData = "原文: " + fromText;
                TextView dataTextView1 = itemLayout.findViewById(R.id.dataTextView1);
                dataTextView1.setText(fromTextData);

                String toTextData = "译文: " + toText;
                TextView dataTextView2 = itemLayout.findViewById(R.id.dataTextView2);
                dataTextView2.setText(toTextData);

                linearLayout.addView(itemLayout);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    private void clearDatabase() {
        db.delete("history", null, null);
    }

}


