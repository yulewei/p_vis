package org.gicentre.data.summary;

import java.util.HashMap;
import java.util.List;

import edu.zjut.common.data.attr.MeasureField;

public class SummariseDiversity extends SummariseField {

	public SummariseDiversity(MeasureField dataField) {
		super(dataField);
	}

	public Object compute(List<Object> values) {
		HashMap<Object, Integer> counts = new HashMap<Object, Integer>();

		for (Object v : values) {
			Integer count = counts.get(v);
			if (count == null) {
				count = 0;
			}
			count++;
			counts.put(v, count);
		}

		// sum them
		int sumCounts = 0;
		for (Integer count : counts.values()) {
			sumCounts += count;
		}

		// find cumulative differences for all counts against every other count
		int cumSumDiffs = 0;
		for (Object value : counts.keySet()) {
			int refCount = counts.get(value);
			for (Integer count : counts.values()) {
				cumSumDiffs += Math.abs(refCount - count);
			}
		}
		return correctObjectType(new Double((double) cumSumDiffs / sumCounts));
	}
}