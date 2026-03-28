package com.blake.gamevault.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.blake.gamevault.model.FavoriteGame;

@Database(entities = {FavoriteGame.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract FavoriteDao favoriteDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "gamevault_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Only for testing! We should use Threads later.
                    .build();
        }
        return instance;
    }
}