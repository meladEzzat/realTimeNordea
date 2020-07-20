package com.nordea;

import com.nordea.processor.Observer;
import com.nordea.processor.Processor;
import com.nordea.publisher.Sink;
import com.nordea.source.Source;

public class Solution {
    private Source source;
    private Sink sink;

    public Solution(Source source, Sink sink) {
        this.source = source;
        this.sink = sink;
    }

    public void start() {
        Observer observer = createObserver(sink);
        source.subscribe(observer);
    }

    protected Observer createObserver(Sink sink) {
        return new Processor(sink);
    }
}
