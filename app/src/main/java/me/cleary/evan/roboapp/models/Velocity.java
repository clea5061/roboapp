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

import me.cleary.evan.roboapp.packet.Packet;

/**
 * Created by Evan on 6/7/2018.
 */

public class Velocity implements PacketSerializable<Velocity> {
    private double mY, mX, mOmega;

    public Velocity(){}

    public Velocity(double linear, double lateral, double angular){
        mY = linear;
        mX = lateral;
        mOmega = angular;
    }

    public double getLinearVelocity(){
        return mY;
    }

    public double getAngularVelocity(){
        return mOmega;
    }

    public double getLateralVelocity() {
        return mX;
    }

    @Override
    public byte[] packetize() {
        byte[] bytes = new byte[24];
        ByteBuffer.wrap(bytes).putDouble(mY).putDouble(mX).putDouble(mOmega);
        return bytes;
    }

    @Override
    public Velocity unpack(byte[] data) {
        ByteBuffer buff = ByteBuffer.wrap(data);
        mY = buff.getDouble();
        mX = buff.getDouble();
        mOmega = buff.getDouble();
        return this;
    }

    @Override
    public int size() {
        return 24;
    }
}
