package com.aby.note_quasars_android.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.aby.note_quasars_android.model.Note;

@Database(entities = {Note.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

//    private static AppDatabase INSTANCE;
//
//    public abstract NoteDao noteDao();
//
//    public static AppDatabase getAppDatabase(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE =
//                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database-name")
//                            .build();
//        }
//        return INSTANCE;
//    }
//
//    public static void destroyInstance() {
//        INSTANCE = null;
//    }
}