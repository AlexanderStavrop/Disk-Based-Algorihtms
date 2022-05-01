package Classes;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class where we perform all of our tests on the B+Tree.
 */
public class Tester {
    /**
     * Main method where we execute all of our tests.
     * @param args The default arguments
     * @throws FileNotFoundException Exception in case there was an error handling the file.
     */
    public static void main(String[] args) throws FileNotFoundException {
        List<Integer> usedKeys_create;                                      // List that contains the keys need to create the B+Tree.
        List<Integer> usedKeys_delete;                                      // List that contains the keys we will delete from the B+Tree.
        List<Integer> usedKeys_search;                                      // List that contains the keys we will search in the B+Tree.
        List<Integer> usedKeys_insert;                                      // List that contains the keys we will use to insert random keys in the B+Tree.
        List<Integer> usedKeys_range;                                       // List that contains the keys we will use a bottom bound in range search in the B+Tree.

        int numOfItems = 100000;                                            // Number of keys inserted in the B+Tree in order to create the tree.
        int upperBound2 = 1000;                                             // Second upper bound value.
        int upperBound1 = 10;                                               // First upper bound value.
        int maxNum = 1000000;                                               // Max value of key.
        int numOfRuns = 20;                                                 // Number of runs.

        int rec_size_data = 32;                                             // Record size in data file.
        int rec_size_index = 8;                                             // Record size in index file.
        int page_size = 256;                                                // Page size of data and index file.
        int info_size = 4;                                                  // Size in bytes of every value that contains information about a node.
        int key_size = 4;                                                   // Size in bytes of every key in the index file.
        int dataPage_size = 5 * info_size;                                  // Size in byte of all the bytes that contain the information of every node.
        int keysInNode = (page_size - dataPage_size) / rec_size_index;      // Number of keys in node.
        int key_value_offset = keysInNode * key_size;                       // Offset between a key and a value in the node.


        BTree Btree = new BTree(numOfItems, maxNum, numOfRuns, page_size, info_size,rec_size_index, rec_size_data, key_size, dataPage_size, key_value_offset);

        usedKeys_create = Btree.createRandomKeys_create();                  // List of keys containing the keys we will use to create a B+Tree.
        usedKeys_insert = Btree.createRandomKeys_insert();                  // List of keys containing the keys we will use to insert random keys in the B+Tree.
        usedKeys_search = Btree.createRandomKeys_search();                  // List of keys containing the keys we will use to search random keys in the B+Tree.
        usedKeys_range = Btree.createRandomKeys_searchRange();              // List of keys containing the keys we will use to search a range of keys in the a B+Tree.
        usedKeys_delete = Btree.createRandomKeys_delete();                  // List of keys containing the keys we will use to delete random keys in the B+Tree.

        Btree.createTree(usedKeys_create);                                  // Creating the tree.
        Btree.insertKeys(usedKeys_insert);                                  // Inserting random keys.
        Btree.searchTree_key(usedKeys_search);                              // Searching random keys.
        Btree.searchTree_Range(usedKeys_range,upperBound1);                 // Searching a range of keys with upperBound equal to upperBound1.
        Btree.searchTree_Range(usedKeys_range,upperBound2);                 // Searching a range of keys with upperBound equal to upperBound2.
        Btree.deleteKeys(usedKeys_delete);                                  // Deleting random keys.
        Btree.printInfo();                                                  // Printing the test results.
    }

}
