package data_structures;

/**
 * @author Braddy Yeoh - 17357376
 */

import java.util.Iterator;

public interface List<E> extends Iterable<E> {
	
	/**
	 * @return boolean value of whether size is 0
	 */
	boolean isEmpty();
	
	/**
	 * @return size of list
	 */
	int size();
	
	/**
	 * return type Iterator<E> to iterate through list
	 */
	Iterator<E> iterator();
	
	/**
	 * @param i
	 * @return element of Node at given index
	 */
	E get(int index);
	
	/**
	 * @param index of node
	 * @param element desired to insert into node of given index
	 */
	void set(int index, E element);
	
	/**
	 * 
	 * @param index of node
	 * @param element of node at given index to add
	 */
	void add(int index, E element);
	
	/**
	 * 
	 * @param index node to remove
	 * @return element of node rmeoved
	 */
	E remove(int index); 
	
}
