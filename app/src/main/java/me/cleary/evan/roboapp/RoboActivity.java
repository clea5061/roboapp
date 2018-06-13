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
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import me.cleary.evan.roboapp.models.ControlState;
import me.cleary.evan.roboapp.network.TCPServer;
import me.cleary.evan.roboapp.packet.ControlPacket;
import me.cleary.evan.roboapp.packet.Packet;
import me.cleary.evan.roboapp.services.RoboServer;

public class RoboActivity extends AppCompatActivity implements TCPServer.PacketReceivedListener {

    private ImageView mLeft, mRight, mUp, mDown;
    private Button mEmergency, mAutonomous;

    private SeekBar mSpeedLimit;

    private TextView mSpeedValue;

    private RoboServer.ListenerBinder mRobotBinder;
    private boolean mAutonomousB = false;

    private SparseBooleanArray mTouchMap = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robo);
        mLeft = findViewById(R.id.btn_left);
        mRight = findViewById(R.id.btn_right);
        mUp = findViewById(R.id.btn_forward);
        mDown = findViewById(R.id.btn_backward);
        mSpeedLimit = findViewById(R.id.sb_speed_limit);

        mSpeedValue = findViewById(R.id.txt_speed_value);
        mAutonomous = findViewById(R.id.btn_autonmous);

        mAutonomous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutonomousB = !mAutonomousB;
                mRobotBinder.sendPacket(new ControlPacket(buildControlState()));
            }
        });

        mSpeedLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSpeedValue.setText(getString(R.string.speed_format, seekBar.getProgress()));
                if (mRobotBinder != null) {
                    mRobotBinder.sendPacket(new ControlPacket(buildControlState()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSpeedValue.setText(getString(R.string.speed_format, mSpeedLimit.getProgress()));

        mEmergency = findViewById(R.id.btn_emergency_stop);
        mAutonomous = findViewById(R.id.btn_autonmous);
        Intent serviceIntent = new Intent(this, RoboServer.class);
        bindService(serviceIntent, mServiceConnection, 0);

        registerControls();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unbindService(mServiceConnection);
        } catch (Exception ex) {
            //
        }
    }

    private void registerControls() {
        mLeft.setOnTouchListener(mControlTouch);
        mRight.setOnTouchListener(mControlTouch);
        mUp.setOnTouchListener(mControlTouch);
        mDown.setOnTouchListener(mControlTouch);
        mEmergency.setOnClickListener(mControlClick);
    }

    private ControlState buildControlState() {
        ControlState cs = new ControlState();
        if (mEmergency.isActivated())
            cs.enableEmergency();
        if (mTouchMap.get(mLeft.getId()))
            cs.enableLeft();
        if (mTouchMap.get(mRight.getId()))
            cs.enableRight();
        if (mTouchMap.get(mUp.getId()))
            cs.enableForward();
        if (mTouchMap.get(mDown.getId()))
            cs.enableReverse();
        cs.setSpeed((char) mSpeedLimit.getProgress());
        if (mAutonomousB)
            cs.enableAuto();
        return cs;
    }

    private View.OnClickListener mControlClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ControlPacket cp = new ControlPacket(buildControlState());
            cp.getControls().enableEmergency();
            mRobotBinder.sendPacket(cp);
        }
    };

    private View.OnTouchListener mControlTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchMap.put(view.getId(), true);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mTouchMap.put(view.getId(), false);
            }
            ControlPacket cp = new ControlPacket(buildControlState());
            mRobotBinder.sendPacket(cp);
            return true;
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRobotBinder = (RoboServer.ListenerBinder) iBinder;
            mRobotBinder.registerListener(RoboActivity.this, null);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            RoboActivity.this.finish();
        }
    };

    @Override
    public void onPacketReceived(Packet p) {

    }
}
