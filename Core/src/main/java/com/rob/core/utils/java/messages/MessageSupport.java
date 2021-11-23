package com.rob.core.utils.java.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author Roberto97
 * Class extended by MessageResource and MEssageResources.
 */
public class MessageSupport {
	private final List<IMessage> messages;

	public MessageSupport() {
		this.messages = new ArrayList<>();
	}

	/**
     * Adds the given message to the resource.
     *
     * @param message
     */
    public void add(IMessage message) {
        Assert.notNull(message, "Message must not be null!");
        this.messages.add(message);
    }

    /**
     * Adds all given IMessages to the resource.
     *
     * @param messages
     */
    public void add(Iterable<? extends IMessage> messages) {
        Assert.notNull(messages, "Given messages must not be null!");
        for (IMessage candidate : messages) {
            add(candidate);
        }
    }

    /**
     * Adds all given IMessages to the resource.
     *
     * @param messages must not be null.
     */
    public void add(IMessage... messages) {
        Assert.notNull(messages, "Given messages must not be null!");
        add(Arrays.asList(messages));
    }

    /**
     * Returns whether the resource contains IMessages at all.
     *
     * @return
     */
    public boolean hasMessages() {
        return !this.messages.isEmpty();
    }

	/**
	 * Returns all IMessages contained in this resource.
	 * 
	 * @return
	 */
	@JsonProperty(value = "_messages")
    public List<IMessage> getMessages() {
        return messages;
    }

	/**
     * Removes all IMessages added to the resource so far.
     */
    public void removeMessages() {
        this.messages.clear();
    }


	@Override
	public String toString() {
		return String.format("messages: %s", messages.toString());
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		MessageSupport that = (MessageSupport) obj;

		return this.messages.equals(that.messages);
	}


	@Override
	public int hashCode() {
		return this.messages.hashCode();
	}
}
