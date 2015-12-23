package edu.sdsu.cs.ramya.ratetheinstructor;

/**
 * Created by sarathbollepalli on 3/3/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "Instructor.db";
  private static final int DATABASE_VERSION = 1;
  private static DatabaseHelper sInstance;
  private String mCreateInstructorListSql = "CREATE TABLE IF NOT EXISTS InstructorsList (id INTEGER PRIMARY KEY, firstName TEXT,lastName TEXT);";
  private String mCreateInstructorComments="CREATE TABLE IF NOT EXISTS InstructorComments (commentId INTEGER PRIMARY KEY, comment TEXT,date TEXT,instructorId INTEGER);";
  private String createDetailsSql="CREATE TABLE IF NOT EXISTS InstructorDetails (id INTEGER PRIMARY KEY, office TEXT, phone TEXT, email TEXT, avgRating FLOAT, totalRatings INTEGER);";

  private DatabaseHelper(Context context)
  {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public static DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null)
        {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
  }

    @Override
   public void onCreate(SQLiteDatabase sqlDb)
   {
     sqlDb.execSQL(mCreateInstructorListSql);
     sqlDb.execSQL(mCreateInstructorComments);
     sqlDb.execSQL(createDetailsSql);
   }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
  }

