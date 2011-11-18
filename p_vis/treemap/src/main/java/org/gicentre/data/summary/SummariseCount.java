package org.gicentre.data.summary;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.Record;


public class SummariseCount extends SummariseField{
	
	public SummariseCount(String name){
		this.name=name;
		this.fieldType=FieldType.INT;
	}
	
	public Object compute(List<Record> records){
		if (dataField==null){
			return records.size();
		}
		else{
			int c=0;
			HashSet<Object> valuesUsed=new HashSet<Object>();
			for(Record record:records){
				if (useRecord(record, valuesUsed)){
					c++;
				}
			}
			return c;
		}
	}

}