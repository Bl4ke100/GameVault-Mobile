package com.blake.gamevault.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteGame {
    @PrimaryKey(autoGenerate = false)
    @androidx.annotation.NonNull
    private String gameId; // Use the Firestore Document ID as the Key

    private String title;
    private String price;
    private String imageUrl;

    public FavoriteGame(String gameId, String title, String price, String imageUrl) {
        this.gameId = gameId;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters (Required by Room)
    public String getGameId() { return gameId; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}