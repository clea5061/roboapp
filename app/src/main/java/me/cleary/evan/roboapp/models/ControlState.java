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

package me.cleary.evan.roboapp.models;

import java.nio.ByteBuffer;

/**
 * Created by Evan on 6/7/2018.
 */

public class ControlState implements PacketSerializable<ControlState> {

    private char mControls = 0;

    public ControlState(){}

    public void enableRight(){
        mControls |= 0x2;
    }

    public void enableLeft(){
        mControls |= 0x1;
    }

    public void enableForward(){
        mControls |= 0x4;
    }

    public void enableReverse(){
        mControls |= 0x8;
    }

    public void enableAuto(){
        mControls |= 0x10;
    }

    @Override
    public byte[] packetize() {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putChar(mControls);
        return bytes;
    }

    @Override
    public ControlState unpack(byte[] data) {
        mControls = ByteBuffer.wrap(data).getChar();
        return this;
    }

    @Override
    public int size() {
        return 2;
    }
}
