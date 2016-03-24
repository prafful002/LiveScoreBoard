package scoreboard.example.com.livescoreboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prafful Um on 24-03-2016.
 */
public class ORM {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public ORM(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /*
        opens the connection to the database
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /*
    closes the connection to the database
     */

    public void close() {
        dbHelper.close();
    }

    /*
        makes an entry into the database
     */
    public void createEntry(String team, String score) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TEAM, team);
        values.put(MySQLiteHelper.COLUMN_SCORE, score);
        database.insert(MySQLiteHelper.TABLE_CLUBS, null,
                values);
    }

    /*
        Returns all the scores cached in the database
     */
    public Map<String, String> getAllScores() {
        Map<String, String> results = new HashMap<String, String>();

        Cursor cursor = database.rawQuery("select * from clubs;", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String team = cursor.getString(1);
            String score = cursor.getString(2);
            results.put(team, score);
            cursor.moveToNext();
        }
        cursor.close();
        return results;

    }

    /*
        delete all entries from the database
     */
    public void deleteAll() {
        database.execSQL("delete from clubs;");
    }
}
