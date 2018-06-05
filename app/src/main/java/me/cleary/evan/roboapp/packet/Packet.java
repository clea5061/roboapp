package me.cleary.evan.roboapp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Evan on 6/4/2018.
 */

public interface Packet {

    int getId();

    void writePacket(OutputStream os) throws IOException;
    boolean readPacket(InputStream is);
}
