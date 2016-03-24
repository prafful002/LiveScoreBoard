package scoreboard.example.com.livescoreboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Prafful Um on 24-03-2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_CLUBS = "clubs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEAM = "team";
    public static final String COLUMN_SCORE = "score";


    private static final String DATABASE_NAME = "clubs.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CLUBS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEAM
            + " text not null," + COLUMN_SCORE + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUBS);
        onCreate(db);
    }
}
