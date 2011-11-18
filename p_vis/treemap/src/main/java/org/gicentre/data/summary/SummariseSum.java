package org.gicentre.data.summary;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.Record;


public class SummariseSum extends SummariseField{

	public SummariseSum(String name,DataField dataField){
		this(name,dataField,dataField.getFieldType());
	}

	public SummariseSum(String name,DataField dataField,FieldType fieldType){
		this.name=name;
		this.dataField=dataField;
		this.fieldType=fieldType;
	}

	public Object compute(List<Record> records){
		if (dataField.getFieldType()==FieldType.STRING){
			return null;
		}
		double sum=0;
		HashSet<Object> valuesUsed=new HashSet<Object>();
		for (Record record:records){
			if (useRecord(record, valuesUsed)){
				Object v=record.getValue(dataField);
				if (v!=null && v instanceof Number){
					sum+=((Number)v).doubleValue();
				}
			}
		}
		return correctObjectType(sum);
	}

}