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

public class ModelChangeEvent {
	public Object model;
	public Object eventType;
	public Object arg;

	public ModelChangeEvent(Object model, Object eventType) {
		this(model, eventType, null);
	}

	public ModelChangeEvent(Object model, Object eventType, Object arg) {
		this.model = model;
		this.eventType = eventType;
		this.arg = arg;
	}

	public Object getModel() {
		return model;
	}

	public Object getArg() {
		return arg;
	}

	public Object getEvent() {
		return eventType;
	}

	@Override
	public String toString() {
		return "ModelChangeEvent: " + model + " - " + eventType + " - " + arg;
	}
}
