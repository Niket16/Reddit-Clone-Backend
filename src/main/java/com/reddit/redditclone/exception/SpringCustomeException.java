package com.reddit.redditclone.exception;

public class SpringCustomeException extends RuntimeException {
    public SpringCustomeException(String message,Exception exception) {
        super(message,exception);
    }
    public SpringCustomeException(String message) {
        super(message);
    }
}