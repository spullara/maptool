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
package net.rptools.maptool.client.swing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trevor
 */
public class AnimationManager {

	private static List<Animatable> animatableList = new ArrayList<Animatable>();

	private static List<Animatable> removeList = new ArrayList<Animatable>();
	private static List<Animatable> addList = new ArrayList<Animatable>();

	private static int delay = 200;

	static {
		new AnimThread().start();
	}

	public static void addAnimatable(Animatable animatable) {

		synchronized (animatableList) {
			if (!animatableList.contains(animatable)) {
				addList.add(animatable);
			}
		}
	}

	public static void removeAnimatable(Animatable animatable) {

		synchronized (animatableList) {
			removeList.remove(animatable);
		}
	}

	private static class AnimThread extends Thread {

		public void run() {

			while (true) {

				if (animatableList.size() > 0) {

				}

				synchronized (animatableList) {

					animatableList.addAll(addList);
					addList.clear();

					for (Animatable animatable : animatableList) {
						animatable.animate();
					}

					animatableList.removeAll(removeList);
					removeList.clear();
				}

				try {
					Thread.sleep(delay);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			}
		}
	}
}
