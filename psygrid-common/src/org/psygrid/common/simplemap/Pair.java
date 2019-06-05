package org.psygrid.common.simplemap;

/**
 * Simple Map class for ws transfer, implemented using generics.
 * 
 * @author Lucy Bridges
 *
 */
public class Pair <T, V> {

	public T name;
	public V value;
	
	public Pair() {
	}
	
	public Pair(T name, V value) {
		this.name = name;
		this.value = value;
	}
	
	public T getName() {
		return name;
	}
	public void setName(T name) {
		this.name = name;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
}
