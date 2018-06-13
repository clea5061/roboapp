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

package me.cleary.evan.roboapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import me.cleary.evan.roboapp.network.TCPServer;
import me.cleary.evan.roboapp.packet.ConnAckPacket;
import me.cleary.evan.roboapp.packet.ConnPacket;
import me.cleary.evan.roboapp.packet.Packet;
import me.cleary.evan.roboapp.services.RoboServer;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements TCPServer.PacketReceivedListener {

    // UI references.
    private EditText mPortView, mServerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPortView = findViewById(R.id.port);
        mServerView = findViewById(R.id.server);

        Button connectButton = findViewById(R.id.email_sign_in_button);
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String server = mServerView.getText().toString();
        short port = Short.parseShort(mPortView.getText().toString());

        Intent serviceIntent = new Intent(this, RoboServer.class);
        serviceIntent.putExtra(RoboServer.BUNDLE_ARGS_PORT, port);
        serviceIntent.putExtra(RoboServer.BUNDLE_ARGS_SERVER, server);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            final RoboServer.ListenerBinder binder = (RoboServer.ListenerBinder) iBinder;
            binder.registerListener(LoginActivity.this, new TCPServer.ServerListener() {
                @Override
                public void connected() {
                    binder.sendPacket(new ConnPacket(Build.MODEL));
                }

                @Override
                public void disconnected() {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    public void onPacketReceived(Packet p) {
        if(p instanceof ConnAckPacket) {
            Log.d("ROBO", ((ConnAckPacket)p).getName());
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent controlIntent = new Intent(LoginActivity.this, RoboActivity.class);
                    controlIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(controlIntent);
                }
            });
        }
    }
}

