package phaseB;
import providedCode.Comparator;
import providedCode.DataCount;
import providedCode.DataCounter;
import providedCode.Hasher;
import providedCode.SimpleIterator;

/**
 * The following is a linear probing implementation of a hash table that uses
 * prime numbers for all hashing operations. This hash table keeps track of the
 * amount of occurrences of any given data type.
 */
public class HashTable<E> extends DataCounter<E> {
	private int size;
	private Comparator<? super E> comparator;
	private Hasher<E> hasher;
	
	private DataCountItem[] table;
	
	//used to maintain prime number size for the hash table
	private int primeNum;
	private int currentPrime;
	private int[] PRIMES_LIST = 
			new int[]{17,41,83,163,337,683,1409,2861,6011,12113,24229,50021,100103,200003, 400009,800029,1299827};

	/**
	 * Constructs a new empty hash table DataCounter using the given 
	 * Comparator and Hasher.
	 * @param c comparator used to compare entries 
	 * @param h hasher used to generate hashcode for inputs
	 */
	@SuppressWarnings("unchecked")
	public HashTable(Comparator<? super E> c, Hasher<E> h) {
		comparator = c;
		hasher = h;
		size = 0;
		primeNum = 0;
		currentPrime = PRIMES_LIST[primeNum];
		table = (HashTable<E>.DataCountItem[])(new HashTable.DataCountItem[currentPrime]);
	}

	/** {@inheritDoc} */
	@Override
	public void incCount(E data){
		if(size + 1 >= table.length/2)
			rehashData();
		int index = getIndex(data, table);
		if(table[index] == null){
			table[index] = new DataCountItem(data);
			size++;
		}else{
			table[index].count++;
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public int getSize(){
		return size;
	}
	
	/** {@inheritDoc} */
	@Override
	public int getCount(E data){
		int index = getIndex(data, table);
		if(table[index] == null)
			return 0;
		
		return table[index].count;
		
	}
	
   /**
	* Retrieve the index for the data of any given table. 
	*/
	private int getIndex(E data, DataCountItem[] array){
		int raw_id = hasher.hash(data);
		int key = raw_id % array.length;
		int i = 1;
		
		while(array[key] != null && comparator.compare(data, array[key].data) !=0){
			key = (raw_id + i) % array.length;
			i++;
		}
		
		return key;		
	}
	
	//Helper method that moves the data to a new, larger array
	private void rehashData(){
		if(primeNum + 1 == PRIMES_LIST.length)
			throw new IndexOutOfBoundsException("Cannot rehash; too many elements");
		
		currentPrime = PRIMES_LIST[++primeNum];
		@SuppressWarnings("unchecked")
		DataCountItem[] newTable = (HashTable<E>.DataCountItem[])(new HashTable.DataCountItem[currentPrime]);
		
		for(int i = 0; i < table.length; i++){
			if(table[i]!= null){
				int index = getIndex(table[i].data, newTable);
				newTable[index] = table[i];
			}
		}
		
		table = newTable;
			
	}
	
	/** {@inheritDoc} */
	@Override
	public SimpleIterator<DataCount<E>> getIterator() {
		return new SimpleIterator<DataCount<E>>() {  
    		DataCountItem[] copyArray = table.clone();
    		private int currentSize = size;
    		
    		int index = 0;
    		public boolean hasNext() {
        		return (currentSize > 0);
        	}
        	public DataCount<E> next() {
        		if(!hasNext()) {
        			throw new java.util.NoSuchElementException();
        		}
        		for(int i = index; i < copyArray.length; i++ ){
        			if(copyArray[i] !=null){
        				currentSize--;
        				
        				if(index + 1 < copyArray.length)
        					index = i + 1;
        				return new DataCount<E>(copyArray[i].data, copyArray[i].count);
        			}
        		}
        		return null;
        	}
    	};
	}

	//Stores the information on how many times we've inserted any given item.
	private class DataCountItem{
		private int count;
		private E data;
		
		private DataCountItem(E data){
			count = 1;
			this.data = data;
		}
	}
	
	/* Methods for testing, should be removed when used by client */
	public int getCapacity(){
		return table.length;
	}
}
