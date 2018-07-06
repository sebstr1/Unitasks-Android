package com.sest1601.bathingsites.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.RoomMasterTable.TABLE_NAME;

@Dao
public interface DAO {

    @Query("SELECT * FROM bathsiteentity")
    List<BathsiteEntity> getAll();

    @Query("SELECT COUNT(*) FROM bathsiteentity WHERE lng = :longitude AND  lat = :latitude")
    int siteExists(String longitude, String latitude);

    @Query("SELECT COUNT(*) FROM BathsiteEntity")
    int count();

    @Insert
    void insert(BathsiteEntity entity);

    @Update
    void update(BathsiteEntity entity);

    @Delete
    void delete(BathsiteEntity entity);



}
