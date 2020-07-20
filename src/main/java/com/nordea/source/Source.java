package com.nordea.source;

import com.nordea.processor.Observer;

public interface Source {
    void subscribe(Observer observer);
}
