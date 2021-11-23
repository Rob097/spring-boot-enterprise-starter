package com.rob.core.exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rob.core.utils.java.Displayable;
import com.rob.core.utils.java.WithCode;
import com.rob.core.utils.java.messages.IMessage;

public class CustomException extends Exception implements WithCode<String>, Displayable {

private static final long serialVersionUID = -7514258454075372293L;
	
	private String code;
	private boolean displayed;
	private String displayContent;

	private List<? extends IMessage> messages;
	
	public CustomException() {
		super();
	}
	
	public CustomException(String message) {
		this(message, false);
	}

	public CustomException(String message, boolean displayed) {
		super(message);
		setDisplayInfo(message, displayed);
	}
	
	public CustomException(Throwable cause) {
		super(cause);
		setDisplayInfo(cause);
	}
	
	public CustomException(String message, Throwable cause) {
		this(message, cause, false);
	}

	public CustomException(String message, Throwable cause, boolean displayed) {
		super(message, cause);
		setDisplayInfo(message, displayed, cause);
	}

	public CustomException(IMessage message) {
		this(message, false);
	}
	
	public CustomException(IMessage message, boolean displayed) {
		this(message.getText(), displayed);
		if (message instanceof WithCode) {
			this.code = ((WithCode<String>) message).getCode();
		}
		setDisplayInfo(message.getText(), displayed);
	}

	public CustomException(IMessage message, List<? extends IMessage> messages) {
		this(message, messages, false);
	}

	public CustomException(IMessage message, List<? extends IMessage> messages, boolean displayed) {
		this(message.getText(), displayed);
		if (message instanceof WithCode) {
			this.code = ((WithCode<String>) message).getCode();
		}
		if (messages == null || messages.isEmpty()){
			this.messages = Collections.emptyList();
		}else{
			this.messages = Collections.unmodifiableList(messages);
		}
	}

	public CustomException(IMessage message, Throwable cause) {
		this(message, cause, false);
	}

	public CustomException(IMessage message, Throwable cause, boolean displayed) {
		this(message.getText(), cause, displayed);
		if (message instanceof WithCode) {
			this.code = ((WithCode<String>) message).getCode();
		}
	}

	public CustomException(IMessage message, List<? extends IMessage> messages, Throwable cause) {
		this(message, messages, cause, false);
	}

	public CustomException(IMessage message, List<? extends IMessage> messages, Throwable cause, boolean displayed) {
		this(message.getText(), cause, displayed);
		if (message instanceof WithCode) {
			this.code = ((WithCode<String>) message).getCode();
		}
		if (messages == null || messages.isEmpty()){
			this.messages = Collections.emptyList();
		}else{
			this.messages = Collections.unmodifiableList(messages);
		}
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	@Override
	public String getDisplayContent() {
		return displayContent;
	}

	public void setDisplayContent(String displayContent) {
		this.displayContent = displayContent;
	}

	public List<? extends IMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<? extends IMessage> messages) {
		this.messages = messages;
	}

	private void setDisplayInfo(String message, boolean displayed){
		setDisplayInfo(message, displayed, null);
	}

	private void setDisplayInfo(Throwable cause){
		setDisplayInfo(null, false, cause);
	}

	private void setDisplayInfo(String message, boolean displayed, Throwable cause){
		Displayable d = Displayable.asDisplayable(Optional.ofNullable(cause));
		if (d.isDisplayed()) {
			this.displayed = true;
			this.displayContent = d.getDisplayContent();
		} else if (displayed){
			this.displayed = true;
			this.displayContent = message;
		}
	}

}
