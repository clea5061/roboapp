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

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 6/4/2018.
 */

public class PacketFactory {

    private static Map<String, Class<? extends Packet>> packetMap = new HashMap<>();

    static {
        packetMap.put(new String(ConnAckPacket.OPCODE),ConnAckPacket.class);
        packetMap.put(new String(AckPacket.OPCODE),AckPacket.class);
        packetMap.put(new String(StatePacket.OPCODE),StatePacket.class);
    }

    public static void registerPacket(byte[] opcode, Class<? extends Packet> packetClass) {
        packetMap.put(new String(opcode), packetClass);
    }

    public static Packet getPacket(byte[] opcode) throws IllegalAccessException, InstantiationException {
        Log.d("ROBO","Decoding "+new String(opcode));
        Class<? extends Packet> c = packetMap.get(new String(opcode));
        return c.newInstance();
    }
}
