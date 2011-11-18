package org.gicentre.hive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

// *****************************************************************************************************
/** Represents the parameters that define an expression. Can be used as part of both HiVE state and
 *  operator expressions. Elements that make up an expression include paths that define tree(s) or
 *  subtree(s) in a hierarchy; variables that represent the data that condition or are shown in a graphic
 *  representation; levels which define the level within a tree or subtree; and constants which identify
 *  types of layout, colour rule and other processes applied to data.  For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally 
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.5, 21st February, 2011.
 */
// *****************************************************************************************************

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
  
public class Expression 
{
    // --------------------------------- Object variables ---------------------------------
    
    private Type type;                      // Type of HiVE expression.
    private Path path;                      // Identifies a node in the hierarchy.
    private List<Variable[]> varGroups; // Ordered list of variable groups, constants and presets that make up expression. There may only be one Variable in the group
    private int level;                      // Optional level at which the expression applies.
    private int level2;                     // Optional second level for expressions that require two levels.
        
    // ----------------------------------- Constructor ------------------------------------ 
        
    /** Creates a new empty expression of the given type.
     *  @param type Type of expression (e.g. sHier, sLayout etc.).
     */
    public Expression(Type type)
    {
        // All expressions will have a path that will at least point to the root of the hierarchy.
        // Level is undefined for state expressions.
        this.type = type;
        path      = new Path();
        varGroups = new ArrayList<Variable[]>();
        level     = 1;//level 1 by default
        level2    = -1;
    }
    
    // ------------------------------------- Methods --------------------------------------
    
    /** Reports the type of expression (e.g. sHier, sLayout etc.).
     *  @return Type of expression.
     */
    public Type getType()
    {
        return type;
    }

    /** Reports the path.
     *  @return Path
     */
    public Path getPath()
    {
        return path;
    }

    
    /** Sets the type of expression (e.g. sHier, sLayout etc.).
     *  @param type Type of expression.
     */
    public void setType(Type type)
    {
        this.type = type;
    }
    
    /** Adds the given variable, constant or preset to those stored in the expression.
     * Use when there's only one in the variable group 
     *  @param varName Variable, constant or preset to add to the expression. 
     */
    public void addVar(String varName)
    {
        Variable[] newGroup=new Variable[]{new Variable(varName)};
    	varGroups.add(newGroup);
    }

    /** Adds the given group of variables, constants or presets to those stored in the expression. 
     *  @param varNames Variable, constant or preset group to add to the expression. 
     */
    public void addVarGroup(String[] varNames)
    {
    	Variable[] newGroup = new Variable[varNames.length]; 
    	for(int i=0;i<varNames.length;i++){
    		newGroup[i]=new Variable(varNames[i]);
    	}
        varGroups.add(newGroup);
    }

    
    /** Reports the list of variables, constants and presets stored in the expression in the 
     *  order in which they were added. If nothing has been added to this expression, this will
     *  return an empty list. If only one variable/constant/preset, will be a list of 1.
     *  @return List of variables, constants and presets (empty if nothing added).
     */
    public List<Variable[]> getVarGroups()
    {
        return varGroups;
    }

        
    /** Sets the path for this expression.
     *  @param path New path to be associated with this expression.
     */
    public void setPath(Path path)
    {
        this.path = path;
    }
    
    /** Sets the level at which this expression applies. This is only relevant to operator expressions
     *  since they apply to a single level (or sometimes pair of levels) in the hierarchy. The level is
     *  specified numerically relative to the path associated with the expression, with 1 indicating 
     *  all nodes one level below the path, 2 indicating two levels below etc.
     *  @param level Level at which an operator expression is to apply.
     */
    public void setLevel(int level)
    {
        if (level>0){
        	this.level = level;
        }
        else{
        	System.out.println("Level must be a number greater than 0");
        }
    }
    
    /** Sets the secondary level at which this expression applies. This is only relevant to operator 
     *  expressions that use two levels such as oSwap(). The level is specified numerically relative
     *  to the path associated with the expression, with 1 indicating all nodes one level below the path,
     *  2 indicating two levels below etc.
     *  @param level Level at which an operator expression is to apply.
     */
    public void setSecondLevel(int level)
    {
        this.level2 = level;
    }
    
    /** Reports the level at which this expression applies. This is only relevant to operator expressions
     *  since they apply to a single level (or sometimes pair of levels) in the hierarchy. The level is
     *  specified numerically relative to the path associated with the expression, with 1 indicating 
     *  all nodes one level below the path, 2 indicating two levels below etc. A negative value indicates
     *  that the level is not used in the expression.
     *  @return Level at which an operator expression is to apply.
     */
    public int getLevel()
    {
        return level;
    }
    
    /** Reports the optional second level at which this expression applies. This is only relevant to operator
     *  expressions that apply to two levels (e.g. oSwap()) in the hierarchy. The level is specified numerically
     *  relative to the path associated with the expression, with 1 indicating all nodes one level below the 
     *  path, 2 indicating two levels below etc. A negative value indicates that the level is not used in the 
     *  expression.
     *  @return Second level at which an operator expression is to apply.
     */
    public int getSecondLevel()
    {
        return level2;
    }
    
    /** Parses the given text representing an expression. If parsed successfully this method returns
     *  an Expression object that stores all relevant parameters. The text should contain a single 
     *  expression but may optionally include a terminating semicolon.
     *  @param expressionText Text of expression to parse.
     *  @return The expression represented by the text or null if it cannot be parsed.
     */
    public static Expression parseExpression(String expressionText)
    {
        // Strip any outer whitespace and a possible final semicolon.
        String text = expressionText.trim();
        if (text.endsWith(";"))
        {
            text = text.substring(0, text.length()-1);
        }
        
        if (text.length()==0)
        {
        	return null;
        }
        
        int pos = text.indexOf("(");
        if (pos <=0)
        {
        	 // No valid type provided.
            System.err.println("Cannot parse expression '"+expressionText+"' - no valid type provided.");
            return null; 
        }
        Type type = Type.parse(text.substring(0, pos));
        if (type == null)
        {
            // No valid type provided.
            System.err.println("Cannot parse expression '"+expressionText+"' - no valid type provided.");
            return null; 
        }
        Expression expression = new Expression(type);
        
        // Check we have outer brackets around the contents of the expression.
        text = text.substring(pos).trim();
        if (!text.startsWith("(") || !text.endsWith(")"))
        {
        	 System.err.println("Cannot parse expression '"+expressionText+"' - does not contain parameters in brackets.");
             return null; 
        }
        text = text.substring(1,text.length()-1);
        List<String> params = extractParams(type.toString(),text);
               
        int paramNumber = 0;

        // The first parameter of all expressions should be a path.
        
        if (params.size() > paramNumber)
        {
            expression.setPath(Path.parse(params.get(paramNumber)));
            paramNumber++;
        }
        
        if (expression.path == null)
        {
            // No path provided or path cannot be parsed.
            System.err.println("Cannot parse expression '"+expressionText+"' - no valid path provided.");
            return null;
        }
        
        // Operator level parameters
        if (expression.type.isOperator())
        {
            // All operators with a second parameter should define a level
            if (params.size() > paramNumber)
            {
                try
                {
                    expression.level = Integer.parseInt(params.get(paramNumber));
                    paramNumber++;
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Cannot identify operator level in '"+expressionText+"'");
                    return null;
                }
                
                if (expression.level < 1)
                {
                    System.err.println("Operator level in '"+expressionText+"' should be at least 1, but is given as "+expression.level);
                    return null;
                }
                
                // If a third parameter exists, only oSwap defines a second level, otherwise it should be a variable/constant.
                if (params.size() > paramNumber)
                {
                    if (expression.type == Type.O_SWAP)
                    {
                        try
                        {
                            expression.level2 = Integer.parseInt(params.get(paramNumber));
                            paramNumber++;
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println("Cannot identify operator level in '"+expressionText+"'");
                            return null;
                        }
                        
                        if (expression.level2 < 1)
                        {
                            System.err.println("Operator level in '"+expressionText+"' should be at least 1, but is given as "+expression.level2);
                            return null;
                        }
                    }
                }
            }
        }
        
        // Any remaining parameters for both states and operators will be variables/constants.
        while(params.size() > paramNumber)
        {
        	String varGroupString=params.get(paramNumber).trim();
        	String[] varNames;
        	//test if a group
        	if (varGroupString.startsWith("[") && varGroupString.trim().endsWith("]")){
        		 // if so, make array of names
        		varNames=varGroupString.substring(1,varGroupString.length()-1).split(",");
        	}
        	else{
       		 // otherwise, an array of one
        		varNames=new String[]{varGroupString};
        	}
            expression.addVarGroup(varNames);
            paramNumber++;
        }
        
        // Semantic validation: Check correct number of parameters given.
        int numVars = expression.varGroups.size();
        
        if ((expression.type == Type.O_CUT)  || (expression.type == Type.O_SWAP)) 
        {
            if (numVars > 0)
            {
                System.err.println(expression.type+" should not contain variables or constants, but '"+expressionText+"' given.");
                return null;
            }
        }
        else if ((expression.type == Type.O_INSERT) || (expression.type == Type.O_LAYOUT) ||
                 (expression.type == Type.O_ORDER)  || (expression.type == Type.O_SHAPE)  ||
                 (expression.type == Type.O_SIZE)   || (expression.type == Type.O_COLOR)  ||
                 (expression.type == Type.O_FOCUS))
        {
            if (numVars != 1)
            {
                System.err.println(expression.type+" should contain 1 variable or constant, but '"+expressionText+"' contains "+numVars+".");
                return null;
            }
        }
        
        // Semantic validation: Check that levels are only given to the relevant expressions.
        if (expression.level >1)
        {
            if ((expression.type != Type.O_INSERT) && (expression.type != Type.O_CUT) && (expression.type != Type.O_SWAP) &&
                (expression.type != Type.O_LAYOUT) && (expression.type != Type.O_ORDER) && (expression.type != Type.O_SHAPE) &&
                (expression.type != Type.O_SIZE) && (expression.type != Type.O_COLOR) && (expression.type != Type.O_FOCUS))
            {
                System.err.println(expression.type+" should not contain numeric levels '"+expressionText+"'.");
                return null;
            }
            else if ((expression.type == Type.O_SWAP) && (expression.level2 <=0))
            {
                System.err.println(expression.type+" should contain two numeric levels '"+expressionText+"'.");
                return null;
            }
          
        }
        else if (expression.level < 0)
        {
            // Check that all relevant operators have a level defined.
            if ((expression.type == Type.O_INSERT) || (expression.type == Type.O_CUT)   || (expression.type == Type.O_SWAP) ||
                (expression.type == Type.O_LAYOUT) || (expression.type == Type.O_ORDER) || (expression.type == Type.O_SHAPE) ||
                (expression.type == Type.O_SIZE)   || (expression.type == Type.O_COLOR) || (expression.type == Type.O_FOCUS))
            {
                System.err.println(expression.type+" should contain a numeric level '"+expressionText+"'.");
                return null;
            }
        }

        // Semantic validation: Check hierarchy expressions only contain groups of 1
        if ((expression.type == Type.S_HIER) || (expression.type == Type.O_HIER))
        {
            for (Variable[] varGroups: expression.varGroups)
            {
            	if (varGroups.length>1){
            		System.err.print(expression.type+" must only have groups of 1 ("+expressionText+")");
            		return null;
            	}
            }
        }

        
        // Semantic validation: Check expressions that require particular types of preset have them.
        // and that they contain only groups of one
        if ((expression.type == Type.S_LAYOUT) || (expression.type == Type.O_LAYOUT))
        {
            for (Variable[] presetGrps : expression.varGroups)
            {
            	if (presetGrps.length>1){
            		System.err.print(expression.type+" must only have groups of 1 ("+expressionText+")");
            		return null;
            	}
            	if (presetGrps[0].isLayoutPreset() == false)
                {
                    System.err.print(expression.type+" should contain only layout presets (");
                    for (Preset layoutPreset : Preset.getLayoutPresets())
                    {
                    	System.err.print(layoutPreset+" ");
                    }
                    System.err.println(")  '"+expressionText+"'.");
                    
                    return null;
                }
            }
        }
        
        if ((expression.type == Type.S_FOCUS) || (expression.type == Type.O_FOCUS))
        {
            for (Variable[] presetGrps : expression.varGroups)
            {
            	if (presetGrps.length>1){
            		System.err.print(expression.type+" must only have groups of 1 ("+expressionText+")");
            		return null;
            	}
                if (presetGrps[0].isFocusPreset() == false)
                {
                    System.err.println(expression.type+" should contain only focus presets '"+expressionText+"'.");
                    
                    System.err.print(expression.type+" should contain only focus presets (");
                    for (Preset focusPreset : Preset.getFocusPresets())
                    {
                    	System.err.print(focusPreset+" ");
                    }
                    System.err.println(")  '"+expressionText+"'.");
                    return null;
                }
            }
        }
        
        /* TODO: Parsing currently does not include the following validation:
         *  
           1. If the wrong type of preset is provided to an expression that can take both variables
              and some presets, this is not currently flagged as an error. e.g. oSize(/,1,SP) would 
              be parsed even though it is not valid. 
        */

        return expression;
    }
    
    /** Provides a text representation of a collection hive expressions. This is just a convenience
     *  method for iterating through a collection of expressions and appending their text representations
     *  into a single string.
     * @param expressions Expressions to show as text.
     * @return Text representation of the expressions.
     */
    public static String formatExpression(Collection<Expression> expressions){
    	StringBuffer str = new StringBuffer();
    	for (Expression expression : expressions){
    		str.append(expression);
    		str.append(" ");
    	}
    	return str.toString();
    }
    
    /** Parses the given text representing one or more expressions. This method will always return
     *  an ordered list of Expressions that have been successfully parsed. If nothing has been parsed
     *  successfully the list will be empty, but not null.
     *  @param expressionsText Text of expressions to parse. Each expression should be separated by a
     *         semicolon.
     *  @return Ordered list of successfully parsed expressions.
     */
    public static List<Expression> parseExpressions(String expressionsText)
    {
        ArrayList<Expression>expressions = new ArrayList<Expression>();
        String[] expressionLines = expressionsText.split(";");
        
        for (String expressionText: expressionLines)
        {
            Expression expression = parseExpression(expressionText.trim());
            if (expression != null)
            {
                expressions.add(expression);
            }
        }
        return expressions;
    }
        
    /** Reports the expression as text string.
     *  @return Text representing the expression.
     */
    public String toString()
    {
        StringBuffer expression = new StringBuffer(type+"("+path.toString());
        
        if (type.isOperator())
        {
            expression.append(",");
            expression.append(level);
            
            if (type.isOperator() && level2 >=0)
            {
                expression.append(",");
                expression.append(level2);
            }
        } 
        
        for (Variable[] varGrp : varGroups)
        {
            if (varGrp.length==1){
            	expression.append(","+varGrp[0].toString());
            }
            else{
            	expression.append(",[");
            	for(int i=0;i<varGrp.length;i++){
            		expression.append(varGrp[i]);
            		if (i<varGrp.length-1){
            			expression.append(",");
            		}
            	}
            	expression.append("]");
            }
        }
              
        expression.append(");");
        return expression.toString();
    }
    
    // ------------------------------------------- Private methods -------------------------------------------
    
    /** Creates an collection of HiVE expression parameters from the given text. Parses for parameterised constants
     *  and grouped constants or variables.
     *  @param expressionType HiVE expression type (e.g. sOrder, oSize etc.).
     *  @param expressionText HiVE expression without expression type or enclosing brackets.
     *  @return Collection of strings where each item is a single HiVE parameter. This could be a path, a level, a
     *          variable, a constant or a group of variables or constants. If text cannot be parsed, null is returned.
     */
    private static List<String> extractParams(String expressionType, String expressionText)
    {
    	ArrayList<String>params = new ArrayList<String>();
    	StringBuffer param = new StringBuffer();
    	boolean withinBrackets = false;
    	boolean withinSqBrackets = false;
    	
    	StringTokenizer sToken = new StringTokenizer(expressionText, "()[],", true);
    	while (sToken.hasMoreTokens())
    	{
    		String token = sToken.nextToken().trim();
    		
    		if (token.equals("["))
    		{
    			if (withinSqBrackets)
    			{
    				System.err.println("Cannot use nested square brackets in "+expressionType+"("+expressionText+")");
    				return null;
    			}
    			
    			withinSqBrackets = true;
    			param.append(token);
    			
    		}
    		else if (token.equals("]"))
    		{
    			if (withinSqBrackets)
    			{
    				withinSqBrackets = false;
    				param.append(token);
    				params.add(param.toString().trim());
    				param = new StringBuffer();
    			}
    			else
    			{
    				System.err.println("Square bracket closed without opening bracket in "+expressionType+"("+expressionText+")");
    				return null;
    			}
    		}
    		else if (token.equals("("))
    		{
    			if (withinBrackets)
    			{
    				System.err.println("Cannot use nested brackets in "+expressionType+"("+expressionText+")");
    				return null;
    			}
    			
    			withinBrackets = true;
    			param.append(token);
    		}
    		else if (token.equals(")"))
    		{
    			if (withinBrackets)
    			{
    				withinBrackets = false;
    				param.append(token);
    				params.add(param.toString().trim());
    				param = new StringBuffer();
    			}
    			else
    			{
    				System.err.println("Bracket closed without opening bracket in "+expressionType+"("+expressionText+")");
    				return null;
    			}
    		}
    		else if (token.equals(","))
    		{
    			if (withinBrackets || withinSqBrackets)
    			{
    				param.append(token);
    			}
    			else
    			{
    				if (param.length() > 0)
    				{
    					params.add(param.toString().trim());
    					param = new StringBuffer();
    				}
    			}
    		}
    		else if (token.length() > 0)
    		{
    			param.append(token);
    		}  		
    	}
    	// Add the final parameter if it has not already been stored.
    	if (param.length() > 0)
    	{
    		params.add(param.toString());
    	}
    	
    	return params;
    }
}
