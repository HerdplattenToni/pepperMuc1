package com.capgemini.phmasson.pepperworkshop;

import android.app.Activity;
import android.os.Bundle;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class MainActivity extends Activity implements RobotLifecycleCallbacks {
    private static final String TAG = "QiChatbotActivity";


    private ChatHandler chatHandler;
    private TouchHandler touchHandler;
    private HumanAwarenessHandler humanAwarenessHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        chatHandler = new ChatHandler(qiContext);
        touchHandler = new TouchHandler(qiContext);
        humanAwarenessHandler = new HumanAwarenessHandler(qiContext);
    }

    @Override
    public void onRobotFocusLost() {
        chatHandler.unregisterChatHandler();
        touchHandler.unregisterTouchSensors();
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

}
