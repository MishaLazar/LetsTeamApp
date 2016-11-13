package com.misha.mor.letsteamapp.letsteamapp;

import java.util.Comparator;

/**
 * Created by Misha on 9/28/2016.
 */
public class Comparators {

    public static Comparator<ChatMessage> MessageOrder = new Comparator<ChatMessage>() {
        @Override
        public int compare(ChatMessage o1, ChatMessage o2) {
            if (DateSpliter(o1.getDate()) == DateSpliter(o2.getDate())) {
                if (timeSpliter(o1.getDate()) > timeSpliter(o2.getDate())) {
                    return 1;
                } else
                    return -1;
            } else if (DateSpliter(o1.getDate()) > DateSpliter(o2.getDate())) {
                return 1;
            }
            return -1;
        }
    };
    private static int DateSpliter(String Date){
        String[] fst_tokens = Date.split("-");
        String[] snd_tokens = fst_tokens[0].split("/");
        int day = Integer.parseInt(snd_tokens[2].trim());
        return day;
    }

    private static int timeSpliter(String Date){
        String[] fst_tokens = Date.split("-");
        String[] snd_tokens = fst_tokens[1].split(":");
        int hours = Integer.parseInt(snd_tokens[0].trim());
        int minutes = Integer.parseInt(snd_tokens[1].trim());
        int seconds = Integer.parseInt(snd_tokens[2].split("]")[0].trim());
        int result = (hours*60)*60+minutes*60+seconds;
        return result;
    }
    public static Comparator<ChatMessage> MessageOrder2 = new Comparator<ChatMessage>() {
        @Override
        public int compare(ChatMessage o1, ChatMessage o2) {
            if (o1.getIdCounter() == o2.getIdCounter()) {
                /*if (timeSpliter(o1.getDate()) > timeSpliter(o2.getDate())) {
                    return 1;
                } else
                    return -1;*/
                return 0;
            } else if (o1.getIdCounter() > o2.getIdCounter()) {
                return 1;
            }
            return -1;
        }
    };
}
