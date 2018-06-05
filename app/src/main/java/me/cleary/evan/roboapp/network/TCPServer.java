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

package me.cleary.evan.roboapp.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

import me.cleary.evan.roboapp.packet.AckPacket;
import me.cleary.evan.roboapp.packet.Packet;
import me.cleary.evan.roboapp.packet.PacketFactory;
import me.cleary.evan.roboapp.packet.UnkPacket;

/**
 * Created by Evan on 6/4/2018.
 */

public class TCPServer implements Server {

    private Socket mSocket;
    private boolean mActive = false;
    private WriteServer mWr;
    private EventServer mEs;
    private Thread mReadThread, mWriteThread, mEventThread;
    private PacketReceivedListener mListener;

    public void init() {
        mEs  = new EventServer();
        mEventThread = new Thread(mEs);
        mEventThread.setDaemon(true);
        mEventThread.start();
    }

    public void shutdown(){
        this.disconnect();
        try {
            mEventThread.join();
        } catch (InterruptedException e) {
            // Swallow
        }
        mListener = null;
    }

    public interface PacketReceivedListener {
        void onPacketReceived(Packet p);
    }

    public void registerListener(PacketReceivedListener listener) {
        mListener = listener;
    }

    private class EventServer implements Runnable {

        private LinkedBlockingQueue<Packet> eventQueue = new LinkedBlockingQueue<>();

        @Override
        public void run() {
            while(mActive) {
                Packet p = null;
                try {
                    p = eventQueue.take();
                    if(mListener != null) {
                        mListener.onPacketReceived(p);
                    }
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private class ReadThread implements Runnable {

        private Socket mSocket;
        private InputStream mIs;

        protected ReadThread(Socket sock) {
            mSocket = sock;
        }

        @Override
        public void run() {
            try {
                mIs = mSocket.getInputStream();
            } catch (IOException e) {
                mActive = false;
                try {
                    mSocket.close();
                } catch (IOException e1) {
                }
            }
            while (mActive) {
                try {
                    byte[] opCode = new byte[4];
                    if (mIs.read(opCode, 0, 4) == 4) {
                        Packet p = null;
                        try {
                            p = PacketFactory.getPacket(opCode);
                        } catch (IllegalAccessException | InstantiationException e) {
                            mWr.addPacket(new UnkPacket());
                            continue;
                        }
                        boolean succ = false;
                        if (p != null) {
                            succ = p.readPacket(mIs);
                        }
                        if (succ) {
                            mWr.addPacket(new AckPacket(p.getId()));
                            if (mListener != null) {
                                mListener.onPacketReceived(p);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        private class WriteServer implements Runnable {

            private LinkedBlockingQueue<Packet> mPacketQueue = new LinkedBlockingQueue<>();
            private Socket mSocket;
            private OutputStream mOs;

            private WriteServer(Socket socket) {
                mSocket = socket;
            }

            @Override
            public void run() {
                try {
                    mOs = mSocket.getOutputStream();
                } catch (IOException e) {
                    mActive = false;
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                    }
                }
                while (mActive) {
                    try {
                        Packet p = mPacketQueue.take();
                        p.writePacket(mOs);
                    } catch (InterruptedException | IOException e){
                        mActive = false;
                    }
                }
            }

            public void addPacket(Packet packet) {
                try {
                    mPacketQueue.put(packet);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public void connect(String server, short port) {
            if (mSocket != null && !mSocket.isClosed()) {
                return;
            }
            try {
                mSocket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(server, port);
                mSocket.connect(socketAddress);
                mActive = true;
                mReadThread = new Thread(new ReadThread(mSocket));
                mWr = new WriteServer(mSocket);
                mWriteThread = new Thread(mWr);
                mReadThread.setDaemon(true);
                mWriteThread.setDaemon(true);
                mReadThread.start();
                mWriteThread.start();
            } catch (IOException e) {
            }
        }

        @Override
        public void disconnect() {
            mActive = false;
            try {
                mWriteThread.join();
                mReadThread.join();
            } catch (InterruptedException e) {
                // Swallow
            } finally {
                try {
                    mSocket.close();
                } catch (IOException e){
                }
            }
        }

        public void sendPacket(Packet p) {
            mWr.addPacket(p);
        }
    }
