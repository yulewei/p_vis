package org.gicentre.hive;

import java.util.Collection;

// *****************************************************************************************************
/** Interface for handling Hierarchical Visualization expressions. Expressions can either describe the
 *  state of a visualization or describe operators that change the state of the visualization. This 
 *  interface describes the minimum contract that any HiVE compatible application should abide by in
 *  order to handle HiVE statements from other applications. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  <br /><br />
 *  Visualization design space is represented as a tree. Trees can have uniform properties in that at
 *  each level of the hierarchy, the given properties are the same for all nodes at that level. 
 *  Alternatively they can be heterogeneous level trees where the tree is composed of subtrees each of
 *  which have their own (possibly different) uniform properties at a given level). In all cases, the 
 *  subtree(s) is (are) determined by the given path that represents the root of the subtree. If uniform,
 *  the given node should be the root of the tree.
 *  <br /><br />
 *  Variables must be prefixed with a $ symbol and are recommended to be short (ideally less than 6 
 *  characters long). For example,<pre>
 *   $price           <i>Indicates a the price associated with something.</i>
 *   $year            <i>Indicates a year associated with a data variable.</i>
 *   $ward            <i>Indicates wards as spatial units.</i></pre>
 *  
 *  A path can consist of any combination of wildcards ('*') and variable values. Levels of the hierarchy 
 *  are separated by a '/' symbol and all paths must start and finish with a '/'. Variable values can consist 
 *  of any alpha-numeric characters along with underscore. Values must not include spaces or other symbols.
 *  For example,<pre>
 *   /                <i>root of hierarchy</i>
 *   /left/           <i>subtree starting with the value 'left' at the first level of the hierarchy</i>
 *   /left/ * /2002/  <i>all subtrees starting with the value '2002' that are grandchildren of 'left'</i></pre>
 * 
 *  Constants should be upper case and are used to indicate particular layouts, colour tables etc. They can
 *  optionally include a parameter list in brackets.<br /> 
 *  For example,<pre>
 *   FX               <i>indicates a fixed size.</i>
 *   ASPECT(0.8)      <i>indicates an aspect ratio of width / height = 0.8.</i></pre>
 *  
 *  State descriptors include:<pre>
 *   sHier(path,var1,var2,...)
 *   sLayout(path,layout1, layout2,...)
 *   sOrder(path,var1,var2,...)
 *   sShape(path,var1,var2,...)
 *   sSize(path,var1,var2,...)
 *   sColor(path,var1,var2,...)
 *   sFocus(path,focusType1, focusType2,...)</pre>
 *
 *  Operators use a similar syntax for paths, variables, variable values and constants. Unlike state descriptors 
 *  they only apply to a single level in the hierarchy. That level is specified numerically relative to the
 *  given path with 1 indicating all nodes one level below the path, 2 indicating two levels below etc. This value 
 *  must be at least 1. Operators include:<pre>
 *   oInsert(path,level,var)
 *   oCut(path,level)
 *   oSwap(path,level1,level2)
 *   oLayout(path,level,layout)
 *   oOrder(path,level,var)
 *   oShape(path,level,var)
 *   oSize(path,level,var)
 *   oColor(path,level,var)
 *   oFocus(path,level,focusType)
 *  </pre>
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

public interface Hive 
{    
    /** Should report the complete state of the graphic.
     *  @return HiVE state expression <code>sHier</code>, or null if no data are represented in the graphic.
     */
    public abstract Collection<Expression> getState();

    /** Should apply the given HiVE operator. The programmer is free not to implement all operators,
     *  in which case, an <code>ExpressionNotSupportedException</code> should be thrown. If the given 
     *  operator is not valid, or cannot be applied as specified, the method should return false.
     *  @param operator Operator to apply.
     *  @return True if operator applied successfully.
     *  @throws ExpressionNotSupportedException if the given operator is not supported.
     */
    public boolean applyOperator(Expression operator) throws ExpressionNotSupportedException;
}
