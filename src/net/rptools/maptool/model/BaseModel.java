/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
	
	private Object readResolve() {
		listenerList = new CopyOnWriteArrayList<ModelChangeListener>();
		return this;
	}
}
