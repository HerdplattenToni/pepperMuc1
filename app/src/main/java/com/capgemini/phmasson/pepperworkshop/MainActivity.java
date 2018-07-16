package com.capgemini.phmasson.pepperworkshop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.human.AttentionState;
import com.aldebaran.qi.sdk.object.human.EngagementIntentionState;
import com.aldebaran.qi.sdk.object.human.ExcitementState;
import com.aldebaran.qi.sdk.object.human.Gender;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.human.PleasureState;
import com.aldebaran.qi.sdk.object.human.SmileState;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.aldebaran.qi.sdk.object.touch.TouchState;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements RobotLifecycleCallbacks {
    private static final String TAG = "QiChatbotActivity";

    private static final List<String> DONT_TOUCH_PHRASES = Arrays.asList("Lass mich", "Berühr mich nicht", "Ich mag das nicht");
    private static final List<String> DO_TOUCH_PHRASES = Arrays.asList("Oh, woher wusstest du dass ich das mag?", "Ja, genau da");
    private Chat chat;
    private Touch touch;
    private HumanAwareness humanAwareness;

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
        registerChat(qiContext);
        registerTouchSensors(qiContext);
        registerHumanAwareness(qiContext);
    }

    @Override
    public void onRobotFocusLost() {
        unregisterChat();
        unregisterTouchSensors();
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private QiChatbot createChatbot(QiContext qiContext) {
        Topic antiWM = TopicBuilder.with(qiContext)
                .withResource(R.raw.anti_wm)
                .build();
        Topic dota = TopicBuilder.with(qiContext)
                .withResource(R.raw.dota)
                .build();
        Topic cap = TopicBuilder.with(qiContext)
                .withResource(R.raw.capgemini)
                .build();
        Topic got = TopicBuilder.with(qiContext)
                .withResource(R.raw.got)
                .build();

        return QiChatbotBuilder.with(qiContext)
                .withTopics(Arrays.asList(antiWM, dota, cap, got))
                .build();
    }

    private void registerChat(QiContext qiContext) {
        QiChatbot qiChatbot = createChatbot(qiContext);
        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        chat.addOnStartedListener(new Chat.OnStartedListener() {
            @Override
            public void onStarted() {
                Log.i(TAG, "Discussion started.");
            }
        });
    }

    private void unregisterChat() {
        if (chat != null) {
            chat.removeAllOnStartedListeners();
        }
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
                            saySynchronously(oneOf(DO_TOUCH_PHRASES), qiContext);
                        } else {
                            saySynchronously(oneOf(DONT_TOUCH_PHRASES), qiContext);
                        }
                    }
                }
            });
        }
    }

    private void unregisterTouchSensors() {
        for (String sensorName : touch.getSensorNames()) {
            touch.getSensor(sensorName).removeAllOnStateChangedListeners();
        }
    }

    private void registerHumanAwareness(QiContext qiContext) {
        humanAwareness = qiContext.getHumanAwareness();
        findHumansAround(qiContext);
    }

    private void findHumansAround(final QiContext qiContext) {
        Future<List<Human>> humansAroundFuture = humanAwareness.async().getHumansAround();

        humansAroundFuture.andThenConsume(new Consumer<List<Human>>() {
            @Override
            public void consume(List<Human> humansAround) throws Throwable {
                Log.i(TAG, humansAround.size() + " human(s) around.");
                retrieveCharacteristics(humansAround, qiContext);
            }
        });
    }

    private void retrieveCharacteristics(List<Human> humansAround, QiContext qiContext) {
        for (Human human : humansAround) {
            AttentionState attentionState = human.getAttention();

            if (attentionState == AttentionState.LOOKING_AT_ROBOT) {
                Integer age = human.getEstimatedAge().getYears();
                Gender gender = human.getEstimatedGender();
                SmileState smileState = human.getFacialExpressions().getSmile();
                // Not used atm
                PleasureState pleasureState = human.getEmotion().getPleasure();
                ExcitementState excitementState = human.getEmotion().getExcitement();
                EngagementIntentionState engagementIntentionState = human.getEngagementIntention();

                String humanAttributes = String.format("Hallo Mensch. Ich schätze dich folgendermassen ein: Alter %d Jahre, Geschlecht %s", age, translateGender(gender));
                saySynchronously(humanAttributes, qiContext);
                if (smileState == SmileState.NOT_SMILING) {
                    saySynchronously("Warum lächelst du nicht?", qiContext);
                }
            }

        }
    }

    private String translateGender(Gender gender) {
        return (gender == Gender.FEMALE ? "weiblich" : (gender == Gender.MALE ? "männlich" : "unbekannt"));
    }

    private void saySynchronously(String s, QiContext qiContext) {
        Phrase phrase = new Phrase(s);
        Say say = SayBuilder.with(qiContext).withPhrase(phrase).build();
        say.run();
    }

    private String oneOf(List<String> possibilities) {
        int index = new Random().nextInt(possibilities.size());
        return possibilities.get(index);
    }
}
