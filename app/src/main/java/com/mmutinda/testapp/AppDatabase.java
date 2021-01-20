package com.mmutinda.testapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mmutinda.testapp.daos.SmsDao;
import com.mmutinda.testapp.entities.SmsEntity;

@Database(entities = {SmsEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SmsDao smsDao();

    private static AppDatabase INSTANCE;

    private static String DATABASE_NAME = "sample_db";

    public static final String TAG = "UAPdatabase";

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE activity_records_table ");
        }
    };


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
//                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_2)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}