package data_structures;

import java.util.Iterator;

import javafx.scene.layout.StackPane;

//import java.util.Iterator;

/**
 * LinkedStack for insertion and removal of disks to each pip and jail
 * @author YeohB - 17357376
 * @author Ee En Goh - 1720691
 */

public class LinkedStack<E> extends StackPane implements Iterable<E>, StackInterface<E> {


	private static class Node<E> {

		/** The E stored at this node */
		private E element;

		/** A reference to the subsequent node in the list */
		private Node<E> next;

		/**
		 * Creates a node with the given E and next node.
		 *
		 * @param e: the E to be stored
		 * @param n: reference to a node that should follow the new node
		 */
		public Node(E element, Node<E> n) {
			this.element = element;
			this.next = n;
		}

		/**
		 * Returns the E stored at the node.
		 * 
		 * @return the E stored at the node
		 */
		public E getElement() {
			return this.element;
		}

		/**
		 * Sets E e to current E of Node
		 * 
		 * @param e
		 */
		@SuppressWarnings("unused")
		public void setElement(E element) {
			this.element = element;
		}

		/**
		 * Returns the node that follows this one (or null if no such node).
		 * 
		 * @return the following node
		 */
		public Node<E> getNext() {
			return this.next;
		}

		/**
		 * Sets the node's next reference to point to Node n.
		 * 
		 * @param n the node that should follow this one
		 */
		@SuppressWarnings("unused")
		public void setNext(Node<E> n) {
			this.next = n;
		}

		/**
		 * @return String representation of the E in the Node
		 */
		@Override
		public String toString() {
			return this.element.toString();
		}
	}

	/**
	 * Instance variables
	 */
	private Node<E> top = null;
	private int size = 0;

	/**
	 * Constructor
	 */
	protected LinkedStack() {
		super();
	}

	/**
	 * @return size of stack
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * @return boolean value of whether size is 0
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @return new ListIterator Object
	 */

	@Override
	public Iterator<E> iterator() {
		return new ListIterator<E>();
	}

	/**
	 * @return Element at top of the Stack
	 */
	@Override
	public E top() {
		if (isEmpty()) {
			throw new StackEmptyException();
		}

		return top.getElement();
	}

	/**
	 * @param Element e to insert into Stack
	 */
	@Override
	public void push(E element) {
		top = new Node<>(element, top);
		size++;
	}

	/**
	 * @return Element of Node removed
	 */
	@Override
	public E pop() {
		if (isEmpty()) {
			throw new StackEmptyException();
		}

		E temp = top.getElement();
		top = top.getNext();
		size--;

		return temp;
	}
	
	/**
	 * String representation of the Stack
	 */
	@Override
	public String toString() {
		String output = new String();
		StringBuffer temp = new StringBuffer();

		Node<E> curr = top;

		for (int i = 0; i < size(); i++) {
			temp.append("]" + curr.getElement() + "[ ");
			curr = curr.getNext();
		}

		/**
		 * Not sure if using the toString() method is allowed here It's more efficient
		 * than reinventing the wheel, especially for larger lists
		 */
		output = temp.reverse().toString();

		return output;
	}

	private class ListIterator<T> implements Iterator<T> {

		@SuppressWarnings("unchecked")
		Node<T> curr = (Node<T>) top;

		@Override
		public boolean hasNext() {
			return curr != null;
		}

		@Override
		public T next() {
			T value = curr.getElement();
			curr = curr.getNext();
			return value;
		}
	}

	/**
	 * Test function
	 */
	public static void test() {
		LinkedStack<Integer> stack = new LinkedStack<>(); // Creating a new Stack

		/**
		 * Inserting into the stack
		 */
		stack.push(1);
		stack.push(2);
		stack.push(3);
		stack.push(4);

		System.out.println(stack.toString());

		/**
		 * Printing the top of the stack
		 */
		System.out.println("Top of stack: " + stack.top()); // Top of the stack: 4

		/**
		 * Removing from the queue
		 */
		System.out.println("Popped: " + stack.pop()); // Popped: 4
		System.out.println("Popped: " + stack.pop()); // Popped : 3

		/**
		 * Printing the queue
		 */
		System.out.println(stack); // Size: 2
									// [2]
									// [1]

		/**
		 * Removing from the queue
		 */
		System.out.println("Popped: " + stack.pop()); // Popped: 2
		System.out.println("Popped: " + stack.pop()); // Popped : 1

		/**
		 * Triggering exception
		 */
		// System.out.println("Popped: " + stack.pop()); // Exception: Stack is empty

	}

	/**
	 * Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}
}
