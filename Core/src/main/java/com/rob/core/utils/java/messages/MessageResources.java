package com.rob.core.utils.java.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roberto97
 * Class used to incapsulate the content returned to the FE and also the messages (IMessage).
 * This class is used for collection. For single objects call MessageResource.
 * @param <T> 
 * 
 */
public class MessageResources<T> extends MessageSupport implements Iterable<T> {

	private final Collection<T> content;

	/**
	 * Creates an empty MessageResources instance.
	 */
	protected MessageResources() {
		this(new ArrayList<T>());
	}


	/**
	 * Creates a MessageResources instance with the given content.
	 * 
	 * @param content must not be null.
	 */
	public MessageResources(Iterable<T> content) {

		Assert.notNull(content, "Content must not be null");

		this.content = new ArrayList<T>();

		for (T element : content) {
			this.content.add(element);
		}
	}
	
	/**
	 * Creates a Resources instance with the given content and messages.
	 * 
	 * @param content must not be  null.
	 * @param messages the messages to be added to the Resources.
	 */
	public MessageResources(Iterable<T> content, IMessage... messages) {
		this(content, Arrays.asList(messages));
	}

	public MessageResources(Iterable<T> content, Iterable<? extends IMessage> messages) {

		Assert.notNull(content, "Content must not be null!");

		this.content = new ArrayList<T>();

		for (T element : content) {
			this.content.add(element);
		}
		if (messages!=null) {
			this.add(messages);
		}
	}

	/**
	 * Creates a new MessageResources instance by wrapping the given domain class instances into a MessageResource.
	 * 
	 * @param content must not be null.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends MessageResource<S>, S> MessageResources<T> wrap(Iterable<S> content) {

		Assert.notNull(content, "Content must not be null");
		ArrayList<T> resources = new ArrayList<T>();

		for (S element : content) {
			resources.add((T) new MessageResource<S>(element));
		}

		return new MessageResources<T>(resources);
	}

	/**
	 * Returns the underlying elements.
	 * 
	 * @return the content will never be null.
	 */
	@JsonProperty("content")
	public Collection<T> getContent() {
		return Collections.unmodifiableCollection(content);
	}

	
	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}

	
	@Override
	public String toString() {
		return String.format("MessageResources { content: %s, %s }", getContent(), super.toString());
	}

	
	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}

		MessageResources<?> that = (MessageResources<?>) obj;

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