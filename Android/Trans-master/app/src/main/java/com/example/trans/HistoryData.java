package com.example.trans;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryData extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "Users.db";   // 创建数据库名叫 Users
    private static final String TABLE_NAME = "history"; // 指定表格名称
    private Context mContext;

    public HistoryData(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
    }

    // 创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "from_column TEXT, " +
                "to_column TEXT, " +
                "fromtext TEXT, " +
                "totext TEXT, " +
                "time_column TEXT)");
    }

    // 数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库版本更新，可以在这里处理相应的操作，比如删除旧表格，创建新表格等
        onCreate(db);
    }
}