package com.rob.core.utils.java.messages;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


/**
 * @author Roberto97
 * Class used to incapsulate the content returned to the FE and also the messages (IMessage).
 * This class is used for single objects. For collection call MessageResources.
 * @param <T> 
 * 
 */
public class MessageResource<T> extends MessageSupport{

	/**
	 * Content returned to FE. It could be of any type as long as is not a collection 
	 */
	private final T content;
	
	
	/**
	 * Creates an empty Resource.
	 */
	MessageResource() {
		this.content = null;
	}

	/**
	 * Creates a new Resource with the given content and messages.
	 * 
	 * @param content must not be null.
	 * @param messages the messages to add to the Resource.
	 */
	public MessageResource(T content, IMessage... messages) {
		this(content, Arrays.asList(messages));
	}

	public MessageResource(T content, Iterable<? extends IMessage> messages) {

		Assert.notNull(content, "Content must not be null!");
		Assert.isTrue(!(content instanceof Collection), "Content must not be a collection! Use Resources instead!");
		this.content = content;
		this.add(messages);
	}

    public MessageResource(T content) {
    	Assert.notNull(content, "Content must not be null!");
		Assert.isTrue(!(content instanceof Collection), "Content must not be a collection! Use MessageResources instead!");
		this.content = content;
    }
    
    /**
	 * Returns the underlying entity.
	 * 
	 * @return the content
	 */
	@JsonUnwrapped
	public T getContent() {
		return content;
	}

	
	@Override
	public String toString() {
		return String.format("MessageResource { content: %s, %s }", getContent(), super.toString());
	}

	
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}

		MessageResource<?> that = (MessageResource<?>) obj;

		boolean contentEqual = this.content == null ? that.content == null : this.content.equals(that.content);
		return contentEqual ? super.equals(obj) : false;
	}

	
	@Override
	public int hashCode() {

		int result = super.hashCode();
		result += content == null ? 0 : 17 * content.hashCode();
		return result;
	}
}
