package com.example.keepintouch;

public class Hom_model {
    public  static boolean seen;
    public long timestamp;

    public Hom_model(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public Hom_model() {
    }

    public static boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
