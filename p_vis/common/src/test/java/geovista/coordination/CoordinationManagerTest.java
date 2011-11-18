/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */

package geovista.coordination;

import java.util.logging.Logger;

import edu.zjut.coordination.CoordinationManager;

import junit.framework.TestCase;

/**
 * @author localadmin
 * 
 */
public class CoordinationManagerTest extends TestCase {
	protected CoordinationManager coord;
	final static Logger logger = Logger.getLogger(CoordinationManager.class
			.getName());

	/**
	 * @param name
	 */
	public CoordinationManagerTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		coord = new CoordinationManager();
	}

	/**
	 * Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#addBean(java.lang.Object)}
	 * . Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#removeBean(java.lang.Object)}
	 * .
	 */
	public void testCoordinateObject() {

		logger.finest("testing coordination.....");

		ExampleBean coorBean = new ExampleBean();
		ExampleBean coorBean2 = new ExampleBean();
		ExampleBean coorBean3 = new ExampleBean();
		ExampleBean coorBean4 = new ExampleBean();

		coord.addBean(coorBean);
		coord.addBean(coorBean2);
		coord.addBean(coorBean3);
		coord.addBean(coorBean4);

		assertTrue(coorBean.getIndication() == 0);
		coorBean2.setIndication(1);
		coorBean2.fireIndicationChanged(1);
		assertTrue(coorBean.getIndication() == 1);
		coord.removeBean(coorBean);

		// coorBean2.setIndication(10);
		// coorBean2.fireIndicationChanged(10);

		assertTrue(coorBean.getIndication() == 1);

		// duplicate removal
		// coord.removeBean(coorBean);

		// trigger events
		coorBean.setIndication(10);
		coorBean.fireIndicationChanged(10);

		assertTrue(coorBean3.getIndication() == 1);

		// let's remove, add, and remove beans and see if that goes ok
		coord.removeBean(coorBean4);
		coord.addBean(coorBean4);
		coord.addBean(coorBean4);
		coord.removeBean(coorBean4);
		coord.removeBean(coorBean3);
		coord.removeBean(coorBean3);
		// assertTrue(coorBean3.getIndication() == 2);

		// assertTrue(coorBean.getIndication() == 0);
		// OK, let's try some other beans.

	}

	/**
	 * Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#getFiringMethods(edu.zjut.coordination.ListeningBean)}
	 * .
	 */
	public void testGetFiringMethods() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#getFiringBeans()}.
	 */
	public void testGetFiringBeans() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#disconnectBeans(edu.zjut.coordination.FiringMethod, edu.zjut.coordination.ListeningBean)}
	 * .
	 */
	public void testDisconnectBeans() {
		// fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link edu.zjut.coordination.CoordinationManager#reconnectBeans(edu.zjut.coordination.FiringMethod, edu.zjut.coordination.ListeningBean)}
	 * .
	 */
	public void testReconnectBeans() {
		// fail("Not yet implemented"); // TODO
	}

}
