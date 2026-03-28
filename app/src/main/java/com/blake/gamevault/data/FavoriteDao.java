package com.blake.gamevault.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.blake.gamevault.model.FavoriteGame;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(FavoriteGame game);

    @Delete
    void removeFavorite(FavoriteGame game);

    @Query("SELECT * FROM favorites")
    List<FavoriteGame> getAllFavorites();

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE gameId = :id)")
    boolean isFavorite(String id);

    @Query("DELETE FROM favorites WHERE gameId = :id")
    void removeFavoriteById(String id);
}