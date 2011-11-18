package org.gicentre.hive;

//  **************************************************************************************************
/** Exception for indicating that a HiVE expression is not supported by a class that implements the 
 *  Hive interface. Typically this will apply to HiVE operators. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally 
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i>  
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 1st July, 2010.
 */
//  **************************************************************************************************

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

public class ExpressionNotSupportedException extends Exception
{
    private static final long serialVersionUID = 3996640520313919090L;
    
}
