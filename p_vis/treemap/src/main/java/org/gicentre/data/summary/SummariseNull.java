package org.gicentre.data.summary;

import java.util.List;

import org.gicentre.data.Record;

public class SummariseNull extends SummariseField {

	public SummariseNull(String name) {
		this.name = name;
	}

	public Object compute(List<Record> records) {
		return null;
	}

}