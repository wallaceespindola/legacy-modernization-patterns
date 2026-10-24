package com.wallaceespindola.modernization.posting;

/** Safe posting: a timeout, which means unknown outcome and not failure. */
public class PostingTimeoutException extends RuntimeException {

    public PostingTimeoutException(String message) {
        super(message);
    }
}
