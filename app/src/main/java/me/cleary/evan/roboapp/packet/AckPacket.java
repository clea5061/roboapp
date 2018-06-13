/*
 * Copyright 2018 Evan Cleary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.cleary.evan.roboapp.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import javax.crypto.Mac;

/**
 * Created by Evan on 6/4/2018.
 */

public class AckPacket extends AbstractPacket{
    public static final byte[] OPCODE = {5, 'A', 'C', 24};

    static {
        PacketFactory.registerPacket(OPCODE, AckPacket.class);
    }

    private int mAckId;
    public AckPacket() {}

    public AckPacket(int ackId) {
        mAckId = ackId;
    }

    @Override
    public void writePacket(OutputStream os) throws IOException {
        os.write(OPCODE);
        super.writePacket(os);
        os.write(ByteBuffer.allocate(4).putInt(mAckId).array());
    }

    @Override
    public boolean readPacket(InputStream is) {
        super.readPacket(is);
        byte[] in = new byte[4];
        try {
            is.read(in);
            mAckId = ByteBuffer.wrap(in).getInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Do not send ack
        return false;
    }
}
