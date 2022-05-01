package Classes;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements a B+tree on the disc using 2 files, one index and one data file.
 */
public class BTree {
    /**
     * List that contains the upper bound values we use (For printing reasons).
     */
    private final List<Integer> upperBounds = new ArrayList<>();
    /**
     * List that contains the measurements we took in the tests (For Testing reasons).
     */
    private final List<Integer> counters = new ArrayList<>();
    /**
     * FileHandler instance that handles the files.
     */
    private final FileHandler fileHandler;
    /**
     * Variable that contains the number of keys needed to be inserted in order to create a B+Tree.
     */
    private final int numOfKeys;
    /**
     * Number of runs we make in each test.
     */
    private final int numOfRuns;
    /**
     * Max key value.
     */
    private final int maxNum;
    /**
     * Variable that holds the root page of the tree.
     */
    private int root_page;


//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the B+tree.
     *
     * @param numOfKeys Number of keys to create the tree.
     * @param maxNum Max value of possible key.
     * @param numOfRuns Number of runs we will perform in each test.
     * @param page_size Size of page that stores a node of the tree.
     * @param info_size Size in bytes of every variable that contains information about a node.
     * @param rec_size_index Size in bytes of a key and its value.
     * @param rec_size_data Size in bytes of a key in the data file.
     * @param key_size Size in bytes of a key.
     * @param dataPage_size Size in bytes of all the information a node has.
     * @param key_value_offset Offset in bytes between a key and its corresponding value.
     * @throws FileNotFoundException Exception if an error occurs while handling the files.
     */
    public BTree(int numOfKeys, int maxNum, int numOfRuns, int page_size, int info_size, int rec_size_index,int rec_size_data, int key_size, int dataPage_size, int key_value_offset) throws FileNotFoundException {
        RandomAccessFile data_file = new RandomAccessFile("Data", "rw");                                                //Creating a new data file if it doesnt exist.
        RandomAccessFile index_file = new RandomAccessFile("Index", "rw");                                              //Creating a new index file if it doesnt exist.
        fileHandler = new FileHandler(page_size, info_size, rec_size_index,rec_size_data, key_size, dataPage_size,key_value_offset, index_file, data_file);

        BTreeLeafNode new_root = new BTreeLeafNode(page_size, 0, fileHandler);                                          // Creating a new BTreeLeaf node that initializes our root and holds its instance.
        this.root_page = new_root.getPage();                                                                            // Setting the root_page value accordingly.
        fileHandler.updateIndex(new_root);                                                                              // Updating the index file to the latest version.
        this.numOfRuns = numOfRuns;                                                                                     // Initializing the numOfRuns variable.
        this.numOfKeys = numOfKeys;                                                                                     // Initializing the numOfKeys variable.
        this.maxNum = maxNum;                                                                                           // Initializing the maxNum variable.
    }


//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for creating a B+ Tree.
     * @param usedKeys_create List that contains the keys we are going to insert in the tree in order to create the tree.
     */
    public void createTree (List<Integer> usedKeys_create){
        for (int i = 0; i < numOfKeys; i++)                                                                             // Looping for as many keys as we have.
            insert(usedKeys_create.get(i), usedKeys_create.get(i));                                                     // Inserting the key and the value to the tree.
    }

    /**
     * Method we call for inserting a number of random and unique keys in the B+ Tree.
     * @param usedKeys_insert List that contains the keys we are going to insert in the tree.
     */
    public void insertKeys (List<Integer> usedKeys_insert){
        fileHandler.initialize_counter();                                                                               // Initializing the counter to 0.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            insert(usedKeys_insert.get(i),usedKeys_insert.get(i));                                                      // Inserting the key and the value to the tree.
        counters.add(fileHandler.getCounts()/numOfRuns);                                                                // Storing the number of disc accesses needed to create the tree.
        fileHandler.emptyHashTable();                                                                                   // Emptying the hashTable.
    }

    /**
     * Method for inserting a key and its value to the B+ Tree.
     * @param key The key we are inserting.
     * @param value The corresponding value we are inserting.
     */
    private void insert(Integer key, Integer value) {
        BTreeNode curr_root = retrieveNode(root_page);                                                                  // Retrieving the root node from the file.
        BTreeLeafNode leaf = this.findCorrectLeafNode(key, curr_root);                                                  // Getting the correct leaf node where the key should be inserted.
        leaf.insertKey(key, fileHandler.getOffset());                                                                   // Inserting the key and its corresponding byteOffset of the value in the data file, to the tree.
        fileHandler.increaseRecs();                                                                                     // Increasing the number of records in the file

        if (leaf.isOverflow()) {                                                                                        // Checking whether the node has overflow.
            BTreeNode n = leaf.dealOverflow();                                                                          // If true fix it.
            if (n != null) {                                                                                            // Checking whether the root has changed.
                this.root_page = n.getPage();                                                                           // If true we update the root_page value.
                fileHandler.removeAndPut(n);                                                                            // Adding the new root node to the hashTable.
            }
        }
        updateData(value);                                                                                              // Updating the data file.
        updateTree();                                                                                                   // Updating the index file.
    }

    /**
     * Method for searching the leaf node in which the key we are looking for should exist.
     * @param key The key we are looking for.
     * @param root The BtreeNode instance that holds the root node.
     * @return The BTreeLeafNode instance in which the key should exist.
     */
    private BTreeLeafNode findCorrectLeafNode(Integer key, BTreeNode root) {
        BTreeNode node = root;                                                                                          // Creating a copy instance of the root node.

        while (node.getNodeType() == 0)                                                                                 // Looping until we find a BtreeNode instance that is not an innerNode one.
            node = retrieveNode(node.getValues(node.search(key)));                                                      // Retrieving a node from the file, that should contain the key we are looking for.

        return (BTreeLeafNode)node;                                                                                     // Returning the correct leaf node.
    }

    /**
     * Method we call in order to search a number of random keys in the tree.
     * @param usedKeys_search List that contains the keys we are going to search in the tree.
     */
    public void searchTree_key(List<Integer> usedKeys_search){
        fileHandler.initialize_counter();                                                                               // Initializing the counter to 0.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            search(usedKeys_search.get(i));                                                                             // Searching for a specific key in the tree.
        counters.add(fileHandler.getCounts()/numOfRuns);                                                                // Storing the number of disc accesses needed to create the tree.
        fileHandler.emptyHashTable();                                                                                   // Emptying the hashTable.
    }

    /**
     * Method for searching a key in the B+tree.
     * @param key The key we are looking for.
     * @return The value of the corresponding key if the key exists or -1 if the key does not exist.
     */
    private int search(int key) {
        BTreeNode curr_root = retrieveNode(root_page);                                                                  // Retrieving the root node from the file.
        BTreeLeafNode leaf = this.findCorrectLeafNode(key,curr_root);                                                   // Getting the correct leaf node where the key should exist.

        int index = leaf.search(key);                                                                                   // Searching for the key in the correct leaf node.
        index =  (index == -1) ? -1 : leaf.getValues(index);                                                            // Setting the index value according the returning value of the search function.

        return (index == -1) ? -1 : retrieveData(index);                                                                // Returning -1 the key does not exist -1 or the corresponding value from the data file if it exists.
    }

    /**
     * Method we call for searching keys in a given range.
     * @param usedKeys_searchRange The list that contains the keys we are going to use as lower bound in the search range.
     * @param upperBound The value that we are going to use as an upperBound.
     */
    public void searchTree_Range(List<Integer> usedKeys_searchRange, int upperBound) {
        fileHandler.initialize_counter();                                                                               // Initializing the counter to 0.
        for (int i = 0; i < numOfRuns; i++)                                                                             // Looping for as many runs as we have.
            searchRange(usedKeys_searchRange.get(i),usedKeys_searchRange.get(i) + upperBound);                          // Searching in range of (key, key + upperbound).
        this.upperBounds.add(upperBound);                                                                               // Adding the value of the upperBound to the list.
        counters.add(fileHandler.getCounts()/numOfRuns);                                                                // Storing the number of disc accesses needed to create the tree.
        fileHandler.emptyHashTable();                                                                                   // Emptying the hashTable.
    }

    /**
     * Method for searching keys in a given range.
     * @param lowerBound The value that we are going to use as an lowerBound.
     * @param upperBound The value that we are going to use as an upperBound.
     */
    private void searchRange (int lowerBound, int upperBound) {
        BTreeNode curr_root = retrieveNode(root_page);                                                                  // Retrieving the root node from the file.
        BTreeNode leaf = findCorrectLeafNode(lowerBound,curr_root);                                                     // Getting the correct leaf node where the key should be inserted.
        int tmp_key = 0;                                                                                                // Creating a variable that holds the current key we are retrieving.

        while (tmp_key < upperBound){                                                                                   // Looping while the key we retrieved is in the range are searching.
            for (int j = 0; j < leaf.getKeyCount(); j++){                                                               // Looping for as many keys as we got in the node.
                tmp_key = leaf.getKey(j);                                                                               // Updating the tmp_key value with the new key from the node.
                if (tmp_key >= lowerBound && tmp_key <= upperBound)                                                     // Checking whether the key we got from the node is in the range we want.
                    retrieveData(leaf.getValues(j));                                                                    // If true we retrieve the value associated with the key from the data file.
            }
            if (leaf.getRightSibling() != -1){                                                                          // Checking whether the leaf node has a rightSibling.
                leaf = retrieveNode(leaf.getRightSibling());                                                            // If true we retrieve the rightSibling node.
                tmp_key = leaf.getKey(0);                                                                               // Updating the tmp_key value with the first key of the node.
            }
        }
    }

    /**
     * Method for deleting a number of random and unique keys in the B+ Tree.
     * @param usedKeys_delete List that contains the keys we are going to delete from the tree.
     */
    public void deleteKeys (List<Integer> usedKeys_delete){
        fileHandler.initialize_counter();                                                                               // Initializing the counter to 0.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            delete(usedKeys_delete.get(i));                                                                             // Deleting a key from the tree.
        counters.add(fileHandler.getCounts()/numOfRuns);                                                                // Storing the number of disc accesses needed to create the tree.
        fileHandler.emptyHashTable();                                                                                   // Emptying the hashTable.
    }

    /**
     * Method we call for deleting a key from the B+tree.
     * @param key The key to be deleted.
     */
    private void delete(int key) {
        BTreeNode curr_root = retrieveNode(root_page);                                                                  // Retrieving the root node from the file.
        BTreeLeafNode leaf = this.findCorrectLeafNode(key,curr_root);                                                   // Getting the correct leaf node where the key should exist.
        if (leaf.delete(key) && leaf.isUnderflow()) {                                                                   // Checking whether we successfully deleted a key and the node has underflow.
            BTreeNode n = leaf.dealUnderflow();                                                                         // If true we deal with the underflow.
            if (n != null) {                                                                                            // Checking whether there is a new root.
                this.root_page = n.getPage();                                                                           // If true, we update the root_page value.
                fileHandler.removeAndPut(n);                                                                            // Adding the root node to the hashTable
            }
        }
        updateTree();                                                                                                   // Updating the index file.
    }

    /**
     * Method for updating the index file.
     */
    private void updateTree(){
        for(Object key: fileHandler.getToUpdate().keySet().toArray())                                                   // Looping for as many items as we got in the hashTable.
            fileHandler.updateAndRemove(fileHandler.getToUpdate().get(key));                                            // Updating the associated page.
    }

    /**
     * Method for updating the data file.
     * @param value The value we are inserting in the data file.
     */
    private void updateData(int value){
        fileHandler.updateData(value);                                                                                  // Updating the data file.
    }

    /**
     * Method for retrieving a node from the index file.
     * @param page The page of the target node.
     * @return Returning the BtreeNode instance we retrieved.
     */
    private BTreeNode retrieveNode(int page){
        return fileHandler.getNode(page);                                                                               // Retrieving and returning the node at given page.
    }

    /**
     * Method for retrieving the corresponding value from the data file.
     * @param index The index where the key is stored.
     * @return Returning the int value we got from the data file.
     */
    private int retrieveData(int index){
        return fileHandler.getData(index);                                                                              // Retrieving and returning the value corresponding value.
    }


    //______________________________________________ Tester Methods ____________________________________________________
    /**
     * Method for creating a list that contains the keys we will use to create the B+ tree.
     * @return The list that contains the keys we are going to use to search the tree (random unique keys).
     */
    public List<Integer> createRandomKeys_create(){
        List<Integer> usedKeys_create = new ArrayList<>();                                                              // Creating a new arrayList that will hold the keys we are going to use in the search testing.
        for(int i=0; i < numOfKeys; i++)                                                                                // Looping for as many runs as we have.
            createRandomKey(usedKeys_create,maxNum);                                                                    // Creating a random and unique number and adding it to the list.
        return usedKeys_create;                                                                                         // Returning the list.
    }

    /**
     * Method for creating a list that contains the keys we will use to search the B+ tree.
     * @return The list that contains the keys we are going to use to search the tree (random unique keys).
     */
    public List<Integer> createRandomKeys_insert(){
        List<Integer> usedKeys_insert = new ArrayList<>();                                                              // Creating a new arrayList that will hold the keys we are going to use in the search testing.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            createRandomKey(usedKeys_insert,maxNum);                                                                    // Creating a random and unique number and adding it to the list.
        return usedKeys_insert;                                                                                         // Returning the list.
    }

    /**
     * Method for creating a list that contains the keys we will use to search the B+ tree.
     * @return The list that contains the keys we are going to use to search the tree (random unique keys).
     */
    public List<Integer> createRandomKeys_search(){
        List<Integer> usedKeys_search = new ArrayList<>();                                                              // Creating a new arrayList that will hold the keys we are going to use in the search testing.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            createRandomKey(usedKeys_search,maxNum);                                                                    // Creating a random and unique number and adding it to the list.
        return usedKeys_search;                                                                                         // Returning the list.
    }

    /**
     * Method for creating a list that contains the keys we will use to search a range of keys in the B+ tree.
     * @return The list that contains the keys we are going to use to search a range of keys int the B+ tree (random unique keys).
     */
    public List<Integer> createRandomKeys_searchRange(){
        List<Integer> usedKeys_searchRange = new ArrayList<>();                                                         // Creating a new arrayList that will hold the keys we are going to use in the range search testing.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            createRandomKey(usedKeys_searchRange,maxNum);                                                               // Creating a random and unique number and adding it to the list.
        return usedKeys_searchRange;                                                                                    // Returning the list.
    }

    /**
     * Method for creating a list that contains the keys we will delete from the B+ tree.
     * @return The list that contains the keys we are going to delete from the B+ tree (random unique keys).
     */
    public List<Integer> createRandomKeys_delete(){
        List<Integer> usedKeys_delete = new ArrayList<>();                                                              // Creating a new arrayList that will hold the keys we are going to use in delete testing.
        for(int i=0; i < numOfRuns; i++)                                                                                // Looping for as many runs as we have.
            createRandomKey(usedKeys_delete, maxNum);                                                                   // Creating a random and unique number and adding it to the list.
        return usedKeys_delete;                                                                                         // Returning the list.
    }

    /**
     * Method for getting a random and unique key.
     * @param keyList The list we check to see if the key has been used before.
     * @param max The max value of the key that can be generated.
     */
    public void createRandomKey(List<Integer> keyList, int max){                                                        // Method for creating a random and unique key.
        while(true){                                                                                                    // Looping until we find a unique key.
            int key = (int)(Math.random() * (max - 1 + 1) + 1);                                                         // Generating a key from 1 to 10^6.
            if (!keyList.contains(key)){                                                                                // If the key already exists.
                keyList.add(key);                                                                                       // If the key is unique we add it to the ArrayList.
                return;                                                                                                 // We return the key so it can be stored.
            }
        }
    }

    /**
     * Method for printing the results of the tests.
     */
    public void printInfo(){
        System.out.println("-------------------------------------------------------- Binary Search Tree -------------------------------------------------------");
        System.out.printf("Number of disc accesses to insert %d new keys to the B+ Tree (on average) : %d\n",numOfRuns, counters.get(0));
        System.out.printf("Number of disc accesses to search for %d random keys in the B+ Tree (on average) : %d\n",numOfRuns, counters.get(1));
        System.out.printf("Number of disc accesses to search for %d a range of keys in the B+ Tree for upperBound %d (on average) : %d\n",numOfRuns,upperBounds.get(0), counters.get(2));
        System.out.printf("Number of disc accesses to search for %d a range of keys in the B+ Tree for upperBound %d (on average) : %d\n",numOfRuns, upperBounds.get(1), counters.get(3));
        System.out.printf("Number of disc accesses to delete %d new keys to the B+ Tree (on average) : %d\n",numOfRuns, counters.get(4));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }


}

