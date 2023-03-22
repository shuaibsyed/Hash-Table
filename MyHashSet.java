import java.util.ArrayList;
import java.util.Iterator;

/** 
 * The MyHashSet API is similar to the Java Set interface. This
 * collection is backed by a hash table.
 */
public class MyHashSet<E> implements Iterable<E> {

	/** Unless otherwise specified, the table will start as
	 * an array (ArrayList) of this size.*/
	private final static int DEFAULT_INITIAL_CAPACITY = 4;

	/** When the ratio of size/capacity exceeds this
	 * value, the table will be expanded. */
	private final static double MAX_LOAD_FACTOR = 0.75;

	
	public ArrayList<Node<E>> hashTable;

	private int size;  // number of elements in the table


	


	
	public static class Node<T> {
		private T data;
		public Node<T> next;  

		private Node(T data) {
			this.data = data;
			next = null;
		}
	}

	/**
	 * Initializes an empty table with the specified capacity.  
	 *
	 * @param initialCapacity initial capacity (length) of the 
	 * underlying table
	 */
	public MyHashSet(int initialCapacity) {
		hashTable = new ArrayList<>();
		for(int i = 0; i < initialCapacity; i++) {
			hashTable.add(null);
		}
	}

	/**
	 * Initializes an empty table of length equal to 
	 * DEFAULT_INITIAL_CAPACITY
	 */
	
	public MyHashSet() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Returns the number of elements stored in the table.
	 * @return number of elements in the table
	 */
	public int size(){
		return size;
	}

	/**
	 * Returns the length of the table (the number of buckets).
	 * @return length of the table (capacity)
	 */
	public int getCapacity(){
		return hashTable.size();
	}
	
	/* Returns the hash value of the element.
	 * 
	 */
	private int getHash(E element) {
		int hashValue = Math.abs(element.hashCode() % hashTable.size());
		return hashValue;
	}

	/* Remakes the table if the load factor has been passed. Doubles the table size and rehashes all the elements and \
	 * puts them back into the bigger table.
	 */
	private void reHash() {
		ArrayList<Node<E>> newTable = new ArrayList<>();
		int size = hashTable.size()*2;
		for(int i = 0; i < size; i++) {
			newTable.add(null);
		}
		//Creates the table double the size
		
		for(int i = 0; i < getCapacity(); i++) {
			if(hashTable.get(i) != null) {
				Node<E> curr = hashTable.get(i);
				
				while(curr != null) {
					int currHash = Math.abs(curr.data.hashCode() % newTable.size()); // new hash value for new table
					Node<E> newCurr = new Node<>(curr.data);// current element that will added to new table
					
					if(newTable.get(currHash) == null) {
						newTable.set(currHash, newCurr);
						//if the bucket in table is empty then the element is first element in that bucket
					} else {
						Node<E> spot = newTable.get(currHash);
						while(spot.next != null) {
							spot= spot.next;
						}
						if(spot.next == null) {
							spot.next = newCurr;
						}
						//adds the element to the end of the bucket if that bucket is not empty
					}
					curr = curr.next;
				}
			}
		}
		hashTable = newTable;
	}
	/** 
	 * Looks for the specified element in the table.
	 * 
	 * @param element to be found
	 * @return true if the element is in the table, false otherwise
	 */
	public boolean contains(Object element) {
		Node<E> current = hashTable.get(getHash((E) element));
		if(current == null) {
			return false;
		} 
		while(current.next != null) {
			if(current.data.equals(element)) {
				return true;
			}
			current = current.next;
			if(current.next == null) {
				if(current.data.equals(element)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Adds the specified element to the collection, if it is not
	 * already present.  If the element is already in the collection, 
	 * then this method does nothing.
	 * 
	 * @param element the element to be added to the collection
	 */
	public void add(E eleme1nt) {
		if(this.contains(eleme1nt)) {
			return;
			//does nothing if element is already there
		} else {
			Node<E> newElement = new Node<>(eleme1nt);
			if(hashTable.get(getHash(eleme1nt)) == null) {
				hashTable.set(getHash(eleme1nt), newElement);
				//if the bucket is empty then the element is the first in that bucket
			} else {
				Node<E> curr = hashTable.get(getHash(eleme1nt));
				while(curr.next != null) {
					curr = curr.next;
				}
				if(curr.next == null) {
					curr.next = newElement;
				}
				//adds the element to the end of the bucket if that bucket is not empty
			}
			size++;
		}
		if((double)size/getCapacity() >= MAX_LOAD_FACTOR) {
			reHash(); // if the load factor is passed it will rehash the entire table
		}
	}

	/** Removes the specified element from the collection.  If the
	 * element is not present then this method should do nothing (and
	 * return false in this case).
	 *
	 * @param element the element to be removed
	 * @return true if an element was removed, false if no element 
	 * removed
	 */
	public boolean remove(Object element) {
		if(!contains(element)) {
			return false;
			//does nothing if element is not present
		} else {
			Node<E> curr = hashTable.get(getHash((E) element));
			Node<E> prev = null;
			while(curr != null) {
				if(curr.data.equals(element)) {
					if(curr == hashTable.get(getHash((E) element))) {
						hashTable.set(getHash((E) element), curr.next);
						return true;
					} else {
						prev.next = curr.next;
						return true;
					}
				} else {
					prev = curr;
					curr = curr.next;
				}
			}
			return false;
		}
	}

	/** Returns an Iterator that can be used to iterate over
	 * all of the elements in the collection.
	 * 
	 * The order of the elements is unspecified.
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int currentItem = 0;
			private int pos = 0;
			Node<E> curr = null;
			Node<E> returnNode = null;
			
			@Override
			public boolean hasNext() {
				return (currentItem < size())? true : false;
			}

			@Override
			public E next() {
				if(hashTable.get(pos) == null || curr == null) {
					while(hashTable.get(pos) == null) {
						pos++;
					}
					curr = hashTable.get(pos);
				}
				if(curr != null) {
					returnNode = curr;
					curr = curr.next;
				}
				if(curr == null) {
					pos++;
				}
				currentItem++;
				return returnNode.data;
			}
			
		};
	}

}
