/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: James Macgill */

package edu.zjut.common.event;

import java.util.EventListener;

/**
 * 
 * @author jamesm
 */
public interface DimensionListener extends EventListener {

	public void dimensionChanged(DimensionEvent e);

}
