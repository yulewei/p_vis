/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */
package edu.zjut.coordination;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class represents a particular method which is used by firing.
 * 
 * A particular FiringBean will have one to many FiringMethod instances.
 * 
 * @see FiringBean
 * @author Frank Hardisty
 */
public class FiringMethod implements Comparable {
	final static Logger logger = Logger.getLogger(FiringMethod.class.getName());
	private String methodName;
	private Method originalAddMethod;
	private Method originalRemoveMethod;
	private Object parentBean;
	transient private FiringBean fBean;
	private final HashSet<ListeningBean> listeners;
	private Class<?> listeningInterface;

	public FiringMethod() {
		listeners = new HashSet<ListeningBean>();
	}

	public int compareTo(Object obj) {
		FiringMethod otherMeth = (FiringMethod) obj;
		String myInterfaceName = listeningInterface.getName();
		String otherInterfaceName = otherMeth.listeningInterface.getName();

		return myInterfaceName.compareTo(otherInterfaceName);
	}

	public void disableAllBeans() {
		Iterator<ListeningBean> it = listeners.iterator();

		while (it.hasNext()) {
			ListeningBean lBean = it.next();
			// find and set its listening status
			Class<?> listenerInterface = checkForListening(lBean);
			if (listenerInterface != null) {
				deregisterListener(lBean, listenerInterface, parentBean);
				lBean.setListeningStatus(ListeningBean.STATUS_WONT_LISTEN);
			}

		}
	}

	public void registerListener(ListeningBean lBean, Class interf,
			Object firingBean) {
		try {
			Object[] args = new Object[1];
			args[0] = lBean.getOriginalBean();
			originalAddMethod.invoke(firingBean, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void deregisterListener(ListeningBean lBean, Class<?> interf,
			Object firingBean) {
		// listenerInterface.for
		// Object[] args = new Object[1];
		// listenerInterface[] args = new listenerInterface.class[1];
		// args[0] = lBean;
		try {
			Object[] args = new Object[1];
			args[0] = lBean.getOriginalBean();
			originalRemoveMethod.invoke(firingBean, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addListeningBean(ListeningBean lBean) {
		ListeningBean newBean = new ListeningBean();
		newBean.setOriginalBean(lBean.getOriginalBean());

		// first, if this bean is the same as the owning bean, don't add it
		if (newBean.getOriginalBean() == parentBean) {
			logger.info("FiringMethod.addListeningBean, found self");

			return;
		}

		// find and set its listening status
		Class<?> listenerInterface = checkForListening(newBean);

		if (listenerInterface != null) {
			newBean.setListeningStatus(ListeningBean.STATUS_LISTENING);
			registerListener(newBean, listenerInterface, parentBean);
		} else {
			newBean.setListeningStatus(ListeningBean.STATUS_CANT_LISTEN);
		}

		listeners.add(lBean);
	}

	public void removeListeningBean(Object oldBean) {

		ListeningBean lBean = findListeningBean(oldBean);
		if (lBean == null) {
			return;
		}

		// find and set its listening status
		Class<?> listenerInterface = checkForListening(lBean);

		if (listenerInterface != null) {
			deregisterListener(lBean, listenerInterface, parentBean);
		}
		listeners.remove(lBean);
		// this.decreaseArraySize(oldBeanPositionInListenersArray);
	}

	private Class<?> checkForListening(ListeningBean listener) {
		Vector<Class<?>> inters = new Vector<Class<?>>();
		inters = findInterfaces(listener.getOriginalBean().getClass(), inters);

		for (int i = 0; i < inters.size(); i++) {
			Class<?> c = inters.get(i);

			if (c.equals(listeningInterface)) {
				// it passes so...
				return c;
			}
		}

		return null;
	}

	private Vector<Class<?>> findInterfaces(Class<?> clazz, Vector<Class<?>> v) {
		if (clazz == Object.class) {
			return v;
		}

		Class[] interfs = clazz.getInterfaces();

		for (Class<?> interf : interfs) {
			v.add(interf);
		}

		Class<?> base = clazz.getSuperclass();

		if (base != null) {
			findInterfaces(base, v);
		}

		return v;
	}

	public boolean listeningBeanOccurs(ListeningBean lBean) {
		boolean occurs = false;
		Iterator<ListeningBean> it = listeners.iterator();

		while (it.hasNext()) {
			ListeningBean listBean = it.next();
			if (lBean.getOriginalBean() == listBean.getOriginalBean()) {
				if (lBean.getListeningStatus() != ListeningBean.STATUS_CANT_LISTEN) {
					occurs = true;
				}
			}
		}
		return occurs;
	}

	public ListeningBean findListeningBean(Object bean) {
		ListeningBean lBean = null;

		Iterator<ListeningBean> it = listeners.iterator();

		while (it.hasNext()) {
			ListeningBean listBean = it.next();
			if (bean == listBean.getOriginalBean()) {
				return listBean;
			}
		}
		return lBean;
	}

	// begin accessors
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object getParentBean() {
		return parentBean;
	}

	public void setParentBean(Object parentBean) {
		this.parentBean = parentBean;
	}

	public void setFiringBean(FiringBean fBean) {
		this.fBean = fBean;
	}

	public FiringBean getFiringBean() {
		return fBean;
	}

	public Method getOriginalRemoveMethod() {
		return originalRemoveMethod;
	}

	public void setOriginalRemoveMethod(Method originalRemoveMethod) {
		this.originalRemoveMethod = originalRemoveMethod;
	}

	public Method getOriginalAddMethod() {
		return originalAddMethod;
	}

	public void setOriginalAddMethod(Method originalAddMethod) {
		this.originalAddMethod = originalAddMethod;
	}

	public Class<?> getListeningInterface() {
		return listeningInterface;
	}

	public void setListeningInterface(Class<?> listeningInterface) {
		this.listeningInterface = listeningInterface;
	}

	// end accessors
}