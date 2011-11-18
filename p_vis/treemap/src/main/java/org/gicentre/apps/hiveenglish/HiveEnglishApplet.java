package org.gicentre.apps.hiveenglish;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.gicentre.hive.Expression;
import org.gicentre.hive.HiveParser;

//*****************************************************************************************************
/** Applet for converting HiVE statements into interpretable English. This version uses a standard
 *  Java applet to host the GUI without reference to Processing. It has the advantage of allowing copy
 *  and paste into the input area without the need for a signed applet. The appearance of the applet
 *  can be optionally customised with parameters <code>hiveColour</code>, <code>englishColour</code> and
 *  <code>consoleColour</code> when embedding the applet in a web page. For full details of HiVE, see 
 *  <a href="http://www.gicentre.org/hive" target="_blank">gicentre.org/hive</a>. HiVE originally 
 *  proposed in <b>Slingsby, Dykes and Wood</b> (2009) Configuring hierarchical layouts  to address 
 *  research questions, <i>IEEE Transactions on Visualization and Computer Graphics 15(6).</i> 
 *  @author giCentre, City University London. Copyright Jo Wood, Aidan Slingsby and Jason Dykes, 2010.
 *  @version 1.4, 7th October, 2010.
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
public class HiveEnglishApplet extends JApplet implements ActionListener
{

    // ----------------------------- Object variables ------------------------------
    
    private TextArea inputArea;     // Have to use AWT text area to allow copy/paste
    private JTextArea outputArea, console;
    private JButton bConvert;
    private ImagePanel qrImage;

    // ---------------------------------- Methods ----------------------------------
    
    /** Displays a editable text area and a button.
     */
   public void init()
   {
       Container contentPane = getContentPane();
       contentPane.setLayout(new GridBagLayout());
       GridBagConstraints gbc = new GridBagConstraints();
       gbc.gridy     = GridBagConstraints.RELATIVE;
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       gbc.fill      = GridBagConstraints.HORIZONTAL;
       gbc.anchor    = GridBagConstraints.PAGE_START;
       gbc.weightx   = 1;
       gbc.weighty   = 1;
       
       qrImage = new ImagePanel();
       qrImage.setPreferredSize(new Dimension(200,200));
              
       // Input and output fonts
       Font inputFont   = new Font("Monospaced",Font.PLAIN,12);
       Font outputFont  = new Font("SansSerif",Font.PLAIN,14);
       Font consoleFont = new Font("Monospaced",Font.ITALIC,10);
       
       // Parse background colours if available
       int inColour      = Integer.parseInt("ffffee",16);
       int outColour     = Integer.parseInt("ffffff",16);
       int consoleColour = Integer.parseInt("eeeeee",16);
       
       String inColourText = getParameter("hiveColour");
       if ((inColourText != null) && (inColourText.length() >=3))
       {
           if (inColourText.startsWith("#"))
           {
               inColourText = inColourText.substring(1);
           }
           try
           {
               inColour = Integer.parseInt(inColourText,16);
           }
           catch (NumberFormatException e)
           {
               System.err.println("hiveColour is not valid '"+inColourText+"'.");
           }
       }
       
       String outColourText = getParameter("englishColour");
       if ((outColourText != null) && (outColourText.length() >=3))
       {
           if (outColourText.startsWith("#"))
           {
               outColourText = outColourText.substring(1);
           }
           try
           {
               outColour = Integer.parseInt(outColourText,16);
           }
           catch (NumberFormatException e)
           {
               System.err.println("englishColour is not valid '"+outColourText+"'.");
           }
       }
       
       String consoleColourText = getParameter("consoleColour");
       if ((consoleColourText != null) && (consoleColourText.length() >=3))
       {
           if (consoleColourText.startsWith("#"))
           {
               consoleColourText = consoleColourText.substring(1);
           }
           try
           {
               consoleColour = Integer.parseInt(consoleColourText,16);
           }
           catch (NumberFormatException e)
           {
               System.err.println("consoleColour is not valid '"+consoleColourText+"'.");
           }
       } 
       
       contentPane.setBackground(new Color(outColour));
  
       // Editable text area.
       inputArea = new TextArea("",8,40,TextArea.SCROLLBARS_NONE);
       inputArea.setBackground(new Color(inColour));
       inputArea.setFont(inputFont);
       contentPane.add(inputArea,gbc);
         
       // Conversion Button.
       bConvert = new JButton("Convert");
       bConvert.addActionListener(this);
       bConvert.setAlignmentX(Component.CENTER_ALIGNMENT);
       gbc.fill = GridBagConstraints.NONE;
       gbc.weighty   = 0.1;
       contentPane.add(bConvert,gbc);
       
       // QR diagram
       gbc.gridwidth = 1;
       gbc.weightx   = 0.1;
       qrImage.setBackground(new Color(outColour));
       contentPane.add(qrImage,gbc);
             
       // Output area showing English.
       outputArea = new JTextArea("");
       outputArea.setBackground(new Color(outColour));
       outputArea.setForeground(Color.GRAY);
       outputArea.setBorder(new EmptyBorder(15,4,4,4));
       outputArea.setFont(outputFont);
       outputArea.setEditable(false);
       outputArea.setLineWrap(true);
       outputArea.setWrapStyleWord(true);
       outputArea.setPreferredSize(new Dimension(300, 200));
       //outputArea.setOpaque(false);
       
       gbc.fill = GridBagConstraints.BOTH;
       gbc.weighty   = 999;
       gbc.weightx   = 999;
       gbc.gridwidth = GridBagConstraints.REMAINDER;
       contentPane.add(outputArea,gbc);
       
       // Console for reporting error messages.
       console = new JTextArea("Type or copy a HiVE expression into the upper window and press 'convert' to translate it");
       console.setBackground(new Color(consoleColour));
       console.setForeground(new Color(50,0,0));
       console.setBorder(new EmptyBorder(4,4,4,4));
       console.setFont(consoleFont);
       console.setEditable(false);
       console.setLineWrap(true);
       console.setWrapStyleWord(true);
       console.setPreferredSize(new Dimension(500, 40));
       
       gbc.fill      = GridBagConstraints.HORIZONTAL;
       gbc.weightx   = 1;
       gbc.weighty   = 1;
       contentPane.add(console,gbc);
       
       inputArea.requestFocusInWindow();
   }
   
   /** Converts the HiVE expression in the input area into English and a QR diagram
    *  when button pressed.
    * @param event Mouse event.
    */
  public void actionPerformed(ActionEvent event)
  {
      if (event.getSource() == bConvert)
      {
          String hiveText = inputArea.getText();
          String hiveEnglishText = new String();
          List<Expression> expressions = Expression.parseExpressions(hiveText);

          if (expressions.size() > 0)
          {                
              hiveEnglishText = HiveParser.toEnglish(expressions);
              console.setText("HiVE converted");
              if (hiveEnglishText == null)
              {
                  console.setText("Problem parsing '"+hiveText+"'");
                  hiveEnglishText = "";
              }
              else
              {
                  // Get the QR image using google's chart API
                  String hiveNoSpaces = hiveText.replaceAll("\\s+", "");
                                    
                  try
                  {
                      Image img = ImageIO.read(new URL("http://deneb.soi.city.ac.uk/~jwo/hive/qr.php?text="+hiveNoSpaces));

                      // TODO: Do we need the media tracker stuff?
                      MediaTracker mt = new MediaTracker(this);
                      mt.addImage(img,0);
                      mt.waitForID(0);
                      
                      qrImage.setImage(img);
                  }
                  catch (InterruptedException e)
                  {
                      // Fail silently.
                  }
                  catch (IOException e)
                  {
                      // Fail silently.
                  }
              }
          }
          else
          {
              console.setText("Problem parsing '"+hiveText+"'");
              hiveEnglishText = "";
          }
          
          outputArea.setText(hiveEnglishText);
      }
  }
}
