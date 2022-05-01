package Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class where we test the different methods.
 */
public class Main_Class {
    /**
     * The main of the program where we test the different methods.
     * @param args The default arguments.
     */
    public static void main(String[] args) {
        List<Integer> usedKeys_search;
        List<Integer> usedKeys_range;
        int numOfItems = 100000;
        int upperBound2 = 1000;
        int upperBound1 = 100;
        int maxNum = 1000000;
        int numOfRuns = 100;

        Binary_Search_Tree bst = new Binary_Search_Tree(numOfItems, maxNum, numOfRuns); // Creating a new Binary_Search_Tree Object.
        bst.createTree();                                                               // Creating the tree.
        usedKeys_search = bst.createRandomKey_search();                                 // Creating the random keys that we will use in the search.
        usedKeys_range = bst.createRandomKey_range(upperBound2);                        // Creating the lower bound keys that we will use in the range search.
        bst.searchTree_key(usedKeys_search);                                            // Searching for a specific number of keys (numOfKeys) in the tree
        bst.searchTree_range(usedKeys_range, upperBound1);                              // Searching in range [key, (key + upperBound1]
        bst.searchTree_range(usedKeys_range, upperBound2);                              // Searching in range [key, (key + upperBound2]
        bst.printInfo();                                                                // Printing the results

        Threaded_Binary_Search_Tree tbst = new Threaded_Binary_Search_Tree(numOfRuns, bst.getListOfKeys());
        tbst.createTree();                                                              // Creating the tree
        tbst.searchTree_key(usedKeys_search);                                           // Searching for a specific number of keys (numOfKeys) in the tree
        tbst.searchTree_range(usedKeys_range, upperBound1);                             // Searching in range [key, (key + upperBound1]
        tbst.searchTree_range(usedKeys_range, upperBound2);                             // Searching in range [key, (key + upperBound2]
        tbst.printInfo();                                                               // Printing the results

        Sorted_Array sorted_list = new Sorted_Array(numOfRuns, bst.getListOfKeys());
        sorted_list.createArray();                                                      // Creating the array
        sorted_list.searchArray_key(usedKeys_search);                                   // Searching for a specific number of keys (numOfKeys) in the tree
        sorted_list.searchArray_range(usedKeys_range, upperBound1);                     // Searching in range [key, (key + upperBound1]
        sorted_list.searchArray_range(usedKeys_range, upperBound2);                     // Searching in range [key, (key + upperBound2]
        sorted_list.printInfo();                                                        // Printing the results
    }
}
