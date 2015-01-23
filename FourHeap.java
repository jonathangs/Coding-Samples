package phaseA;

import providedCode.*;

import java.util.NoSuchElementException;

/**
 * This FourHeap is an implementation of the Heap interface that is used with Sorter 
 * for the heapSort algorithm. 
 * 
 */
public class FourHeap<E> extends Heap<E> {

    private static final int INITIAL_CAPACITY = 650;
    private static final int RESIZE_FACTOR = 2;
    private Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public FourHeap(Comparator<? super E> c) {
        heapArray = (E[]) new Object[INITIAL_CAPACITY];
        comparator = c;
        size = 0;
    }

    /**
     * Insert an item into the heap
     */
    @Override
    public void insert(E item) {

        if (size == heapArray.length - 1) {
            myResize();
        }

        // Percolate up
        int hole = size;
        heapArray[size] = item;
        if (size > 0) {
            for (; comparator.compare(item, heapArray[(hole - 1) / 4]) < 0
                    && hole > 0; hole = (hole - 1) / 4) {
                heapArray[hole] = heapArray[(hole - 1) / 4];
            }
        }
        heapArray[hole] = item;
        size++;
    }

    /**
     * Helper function that resizes the array for the heap
     */
    private void myResize() {
        @SuppressWarnings("unchecked")
        E new_arr[] = (E[]) new Object[heapArray.length * RESIZE_FACTOR];

        // Copy old array to new array
        for (int i = 0; i < heapArray.length; i++) {
            new_arr[i] = heapArray[i];
        }
        heapArray = new_arr;
    }

    /**
     * Allows us to look at the top of the heap without removing the top element
     */
    @Override
    public E findMin() {
        if (size == 0)
            throw new NoSuchElementException("The heap is empty...");
        return heapArray[0];
    }

    public int getSize() {
        return size;
    }

    /** 
     * Returns and removes the top value from the heap
     */
    @Override
    public E deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("The heap is empty...");
        }
        E removed_element = heapArray[0];
        heapArray[0] = heapArray[size - 1];
        size--;
        if (size > 0 && findBestIndex(0, heapArray[0]) != 0) {
            percolateDown(0, heapArray[0]);
        }
        return removed_element;
    }

    /**
     * Rearranges the heap when a value is deleted
     */
    private void percolateDown(int hole, E plug) {
        while (hole != findBestIndex(hole, plug)) {
            int best_index = findBestIndex(hole, plug);
            heapArray[hole] = heapArray[best_index];
            hole = best_index;
        }
        heapArray[hole] = plug;
    }

    /** 
     * Returns the best possible child based on the index of the parent
     */
    private int findBestIndex(int hole, E plug) {
        // We must find the best possible choice out of the options below
        // Starts at first child
        int searcher = hole * 4 + 1;

        // Keep track of best index. 
        int best_index = searcher;

        for (; searcher < size && searcher <= hole * 4 + 4; searcher++) {
            if (comparator.compare(heapArray[searcher], heapArray[best_index]) < 0) {
                best_index = searcher;
            }
        }
        
        //Ensure that the new index will sort the heap.
        if (best_index < size
                && comparator.compare(heapArray[best_index], plug) < 0) {
            return best_index;
        }
        return hole;
    }

    // Check to see if the heap is empty
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
