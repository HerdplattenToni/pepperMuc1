package com.capgemini.phmasson.pepperworkshop;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.aldebaran.qi.sdk.object.touch.TouchState;

import java.util.Arrays;
import java.util.List;

public class TouchHandler {
    private static final List<String> DONT_TOUCH_PHRASES = Arrays.asList("Lass mich", "Berühr mich nicht", "Ich mag das nicht");
    private static final List<String> DO_TOUCH_PHRASES = Arrays.asList("Oh, woher wusstest du dass ich das mag?", "Ja, genau da");
    private static final String TAG = "QiChatbotActivity";
    private Touch touch;

    public TouchHandler(QiContext qiContext) {
        registerTouchSensors(qiContext);
    }

    private void registerTouchSensors(QiContext qiContext) {
        touch = qiContext.getTouch();
        //Sensors: "Head/Touch", "LHand/Touch", "RHand/Touch", "Bumper/FrontLeft", "Bumper/FrontRight", "Bumper/Back"
        TouchSensor headSensor = touch.getSensor("Head/Touch");
        TouchSensor leftHandSensor = touch.getSensor("LHand/Touch");
        TouchSensor rightHandSensor = touch.getSensor("RHand/Touch");
        TouchSensor leftBumperSensor = touch.getSensor("Bumper/FrontLeft");
        TouchSensor rightBumperSensor = touch.getSensor("Bumper/FrontRight");
        TouchSensor backBumperSensor = touch.getSensor("Bumper/Back");
        List<TouchSensor> negativeFeelingSensors = Arrays.asList(headSensor, leftHandSensor, rightHandSensor, leftBumperSensor, rightBumperSensor);
        List<TouchSensor> positiveFeelingSensors = Arrays.asList(backBumperSensor);

        registerTouches(positiveFeelingSensors, qiContext, true);
        registerTouches(negativeFeelingSensors, qiContext, false);

    }

    private void registerTouches(List<TouchSensor> feelingSensors, final QiContext qiContext, final boolean isPositive) {
        for (TouchSensor sensor : feelingSensors) {
            sensor.addOnStateChangedListener(new TouchSensor.OnStateChangedListener() {
                @Override
                public void onStateChanged(TouchState touchState) {
                    Log.i(TAG, "Sensor " + (touchState.getTouched() ? "touched" : "released") + " at " + touchState.getTime());
                    if(touchState.getTouched()) {
                        if (isPositive) {
                            PepperUtil.saySynchronously(PepperUtil.<String>oneOf(DO_TOUCH_PHRASES), qiContext);
                        } else {
                            PepperUtil.saySynchronously(PepperUtil.<String>oneOf(DONT_TOUCH_PHRASES), qiContext);
                        }
                    }
                }
            });
        }
    }

    public void unregisterTouchSensors() {
        for (String sensorName : touch.getSensorNames()) {
            touch.getSensor(sensorName).removeAllOnStateChangedListeners();
        }
    }

}
