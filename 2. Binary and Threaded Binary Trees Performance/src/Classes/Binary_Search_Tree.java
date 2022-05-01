package Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>Class for creating and searching a Binary Search Tree.</h2>
 */
public class Binary_Search_Tree {
    /**
     * List of keys that contains the used keys for searching a random key in bst.
     */
    private final List<Integer> usedKeys_search = new ArrayList<>();
    /**
     * List of keys that contains the used keys for searching a random range of keys in bst.
     */
    private final List<Integer> usedKeys_range = new ArrayList<>();
    /**
     * List of keys that contains the used keys for creating the bst.
     */
    private final List<Integer> listOfKeys = new ArrayList<>();
    /**
     * List that contains the number of counts we got in every method.
     */
    private final List<Integer> counters = new ArrayList<>();
    /**
     * List that contains the upper bounds used in the bst.
     */
    private final List<Integer> uppers = new ArrayList<>();
    /**
     * The column that contains the right leaf of the keys.
     */
    private final int right_column = 2;
    /**
     * The column that contains the left leaf of the keys.
     */
    private final int left_column = 1;
    /**
     * The column that contains the keys of the matrix.
     */
    private final int info_column = 0;
    /**
     * The 2 dimensional matrix used to iterate the bst.
     */
    private final int[][] treeMatrix;
    /**
     * The value that stores the number of keys in the tree.
     */
    private final int numOfKeys;
    /**
     * The value that stores the number of runs we do to get our measurements.
     */
    private final int numOfRuns;
    /**
     * The value that stores the max key value.
     */
    private final int maxNum;
    /**
     * The value that stores the number of comparisons and assignments.
     */
    private int counter = 0;
    /**
     * The value that stores the next available row of the matrix.
     */
    private int AVAIL = 0;
    /**
     * The value that stores the value of the root.
     */
    private int root;

//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the Binary Search Tree.
     * @param numOfKeys Number of keys we want to have in our tree.
     * @param maxNum Max value of possible key.
     * @param numOfRuns Number of tests we will perform.
     */
    public Binary_Search_Tree(int numOfKeys, int maxNum, int numOfRuns) {
        treeMatrix = new int[numOfKeys + 1000][3];
        this.numOfKeys = numOfKeys;
        this.numOfRuns = numOfRuns;
        this.maxNum = maxNum;
        this.root = -1;
    }

//_____________________________________________________ Setters ________________________________________________________
    /**
     * Setter for the root value.
     * @param key The key we want to insert to the tree.
     * @return  The matrix index where we stored the key.
     */
    private int setRoot_value(int key){
        int index = AVAIL;                                                              // Creating a new tmp value index equal to the AVAIL value.
        AVAIL = treeMatrix[index][right_column];                                        // Setting the value of the AVAIL equal to the next available position.
        treeMatrix[index][info_column] = key;                                           // Setting the value of the first column equal to the key.
        treeMatrix[index][left_column] = treeMatrix[index][right_column] = -1;          // Setting the second and the third columns equal to -1.
        return index;                                                                   // Returning the position of the row we filled.
    }

//_____________________________________________________ Getters ________________________________________________________
    /**
    * Getter for the list of keys we created in order to search the Binary Search Tree.
    * @return List of keys we used to search the tree.
    */
    public List<Integer> getUsedKeys_search() {
    return usedKeys_search;
    }

    /**
     * Getter for the list of keys we created in order to create the Binary Search Tree.
     * @return List of keys we used to create the tree.
     */
    public List<Integer> getListOfKeys() {
        return listOfKeys;
    }

//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for initialising the matrix in order to create the tree.
     * We set the first 2 columns equal to -1 and the third column equal to 1,2,3... so we can get the next available position.
     */
    private void initialiseMatrix(){
        for (int i=0; i < numOfKeys; i++){                                                              // Looping for as many keys as we have.
            treeMatrix[i][info_column] = treeMatrix[i][left_column] = -1;                               // Setting the first and the second column equal to -1.
            treeMatrix[i][right_column] = i + 1;                                                        // Setting the third column equal to i + 1 so it can be used as the of AVAIL stack
        }                                                                                               // the next available position.
        treeMatrix[numOfKeys - 1][right_column] = -1;                                                   // Setting the third column equal to -1 for the last row
    }

    /**
     * Method for creating a Binary Search Tree.
     */
    public void createTree(){
        counter = 0;                                                                                    // Setting the number of comparisons equal to 0
        initialiseMatrix();                                                                             // Initialising the matrix
        for(int i=0; i < numOfKeys; i++)                                                                // Looping for as many keys as we have
            root = insertKey(createRandomKey(listOfKeys,maxNum), root);                                 // Inserting a unique and random key to the tree
        counters.add(counter/numOfKeys);                                                                // Storing the number of comparisons needed to create the tree
    }

    /**
     * Method for inserting a key to the tree recursively.
     * @param key The key we want to insert to the key.
     * @param curr_root The value of the tree root.
     * @return Returning the value of the tree root.
     */
    private int insertKey(int key, int curr_root) {
        counter++;                                                                                      // Increasing the number of comparisons by 1
        if (curr_root == -1) {                                                                          // Checking if the root we inserted is equal to -1 (If the tree is empty)
            counter += 5;                                                                               // Adding 5 to the counter because set_Root method has 5 assignments we have to count
            return setRoot_value(key);                                                                  // If true we return
        }

        counter ++;                                                                                     // Increasing the number of comparisons by 1
        if (key < treeMatrix[curr_root][info_column]){                                                  // Checking if the key is smaller than the root of the sub-tree we are checking
            treeMatrix[curr_root][left_column] = insertKey(key, treeMatrix[curr_root][left_column]);    // If true, we recursively call the method to check the left sub-tree
            counter ++;                                                                                 // Increasing the number of comparisons by 1 due to the assignment
        }
        else {                                                                                          // Else checking if the key is bigger than the root of the sub-tree we are checking
            treeMatrix[curr_root][right_column] = insertKey(key, treeMatrix[curr_root][right_column]);  // If true, we recursively call the method to check the right sub-tree
            counter++;                                                                                  // Increasing the number of comparisons by 1 due to the assignment
        }
        return curr_root;                                                                               // Returning the root of our sub-tree
    }

    /**
     * Method we call in order to search a random key in the tree.
     * @param usedKeys_search List that contains the keys we are going to search in the tree.
     */
    public void searchTree_key(List<Integer> usedKeys_search){
        counter = 0;                                                                                    // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++)                                                                // Looping for as many runs as we have.
            searchKey(usedKeys_search.get(i),root);                                                     // Searching for a specific key in the tree.
        counters.add(counter/numOfRuns);                                                                // Adding the number of comparisons needed to create the tree.
    }

    /**
     * Method for searching a key in the tree recursively.
     * @param key The key we are looking for.
     * @param curr_root The root of the sub-tree we are checking.
     */
    private void searchKey(int key,int curr_root) {

        counter++;                                                                                      // Increasing the number of comparisons by 1.
        if (curr_root == -1)                                                                            // Checking whether the root is equal to -1 (Empty tree).
            return;                                                                                     // If true we return.

        counter++;                                                                                      // Increasing the number of comparisons by 1.
        if (key < treeMatrix[curr_root][info_column])                                                   // Checking whether the key is smaller than the root of the current sub-tree.
            searchKey(key, treeMatrix[curr_root][left_column]);                                         // If true we recursively search the left sub-tree.
        else if (key > treeMatrix[curr_root][info_column]){                                             // Checking whether the key is bigger than the root of the current sub-tree.
            searchKey(key, treeMatrix[curr_root][right_column]);                                        // If true, we recursively call the method to check the right sub-tree.
            counter++;                                                                                  // Increasing the number of comparisons by 1.
        }else{                                                                                          // Else the current root value is equal to the key.
            counter++;                                                                                  // Increasing the number of comparisons by 1.
            // Returning.
        }
    }

    /**
     * Method we call to search a range of keys in the Binary Search Tree.
     * @param usedKeys_range that contains the keys we are going to search in the tree.
     * @param upper The value of upperbound we add to the key.
     */
    public void searchTree_range(List<Integer> usedKeys_range, int upper){
        counter = 0;                                                                                    // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++)                                                                // Looping for as many runs as we have.
            searchRange(root, usedKeys_range.get(i), (usedKeys_range.get(i) + upper));                  // Searching for a specific key range in the tree.
        counters.add(counter/numOfRuns);                                                                // Storing the number of comparisons needed to create the tree.
        uppers.add(upper);                                                                              // Storing the upper value we used.
    }

    /**
     * Method for searching a range of keys in the Binary Search Tree.
     * @param curr_root  The root of the sub-tree we are currently checking.
     * @param k1 The lower bound of the range.
     * @param k2 The upper bound of the range.
     */
    private void searchRange(int curr_root, int k1, int k2){
        counter ++;                                                                                     // Increasing the number of comparisons by 1.
        if (curr_root == -1)                                                                            // Checking if the root we inserted is equal to -1 (If the tree is empty).
            return;                                                                                     // If true we return.

        counter ++;                                                                                     // Increasing the number of comparisons by 1.
        if (k2 < treeMatrix[curr_root][info_column]){                                                   // Checking if the upper bound is smaller than the root of the sub-tree we are checking.
            searchRange(treeMatrix[curr_root][left_column],k1,k2);                                      // If true, we recursively call the method to check the right sub-tree.
        }else if (k1 > treeMatrix[curr_root][info_column]){                                             // Checking if the key is smaller than the root of the sub-tree we are checking.
            counter ++;                                                                                 // Increasing the number of comparisons by 1.
            searchRange(treeMatrix[curr_root][right_column],k1,k2);                                     // If true, we recursively call the method to check the left sub-tree.
        }else{
            counter ++;                                                                                 // Increasing the number of comparisons by 1.
            searchRange(treeMatrix[curr_root][left_column],k1,k2);                                      // If true, we recursively call the method to check the left sub-tree.
            searchRange(treeMatrix[curr_root][right_column],k1,k2);                                     // If true, we recursively call the method to check the left sub-tree.
        }
    }

    /**
     * Method for getting a random and unique key.
     * @param keyList The list we check to see if the key has been used before.
     * @param max The max value of the key that can be generated.
     * @return Returning the new key.
     */
    public int createRandomKey(List<Integer> keyList, int max){                                         // Method for creating a random and unique key.
        while(true){                                                                                    // Looping until we find a unique key.
            int key = (int)(Math.random() * (max - 1 + 1) + 1);                                         // Generating a key from 1 to 10^6.
            if (!keyList.contains(key)){                                                                // If the key already exists.
                keyList.add(key);                                                                       // If the key is unique we add it to the ArrayList.
                return key;                                                                             // We return the key so it can be stored.
            }
        }
    }

    /**
     * Method for creating a list that contains the keys we will use to search the tree.
     * @return The list that contains the keys we are going to use to search the tree (random unique keys).
     */
    public List<Integer> createRandomKey_search(){
        for(int i=0; i < numOfRuns; i++)
            createRandomKey(usedKeys_search,maxNum);                                                    // Creating a random key and adding it to the list usedKeys_search.
        return usedKeys_search;
    }

    /**
     * Method for creating a list that contains the keys we will use as lower bound in the range search.
     * @param upper The Value we subtract from the max number so we won't get a lower bound equal to the maxNum.
     * @return The list that contains the keys we are going to use to search a range of keys in the tree (random unique keys).
     */
    public List<Integer> createRandomKey_range(int upper){
        for(int i=0; i < numOfRuns; i++)
            createRandomKey(usedKeys_range,maxNum - upper);                                         	// Creating a random key to be our bottom bound.
        return usedKeys_range;
    }

    /**
     * Method for printing the information we gathered from the measurements.
     */
    public void printInfo(){
        System.out.println("-------------------------------------------------------- Binary Search Tree -------------------------------------------------------");
        System.out.println("Number of comparisons to create a binary tree (in average) : " + counters.get(0));
        System.out.println("Number of comparisons to search the binary tree (in average) : " + counters.get(1));
        System.out.println("Number of comparisons to search the binary tree in range(k, k + " + uppers.get(0) + ") (in average) : " + counters.get(2));
        System.out.println("Number of comparisons to search the binary tree in range(k, k + "  +uppers.get(1) + ") (in average) : " + counters.get(3));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }

}
