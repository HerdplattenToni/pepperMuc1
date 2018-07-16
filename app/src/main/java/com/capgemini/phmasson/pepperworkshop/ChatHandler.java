package com.capgemini.phmasson.pepperworkshop;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChatHandler {
    private static final String TAG = "QiChatbotActivity";
    private Chat chat;

    public ChatHandler(QiContext qiContext) {
        createChatbot(qiContext);
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
        registerExecutors(qiChatbot, qiContext);
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

    private void registerExecutors(QiChatbot qiChatbot, QiContext qiContext) {
        Map<String, QiChatExecutor> executors = new HashMap<>();
        executors.put("danceExecutor", new DanceHandler(qiContext));
        qiChatbot.setExecutors(executors);
    }

    public void unregisterChatHandler() {
        if (chat != null) {
            chat.removeAllOnStartedListeners();
        }
    }

}
