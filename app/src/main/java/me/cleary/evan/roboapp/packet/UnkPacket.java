package me.cleary.evan.roboapp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Evan on 6/4/2018.
 */

public class UnkPacket extends AbstractPacket {
    private static final byte[] OPCODE = {5, 'U', 'K', 24};

    static {
        PacketFactory.registerPacket(OPCODE, UnkPacket.class);
    }

    @Override
    public void writePacket(OutputStream os) throws IOException {

    }

    @Override
    public boolean readPacket(InputStream is) {
        return false;
    }
}
