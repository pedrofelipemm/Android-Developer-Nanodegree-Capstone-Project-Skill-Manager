package study.pmoreira.skillmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import study.pmoreira.skillmanager.data.SkillManagerContract.CollaboratorsEntry;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "skillmanager.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_COLLABORATOR_TABLE = "CREATE TABLE " + CollaboratorsEntry.TABLE_NAME + " (" +
                CollaboratorsEntry._ID + " INTEGER PRIMARY KEY, " +
                CollaboratorsEntry.COLUMN_ID + " VARCHAR NOT NULL, " +
                CollaboratorsEntry.COLUMN_NAME + " VARCHAR NOT NULL, " +
                CollaboratorsEntry.COLUMN_BIRTHD_ATE + " INTEGER NOT NULL, " +
                CollaboratorsEntry.COLUMN_ROLE + " VARCHAR NOT NULL," +
                CollaboratorsEntry.COLUMN_EMAIL + " VARCHAR NOT NULL, " +
                CollaboratorsEntry.COLUMN_PHONE + " VARCHAR NOT NULL, " +
                CollaboratorsEntry.COLUMN_PICTURE_URL + " VARCHAR NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_COLLABORATOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CollaboratorsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}