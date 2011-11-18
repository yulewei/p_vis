package org.gicentre.tests;

import org.gicentre.hive.Expression;
import org.gicentre.hive.HiveParser;

import junit.framework.TestCase;

//  ****************************************************************************************************
/** Class for testing HiVE to English conversion. For full details of HiVE, see 
 * <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally proposed
 *  in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address research
 *  questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
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

public class HiveEnglishTests extends TestCase
{
	private String[] invalidStateExpressions = 
    { 
        "sHier(/,); sLayout(/,); sSize(/,); sOrder(/,); sColor(/,); ",      // Empty expressions.
       
    };
	
    private String[] validStateExpressions = 
    { 
        "sHier(/,$year); sLayout(/,CA); sSize(/,FX); sOrder(/,DEFAULT); sColour(/,$party);",
        "sHier(/,$year,$region,$division,$state,$party); sLayout(/,CA, PO, AN, PA, CA); sSize(/,FX,$numVotes,$totalEC,$landArea,$pop); sOrder(/,DEFAULT,$numVotes,$totalEC,$landArea,$pop); sColour(/,$party,$party,$party,$numVotes,$numVotes); ",
        "sHier(/,$month,$location); sSize(/,FX,FX); sShape(/,RT,PT); sOrder(/,$month,[$lon,$lat]); sLayout(/,SF,CA); sColor(/,NULL,$avWind)",
    };
    
    
    
    private String[] operatorExpressions = 
    { 
        "oInsert(/,1,$year)",
        "oCut(/,4)",
        "oSwap( /,1 ,2)",
        "oLayout(/,3,DEFAULT);oLayout(/,4,DEFAULT);oLayout(/,5,CA)",
        "oOrder(/,10,$price)",
        "oShape(/,10,FX)",
        "oSize(/,10,$population)",
        "oColor(/,5,$gender);",
    };
    
    // ---------------------------------- Methods ----------------------------------
    
    /** Called before every test case method.
     */
    protected void setUp()
    {
        // Do nothing for the moment.
    }

    /** Called after every test case method.
     */
    protected void tearDown()
    {
        // Do nothing for the moment.
    }

    // --------------------------------- Test methods -------------------------------
   
    /** Checks that state expressions produce valid results. Note that the unit test itself
     *  can only check for valid parsing of expressions. The quality of the English
     *  conversion has to judged by examining standard output.
     */
    public void testStateExpressions()
    {
        String result;

        System.out.println("Invalid expressions:");
        System.out.println("====================");
        
        for (String stateExpression : invalidStateExpressions)
        {
        	 System.out.println(stateExpression);
             assertNull(result=HiveParser.toEnglish(Expression.parseExpressions(stateExpression)));
             System.out.println(result+"\n"); 	
        }
        
        System.out.println("Valid expressions:");
        System.out.println("==================");
        
        for (String stateExpression : validStateExpressions)
        {
        	 System.out.println(stateExpression);
             assertNotNull(result=HiveParser.toEnglish(Expression.parseExpressions(stateExpression)));
             System.out.println(result+"\n"); 	
        }

    }
    
    /** Checks that state expressions produce valid results. Note that the unit test itself
     *  can only check for valid parsing of expressions. The quality of the English
     *  conversion has to judged by examining standard output.
     */
    public void testOperatorExpressions()
    {
        String result;
        for (String expression : operatorExpressions)
        {
            System.out.println(expression);
            assertNotNull(result=HiveParser.toEnglish(Expression.parseExpressions(expression)));
            System.out.println(result);
        }
    }

}