package Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for creating and searching a Threaded Binary Search Tree.
 */
public class Threaded_Binary_Search_Tree {
    /**
     * List that contains the number of counts we got in every method.
     */
    private final List<Integer> counters = new ArrayList<>();
    /**
     * The column that contains the right lead of the keys.
     */
    private final List<Integer> uppers = new ArrayList<>();
    /**
     * The column that tell us if we have a right thread.
     */
    private final int right_thread = 4;
    /**
     * The column that contains the right leaf of the keys.
     */
    private final int right_column = 2;
    /**
     * The column that tell us if we have a left thread.
     */
    private final int left_thread = 3;
    /**
     * The column that contains the right leaf of the keys.
     */
    private final int left_column = 1;
    /**
     * The column that contains the keys of the matrix.
     */
    private final int info_column = 0;
    /**
     * The 2 dimensional matrix used to iterate the tbst.
     */
    private final int[][] treeMatrix;
    /**
     * The value that stores the number of runs we do to get our measurements.
     */
    private final int numOfRuns;
    /**
     * List of keys that contains the used keys for creating the tbst.
     */
    List<Integer> listOfKeys;
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
     * Constructor for the Threaded Binary Search Tree.
     * @param numOfRuns Number of tests we will perform.
     * @param listOfKeys List that contains all the keys used to create the Binary Search Tree
     */
    public Threaded_Binary_Search_Tree(int numOfRuns, List<Integer> listOfKeys) {
        treeMatrix = new int[listOfKeys.size()][5];
        this.listOfKeys = listOfKeys;
        this.numOfRuns = numOfRuns;
        this.root = -1;
    }

//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for initialising the matrix in order to create the tree.
     */
    private void initialiseMatrix(){
        for (int i=0; i < listOfKeys.size(); i++){                                      // Looping for as many keys as we have.
            treeMatrix[i][right_column] = i + 1;                                        // Setting the third column equal to i + 1 so it can be used as the of the next available position.
            treeMatrix[i][info_column] = treeMatrix[i][left_column] = -1;               // Setting the first and the second column equal to -1.
            treeMatrix[i][left_thread] = treeMatrix[i][right_thread] = -1;              // Setting the third and the forth column equal to -1.
        }
        treeMatrix[listOfKeys.size() - 1][right_column] = -1;                           // Setting the third column equal to -1 for the last row.
    }

    /**
     * Method for creating a Threaded Binary Search Tree.
     */
    public void createTree(){
        counter = 0;                                                                    // Setting the number of comparisons equal to 0.
        initialiseMatrix();                                                             // Initialising the matrix
        for (Integer listOfKey : listOfKeys)                                            // Looping for as many keys as we have.
            root = insertKey(listOfKey, root);                                          // Inserting a unique and random key to the tree.
        counters.add(counter/listOfKeys.size());                                        // Storing the number of comparisons needed to create the tree.
    }

    /**
     * Method for inserting a key to the tree recursively.
     * @param key The key we want to insert to the key.
     * @param curr_root The value of the tree root.
     * @return Returning the root value of the tree.
     */
    private int insertKey(int key, int curr_root) {
        counter += 2;                                                                   // Increasing the number of comparisons by 2 due to the assignments.
        int ptr = curr_root;                                                            // Creating a variable to store the current root of our sub-tree.
        int parent = -1;                                                                // Creating a variable to represent the parent of our sub-tree.

        counter ++;                                                                     // Increasing the number of comparisons by 1.
        while (ptr != -1){                                                              // Looping while the value of ptr is not -1.

            counter ++;                                                                 // Increasing the number of comparisons by 1 due to the assignment.
            parent = ptr;                                                               // Setting the value of parent equal to ptr.

            counter ++;                                                                 // Increasing the number of comparisons by 1.
            if (key < treeMatrix[ptr][info_column]){                                    // Checking whether the value of the key is smaller than the value of the current root.
                counter ++;                                                             // If true we increase the number of comparisons.
                if (treeMatrix[ptr][left_thread] == 0) {                                // Checking if the sub-tree doesn't have a left thread.
                    ptr = treeMatrix[ptr][left_column];                                 // If true we set the value of ptr equal to the left sub-tree.
                    counter ++;                                                         // Increasing the number of comparisons by 1 due to the assignment.
                }else break;                                                            // Else we break.
            }
            else{                                                                       // Else, the key is bigger than the root value of the current sub-tree.
                counter ++;                                                             // We increase the number of comparisons by 1.
                if (treeMatrix[ptr][right_thread] == 0){                                // Checking if the sub-tree doesn't have a right thread.
                    ptr = treeMatrix[ptr][right_column];                                // If true we set the value of ptr equal to the right sub-tree.
                    counter ++;                                                         // Increasing the number of comparisons by 1 due to the assignment.
                }else break;                                                            // Else we break.
            }
        }

        counter += 5;                                                                   // Increasing the number of comparisons by 5 due to the assignments.
        int index = AVAIL;                                                              // Storing the value of the next available row in the matrix.
        AVAIL = treeMatrix[index][right_column];                                        // Updating the value of AVAIL.
        treeMatrix[index][info_column] = key;                                           // Storing the key to the matrix in the corresponding value.
        treeMatrix[index][left_thread] = treeMatrix[index][right_thread] = 1;           // Setting the left and right thread True.

        counter ++;                                                                     // Increasing the number of comparisons.
        if (parent == -1){                                                              // Checking whether the parent variable is equal to -1.
            counter += 3;                                                               // Increasing the number of comparisons by 3 due to the assignment.
            curr_root = index;                                                          // Setting the value of current root equal to the index variable.
            treeMatrix[index][left_column] = treeMatrix[index][right_column] = -1;      // Setting the value of the left and the right sub-tree indexes to -1.
        }
        else if (key < treeMatrix[parent][info_column]){                                // Else checking whether the value of the key is smaller than the current sub-trees root value.
            treeMatrix[index][left_column] = treeMatrix[parent][left_column];           // setting the left thread.
            treeMatrix[index][right_column] = parent;                                   // Setting the right thread.
            treeMatrix[parent][left_column] = index;                                    // Setting the left sub-tree of parent.
            treeMatrix[parent][left_thread] = 0;                                        // Setting parents thread value to False.
            counter += 5;                                                               // Increasing the number of comparisons by 4 due to the assignments and by 1 due to the comparison.
        }
        else {                                                                          // Else the key is bigger than the value of the root.
            treeMatrix[index][right_column] = treeMatrix[parent][right_column];         // Setting the right thread.
            treeMatrix[index][left_column] = parent;                                    // setting the left thread.
            treeMatrix[parent][right_column] = index;                                   // Setting the left sub-tree of parent.
            treeMatrix[parent][right_thread] = 0;                                       // Setting parents thread value to False.
            counter += 4;                                                               // Increasing the number of comparisons by 4 due to the assignments.
        }
        return curr_root;                                                               // Returning the current root  value.
    }

    /**
     * Method we call in order to search a random key in the tree.
     * @param usedKeys_search List that contains the keys we are going to search in the tree.
     */
    public void searchTree_key(List<Integer> usedKeys_search){
        counter = 0;                                                                    // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++)                                                // Looping for as many runs as we have.
            searchKey(usedKeys_search.get(i),root);                                            // Searching for a specific key in the tree.
        counters.add(counter/numOfRuns);                                                // Adding the number of comparisons needed to create the tree.
    }

    /**
     * Method for searching a key in the tree recursively.
     * @param key The key we are looking for.
     * @param curr_root The root of the sub-tree we are checking.
     * @return Returning the curr_root value.
     */
    private int searchKey(int key,int curr_root){

        counter++;                                                                      // Increasing the number of comparisons by 1.
        while (curr_root != -1){                                                        // Looping until the curr_root value is equal to -1.

            counter++;                                                                  // Increasing the number of comparisons by 1.
            if (key == treeMatrix[curr_root][info_column])                              // Checking whether the key is equal to the currents sub-tree root.
                break;                                                                  // If true we would print the value (if we wanted to) and then break.

            counter++;                                                                  // Increasing the number of comparisons by 1.
            if (key < treeMatrix[curr_root][info_column]) {                             // Checking whether the key is smaller than the root of the current sub-tree.
                counter ++;                                                             // Increasing the number of comparisons by 1.
                if (treeMatrix[curr_root][left_thread] == 0) {                          // Checking whether the current root has a left thread.
                    curr_root = treeMatrix[curr_root][left_column];                     // If it doesn't we set the value of curr_root equal to the left sub-tree.
                    counter++;                                                          // Increasing the number of comparisons by 1.
                }else break;                                                            // Else we break.
            }
            else{                                                                       // Else, the key is bigger than the root value of the current sub-tree.
                counter++;                                                              // Increasing the number of comparisons by 1.
                if (treeMatrix[curr_root][right_thread] == 0) {                         // Checking whether the current root has a right thread.
                    curr_root = treeMatrix[curr_root][right_column];                    // If it doesn't we set the value of curr_root equal to the right sub-tree.
                    counter++;                                                          // Increasing the number of comparisons by 1.
                }else break;                                                            // Else we break.
            }
            counter ++;                                                                 // Increasing the number of comparisons by 1 due to the assignment.
        }
        return curr_root;                                                               // Returning the curr_root value.
    }

    /**
     * Method we call to search a range of keys in the Threaded Binary Search Tree.
     * @param usedKeys_range List that contains the keys we are going to search in the tree.
     * @param upper The value of upperbound we add to the key.
     */
    public void searchTree_range(List<Integer> usedKeys_range, int upper){
        counter = 0;                                                                    // Setting the number of comparisons equal to 0.
        for(int i=0; i < numOfRuns; i++)                                                // Looping for as many runs as we have.
            searchRange(root, usedKeys_range.get(i), (usedKeys_range.get(i) + upper));  // Searching for a specific key range in the tree.
        counters.add(counter/numOfRuns);                                                // Storing the number of comparisons needed to create the tree.
        uppers.add(upper);                                                              // Storing the upper value we used.
    }

    /**
     * Method for searching a range of keys in the Threaded Binary Search Tree.
     * @param curr_root  The root of the sub-tree we are currently checking.
     * @param k1 The lower bound of the range.
     * @param k2 The upper bound of the range.
     */
    private void searchRange(int curr_root, int k1, int k2) {
        counter ++;                                                                     // Increasing the number of comparisons by 1 due to the assignment.
        int index = searchKey(k1,curr_root);                                            // Creating a new variable that has the index value the K1 key exists in the tree
                                                                                        // we use the method searchKey, because we want to find the index of the K1 (our lower bound).

        counter ++;                                                                     // Increasing the number of comparisons by 1 due to the assignment.
        if (treeMatrix[index][info_column] < k1) {                                      // Checking whether the k1 is bigger than the current root value.
            index = treeMatrix[index][right_column];                                    // Setting the index value equal to the right column.
            counter ++;                                                                 // Increasing the number of comparisons by 1 due to the assignment.
        }

        counter ++;                                                                     // Increasing the number of comparisons by 1 due to the assignment.
        while(index != -1){                                                             // Looping until the curr_root value is equal to -1.

            counter ++;                                                                 // Increasing the number of comparisons by 1 due to the assignment.
            if(treeMatrix[index][info_column] > k2)                                     // Checking whether the k2 is bigger than the current root value.
                break;                                                                  // If true we break.

            counter ++;                                                                 // Increasing the number of comparisons by 1 due to the assignment.
            if (treeMatrix[index][right_thread] == 1){                                  // Checking if the index has a right thread.
                index = treeMatrix[index][right_column];                                // If true we set the index value equal to the right column which means the next value from the one we are checking.
                counter ++;                                                             // Increasing the number of comparisons by 1 due to the assignment.
            }
            else{                                                                       // Else the index doesn't have a right thread.
                counter += 2;                                                           // Increasing the number of comparisons by 1 due to the assignment and by 1 due to the comparison in the while.
                index = treeMatrix[index][right_column];                                // Setting the value of the index to the right column.
                while (treeMatrix[index][left_thread] == 0) {                           // Looping while the left thread is zero.
                    index = treeMatrix[index][left_column];                             // Setting the value of the index to the right column.
                    counter += 2;                                                       // Increasing the number of comparisons by 1 due to the assignment and by 1 due to the comparison in the while.
                }
            }
            counter ++;                                                                 // Increasing the number of comparisons by 1
        }
    }

    /**
     * Method for printing the information we gathered from the measurements.
     */
    public void printInfo(){
        System.out.println("--------------------------------------------------- Threaded Binary Search Tree ---------------------------------------------------");
        System.out.println("Number of comparisons to create a threaded binary tree (in average) : " + counters.get(0));
        System.out.println("Number of comparisons to search the threaded binary tree (in average) : " + counters.get(1));
        System.out.println("Number of comparisons to search the threaded binary tree in range(k, k + " + uppers.get(0) + ") (in average) : " + counters.get(2));
        System.out.println("Number of comparisons to search the threaded binary tree in range(k, k + "  +uppers.get(1) + ") (in average) : " + counters.get(3));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();
    }
}
