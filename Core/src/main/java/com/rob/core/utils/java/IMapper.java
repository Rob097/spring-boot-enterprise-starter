package com.rob.core.utils.java;


/**
 * @author Roberto97
 * Interface used by Mapper and RMapper to convert from DTO to ValueObject
 * @param <I>
 * @param <O>
 */
public interface IMapper<I,O> {
	O map(I input);
	O map(I input, O output);
}
