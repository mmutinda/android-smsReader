package com.mmutinda.smsreader.daos;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mmutinda.smsreader.entities.SmsEntity;

import java.util.List;

@Dao
public interface SmsDao {

    @Query("SELECT * FROM tb_sms")
    List<SmsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<SmsEntity> users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SmsEntity users);

    @Delete
    void delete(SmsEntity user);
}

