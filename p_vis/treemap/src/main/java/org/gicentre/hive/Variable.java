package org.gicentre.hive;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// ******************************************************************************************************
/** Represents a variable, constant or a set of grouped variables or constants used in a HiVE expression.
 *  Variables are references to data types and in HiVE are prefixed with a $ symbol. Constants can either
 *  be one of the pre-existing 'presets' such as <code>FX</code> or <code>DEFAULT</code>, or they can be 
 *  user defined. Constants should generally be named with capital letters and not prefixed with any
 *  symbol. Variables or constants can be grouped in square brackets if they apply to the same level of 
 *  the hierarchy, e.g. <code>[$lon,$lat]</code>. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally proposed
 *  in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address research
 *  questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 2nd July, 2010.
 */
// ******************************************************************************************************

/* This file is part of HiVE - the Hierarchical Visualization Expression language.
   HiVE is free software: you can redistribute it and/or modify it under the terms of the 
   GNU Lesser General Public License as published by the Free Software Foundation, either version 3 
   of the License, or (at your option) any later version.

   HiVE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
   implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
   Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with HiVE (see 
   COPYING.LESSER included with this source code). If not, see <http://www.gnu.org/licenses/>.
*/

public class Variable 
{      
    // --------------------- Object variables and enumerated constants --------------------
    
	private static Pattern NOT_ALPHA_NUM = Pattern.compile("\\W[^\u4e00-\u9fa5]");    // Non-alphanumeric regular expression.
    private String name;                                // Name of variable, preset or constant.
    private List<Float>params;                          // Optional numeric parameters.
    private Preset preset;                              // Preset type (if used).
    private VariableType type;
    private enum VariableType {UNDEFINED,VARIABLE, CONSTANT, LAYOUT_PRESET, FOCUS_PRESET, DEFAULT}
    
    private static DecimalFormat formatter = new DecimalFormat("#0.######");
            
    // ----------------------------------- Constructors ------------------------------------ 
      
    /** Creates a variable or constant with the given name. If the name starts with a $ symbol
     *  it will be stored as a data variable. If not, the name is regarded as a constant, which
     *  will be matched against the list of variable presets. If it does not match, it is 
     *  regarded as a user-defined constant.
     *  @param varText Text representing variable, constant or preset.
     */
    public Variable(String varText)
    { 	
        type = VariableType.DEFAULT;
        params = new ArrayList<Float>();
                
        // We are now using round brackets to parameterise variables and constants.
        String[] tokens = varText.split("\\(|\\)|,");
        if (tokens.length >= 1)
        {
            name = tokens[0].trim();
            
            // Check for optional parameters
            for (int i=1; i<tokens.length; i++)
            {
                try
                {
                    params.add(new Float(tokens[i]));
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Cannot extract parameter from "+varText);
                }
            }
        }
               
        if (name.startsWith("$"))
        {
            name = name.substring(1);
            
            if (name.length() == 0)
            {
                System.err.println("No name provided for variable.");
                type = VariableType.UNDEFINED;
            } 
     
            if (NOT_ALPHA_NUM.matcher(name).find())
            {
                System.err.println("Variable '"+name+"' contains an invalid character.");
                type = VariableType.UNDEFINED;
            }
            else
            {
                type = VariableType.VARIABLE;
            }
        }
        else
        {
            if (NOT_ALPHA_NUM.matcher(name).find())
            {
                System.err.println("Constant '"+name+"' contains an invalid character.");
                type = VariableType.UNDEFINED;
            }
            else
            {
                // Try to match with existing presets before defaulting to a user-defined constant.
                preset = Preset.parse(name);
                
                if (preset == null)
                {
                    if (name.length() == 0)
                    {
                        System.err.println("No text provided for constant.");
                        type = VariableType.UNDEFINED;
                    } 
                    else
                    {
                        type = VariableType.CONSTANT;
                    }
                }
                else
                {
                    if (preset.isLayout())
                    {
                        type = VariableType.LAYOUT_PRESET;
                    }
                    else if (preset.isFocus())
                    {
                        type = VariableType.FOCUS_PRESET;
                    }
                    
                    // Standardise the name of the preset
                    name = preset.toString();
                }
            }
        }
    }
    
    
    /** Reports whether or not this is a data variable. If not, it will be either a preset or a 
     *  user-defined constant. If a group of variables is being stored this will always return 
     *  false. To query a variable in a group, use the parameterised version of this method.
     *  @return True if this is a single data variable.
     */
    public boolean isDataVariable()
    {
        if (type == VariableType.VARIABLE)
        {
            return true;
        }
        return false;
    }
    
    /** Reports whether or not the variable in the given position in a group is a data variable. 
     *  If not, it will be either a preset or a user-defined constant.
     *  @param groupIndex Position in group of variable to query (first element is 0, second is 1 etc.).
     *  @return True if the variable at the given position in the group is a single data variable.
     */
    public boolean isDataVariable(int groupIndex)
    {
    	if (groupIndex == 0)
    	{
    		return isDataVariable();
    	}
    	System.err.println("No group defined for "+toString());
    	return false;
    }
    
    /** Reports whether or not this variable could represent a layout preset. Note that this method will
     *  return true if either a predefined layout preset or a default setting is used. If a group of
     *  variables is being stored this will always return false. To query a variable in a group, use the
     *  parameterised version of this method.
     *  @return True if this could store a single layout preset value.
     */
    public boolean isLayoutPreset()
    {    	
        if ((type == VariableType.DEFAULT) || (type == VariableType.LAYOUT_PRESET))
        {
            return true;
        }
        return false;
    }
       
    
    
    /** Reports whether or not the variable in the given position in a group could store a layout preset. 
     *  This method will return true if either a predefined layout preset or a default setting is used.
     *  @param groupIndex Position in group of variable to query (first element is 0, second is 1 etc.).
     *  @return True if the variable at the given position in the group could store a layout preset value.
     */
    public boolean isLayoutPreset(int groupIndex)
    {
    	if (groupIndex == 0)
    	{
    		return isLayoutPreset();
    	}
    	System.err.println("No group defined for "+toString());
    	return false;
    }
    
    /** Reports whether or not this variable could represent a focus type preset. Note that this 
     *  method will return true if either a predefined focus preset or a default setting is used. If a 
     *  group of variables is being stored this will always return false. To query a variable in a
     *  group, use the parameterised version of this method.
     *  @return True if this could store a single focus type preset.
     */
    public boolean isFocusPreset()
    {    	
        if ((type == VariableType.DEFAULT) || (type == VariableType.FOCUS_PRESET))
        {
            return true;
        }
        return false;
    }
    
    
    /** Reports whether or not this variable represents a user-defined constant. If a group of 
     *  variables is being stored this will always return false. To query a variable in a group, use 
     *  the parameterised version of this method.
     *  @return True if this stores a single user-defined constant.
     */
    public boolean isUserConstant()
    {
        if (type == VariableType.CONSTANT)
        {
            return true;
        }
        return false;
    }
    
    /** Reports whether or not the variable in the given position in a group represents a user-defined constant. 
     *  @param groupIndex Position in group of variable to query (first element is 0, second is 1 etc.).
     *  @return True if the variable at the given position in the group is a user-defined constant.
     */
    public boolean isUserConstant(int groupIndex)
    {
    	if (groupIndex == 0)
    	{
    		return isUserConstant();
    	}
    	System.err.println("No group defined for "+toString());
    	return false;
    }
    
    
    /** Reports the HiVE notation used to represent this variable, constant, preset or group.
     *  @return HiVE notation for this variable, constant, preset or group.
     */
    public String toString()
    {    	
        StringBuffer paramText = new StringBuffer();
        
        if (params.size() > 0)
        {
            paramText.append("("+formatter.format(params.get(0)));
            for (int i=1; i<params.size(); i++)
            {
                paramText.append(","+formatter.format(params.get(i)));
            }
            paramText.append(")");
        }
        
        if (type == VariableType.VARIABLE)
        {
            return "$"+name+paramText;
        }
        
        // Must be a constant or preset.
        return name+paramText;
    }
    
    /** Reports the numeric parameters associated with this variable or constant. If a group of variables
     *  is being stored this will always return null. To query a variable in a group, use the parameterised
     *  version of this method.
     *  @return Numeric parameters associated with this variable or constant or null if no parameters.
     */
    public float[] getParameters()
    {
    	
        if (params.size() == 0)
        {
            return null;
        }
        
        float[] paramArray = new float[params.size()];
        for (int i=0; i<params.size(); i++)
        {
            paramArray[i] = params.get(i).floatValue();
        }
        return paramArray;
    }
    
    
    /** Reports the preset used by this variable. If a group of variables is being stored this will always
     *  return null. To query a variable in a group, use the parameterised version of this method. Can be 
     *  null if this is a variable or user constant.
     *  @return Preset representing this variable or null if variable or user constant.
     */
    Preset getPreset()
    {
    	// Note package-wide scope for this method as there should be no reason to expose the Preset
        // enumerated types externally.
    	
        return preset;
    }
    
    
    /**Provides the name of the variable
     * 
     * @return Name
     */
    public String getName(){
    	return this.name;
    }
    
    /** Provides the a full name for the variable, constant, preset or group. This can be useful for 
     *  representing the variable in English rather than HiVE notation.
     *  @return Name describing the preset.
     */
    public String getFullName()
    {
    	
        if (type==VariableType.UNDEFINED)
        {
            return "undefined variable or constant";
        }
        
        if ((type == VariableType.VARIABLE) || (type == VariableType.CONSTANT))
        {
            return name;
        }
        
        // Must be a preset.
        return preset.getFullName();
    }
}