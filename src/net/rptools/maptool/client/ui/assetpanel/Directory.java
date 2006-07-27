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
package net.rptools.maptool.client.ui.assetpanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Directory {

    private static final FileFilter DIRECTORY_FILTER = new DirectoryFileFilter();

    private List<PropertyChangeListener> listenerList = new CopyOnWriteArrayList<PropertyChangeListener>();  
    
    private File directory;

    private Directory parent;
    private List<Directory> subdirs;
    private List<File> files;

    private FilenameFilter fileFilter;
    
    public Directory(File directory) {
        this (directory, null);
    }
    public Directory(File directory, FilenameFilter fileFilter) {
        if (directory.exists() && !directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
        
        this.directory = directory;
        this.fileFilter = fileFilter;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(listener);
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Directory)) {
            return false;
        }
        
        return directory.equals(((Directory)o).directory);
    }
    
    public File getPath() {
        return directory;
    }
    
    public void refresh() {
    	subdirs = null;
    	files = null;
    }
    
    public List<Directory> getSubDirs() {
        load();
        return subdirs;
    }
    
    public List<File> getFiles() {
        load();
        return files;
    }
    
    public Directory getParent() {
        return parent;
    }
    
    private void load() {
        if (files == null && subdirs == null) {

            files = Collections.unmodifiableList((List<File>)addAll(new ArrayList(), directory.listFiles(fileFilter)));
            File [] subdirList = directory.listFiles(DIRECTORY_FILTER);
            subdirs = new ArrayList<Directory>();
            for (int i = 0; i < subdirList.length; i++) {
                Directory newDir = newDirectory(subdirList[i], fileFilter); 
                newDir.parent = this;
                subdirs.add(newDir);
            }
            subdirs = Collections.unmodifiableList(subdirs);
        }
    }
    
    private static class DirectoryFileFilter implements FileFilter {
        
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }
    
    private static List addAll(List list, Object[] elems) {
        for (Object o : elems) {
            list.add(o);
        }
        return list;
    }
    
    protected Directory newDirectory(File directory, FilenameFilter fileFilter) {
    	return new Directory(directory, fileFilter);
    }
    
    protected void firePropertyChangeEvent(PropertyChangeEvent event) {
        
    	// Me
        for (PropertyChangeListener listener : listenerList) {
            listener.propertyChange(event);
        }
        
        // Propagate up
        if (parent != null) {
        	parent.firePropertyChangeEvent(event);
        }
    }
    
    public static Comparator<Directory> COMPARATOR = new Comparator<Directory>() {
    	public int compare(Directory o1, Directory o2) {
    		String filename1 = o1.getPath().getName();
    		String filename2 = o2.getPath().getName();
    		return filename1.compareToIgnoreCase(filename2);
    	}
    };
}
