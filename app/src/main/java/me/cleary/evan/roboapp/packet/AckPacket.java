package me.cleary.evan.roboapp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Evan on 6/4/2018.
 */

public class AckPacket extends AbstractPacket{
    private static final byte[] OPCODE = {5, 'A', 'C', 24};

    static {
        PacketFactory.registerPacket(OPCODE, AckPacket.class);
    }

    private int mAckId;

    public AckPacket(int ackId) {

    }

    @Override
    public void writePacket(OutputStream os) throws IOException {
        os.write(OPCODE);
        os.write(ByteBuffer.allocate(4).putInt(mAckId).array());
    }

    @Override
    public boolean readPacket(InputStream is) {
        return true;
    }
}
