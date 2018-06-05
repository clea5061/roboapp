package me.cleary.evan.roboapp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by Evan on 6/4/2018.
 */

public abstract class AbstractPacket implements Packet {

    private int mPacketId;

    AbstractPacket() {
        mPacketId = new Random().nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int getId() {
        return mPacketId;
    }

    @Override
    public void writePacket(OutputStream os) throws IOException {
        os.write(ByteBuffer.allocate(4).putInt(mPacketId).array());
    }

    @Override
    public boolean readPacket(InputStream is) {
        byte[] idBuffer = new byte[32];
        try {
            is.read(idBuffer,0,32);
        } catch (IOException e) {
        }
        mPacketId = ByteBuffer.wrap(idBuffer).getInt();
        return true;
    }
}
