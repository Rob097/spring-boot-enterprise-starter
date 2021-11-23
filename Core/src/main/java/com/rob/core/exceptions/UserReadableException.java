package com.rob.core.exceptions;

import java.util.List;

import com.rob.core.utils.java.messages.IMessage;

public class UserReadableException extends CustomException{

	private static final long serialVersionUID = 4904837161296936067L;
	
	public UserReadableException(String message) {
		super(message, true);
	}
	
	public UserReadableException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UserReadableException(IMessage message) {
		super(message);
	}

	public UserReadableException(IMessage message, List<? extends IMessage> messages) {
		super(message, messages);
	}
	
	public UserReadableException(IMessage message, Throwable cause) {
		super(message, cause);
	}

	public UserReadableException(IMessage message, List<? extends IMessage> messages, Throwable cause) {
		super(message, messages, cause);
	}
	
}