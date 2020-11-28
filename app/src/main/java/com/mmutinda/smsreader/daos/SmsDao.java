package com.mmutinda.smsreader.daos;


import android.util.Log;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mmutinda.smsreader.entities.SmsEntity;

import java.util.List;

@Dao
public abstract class SmsDao {

    private static final String TAG = "SmsDao";

    @Query("SELECT * FROM tb_sms where status = 0")
    public abstract List<SmsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<SmsEntity> users);


    @Query("UPDATE  tb_sms SET status = 1")
    public abstract  void updateall();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertOne(SmsEntity users);

    @Transaction
    public void insert(SmsEntity smsEntity) {
        try {
            insertOne(smsEntity);
        } catch (Exception e) {
            Log.d(TAG, "insert: "+ e.getMessage());
        }

    }

    @Delete
    public abstract void delete(SmsEntity user);
}

