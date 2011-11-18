package org.gicentre.data.summary;


import java.util.List;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.Record;


public class SummariseNormalisedSum extends SummariseField{

	protected DataField normaliseField;//this would usually be area
	
	/** Class that computes a normalised sum from a set of records
	 * 
	 * (uses 'float' data type as default)
	 * 
	 * @param name
	 * @param dataField
	 * @param normaliseField
	 */
	public SummariseNormalisedSum(String name,DataField dataField,DataField normaliseField){
		this(name,dataField,normaliseField,FieldType.FLOAT);
	}

	public SummariseNormalisedSum(String name,DataField dataField,DataField normaliseField,FieldType fieldType){
		this.name=name;
		this.dataField=dataField;
		this.fieldType=fieldType;
		this.normaliseField=normaliseField;
	}

	public Object compute(List<Record> records){
		if (dataField.getFieldType()==FieldType.STRING || normaliseField.getFieldType()==FieldType.STRING){
			return null;
		}
		double sum1=0;
		double sum2=0;
		try{
			sum1=((Number)new SummariseSum(null,dataField).compute(records)).doubleValue();
			sum2=((Number)new SummariseSum(null,normaliseField).compute(records)).doubleValue();
		}
		catch (NullPointerException e){
			//returns null (no data) if either of the stats (above) return null (no data)
			return null;
		}
		double normalised= (sum1/sum2);
		return correctObjectType(normalised);
	}

}