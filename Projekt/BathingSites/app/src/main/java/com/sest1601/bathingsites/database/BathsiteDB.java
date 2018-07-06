package com.sest1601.bathingsites.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {BathsiteEntity.class}, version = 2)
public abstract class BathsiteDB extends RoomDatabase {

    private static BathsiteDB INSTANCE;

    public abstract DAO dao();

    // Singleton to get the DB instance since Creating a room db object is expensive.
    public static BathsiteDB getInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(c, BathsiteDB.class, "BathsiteDB")
                    .build();
        }
        return INSTANCE;
    }
}
