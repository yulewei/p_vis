package org.gicentre.data.summary;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.Record;


public class SummariseNull extends SummariseField{
	
	
	public SummariseNull(String name){
		this.name=name;
	}


	public Object compute(List<Record> records){
		return null;
	}

}