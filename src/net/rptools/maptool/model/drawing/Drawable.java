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

import java.awt.Graphics2D;
import java.awt.geom.Area;

import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;


/**
 * @author drice
 */
public interface Drawable {
	
    public void draw(Graphics2D g, Pen pen);
    public java.awt.Rectangle getBounds();
    public Area getArea();
    public GUID getId();
    public Zone.Layer getLayer();
    public void setLayer(Zone.Layer layer);
}
