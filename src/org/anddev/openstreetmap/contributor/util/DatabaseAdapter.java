package org.anddev.openstreetmap.contributor.util;

import org.anddev.openstreetmap.contributor.util.constants.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter implements Constants {
	protected final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDatabase;
	
	public Cursor getNodesForJourney(long journeyID) {
		String sql = "SELECT * FROM " + T_ROUTERECORDER_NODE_TABLE + " WHERE " + T_ROUTERECORDER_ROUTE_ID + " = " + journeyID;
		Cursor c = mDatabase.rawQuery(sql, null);
		return c;
	}
	
	public long insertNode(ContentValues contentValues) {
		return mDatabase.insert(T_ROUTERECORDER_NODE_TABLE, null, contentValues);
	}
	
	public long createNewJourney(ContentValues cv) {
		return mDatabase.insert(T_ROUTERECORDER_JOURNEY_TABLE, null, cv);
	}
	
	public Cursor getJourneys() {
		String sql = "SELECT * FROM " + T_ROUTERECORDER_JOURNEY_TABLE;
		Cursor c = mDatabase.rawQuery(sql, null);
		return c;
	}
	
	public DatabaseAdapter(Context ctx) {
        this.mCtx = ctx;
    }
	
	public DatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDatabase = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        		
            db.execSQL(T_ROUTERECORDER_CREATE_JOURNEY_TABLE);
            db.execSQL(T_ROUTERECORDER_CREATE_NODE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DEBUGTAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }


	


	
}
