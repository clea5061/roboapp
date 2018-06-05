package me.cleary.evan.roboapp.packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 6/4/2018.
 */

public class PacketFactory {

    private static Map<byte[], Class<? extends Packet>> packetMap = new HashMap<>();

    public static void registerPacket(byte[] opcode, Class<? extends Packet> packetClass) {
        packetMap.put(opcode, packetClass);
    }

    public static Packet getPacket(byte[] opcode) throws IllegalAccessException, InstantiationException {
        return packetMap.get(opcode).newInstance();
    }
}
