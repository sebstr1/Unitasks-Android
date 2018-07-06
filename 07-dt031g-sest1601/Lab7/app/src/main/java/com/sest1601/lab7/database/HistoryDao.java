package com.sest1601.lab7.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface HistoryDao {

    @Query("SELECT * FROM historyentity")
    List<HistoryEntity> getAll();


//    @Query("SELECT * FROM historyentity WHERE name LIKE :name LIMIT 1")
//    HistoryEntity findbyName(String name);

    @Insert
    void insert(HistoryEntity entity);

    @Update
    void update(HistoryEntity entity);

    @Delete
    void delete(HistoryEntity entity);



}
