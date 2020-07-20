package com.nordea.publisher;

public class Publisher implements Sink {
    public void publishHash(long id, byte[] message, byte[] salt, byte[] hash) {
        System.out.println("Message id :: " + id
                + ", Message :: " + new String(message)
                + ", Salt :: " + new String(salt)
                + ", Hash :: " + new String(hash));
    }
}
