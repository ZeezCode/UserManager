package com.aidanmurphey.usermanager.exceptions;

public class CommandFailedException extends Exception {

    public CommandFailedException() {
        super();
    }
    public CommandFailedException(String message) { super(message); }
    public CommandFailedException(String message, Throwable cause) { super(message, cause); }
    public CommandFailedException(Throwable cause) { super(cause); }

}
