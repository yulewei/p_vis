package org.gicentre.tests;

import org.gicentre.hive.Expression;
import org.gicentre.hive.Path;

import junit.framework.TestCase;

//  ****************************************************************************************************
/** Class for testing HiVE parsing. Tests include those for individual components of an expression as 
 *  well as for entire expressions. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally 
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 5th July, 2010.
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
 
public class ParseTests extends TestCase
{
    // TODO: Add a more complete set of parsing tests.

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
   
    /** Checks that valid paths are permitted and invalid ones identified.
     */
    public void testPathParsing()
    {   
        // Valid paths.
        assertEquals("/", Path.parse("/").toString());
        assertEquals("/", Path.parse("//").toString());
        assertEquals("/", Path.parse("///").toString());
        assertEquals("/value/", Path.parse("/value/").toString());
        assertEquals("/*/", Path.parse("/*/").toString());
        assertEquals("/*/", Path.parse(" /    *  / ").toString());
        assertEquals("/value1/*/value2/*/*/*/1/2/", Path.parse(" / value1 / * / value2 / * / * / * / 1 / 2 /").toString());
        
        // Invalid paths.
        assertNull(Path.parse(""));
        assertNull(Path.parse("/value"));
        assertNull(Path.parse("/$variable/"));
        assertNull(Path.parse("/*variable/"));
        assertNull(Path.parse("/vari!able/"));
        assertNull(Path.parse("/vari able/"));
    }
    
    
    /** Checks that individual expressions are parsed correctly.
     */
    public void testExpressionParsing()
    {  
        // Valid expressions.
        
        assertEquals("sHier(/,$state,$year,$party);", Expression.parseExpression("sHier(/,$state,$year,$party);").toString());
        assertEquals("sSize(/,$totalEC,$numVotes,$numVotes);", Expression.parseExpression("sSize( / ,$totalEC  , $numVotes,  $numVotes); ").toString());
        assertEquals("sLayout(/,CA,PO);", Expression.parseExpression("sLayout(/,CA,PO)").toString());
        assertEquals("oOrder(/,3,$numVotes);", Expression.parseExpression("oOrder( /,3 ,$numVotes)").toString());
        assertEquals("oSwap(/,1,2);", Expression.parseExpression("oSwap( /,1 ,2);").toString());
        assertEquals("oSwap(/,1,2);", Expression.parseExpression("oSwap( /,1 ,2,,,,,);").toString());
                
        
        // Invalid expressions.
        assertNull(Expression.parseExpression("oCut(/,)"));
        assertNull(Expression.parseExpression("oZoom(/,*)"));
        
        // Syntactically valid but semantically invalid.
        assertNull(Expression.parseExpression("sHighlight(/,$dummy)"));
        assertNull(Expression.parseExpression("sZoom(/,VT)"));
        assertNull(Expression.parseExpression("oHighlight(/,1)"));
        assertNull(Expression.parseExpression("oSwap(/,1)"));
        assertNull(Expression.parseExpression("oLayout(/,1,dummy)"));
        assertNull(Expression.parseExpression("oLayout(/,1,$dummy)"));
        assertNull(Expression.parseExpression("oLayout(/,1,*)"));
        assertNull(Expression.parseExpression("oLayout(/,1,2)"));
        assertNull(Expression.parseExpression("oLayout(/,1,RD_PU)"));      
        
        // Expression with parameterized constants.
        assertEquals("oLayout(/,1,PA(12))", Expression.parseExpression("oLayout( /,1 ,PA(12));").toString());
        assertEquals("oLayout(/,1,PA(5,6,7,8))", Expression.parseExpression("oLayout( /,1 , PA(5,6,7,8);").toString());
        
        // Expression with grouped variables.
        assertEquals("oOrder(/,1,[$longitude,$latitude])", Expression.parseExpression("oOrder(/,1,[$longitude ,  $latitude    ] )  ;").toString());        
    }
    
    /** Checks that collections of expressions are parsed correctly.
     */
    public void testExpressionsParsing()
    {  
        // Valid groups of expressions.
        assertEquals(5,Expression.parseExpressions("sHier(/,$region,$party,$year); sLayout(/,CA,CA,NULL); sSize(/,$numVotes,$numVotes,$numVotes); sOrder(/,DEFAULT,DEFAULT,DEFAULT); sColor(/,$numVotes,$numVotes,$numVotes);").size());
        assertEquals(1, Expression.parseExpressions("oSwap( /,1 ,2)").size());
    }
}
