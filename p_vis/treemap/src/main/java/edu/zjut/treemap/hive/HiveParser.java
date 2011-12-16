package edu.zjut.treemap.hive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//  ****************************************************************************************************
/** Class for parsing a collection of HiVE expressions and producing English language text to represent them.
 *  For full details of HiVE, see <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>.
 *  HiVE originally proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts
 *  to address research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 5th October, 2010.
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

public class HiveParser
{
    // -------------------------------- Constructor --------------------------------
    
    /** Private constructor that does nothing to prevent objects being instantiated from this
     *  class. Contains static methods only.
     */
    private HiveParser()
    {
        // Do nothing.
    }
    
    // ---------------------------------- Methods ----------------------------------

    /** Provides an English language representation of the given ordered list of 
     *  HiVE expressions.
     *  @param expressions Expressions to parse.
     *  @return Text representing an English language equivalent to the given expressions.
     *               If no expressions are provided, null is returned.
     */
    public static String toEnglish(List<Expression>expressions)
    {        
        if (expressions == null)
        {
            return null;
        }
        
        Expression sHier   = null, 
                   sLayout = null,
                   sOrder  = null,
                   sSize   = null,
                   sShape  = null,
                   sColor  = null,
                   sFocus  = null;
        
        List<Expression> operators = new ArrayList<Expression>(); 
        
        // Extract the state expressions individually and collect the operators together.
        for (Expression expression : expressions)
        {
            switch (expression.getType())
            {
                case S_HIER:
                    sHier = expression;
                    break;
                case S_LAYOUT:
                    sLayout = expression;
                    break;
                case S_ORDER:
                    sOrder = expression;
                    break;
                case S_SIZE:
                    sSize = expression;
                    break;
                case S_SHAPE:
                    sShape = expression;
                    break;
                case S_COLOR:
                    sColor = expression;
                    break;
                case S_FOCUS:
                    sFocus = expression;
                    break;
                default:
                    // Must be an operator
                    operators.add(expression);
            }
        }

        // Do the conversion.
        StringBuffer english = null;
        List<Variable[]>varGroups, colors=null, sizes=null, orders=null, shapes=null, layouts=null, focus=null;
        boolean isFirst = true;
        english = new StringBuffer();
        
       
        // We can only describe the state if the hierarchy of variables has been identified.
        if (sHier != null)
        {
            // TODO: Need to incorporate paths into the conversion. It is possible that each expression could refer
            //       to a different sub path.
            
            english.append("Visualization showing ");
            varGroups = new ArrayList<Variable[]>(sHier.getVarGroups());
            Collections.reverse(varGroups);
  
            if (sColor != null)
            {
                colors = new ArrayList<Variable[]>(sColor.getVarGroups());
                Collections.reverse(colors);
            }
            if (sSize != null)
            {
                sizes = new ArrayList<Variable[]>(sSize.getVarGroups()); 
                Collections.reverse(sizes);
            }
            if (sOrder != null)
            {
                orders  = new ArrayList<Variable[]>(sOrder.getVarGroups());
                Collections.reverse(orders);
            }
            if (sShape != null)
            {
                shapes  = new ArrayList<Variable[]>(sShape.getVarGroups());
                Collections.reverse(shapes);
            }
            if (sLayout != null)
            {
                layouts  = new ArrayList<Variable[]>(sLayout.getVarGroups());
                Collections.reverse(layouts);
            }
            if (sFocus != null)
            {
                focus  = new ArrayList<Variable[]>(sFocus.getVarGroups());
                Collections.reverse(focus);
            }

            for (Variable[] varGroup : varGroups)
            {
                if (isFirst)
                {
                    english.append(getFullName(varGroup));
                    isFirst = false;
                }
                else
                {
                    english.append(" for each "+getFullName(varGroup));
                }
            }

            boolean addedClause = false;
            
            for (int i=0; i<varGroups.size(); i++)
            {
                if (addedClause == true)
                {
                    english.append(getFullName(varGroups.get(i))+" is ");
                }

                if ((colors != null) && (colors.size() >i))
                {
                    // If coloured by NULL, no need to show the coloured by clause.
                    if ((colors.get(i).length == 1) && (colors.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" ");
                            addedClause = true;
                        }
                        english.append("coloured by "+getFullName(colors.get(i))+", ");
                    }
                }

                if ((sizes != null) && (sizes.size() >i))
                {
                    // If sized by NULL, no need to show the sized by clause.
                    if ((sizes.get(i).length == 1) && (sizes.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" is ");
                            addedClause = true;
                        }

                        if ((sizes.get(i).length == 1) && (sizes.get(i)[0].getPreset() == Preset.FIXED))
                        {
                            english.append("a fixed size, ");
                        }
                        else
                        {
                            english.append("sized by "+getFullName(sizes.get(i))+", ");
                        }
                    }
                }

                if ((orders != null) && (orders.size() >i))
                {
                    // If ordered by NULL, no need to show the ordered by clause.
                    if ((orders.get(i).length == 1) && (orders.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" is ");
                            addedClause = true;
                        }
                        english.append("ordered by "+getFullName(orders.get(i))+", ");
                    }
                }
                
                if ((shapes != null) && (shapes.size() >i))
                {
                    // If shaped by NULL, no need to show the shaped by clause.
                    if ((shapes.get(i).length == 1) && (shapes.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" is ");
                            addedClause = true;
                        }

                        if ((shapes.get(i).length == 1) && (shapes.get(i)[0].getPreset().isShape()))
                        {
                            english.append("using "+getFullName(shapes.get(i))+" shapes, ");
                        }
                        else
                        {
                            english.append("shaped by "+getFullName(shapes.get(i))+", ");
                        }
                    }
                }
                
                if ((layouts != null) && (layouts.size() >i))
                {
                    // If layout is NULL, no need to show the layout clause.
                    if ((layouts.get(i).length == 1) && (layouts.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" is ");
                            addedClause = true;
                        }
                        
                        if (layouts.get(i)[0].getPreset() == Preset.ANIMATION)
                        {
                            english.append("in an ");
                        }
                        else
                        {
                            english.append("in a ");
                        }
                        english.append(getFullName(layouts.get(i))+" layout ");
                    }
                }
                
                if ((focus != null) && (focus.size() >i))
                {
                    // If focus is NULL, no need to show the focus clause.
                    if ((focus.get(i).length == 1) && (focus.get(i)[0].getPreset() != Preset.NULL))
                    {
                        if (addedClause == false)
                        {
                            english.append("\nwhere "+getFullName(varGroups.get(i))+" is ");
                            addedClause = true;
                        }
                        english.append("with a "+getFullName(focus.get(i))+" focus ");
                    }
                }
                english.append("\n");
            }
        }
        
        // Unlike state expressions, operators must be applied in sequence and are independent of each other.
        for (Expression operator : operators)
        {
            if (isFirst)
            {
                english.append("Visualization changed by ");
                isFirst = false;
            }
            
            Variable[] var = null;
            if (operator.getVarGroups().size() > 0)
            {
                var = operator.getVarGroups().get(0);
            }
            int level       = operator.getLevel();
            int secondLevel = operator.getSecondLevel();
            
            // TODO: For HIER terms, replace 'by the conditioning variable' with the actual variable name. 
            
            switch (operator.getType())
            {
                case O_INSERT:
                    english.append("inserting "+getFullName(var)+" below level "+level);
                    break;
                case O_CUT:
                    english.append("removing data at level "+level);
                    break;
                case O_SWAP:
                    english.append("swapping data at levels "+level+" and "+secondLevel);
                    break;
                case O_LAYOUT:
                    String article = "a ";
                    if ((getFullName(var).startsWith("a")) || (getFullName(var).startsWith("e")) ||
                        (getFullName(var).startsWith("i")) || (getFullName(var).startsWith("o")) ||
                        (getFullName(var).startsWith("u")))
                    {
                        article = "an ";
                    }
                    english.append("using "+article+getFullName(var)+" layout at level "+level);
                    break;
                case O_ORDER:
                    if ((var.length == 1) && (var[0].getPreset() == Preset.NULL))
                    {
                        english.append("using a default ordering for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.FIXED))
                    {
                        english.append("using a fixed order for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.HIER))
                    {
                        english.append("ordering data at level "+level+" by the conditioning variable");
                    }
                    else
                    {
                        english.append("ordering data at level "+level+" by "+getFullName(var));
                    }
                    break;
                case O_SIZE:
                    if ((var.length == 1) && (var[0].getPreset() == Preset.NULL))
                    {
                        english.append("using a default size for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.FIXED))
                    {
                        english.append("using a fixed size for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.HIER))
                    {
                        english.append("sizing data at level "+level+" by the conditioning variable");
                    }
                    else
                    {
                        english.append("sizing data at level "+level+" by "+getFullName(var));
                    }
                    break;
                case O_SHAPE:
                    if ((var.length == 1) && (var[0].getPreset() == Preset.NULL))
                    {
                        english.append("using a default shape for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.FIXED))
                    {
                        english.append("using a fixed shape for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.HIER))
                    {
                        english.append("shaping data at level "+level+" by the conditioning variable");
                    }
                    else
                    {
                        english.append("shaping data at level "+level+" by "+getFullName(var));
                    }
                    break;
                case O_COLOR:
                    if ((var.length == 1) && (var[0].getPreset() == Preset.NULL))
                    {
                        english.append("using a default colouring for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.FIXED))
                    {
                        english.append("using a fixed colour for data at level "+level);
                    }
                    else if ((var.length == 1) && (var[0].getPreset() == Preset.HIER))
                    {
                        english.append("coloring data at level "+level+" by the conditioning variable");
                    }
                    else
                    {
                        english.append("coloring data at level "+level+" by "+getFullName(var));
                    }
                    break;
                case O_FOCUS:
                	//focus can only have one in the group
                    if (var[0].getPreset() == Preset.ZOOM)
                    {
                        english.append("zooming to data at level "+level);
                    }
                    else if (var[0].getPreset() == Preset.HIGHLIGHT)
                    {
                        english.append("highlighting data at level "+level);
                    }
                    else if (var[0].getPreset() == Preset.SELECT)
                    {
                        english.append("selecting data at level "+level);
                    }
                    break;
                    
                default:
                    // Do nothing for non-operator expressions.
            }
            
            english.append("\n");
        }
        
        if (isFirst == true)
        {
            //No clauses were found.
            return null;
        }
        return english.toString();
    }
    
    // ------------------------------- Private Methods ----------------------------------
    
    /** Provides text representing the given group of variables.
     *  @param varGroup Group of variables (can commonly contain a single variable).
     *  @return Text representing the group of variables.
     */
    private static String getFullName(Variable[] varGroup){
		StringBuffer groupText = new StringBuffer();
		for (int i=0; i<varGroup.length-1; i++)
		{
			groupText.append(varGroup[i].getFullName());
			groupText.append(" and ");
		}
		groupText.append(varGroup[varGroup.length-1].getFullName());
		return groupText.toString();
    }
}
