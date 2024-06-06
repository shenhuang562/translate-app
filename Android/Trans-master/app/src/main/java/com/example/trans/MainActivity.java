package com.example.trans;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends AppCompatActivity {

    private Fragment transFragment;
    private Fragment historyFragment;
    private Fragment settingFragment;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建数据库实例并存储在成员变量中
        HistoryData dbHelper = new HistoryData(this);
        db = dbHelper.getWritableDatabase();

        BottomNavigationView navigationView = findViewById(R.id.navigation_view);

        // 初始化各个 Fragment
        transFragment = new Trans();
        historyFragment = new History();
        settingFragment = new Setting();

        // 设置默认显示的 Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, settingFragment, "setting")
                .hide(settingFragment)
                .add(R.id.container, historyFragment, "history")
                .hide(historyFragment)
                .add(R.id.container, transFragment, "trans")
                .show(transFragment)
                .commit();

        // 设置底部导航栏的监听器
        navigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navigation_a) {
                selectedFragment = transFragment;
            } else if (item.getItemId() == R.id.navigation_b) {
                selectedFragment = historyFragment;
            } else if (item.getItemId() == R.id.navigation_c) {
                selectedFragment = settingFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .hide(transFragment)
                        .hide(historyFragment)
                        .hide(settingFragment)
                        .show(selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public History getHistoryFragment() {
        return (History) historyFragment;
    }
}
