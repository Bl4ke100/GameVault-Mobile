package com.blake.gamevault.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
    private String publisherId;
    private String name;
    private String imageUrl;
}
