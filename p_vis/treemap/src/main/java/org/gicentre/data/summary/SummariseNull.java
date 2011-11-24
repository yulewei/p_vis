package org.gicentre.data.summary;

import java.util.List;

public class SummariseNull extends SummariseField {

	public SummariseNull(String name) {
		this.name = name;
	}

	public Object compute(List<Object> values) {
		return null;
	}

}