package org.gicentre.data.summary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gicentre.data.DataField;
import org.gicentre.data.FieldType;
import org.gicentre.data.Data.Record;




public abstract class SummariseField{
	protected String name;
	protected FieldType fieldType;
	protected DataField dataField;
	protected String units;
	
	public String getName() {
		return name;
	}

	public DataField getDataField() {
		return dataField;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	/**Get the units which this measurement is in
	 * 
	 * @return
	 */
	public String getUnits() {
		return units;
	}

	public void setUnits(String units){
		this.units=units;
	}
	
	public abstract Object compute(List<Record> records);

	protected Object correctObjectType(Object value){
		if (value==null){
			return null;
		}
		if (fieldType==FieldType.STRING){
			return value.toString();
		}
		if (fieldType==FieldType.INT){
			if (value instanceof Integer){
				return value;
			}
			if (value instanceof String){
				return Integer.parseInt((String)value);
			}
			else{
				return ((Number)value).intValue();
			}
		}
		if (fieldType==FieldType.LONG){
			if (value instanceof Long){
				return value;
			}
			if (value instanceof String){
				return Long.parseLong((String)value);
			}
			else{
				return ((Number)value).longValue();
			}
		}
		if (fieldType==FieldType.FLOAT){
			if (value instanceof Float){
				return value;
			}
			if (value instanceof String){
				return Float.parseFloat((String)value);
			}
			else{
				return ((Number)value).floatValue();
			}
		}
		if (fieldType==FieldType.DOUBLE){
			if (value instanceof Double){
				return value;
			}
			if (value instanceof String){
				return Double.parseDouble((String)value);
			}
			else{
				return ((Number)value).doubleValue();
			}
		}
		return null;
	}
	
	/** this checks if the record should be used. Returns true unless
	 * dependent datafield applies at a specific level, in which case
	 * it will only return true if the value of the appliesTo data variable
	 * has not been accessed.
	 * 
	 * @param record The record in question
	 * @param valuesUsed Start with an empty set and pass the same one
	 * into this for each record in this summarisation. This method will
	 * modify this set if needed
	 * 
	 * @return
	 */
	protected boolean useRecord(Record record,Set<Object> valuesUsed){
		if (dataField.getAppliesToHierField()==null){
			return true;
		}
		else{
			DataField appliesToField = dataField.getAppliesToHierField();
			Object v=record.getValue(appliesToField);
			if (valuesUsed.contains(v)){
				return false;
			}
			else{
				valuesUsed.add(v);
				return true;
			}
		}
	}
	
	public String toString(){
		return name;
	}

}