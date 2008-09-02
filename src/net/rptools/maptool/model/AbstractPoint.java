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

public abstract class AbstractPoint implements Cloneable {

    public int x;
    public int y;
    
    public AbstractPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof AbstractPoint)) return false;
    	AbstractPoint p = (AbstractPoint) o;
    	
    	return p.x == x && p.y == y;
    }

    public int hashCode() {
    	return new String(x+"-"+y).hashCode();
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }
    
    public AbstractPoint clone() {
    	try {
    	    return (AbstractPoint) super.clone();
    	} catch (CloneNotSupportedException e) {
    	    // this shouldn't happen, since we are Cloneable
    	    throw new InternalError();
    	}
    }
}
