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
