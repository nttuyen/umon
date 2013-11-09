package com.nttuyen.android.umon.core.mapper;

/**
 * @author nttuyen266@gmail.com
 */
public interface Mapper<Source, Target> {
	public Target map(Source source, Target target);
}
