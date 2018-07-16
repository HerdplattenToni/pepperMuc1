package com.capgemini.phmasson.pepperworkshop;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.Say;

import java.util.List;
import java.util.Random;

public final class PepperUtil {

    public static void saySynchronously(String s, QiContext qiContext) {
        Phrase phrase = new Phrase(s);
        Say say = SayBuilder.with(qiContext).withPhrase(phrase).build();
        say.run();
    }

    public static <T> T oneOf(List<T> possibilities) {
        int index = new Random().nextInt(possibilities.size());
        return possibilities.get(index);
    }
}
