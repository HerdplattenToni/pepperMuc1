package com.capgemini.phmasson.pepperworkshop;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;

import java.util.Arrays;
import java.util.List;

public class DanceHandler extends BaseQiChatExecutor {
    private static final String TAG = "QiChatbotActivity";
    private static final List<Integer> AVAILABLE_DANCES = Arrays.asList(R.raw.disco_a001, R.raw.headbang_a001, R.raw.taichichuan_a001, R.raw.dance_b001, R.raw.dance_b002, R.raw.dance_b003, R.raw.dance_b004, R.raw.dance_b005);
    private final QiContext qiContext;
    private Animate animate;

    public DanceHandler(QiContext qiContext) {
        super(qiContext);
        this.qiContext = qiContext;
    }

    @Override
    public void runWith(List<String> params) {
        dance(qiContext);
    }

    @Override
    public void stop() {
        Log.i(TAG, "Dance executor stopped");
    }

    public void dance(QiContext qiContext) {
        animate = AnimateBuilder.with(qiContext) // Create the builder with the context.
                .withAnimation(getRandomDance(qiContext)) // Set the animation.
                .build();

        animate.addOnStartedListener(new Animate.OnStartedListener() {
            @Override
            public void onStarted() {
                Log.i(TAG, "Animation started.");
                //TODO: Add some sound with MediaPlayer maybe
            }
        });

        animate.async().run();
    }

    private Animation getRandomDance (QiContext qiContext) {
        return AnimationBuilder.with(qiContext)
                .withResources(PepperUtil.oneOf(AVAILABLE_DANCES))
                .build();
    }


}
