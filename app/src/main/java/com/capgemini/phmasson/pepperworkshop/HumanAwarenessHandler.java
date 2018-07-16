package com.capgemini.phmasson.pepperworkshop;

import android.util.Log;

import com.aldebaran.qi.Consumer;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.human.AttentionState;
import com.aldebaran.qi.sdk.object.human.EngagementIntentionState;
import com.aldebaran.qi.sdk.object.human.ExcitementState;
import com.aldebaran.qi.sdk.object.human.Gender;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.human.PleasureState;
import com.aldebaran.qi.sdk.object.human.SmileState;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;

import java.util.List;

public class HumanAwarenessHandler {
    private static final String TAG = "QiChatbotActivity";
    private HumanAwareness humanAwareness;

    public HumanAwarenessHandler(QiContext qiContext) {
        registerHumanAwareness(qiContext);
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
                PepperUtil.saySynchronously(humanAttributes, qiContext);
                if (smileState == SmileState.NOT_SMILING) {
                    PepperUtil.saySynchronously("Warum lächelst du nicht?", qiContext);
                }
            }

        }
    }

    private String translateGender(Gender gender) {
        return (gender == Gender.FEMALE ? "weiblich" : (gender == Gender.MALE ? "männlich" : "unbekannt"));
    }

}
