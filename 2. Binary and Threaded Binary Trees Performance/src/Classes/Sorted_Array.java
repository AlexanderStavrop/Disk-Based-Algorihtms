package Classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2>Class for creating and searching a Sorted List.</h2>
 */
public class Sorted_Array {
    /**
     * List that contains the number of counts we got in every method.
     */
    private final List<Integer> counters = new ArrayList<>();
    /**
     * List that contains the upper bounds used in the sorted array.
     */
    private final List<Integer> uppers = new ArrayList<>();
    /**
     * List of keys that contains the used keys for creating the sorted array.
     */
    private final List<Integer> listOfKeys;
    /**
     * The array we will use as our sorted array.
     */
    private final int[] sorted_array;
    /**
     * The value that stores the number of runs we do to get our measurements.
     */
    private final int numOfRuns;
    /**
     * The value that stores the number of comparisons and assignments.
     */
    private int counter = 0;

//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the Sorted List.
     * @param numOfRuns      Number of tests we will perform.
     * @param listOfKeys     List that contains all the keys used to create the Binary Search Tree
     */
    public Sorted_Array(int numOfRuns, List<Integer> listOfKeys) {
        this.sorted_array = new int[listOfKeys.size()];
        this.listOfKeys = listOfKeys;
        this.numOfRuns = numOfRuns;
    }

//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for creating a Sorted Array.
     */
    public void createArray(){
        for (int i = 0; i < listOfKeys.size(); i++)                                 // Looping for as many keys as we have.
            sorted_array[i] = listOfKeys.get(i);                                    // Setting the value of sorted_array[i] equal to listOfKeys(i).
        Arrays.sort(sorted_array);                                                  // Sorting the Array.
    }

    /**
     * Method for searching the sorted Array with Binary Search.
     * @param usedKeys_search List that contains the keys we are going to search in the tree.
     */
    public void searchArray_key(List<Integer> usedKeys_search){
        counter = 0;                                                                // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++)                                            // Looping for as many runs as we have.
            binarySearch(usedKeys_search.get(i));                                         // Searching for the key in the array with Binary search.
        counters.add(counter/numOfRuns);                                            // Adding the number of comparisons needed to create the tree.
    }

    /**
     * Method for implementing Binary Search.
     * @param key The key value we are looking for
     * @return  Returning the index of the key if it exists, else returning the next value in the array (the next bigger value)
     */
    private int binarySearch(int key) {
        counter += 3;                                                               // Increasing the number of comparisons by 2 due to the assignments.
        int mid = 0, left = 0, right = sorted_array.length - 1;                     // Creating the left right and mid variables

        counter ++;                                                                 // Increasing the number of comparisons by 1.
        while (left <= right) {                                                     // Looping while the left value is smaller or equal to the right value.
            mid = left + (right - left) / 2;                                        // Setting the value of mid equal to the half size of the sub-array we are checking.
            counter ++;                                                             // Increasing the number of comparisons by 1 due to the assignment.

            counter ++;                                                             // Increasing the number of comparisons by 1.
            if (sorted_array[mid] < key) {                                          // Checking if the value of array is smaller than the key.
                left = mid + 1;                                                     // If true we check the right part of the array.
                counter ++;                                                         // Increasing the number of comparisons by 1.
            }else if (sorted_array[mid] > key) {                                    // Else the key is bigger than the array value.
                right = mid - 1;                                                    // We check the left part of the array.
                counter += 2;                                                       // Increasing the number of comparisons by 1 due to the comparison and by 1 due to the assignment.
            }else{                                                                  // Else the current array value is equal to the key.
                counter ++;                                                         // Increasing the number of comparisons by 1.
                return mid;                                                         // Return the mid value(index).
            }
            counter ++;                                                             // Increasing the number of comparisons by 1.
        }
        return mid + 1;                                                             // Returning the mid + 1 index.
    }

    /**
     * Method for searching a range of keys in the sorted Array with Binary Search.
     * @param usedKeys_range List that contains the keys we are going to search in the tree.
     * @param upper The upper bound of the range.
     */
    public void searchArray_range(List<Integer> usedKeys_range, int upper) {
        counter = 0;                                                                // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++) {                                          // Looping for as many runs as we have.

            counter ++;                                                             // Increasing the number of comparisons by 1 due to the assignment.
            int index = binarySearch(usedKeys_range.get(i));                        // Setting the index value equal to the index of the lower bound in the sorted array.

            counter ++;                                                             // Increasing the number of comparisons by 1.
            while (sorted_array[index] <= (usedKeys_range.get(i) + upper)){         // Looping until the current array value is smaller than the upperbound.
                counter += 2;                                                       // Increasing the number of comparisons by 1 due to the assignment and by 1 due to the comparison.
                index ++;                                                           // Increasing the index value by 1.
            }
        }
        counters.add(counter/numOfRuns);                                            // Storing the number of comparisons needed to create the tree.
        uppers.add(upper);                                                          // Storing the upper value we used.
    }

    /**
     * Method for printing the information we gathered from the measurements.
     */
    public void printInfo(){
        System.out.println("----------------------------------------------------------- Sorted List -----------------------------------------------------------");
        System.out.println("Number of comparisons to search the sorted list (in average) : " + counters.get(0));
        System.out.println("Number of comparisons to search the sorted list in range(k, k + " + uppers.get(0) + ") (in average) : " + counters.get(1));
        System.out.println("Number of comparisons to search the sorted list in range(k, k + " + uppers.get(1) + ") (in average) : " + counters.get(2));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
