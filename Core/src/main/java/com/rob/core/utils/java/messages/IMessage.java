package com.rob.core.utils.java.messages;

import com.rob.core.utils.java.WithCode;

/**
 * @author Roberto97
 * Interface used to define the type of message returned to the FE
 */
public interface IMessage extends WithCode<String>{
	enum Level {
		TRACE,
		DEBUG,
		INFO,
		WARNING,
		ERROR,
		FATAL
	}
	String getText();
	String getCode();
	Level getLevel();
}