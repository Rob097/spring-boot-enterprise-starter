package com.rob.uiapi.utils;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;


/**
 * @author Roberto97
 * Class used to encapsulate the body content of post, put and patch requests
 * @param <T>
 */
public class MetadataResource<T> {

	@JsonUnwrapped
	private T content;
	@JsonProperty(value = "_meta")
    private final Map<String, Object> meta;
    
    public MetadataResource() {
    	this(null);
	}

    public MetadataResource(T content) {
		this.content = content;
		this.meta = new HashMap<>();
    }

    public Map<String, Object> getMeta() {
        return meta;
    }
    
    /**
	 * Returns the underlying entity.
	 * 
	 * @return the content
	 */
	
	public T getContent() {
		return content;
	}
	
	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return String.format("Resource { content: %s, %s }", getContent(), super.toString());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}

		MetadataResource<?> that = (MetadataResource<?>) obj;

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
