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
package net.rptools.maptool.client.ui;


public class StaticMessageDialog extends MessageDialog {

	private String status;

	public StaticMessageDialog(String status) {
		this.status = status;
	}

	@Override
	protected String getStatus() {
		return status;
	}

	/**
	 * Doesn't work right as it forces a repaint of the GlassPane object which takes a snapshot
	 * of the RootPane and then adds the 'status' message as an overlay.  The problem is
	 * that the RootPane snapshot includes the previous image that might have been
	 * displayed previously.
	 * @param s
	 */
	public void setStatus(String s) {
		this.status = s;
		revalidate();
		repaint();
	}
}
