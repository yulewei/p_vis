package org.gicentre.hive;

//  ****************************************************************************************************
/** Types of HiVE expression. All HiVE expressions should be one of those listed here. They fall into 
 *  two categories - state expressions and operator expressions.  For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i>  
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 1st July, 2010.
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

public enum Type
{
    /** sHier expression type.*/        S_HIER,
    /** sLayout expression type.*/      S_LAYOUT,
    /** sOrder expression type.*/       S_ORDER,
    /** sShape expression type.*/       S_SHAPE,
    /** sSize expression type.*/        S_SIZE,
    /** sColor expression type.*/       S_COLOR,
    /** sHighlight expression type.*/   S_FOCUS,
    
    /** oHier expression type.*/      	O_HIER,
    /** oInsert expression type.*/      O_INSERT,
    /** oCut expression type.*/         O_CUT,
    /** oSwap expression type.*/        O_SWAP,
    /** oLayout expression type.*/      O_LAYOUT,
    /** oOrder expression type.*/       O_ORDER,
    /** oShape expression type.*/       O_SHAPE,
    /** oSize expression type.*/        O_SIZE,
    /** oColor expression type.*/       O_COLOR,
    /** oHighlight expression type.*/   O_FOCUS;
    
    /** Reports whether or not a given type is a HiVE operator.
     *  @return True if the type is an operator. If false, the type must be a state expression.
     */
    public boolean isOperator()
    {
        switch(this)
        {
            case S_HIER:
            case S_LAYOUT:
            case S_ORDER:
            case S_SHAPE:
            case S_SIZE:
            case S_COLOR:
            case S_FOCUS:
                return false;
            default:
                return true;
        }
    }
    
    /** Returns a type matching the given text. This will perform a case-insensitive matching.
     *  @param typeText Text representing a type.
     *  @return Type represented by the given text or null if not matching type found.
     */
    public static Type parse(String typeText)
    {
        String token = typeText.trim().toLowerCase();   // Case insensitive with no whitespace.
        if (token.equals("shier"))
        {
            return S_HIER;
        }
        if (token.equals("slayout"))
        {
            return S_LAYOUT;
        }
        if (token.equals("sorder"))
        {
            return S_ORDER;
        }
        if (token.equals("sshape"))
        {
            return S_SHAPE;
        }
        if (token.equals("ssize"))
        {
            return S_SIZE;
        }
        if ((token.equals("scolor")) || (token.equals("scolour")))
        {
            return S_COLOR;
        }
        if (token.equals("sfocus"))
        {
            return S_FOCUS;
        }
        if (token.equals("oinsert"))
        {
            return O_INSERT;
        }
        if (token.equals("ohier"))
        {
            return O_HIER;
        }
        if (token.equals("ocut"))
        {
            return O_CUT;
        }
        if (token.equals("oswap"))
        {
            return O_SWAP;
        }
        if (token.equals("olayout"))
        {
            return O_LAYOUT;
        }
        if (token.equals("oorder"))
        {
            return O_ORDER;
        }
        if (token.equals("oshape"))
        {
            return O_SHAPE;
        }
        if (token.equals("osize"))
        {
            return O_SIZE;
        }
        if (token.equals("ocolor"))
        {
            return O_COLOR;
        }
        if (token.equals("ofocus"))
        {
            return O_FOCUS;
        }
        
        // If we get to this stage, no type has been identified.
        return null;
    }
    
    
    /** Provides a text representation of the HiVE type.
     *  @return Type represented by the given text.
     */
    public String toString()
    {
        switch(this)
        {
            case S_HIER:
                return "sHier";
            case S_LAYOUT:
                return "sLayout";
            case S_ORDER:
                return "sOrder";
            case S_SHAPE:
                return "sShape";
            case S_SIZE:
                return "sSize";
            case S_COLOR:
                return "sColor";
            case S_FOCUS:
                return "sFocus";
            case O_HIER:
                return "oHier";
            case O_INSERT:
                return "oInsert";
            case O_CUT:
                return "oCut";
            case O_SWAP:
                return "oSwap";
            case O_LAYOUT:
                return "oLayout";
            case O_ORDER:
                return "oOrder";
            case O_SHAPE:
                return "oShape";
            case O_SIZE:
                return "oSize";
            case O_COLOR:
                return "oColor";
            case O_FOCUS:
                return "oFocus";
        }
        
        // We shouldn't ever get to this line.
        return super.toString();
    }
}
