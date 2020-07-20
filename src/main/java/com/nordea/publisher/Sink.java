package com.nordea.publisher;

public interface Sink {
    void publishHash(long id, byte[] message, byte[] salt, byte[] hash);
}
