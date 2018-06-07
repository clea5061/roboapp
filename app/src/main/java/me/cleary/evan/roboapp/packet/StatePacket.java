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
import java.nio.ByteBuffer;

import me.cleary.evan.roboapp.models.Velocity;

/**
 * Created by Evan on 6/7/2018.
 */

public class StatePacket extends AbstractPacket {
    private Velocity mVelocity;
    private boolean mManual;
    public static final byte[] OPCODE = {5, 'S', 'T', 24};

    static {
        PacketFactory.registerPacket(OPCODE, StatePacket.class);
    }

    public Velocity getVelocity() {
        return mVelocity;
    }

    public boolean isManual() {
        return mManual;
    }


    @Override
    public boolean readPacket(InputStream is) {
        super.readPacket(is);
        mVelocity = new Velocity();
        byte[] velocityBytes = new byte[mVelocity.size()];
        try {
            is.read(velocityBytes);
            mVelocity.unpack(velocityBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] manualBytes = new byte[2];
        try {
            is.read(manualBytes);
            mManual = ByteBuffer.wrap(manualBytes).getChar() == 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
