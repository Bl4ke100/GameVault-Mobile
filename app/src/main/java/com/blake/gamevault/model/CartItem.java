package com.blake.gamevault.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Getter(onMethod_ = {@Exclude})
    @Setter(onMethod_ = {@Exclude})
    private String documentId;
    private String gameId;
    private int qty;
    private List<Attribute> attributes;

    public CartItem(String gameId, int qty, List<Attribute> attributes) {
        this.gameId = gameId;
        this.qty = qty;
        this.attributes = attributes;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attribute{
        private String name;
        private String value;
    }
}
