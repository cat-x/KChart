package com.stomped.stomped.component;


import androidx.annotation.Nullable;

import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StompedMessageParser {

    private final static String TAG = "StompedMessageParser";

    private StompedMessageParser() {
    }

    public static StompedFrame constructFrame(String message) {

        int currentPosition = 1;
        String command;
        StompedHeaders headers = new StompedHeaders();
        StringBuilder body = new StringBuilder();

        String[] splitMessage = message.split("\n");

        command = splitMessage[0];

        for (int i = currentPosition; i < splitMessage.length; i++) {
            if (splitMessage[i].equals("")) {
                currentPosition = i;
                break;
            } else {
                String[] header = splitMessage[i].split(":");
                headers.addHeader(header[0], header[1]);
            }
        }

        for (int i = currentPosition; i < splitMessage.length; i++) {
            body.append(splitMessage[i]);
        }

        return StompedFrame.construct(command, headers, body.toString());
    }

    public static final String TERMINATE_MESSAGE_SYMBOL = "\u0000";

    private static final Pattern PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)");

    public static StompedFrame constructFrame2(@Nullable String data) {
        if (data == null || data.trim().isEmpty()) {
            return StompedFrame.construct("UNKNOWN");
        }
        Scanner reader = new Scanner(new StringReader(data));
        reader.useDelimiter("\\n");
        String command = reader.next();
        StompedHeaders headers = new StompedHeaders();

        while (reader.hasNext(PATTERN_HEADER)) {
            Matcher matcher = PATTERN_HEADER.matcher(reader.next());
            matcher.find();
            headers.addHeader(matcher.group(1), matcher.group(2));
        }

        reader.skip("\\s");

        reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL);
        String payload = reader.hasNext() ? reader.next() : null;

        return StompedFrame.construct(command, headers, payload);
    }

}
