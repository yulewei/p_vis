package org.gicentre.data.summary;


import java.util.HashSet;
import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Record;


public class SummariseStandardDeviation extends SummariseField{
	
	
	public SummariseStandardDeviation(String name,DataField dataField){
		this(name,dataField,FieldType.FLOAT);
	}

	public SummariseStandardDeviation(String name,DataField dataField,FieldType fieldType){
		this.name=name;
		this.dataField=dataField;
		this.fieldType=fieldType;
	}

	public Object compute(List<Record> records){
		if (dataField.getFieldType()==FieldType.STRING){
			return null;
		}
		double mean=((Number)new SummariseMean(null,dataField).compute(records)).doubleValue();

		double temp=0;
		int n=0;
		HashSet<Object> valuesUsed=new HashSet<Object>();
		for (Record record: records){
			if (useRecord(record, valuesUsed)){
				Object v=record.getValue(dataField);
				if (v!=null){
					temp+=Math.pow(((Number)v).doubleValue()-mean,2);
				}
				n++;
			}
		}
		temp/=n-1;
		temp=Math.sqrt(temp);
		return correctObjectType(temp);
	}
	
}