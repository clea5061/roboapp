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

package me.cleary.evan.roboapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import me.cleary.evan.roboapp.network.TCPServer;
import me.cleary.evan.roboapp.packet.Packet;

public class RoboServer extends Service implements TCPServer.PacketReceivedListener, TCPServer.ServerListener {

    public static final String BUNDLE_ARGS_SERVER = "RA_SERVER";
    public static final String BUNDLE_ARGS_PORT = "RA_PORT";

    private ArrayList<TCPServer.PacketReceivedListener> mPacketListeners = new ArrayList<>();
    private ArrayList<TCPServer.ServerListener> mServerListeners = new ArrayList<>();
    private IBinder mBinder = new ListenerBinder();
    private TCPServer mTCPServer;
    private short mPort;
    private String mServer;

    @Override
    public void onPacketReceived(final Packet p) {
        for(TCPServer.PacketReceivedListener listener: mPacketListeners) {
            if(listener != null) {
                listener.onPacketReceived(p);
            }
        }
    }

    @Override
    public void connected() {
        for(TCPServer.ServerListener listener: mServerListeners) {
            if(listener != null) {
                listener.connected();
            }
        }
    }

    @Override
    public void disconnected() {
        for(TCPServer.ServerListener listener: mServerListeners) {
            if(listener != null) {
                listener.connected();
            }
        }
    }

    public class ListenerBinder extends Binder {
        public int registerListener(TCPServer.PacketReceivedListener listener, TCPServer.ServerListener severListener) {
            mPacketListeners.add(listener);
            mServerListeners.add(severListener);
            return mPacketListeners.size() - 1;
        }

        public void deregisterCallback(int listenerIndex) {
            mPacketListeners.remove(listenerIndex);
            mServerListeners.remove(listenerIndex);
            mPacketListeners.add(listenerIndex, null);
            mServerListeners.add(listenerIndex, null);
        }

        public void sendPacket(Packet p) {
            mTCPServer.sendPacket(p);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle serviceArgs = intent.getExtras();
        if(serviceArgs != null) {
            mServer = serviceArgs.getString(BUNDLE_ARGS_SERVER, "");
            mPort = serviceArgs.getShort(BUNDLE_ARGS_PORT, (short)0);
        }
        mTCPServer = new TCPServer();
        mTCPServer.registerListener(this, this);
        mTCPServer.init();
        new Thread() {
            @Override
            public void run() {
                mTCPServer.connect(mServer, mPort);
            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTCPServer != null){
            mTCPServer.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
