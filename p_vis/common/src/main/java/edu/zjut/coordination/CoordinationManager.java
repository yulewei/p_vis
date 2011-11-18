/* Licensed under LGPL v. 2.1 or any later version;
 see GNU LGPL for details.
 Original Author: Frank Hardisty */
package edu.zjut.coordination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * 利用Java反射, 解析"addXXX"开头方法("removeXXX"方法), 自动完成注册(及被注册), 添加(及被添加)事件监听器,
 * 自动关联事件源与事件监听器.
 * <p/>
 * This is the normal entry point into the coordination package.
 * 
 * This class accepts an arbitrary number of objects, and performs a default
 * coordination based on their method signatures.
 * 
 * Objects with methods starting with addFooListener(FooListener l) and
 * removeFooListener(FooListener l) are considered to be trying to broadcast
 * events of type Foo. Objects that implement interfaces of type FooListener
 * will be added to the broadcasting classes ListenerList, except for events in
 * the "disallowedPackages" listed in the FiringBean class (such as
 * java.awt.event and javax.swing.event).
 */
public class CoordinationManager {
	private transient final HashSet<FiringBean> firingBeans;
	private transient final HashSet<ListeningBean> listeningBeans;

	/**
	 * Null constructor.
	 */
	public CoordinationManager() {
		firingBeans = new HashSet<FiringBean>();
		listeningBeans = new HashSet<ListeningBean>();
	}

	public boolean containsBean(Object bean) {
		for (FiringBean fireBean : firingBeans) {
			if (fireBean.getOriginalBean() == bean) {
				return true;
			}
		}
		for (ListeningBean listenBean : listeningBeans) {
			if (listenBean.getOriginalBean() == bean) {
				return true;
			}
		}

		return false;
	}

	private boolean containsOriginalBean(Object beanIn) {
		Iterator<FiringBean> it = firingBeans.iterator();
		boolean inThere = false;

		while (it.hasNext()) {
			FiringBean fBean = it.next();
			// we use == instead of object.equals() because
			// == refers to memory locations, which is what we want.
			// Normally, object.equals is preferred, but not here.
			if (fBean.getOriginalBean() == beanIn) {
				inThere = true;
			}
		}
		return inThere;
	}

	/**
	 * Adds bean to the lists of firing beans and to the list of listening
	 * beans. Also registers this bean with previously added beans, and visa
	 * versa, based on the criteria above. Returns the instance of "FiringBean"
	 * that is created based on the added bean, or null if the bean was
	 * previously added. Also returns null if null is handed in.
	 */
	public FiringBean addBean(Object beanIn) {
		if (beanIn == null || containsOriginalBean(beanIn)) {
			return null;
		}
		FiringBean newBean = new FiringBean();
		newBean.setOriginalBean(beanIn);

		addNewBean(newBean);
		findUniqueName(newBean);
		return newBean;
	}

	private void findUniqueName(FiringBean newBean) {
		Object bean = newBean.getOriginalBean();

		// how many instances of this class already?
		int numFound = 0;
		Iterator<FiringBean> fIt = firingBeans.iterator();
		while (fIt.hasNext()) {
			FiringBean fBean = fIt.next();

			Object obj = fBean.getOriginalBean();

			if (obj.getClass() == bean.getClass() && fBean != newBean) {
				numFound++;
			} // end if
		} // next bean

		if (numFound > 0) {
			String beanName = newBean.getBeanName();
			numFound++; // let's count like humans
			beanName = beanName + " (" + numFound + ")";
			newBean.setBeanName(beanName);
		}
	}

	public int removeBean(Object oldBean) {
		int searchResult = 0;

		removeOldBean(oldBean);
		return searchResult;
	}

	private void removeOldBean(Object oldBean) {

		// remove this bean as a listener from all existing firing beans
		Iterator<FiringBean> fireIt = firingBeans.iterator();
		while (fireIt.hasNext()) {
			FiringBean fBean = fireIt.next();
			fBean.removeListeningBean(oldBean);

		}

		// update sets
		Iterator<FiringBean> fIt = firingBeans.iterator();

		FiringBean removeBean = null;
		while (fIt.hasNext()) {
			FiringBean fBean = fIt.next();
			if (fBean.getOriginalBean() == oldBean) {
				removeBean = fBean;
			}

		}
		if (removeBean != null) {
			firingBeans.remove(removeBean);
			removeBean = null;
		}

		Iterator<ListeningBean> lIt = listeningBeans.iterator();
		ListeningBean removeListener = null;
		while (lIt.hasNext()) {
			ListeningBean lBean = lIt.next();
			if (lBean.getOriginalBean() == oldBean) {
				removeListener = lBean;
			}
		}
		listeningBeans.remove(removeListener);
		removeListener = null;

	}

	private void addNewBean(FiringBean newBean) {

		// make a listening bean out of the new bean
		ListeningBean newListener = new ListeningBean();
		newListener.setOriginalBean(newBean.getOriginalBean());

		// add this bean as a listener to all existing firing beans
		Iterator<FiringBean> it = firingBeans.iterator();
		while (it.hasNext()) {
			FiringBean fBean = it.next();
			fBean.addListeningBean(newListener);
		}
		// next add all existing beans as listeners of the new one
		// is it OK if we share listening bean instances? Let's go for it
		Iterator<ListeningBean> itL = listeningBeans.iterator();

		while (itL.hasNext()) {
			newBean.addListeningBean(itL.next());
		}

		// update sets
		listeningBeans.add(newListener);
		firingBeans.add(newBean);
	}

	/**
	 * This method returns a list of all FiringMethods which reference the
	 * ListeningBean passed in.
	 */
	FiringMethod[] getFiringMethods(ListeningBean lBean) {
		ArrayList<FiringMethod> li = new ArrayList<FiringMethod>();
		Iterator<FiringBean> fIt = firingBeans.iterator();
		while (fIt.hasNext()) {

			FiringBean fireBean = fIt.next();
			FiringMethod[] meths = fireBean.getMethods();

			for (FiringMethod meth : meths) {
				boolean occurs = meth.listeningBeanOccurs(lBean);

				if (occurs) {
					li.add(meth);
				} // if occurs
			} // next firingMethod
		} // next firingBean

		FiringMethod[] allMeths = new FiringMethod[li.size()];

		for (int i = 0; i < li.size(); i++) {
			allMeths[i] = (FiringMethod) li.get(i);
		}

		return allMeths;
	}

	public Set<FiringBean> getFiringBeans() {
		return firingBeans;
	}

	public void disconnectBeans(FiringMethod meth, ListeningBean lBean) {
		meth.deregisterListener(lBean, meth.getListeningInterface(),
				meth.getParentBean());
	}

	public void reconnectBeans(FiringMethod meth, ListeningBean lBean) {
		meth.registerListener(lBean, meth.getListeningInterface(),
				meth.getParentBean());
	}

}
