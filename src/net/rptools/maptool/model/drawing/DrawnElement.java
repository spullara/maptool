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
package net.rptools.maptool.model.drawing;

import java.io.Serializable;

/**
 */
public class DrawnElement {

	private Drawable drawable;
	private Pen pen;
	
	public DrawnElement(Drawable drawable, Pen pen) {
		this.drawable = drawable;
		this.pen = pen;
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
	
	public Pen getPen() {
		return pen;
	}
}
