package org.gicentre.hive;

/// ******************************************************************************************************
/** Represents a preset used in a HiVE expression. Presets are currently defined for layout, shape and 
 *  colour mapping types. Note that they do not specify the precise layout or colour rules in detail, but
 *  rather provide an identifier that would allow an implementing program to construct those rules.  For 
 *  full details of HiVE, see <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>.
 *  HiVE originally proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts
 *  to address research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 5th October, 2010.
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

public enum Preset
{
    // Note: Have made this public even though there should be no reason to these outside of the hive
    // package. The reason for doing so is to itemise them in the API documentation. This helps to
    // clarify the extent of presets that can be handled by the package.
        
    // Layouts

    /** Indicates a Cartesian layout with two orthogonal graphical axes. */       						   CARTESIAN,
    /** Indicates a polar layout with a central origin from which magnitude and direction are defined. */  POLAR,
    /** Indicates a parallel layout with multiple parallel graphical axes. */  						       PARALLEL,
    /** Indicates a sequential layout over time. */               										   ANIMATION,
    /** Indicates a layout that fills space without gaps or overlap. */         				           SPACE_FILLING,

    // Shape presets.
    /** Indicates a point-based shape. */							POINT,
    /** Indicates a line-based shape. */							LINE,
    /** Indicates a rectangular shaping. */                     	RECT,
    /** Indicates a circular shaping. */                        	CIRCLE,
    /** Indicates a hexagonal shaping. */                       	HEXAGON,
    
    // Focus presets.
    /** Indicates focus by zooming.	*/								ZOOM,
    /** Indicates focus by symbolic highlighting. */				HIGHLIGHT,
    /** Indicates focus by selection. */							SELECT,
           
    // General.
    /** Indicates that the value relates to hier. */              	HIER,
    /* Indicates that the value relates to default.             	DEFAULT, */
    /** Indicates a fixed value for a variable (e.g. size). */		FIXED,
    /** Indicates an undefined value for a variable or constant. */ NULL;
    
    
    /** Provides a collection of all the layout presets.
     *  @return Array of layout presets.
     */
    public static Preset[] getLayoutPresets()
    {

    	return new Preset[] {CARTESIAN, POLAR, PARALLEL, ANIMATION, SPACE_FILLING, HIER, NULL};
    }
    
    /** Provides a collection of all the shape presets.
     *  @return Array of shape presets.
     */
    public static Preset[] getShapePresets()
    {
    	return new Preset[] {POINT, LINE, RECT, CIRCLE, HEXAGON, HIER, NULL};
    }
    
    /** Provides a collection of all the focus presets.
     *  @return Array of focus presets.
     */
    public static Preset[] getFocusPresets()
    {
    	return new Preset[] {ZOOM, HIGHLIGHT, SELECT, HIER, NULL};
    }
    
    /** Provides the HiVE notation for any of the preset identifiers.
     *  @return HiVE layout constant notation.
     */
    public String toString()
    {
        switch (this)
        {
            case CARTESIAN:
                return "CA";
            case POLAR:
                return "PO";
            case PARALLEL:
                return "PA";
            case ANIMATION:
                return "AN";
            case SPACE_FILLING:
                return "SF";
                
            case POINT:
                return "PT";
            case LINE:
                return "LN";
            case RECT:
                return "RT";
            case CIRCLE:
                return "CR";
            case HEXAGON:
                return "HX";
                
            case ZOOM:
            	return "ZM";
            case HIGHLIGHT:
            	return "HL";
            case SELECT:
            	return "SL";
                 
            case FIXED:
            	return "FX";
            case NULL:
            	return "NULL";
            case HIER:
                return "HIER";
        }
        
        // Should never get to this line.
        return "Unknown preset";
    }
    
    /** Provides the a full name for the preset. Useful for English language description of a preset.
     *  @return Name describing the preset.
     */
    public String getFullName()
    {
        switch (this)
        {
            case CARTESIAN:
                return "Cartesian";
            case POLAR:
                return "polar";
            case PARALLEL:
                return "parallel";
            case ANIMATION:
                return "animated";
            case SPACE_FILLING:
                return "space filling";
                           
            case POINT:
                return "point-based";
            case LINE:
                return "line-based";
            case RECT:
                return "rectangular";
            case CIRCLE:
                return "circular";
            case HEXAGON:
                return "hexagonal";
                
            case ZOOM:
            	return "zoom";
            case HIGHLIGHT:
            	return "highlight";
            case SELECT:
            	return "select";
               
            case FIXED:
            	return "fixed";
            case NULL:
            	return "undefined";
            case HIER:
                return "hierarchy";
            //case DEFAULT:
            //    return "default";
                
        }
        
        // Should never get to this line.
        return "";
    }
    
    /** Indicates if this preset is one that indicates a known layout.
     *  @return True if this preset indicates a known layout.
     */
    public boolean isLayout()
    { 
       switch (this)
       {
           case CARTESIAN: 
           case POLAR:
           case PARALLEL:
           case SPACE_FILLING:
           case ANIMATION:
        	   return true;
        	   
           case HIER:
           case NULL:
               return true;
           default:
               return false;
       }
    }
    
    /** Indicates if this preset is one that indicates a known shape.
     *  @return True if this preset indicates a known shape.
     */
    public boolean isShape()
    { 
        switch (this)
        {
        	case POINT:
        	case LINE:
        	case RECT:
            case CIRCLE:
            case HEXAGON:
                return true;
            
            case HIER:
            case NULL:
                return true;
            default:
                return false;
        }
    }
            
    /** Indicates if this preset is one that indicates a type of focus.
     *  @return True if this preset indicates a known type of focus.
     */
    public boolean isFocus()
    { 
       switch (this)
       {
           case ZOOM:
           case HIGHLIGHT:
           case SELECT:
               return true;
           
           case HIER:
           case NULL:
               return true;
           default:
               return false;
       }
    }
    
    /** Indicates if this preset is one that indicates a known colour mapping.
     *  @return True if this preset indicates a known colour mapping.
     *  @deprecated Colour mapping is no longer part of the HiVE language, so there should be no need to use this method.
     */
    @Deprecated
    boolean isColor()
    {        
        return false;
    }
    
    /** Provides a preset that corresponds to the given preset code.
     *  @param presetText Preset notation (e.g. CA, FX, HIERARCHY etc.).
     *  @return HiVE preset or null if preset code not recognised.
     */
    public static Preset parse(String presetText)
    {
    	for (Preset preset: Preset.values())
    	{
    		if (presetText.equalsIgnoreCase(preset.toString()))
    		{
    			return preset;
    		}
    	}
  	       
        // Preset not recognised.
        return null;
    }
}