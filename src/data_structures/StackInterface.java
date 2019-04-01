package data_structures;

/**
 * @author Ferdia Fagan - 16372803
 * @author Braddy Yeoh - 17357376
 * @param <E>
 */

public interface StackInterface<E> {

	/**
	 * @return The size of Stack
	 */
	int size();
	
	/**
	 * @return boolean value of whether this stack size is 0
	 */
	boolean isEmpty();
	
	/**
	 * Insert a new element with value e into Stack
	 * @param e
	 */
	void push(E e);
	
	/**
	 * Reference to the top element of this stack
	 * @return The value of the top element of Stack 
	 */
	E top();
	
	/**
	 * @return The element of Node removed
	 */
	E pop();
}
