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
import java.util.Random;

/**
 * Created by Evan on 6/4/2018.
 */

public abstract class AbstractPacket implements Packet {

    protected int mPacketId;
    protected int mPacketLength = 0;

    AbstractPacket() {
        mPacketId = new Random().nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int getId() {
        return mPacketId;
    }

    @Override
    public int getLength() { return mPacketLength; }

    @Override
    public void writePacket(OutputStream os) throws IOException {
        os.write(ByteBuffer.allocate(4).putInt(mPacketId).array());
        os.write(ByteBuffer.allocate(4).putInt(mPacketLength).array());
    }

    @Override
    public boolean readPacket(InputStream is) {
        byte[] smBuffer = new byte[4];
        try {
            is.read(smBuffer,0,4);
            mPacketId = ByteBuffer.wrap(smBuffer).getInt();
            is.read(smBuffer, 0, 4);
            mPacketLength = ByteBuffer.wrap(smBuffer).getInt();
        } catch (IOException e) {
        }
        return true;
    }
}
