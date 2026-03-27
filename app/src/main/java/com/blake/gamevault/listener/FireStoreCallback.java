package com.blake.gamevault.listener;

public interface FireStoreCallback<T> {

    void onCallback(T data);
}
