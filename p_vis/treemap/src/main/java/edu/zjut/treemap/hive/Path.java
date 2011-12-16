package edu.zjut.treemap.hive;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// ******************************************************************************************************
/** Represents a path to a node or nodes on a tree representing the graphic hierarchy. A path can be to
 *  any location on the tree of conditioning variables including the root of the entire tree or to any
 *  of its leaves. Wildcards can be used to specify multiple branches. Part of the HiVE expression language.
 *  For full details of HiVE, see <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>.
 *  HiVE originally proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts
 *  to address research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i>  
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.5, 21st February, 2011.
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

public class Path 
{
    // --------------------------------- Object variables ---------------------------------
    
    private ArrayList<String>path;      // Stores the nodes that make up the path.
    private static Pattern NOT_ALPHA_NUM = Pattern.compile("\\W");    // Non-alphanumeric regular expression.
        
    // ----------------------------------- Constructor ------------------------------------ 
    
    /** Creates a new path consisting of a single root node.
     */
    public Path()
    {
        path = new ArrayList<String>();
    }
    
    // ------------------------------------- Methods --------------------------------------
    
    /** Adds a variable value to the end of the path. This should be the value of a conditioning
     *  variable that defines the graphic hierarchy.
     *  @param varValue Value of a conditioning variable.
     */
    public void addNode(String varValue)
    {
        if (varValue.trim().equals("*"))
        {
            addWildcard();
        }
        else
        {
            path.add(varValue.trim());
        }
    }
        
    /** Adds a wildcard to the current end of the path.
     */
    public void addWildcard()
    {
        path.add("*");
    }
    
    /** Parses the given text representing a path. If parsed successfully this method returns
     *  an Path object that stores the path to a node in the hierarchy. All paths should start
     *  and end with a '/'.
     *  @param pathText Text of path to parse.
     *  @return The path represented by the text or null if it cannot be parsed.
     */
    public static Path parse(String pathText)
    {
        pathText = pathText.trim();     // Remove outer whitespace.
        
        if ((!pathText.startsWith("/")) || (!pathText.endsWith("/")))
        {
            System.err.println("Path '"+pathText+"' must start and end with a '/'");
            return null;
        }
        
        Path path = new Path();
        String[] tokens = pathText.split("/");
        
        for (String token : tokens)
        {
            token = token.trim();
            if (token.equals("*"))
            {
                path.addWildcard();
            }
            else
            {
//                if (NOT_ALPHA_NUM.matcher(token).find())
//                {
//                    System.err.println("Path '"+pathText+"' contains an invalid character '"+token+"'");
//                    return null;
//                }
                if (token.length() > 0)
                {
                    path.addNode(token);
                }
            }
        }        
        return path;
    }
        
    /** Reports the path as a text string.
     *  Omits wildcards at the end of the list
     *  @return Text representing the full path.
     */
    public String toString()
    {    	
    	boolean dontAdd=true;
        StringBuffer pathText = new StringBuffer("/");
        for (int i=path.size()-1;i>=0;i--){
        	if (!path.get(i).equals("*"))
        		dontAdd=false;
        	if (!dontAdd){
        		pathText.insert(0,path.get(i));
        		pathText.insert(0,"/");
        	}
            
        }
        return pathText.toString();
    }
    
    /** Reports the components of the path as a list, using clone so it's read only
     *  @return List of the components of the path
     */
    public List<String> getValues()
    {
        return (List<String>)this.path.clone();
    }
}