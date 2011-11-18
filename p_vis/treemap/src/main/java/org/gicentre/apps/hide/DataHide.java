package org.gicentre.apps.hide;

import java.io.BufferedReader;
import java.util.Collection;

import org.gicentre.data.Data;
import org.gicentre.data.DataField;

public class DataHide extends Data {

	public DataHide(Collection<DataField> dataFields,BufferedReader inputReader, FileType fileType,boolean ignoreFirstLine) {
		super(dataFields, inputReader, fileType, ignoreFirstLine);
		// TODO Auto-generated constructor stub
	}
	
	public String transformToValidString(String value){
		if (value.matches("[^ \\\t\\\f,$*()]*")){
			return value;
		}
		else{
			value=value.replaceAll("[ \\\t\\\f]","_");
			value=value.replaceAll(",$*()]","");
			return value;
		}
	}

}
