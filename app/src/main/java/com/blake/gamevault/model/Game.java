package com.blake.gamevault.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private String gameId;
    private String title;
    private String description;
    private double price;
    private int releasedYear;
    private String categoryId;
    private String developerId;
    private String publisherId;
    private String posterUrl;
    private List<String> images;
    private int stock;
    private boolean status;
    private float rating;
}
