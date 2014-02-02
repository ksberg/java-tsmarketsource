package bitzguild.mkt.io;

import bitzguild.mkt.event.TickObserver;

public interface TickSource {
    void open(TickObserver observer) throws TickSourceException;
    void close();
}
