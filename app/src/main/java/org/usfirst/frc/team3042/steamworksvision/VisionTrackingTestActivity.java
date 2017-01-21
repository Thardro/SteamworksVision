package org.usfirst.frc.team3042.steamworksvision;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.usfirst.frc.team3042.steamworksvision.communication.RobotConnectionStateListener;
import org.usfirst.frc.team3042.steamworksvision.communication.RobotConnectionStatusBroadcastReceiver;
import org.usfirst.frc.team3042.steamworksvision.communication.TargetInfo;
import org.usfirst.frc.team3042.steamworksvision.communication.VisionUpdate;
import org.usfirst.frc.team3042.steamworksvision.communication.messages.TargetUpdateMessage;

public class VisionTrackingTestActivity extends AppCompatActivity {

    TextView isConnected;
    RobotConnectionStatusBroadcastReceiver connectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_tracking);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        isConnected = (TextView)findViewById(R.id.isConnected);

        connectionReceiver = new RobotConnectionStatusBroadcastReceiver(AppContext.getDefaultContext(), new ConnectionTracker());

        final EditText xPos = (EditText)findViewById(R.id.xPos);
        final EditText yPos = (EditText)findViewById(R.id.yPos);
        final EditText timestamp = (EditText)findViewById(R.id.timestamp);

        // Setting up button to send data from fields as a test message
        final Button messageButton = (Button) findViewById(R.id.sendMessage);
        messageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int x = (isInteger(xPos.getText().toString()))? Integer.parseInt(xPos.getText().toString()) : 0;
                int y = (isInteger(yPos.getText().toString()))? Integer.parseInt(yPos.getText().toString()) : 0;
                int time = (isInteger(timestamp.getText().toString()))? Integer.parseInt(timestamp.getText().toString()) : 0;

                TargetInfo testTarget = new TargetInfo(x, y, 0);
                VisionUpdate testUpdate = new VisionUpdate(System.nanoTime());
                testUpdate.addCameraTargetInfo(testTarget);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TargetUpdateMessage testMessage = new TargetUpdateMessage(testUpdate, System.nanoTime());
                AppContext.getRobotConnection().send(testMessage);
            }
        });

        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(AppContext.getDefaultContext(), "Could not connect", Toast.LENGTH_SHORT);
            }
        });
    }

    // Changes text when connection status updates
    protected class ConnectionTracker implements RobotConnectionStateListener {

        @Override
        public void robotConnected() {
            isConnected.setText("Connected");
        }

        @Override
        public void robotDisconnected() {
            isConnected.setText("Not Connected");
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
