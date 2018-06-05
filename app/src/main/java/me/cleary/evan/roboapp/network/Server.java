package me.cleary.evan.roboapp.network;

/**
 * Created by Evan on 6/4/2018.
 */

public interface Server {

    void connect(String server, short port);
    void disconnect();
}
