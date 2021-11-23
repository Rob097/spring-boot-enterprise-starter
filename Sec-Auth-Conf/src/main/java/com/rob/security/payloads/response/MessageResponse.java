package com.rob.security.payloads.response;

/**
 * @author Roberto97 This is the entity used for the response of Messages. It
 *         allows to pass all the elements as a single object.
 */
public class MessageResponse {
	private String message;

	public MessageResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
