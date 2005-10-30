/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.walker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.model.Zone;

public abstract class AbstractZoneWalker implements ZoneWalker {
    protected List<PartialPath> partialPaths = new ArrayList<PartialPath>();
    protected final Zone zone;
    
    public AbstractZoneWalker(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone() {
        return zone;
    }
    
    public void setWaypoints(CellPoint... points) {
    	partialPaths.clear();
    	addWaypoints(points);
    }
    
    public void addWaypoints(CellPoint... points) {

    	CellPoint previous = partialPaths.size() > 0 ? partialPaths.get(partialPaths.size()-1).end : null;
    	for (CellPoint current : points) {
    		if (previous != null) {
    			partialPaths.add(new PartialPath(previous, current, calculatePath(previous, current)));
    		}
    		
    		previous = current;
    	}
    }
    
    public CellPoint replaceLastWaypoint(CellPoint point) {
    	if (partialPaths.size() == 0) return null;
    	
    	PartialPath oldPartial = partialPaths.remove(partialPaths.size()-1);
    	
    	// short circuit exit of the point hasn't changed.
    	//if (oldPartial.end.equals(point)) return null;
    	
    	partialPaths.add(new PartialPath(oldPartial.start, point, calculatePath(oldPartial.start, point)));

    	return oldPartial.end;
    }
    
    public List<CellPoint> getPath() {
    	List<CellPoint> ret = new ArrayList<CellPoint>();
    	
    	PartialPath last = null;
    	for (PartialPath partial : partialPaths) {
    		if (partial.path != null && partial.path.size() > 1) {
    			ret.addAll(partial.path.subList(0, partial.path.size() - 1));
    		}
    		last = partial;
    	}
    	
    	if (last != null) {
    		ret.add(last.end);
    	}
    	
    	return ret;
    }
    
    public boolean isWaypoint(CellPoint point) {
    	if (point == null) return false;

    	PartialPath last = null;
    	for (PartialPath partial : partialPaths) {
    		if (partial.start.equals(point)) return true;
    		
    		last = partial;
    	}
    	
    	if (last.end.equals(point)) return true;
    	
    	return false;
    }

    /**
     * @see net.rptools.maptool.client.walker.ZoneWalker#removeWaypoint(net.rptools.maptool.client.CellPoint)
     */
    public boolean removeWaypoint(CellPoint aPoint) {
      if (aPoint == null || partialPaths == null || partialPaths.isEmpty()) return false;
      
      // Find the partial path with the given end point
      ListIterator<PartialPath> i = partialPaths.listIterator();
      while (i.hasNext()) {
        PartialPath path = i.next();
        if (path.end.equals(aPoint)) {
          
          // If this is the last partial path then done, otherwise 
          // combine this path and the next and replace them with a combined path
          if (!i.hasNext()) return false;
          i.remove();
          PartialPath path2 = i.next();
          i.set(new PartialPath(path.start, path2.end, calculatePath(path.start, path2.end)));
          return true;
        } // endif
      } // endwhile
      return false;
    }
    
    /**
     * @see net.rptools.maptool.client.walker.ZoneWalker#toggleWaypoint(net.rptools.maptool.client.CellPoint)
     */
    public boolean toggleWaypoint(CellPoint aPoint) {
      if (removeWaypoint(aPoint)) return true;
      addWaypoints(aPoint);
      return true;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      StringBuilder s = new StringBuilder("Path: ");
      for (PartialPath path : partialPaths) {
        s.append("\n   ");
        s.append(path.toString());
      } // endfor
      return s.toString();
    }
    
    protected abstract List<CellPoint> calculatePath(CellPoint start, CellPoint end);
    
    protected static class PartialPath {
    	final CellPoint start;
    	final CellPoint end;
    	final List<CellPoint> path;
    	
    	public PartialPath(CellPoint start, CellPoint end, List<CellPoint> path) {
    		this.start = start;
    		this.end = end;
    		this.path = path;
    	}
      
      /**
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString() {
        StringBuilder s = new StringBuilder("PartialPath([");
        s.append(start.x);
        s.append(",");
        s.append(start.y);
        s.append("], [");
        s.append(end.x);
        s.append(",");
        s.append(end.y);
        s.append("]");
        return s.toString();
      }
    }
}
