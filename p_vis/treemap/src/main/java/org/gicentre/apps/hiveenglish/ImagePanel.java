package org.gicentre.apps.hiveenglish;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

//*****************************************************************************************************
/** Component for displaying an image. Since this a Swing component, it can be laid out and drawn like
 *  any other component. 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 6th October, 2010.
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

public class ImagePanel extends JPanel
{    
    // ----------------------------- Object variables ------------------------------

    private Image image;
    private static final long serialVersionUID = -817430200434524622L;

    // ------------------------------- Constructors --------------------------------

    /** Creates an empty image panel. This can be updated with a call to setImage().
     */
    public ImagePanel()
    {
        super();
    }
    
    /** Creates an image panel that will display the given Image.
     *  @param image Image to display in panel.
     */
    public ImagePanel(Image image)
    {
        super();
        this.image = image;     // Store image as object variable.
    }

    // ---------------------------------- Methods ----------------------------------

    /** Sets the image to be displayed in the panel.
     *  @param image New image to be displayed.
     */
    public void setImage(Image image)
    {
        this.image = image;
        repaint();
    }
    
    /** Displays the image in the panel if it exists.
     *  @param g Graphics context in which to paint.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if (image != null)
        { 
            Dimension size  = getSize();
            g.drawImage(image,0,0, size.width, size.height,this);
        }
    }
}
