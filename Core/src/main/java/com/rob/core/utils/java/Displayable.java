package com.rob.core.utils.java;

import java.util.Optional;

public interface Displayable {
	static Displayable asDisplayable(Optional<Object> optional) {

		return optional.map(o -> {
			if (o instanceof Displayable){
				return (Displayable)o;
			}
			return null;
		}).orElse(
			new Displayable() {
				@Override
				public boolean isDisplayed() {
					return false;
				}

				@Override
				public String getDisplayContent() {
					return null;
				}
			}
		);
	}

	static String getDisplayContent(Object object, String defaultContent){
		if (!(object instanceof Displayable)){
			return defaultContent;
		}
		Displayable d = (Displayable)object;
		return d.isDisplayed() ? d.getDisplayContent() : defaultContent;
	}

	boolean isDisplayed();
	String getDisplayContent();
}
