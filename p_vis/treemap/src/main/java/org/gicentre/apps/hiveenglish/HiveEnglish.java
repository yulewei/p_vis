package org.gicentre.apps.hiveenglish;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import org.gicentre.hive.Expression;
import org.gicentre.hive.HiveParser;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

//*****************************************************************************************************
/** Graphical User Interface for converting HiVE statements into interpretable English. Uses Processing
 *  to construct the interface. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally 
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts  to address 
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

@SuppressWarnings("serial")
public class HiveEnglish extends PApplet
{
    // ----------------------------- Object variables ------------------------------

    private boolean isCtrlDown,isMetaDown;
    private CopyHandler copyHandler;
    private PFont inFont, outFont;
    private String hiveText, hiveEnglishText;
    private PImage qrImage;                     // QR (barcode) image of expression.
    
    private static final int V_SPACING = 40;

    
	/**Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		PApplet.main(new String[]{"org.gicentre.apps.hiveenglish.HiveEnglish"});
	}
    
    // ------------------------- Processing initialisation -------------------------

    /** Initialises the applet.
     */
    public void setup()
    {
        size(1100,600);
        noLoop();
        smooth();
        isCtrlDown = false;
        isMetaDown = false;

        copyHandler = new CopyHandler();
        hiveText = "";
        hiveEnglishText = "";
         
        inFont = loadFont("../data/Monaco-12.vlw");
        outFont = loadFont("../data/HelveticaNeue-20.vlw");

        textAlign(LEFT,TOP);
        textLeading(28);
        noStroke();
        fill(0,100);
    }

    // ------------------------------ Processing draw ------------------------------

    /** Displays text representing any valid HiVE statements and their English equivalent.
     *  Will also draw a QR (2D barcode) representation of the expressions.
     */
    public void draw()
    {
        noLoop();
        background(255);

        // HiVE output 
        float vPosition = 10;
        float hiveHeight = countLines(hiveText)*(textAscent()+textDescent());
        
        textFont(inFont,12);
        text(hiveText, 10,vPosition, width-20,height-vPosition);
       
        // English output
        textFont(outFont,20);
        vPosition += hiveHeight + V_SPACING;
        float hiveEnglishHeight = countLines(hiveEnglishText)*(textAscent()+textDescent());
        text(hiveEnglishText, 10,vPosition, width-20, height-vPosition);
                
        // Display QR image of expression.
        if (qrImage != null)
        {
            vPosition += hiveEnglishHeight+V_SPACING;
            image(qrImage,10,vPosition);
        }
    }

    // ----------------------------- Keyboard handling -----------------------------

    /** Responds to copy or paste key presses by transferring text in the copy buffer to the application. 
     */
    public void keyPressed()
    {
        if ((isCtrlDown) || (isMetaDown))
        {
            if (key=='v')
            {
                qrImage = null;
                hiveText = copyHandler.getClipboardText();
                List<Expression> expressions = Expression.parseExpressions(hiveText);
                
                if (expressions.size() > 0)
                {                
                    hiveEnglishText = HiveParser.toEnglish(expressions);
                    if (hiveEnglishText == null)
                    {
                        hiveEnglishText = "Problem parsing '"+hiveText+"'";
                    }
                    else
                    {
                        // Get the QR image using google's chart API
                        String hiveNoSpaces = hiveText.replaceAll("\\s+", "");
                        qrImage = loadImage("http://chart.apis.google.com/chart?cht=qr&chs=200x200&chld=M|1&chl="+hiveNoSpaces,"png");
                    }
                }
                else
                {
                    hiveText = "Problem parsing '"+hiveText+"'";
                }

                loop();
                return;
            }
        }

        if (key==CODED)
        { 
            if (keyCode == CONTROL)
            {
                isCtrlDown = true;
            }
            else if (keyCode == KeyEvent.VK_META)
            {
                isMetaDown = true;
            }
        }
    }

    /** Monitors key-release actions to ensure the copy buffer is not accidently transferred 
     *  to the application when the control or command key is not pressed.
     */
    public void keyReleased()
    {
        if (key==CODED)
        { 
            if (keyCode == CONTROL)
            {
                isCtrlDown = false;
            }
            else if (keyCode == KeyEvent.VK_META)
            {
                isMetaDown = false;
            }
        }
    }
    
    /** Counts the number of lines in a give string.
     *  @param text Text whose lines will be counted.
     */
    private int countLines(String text)
    {
        if ((text == null) || (text.trim().length()==0))
        {
            return 0;
        }
        
        return text.replaceAll("[^\n]","").length() + 1;
    }

    // ------------------------------- Nested classes ------------------------------

    /** Handles copy and paste functionality.
     */
    private class CopyHandler implements ClipboardOwner
    {
        // ------ Object Variables ------

        private Clipboard clipboard;

        // -------- Constructor ---------

        /** Creates a copy handler for retrieving text copied into the copy buffer.
         */
        public CopyHandler()
        {
            clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
        }

        // ---------- Methods -----------

        /** Extracts a textual representation of the contents of the buffer.
         *  @return Textual representation of the copy buffer.
         */
        public String getClipboardText()
        {
            try
            {
                String clipboardText = (String)(clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
                return clipboardText;
            }
            catch (UnsupportedFlavorException e)
            {
                System.err.println("Problem capturing copy buffer: "+e);
            }
            catch (IOException e)
            {
                System.err.println("Problem capturing copy buffer: "+e);                
            }

            // Default to empty string if we can't grab the clipboard as text.
            return "";
        }

        public void lostOwnership(Clipboard cb, Transferable transObj)
        {
            // Do nothing for the moment.
        }
    }
}