package net.rptools.maptool.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BaseModel {

	// Transient so that it isn't transfered over the wire
	private transient List<ModelChangeListener> listenerList = new CopyOnWriteArrayList<ModelChangeListener>();

	
	public void addModelChangeListener(ModelChangeListener listener) {
		listenerList.add(listener);
	}

	public void removeModelChangeListener(ModelChangeListener listener) {
		listenerList.remove(listener);
	}

	protected void fireModelChangeEvent(ModelChangeEvent event) {

		for (ModelChangeListener listener : listenerList) {
			listener.modelChanged(event);
		}
	}

}
