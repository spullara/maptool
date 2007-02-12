package net.rptools.maptool.model;

public class Association <E, T>{

	private E lhs;
	private T rhs;
	
	public Association(E lhs, T rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void setLeft(E value) {
		lhs = value;
	}
	
	public void setRight(T value) {
		rhs = value;
	}
	
	public E getLeft() {
		return lhs;
	}
	
	public T getRight() {
		return rhs;
	}
}
