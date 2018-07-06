package com.sest1601.lab7.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

@Database(entities = {HistoryEntity.class}, version = 2)
public abstract class HistoryDB extends RoomDatabase {
    public abstract HistoryDao historyDao();
//
//    public static final Migration MIGRATION_1 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("AlTER TABLE History "
//                    + " ADD COLUMN number INTEGER "
//                    + " ADD COLUMN ");
//        }
//    }

}
