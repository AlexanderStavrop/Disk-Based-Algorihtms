package Classes;

/**
 * BTreeNode class for inner nodes.
 */
class BTreeInnerNode extends BTreeNode {
    /**
     * FileHandler instance that handles the files.
     */
    private final FileHandler fileHandler;

//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the BTreeInnerNode.
     * @param fileHandler FileHandler instance that accesses the file.
     * @param page_size Size in bytes of every innerNode.
     * @param page Page number of the node.
     */
    public BTreeInnerNode(int page_size, int page, FileHandler fileHandler) {
        super(page_size, page, fileHandler);                                                                            // Passing the arguments to the super class.
        this.Values = new Integer[INNER_ORDER + 1];                                                                     // Initializing the Values array.
        this.keys = new Integer[INNER_ORDER];                                                                           // Initializing the keys array.
        this.fileHandler = fileHandler;                                                                                 // Setting the fileHandler.
        this.nodeType = 0;                                                                                              // Setting the node type equal to 0.
    }


//_____________________________________________________ Getters ________________________________________________________
    /**
     * Getter for getting the node type of the innerNode node.
     * @return Returning 0 as it is an inner node.
     */
    public int getNodeType() {
        return 0;                                                                                                       // Returning 0.
    }


//_____________________________________________________ Getters ________________________________________________________
    /**
     * Method for setting a child to the current node.
     * @param index The index of the child in the corresponding node.
     * @param value The child's page.
     */
    public void setChild (int index, Integer value) {
        this.Values[index] = value;                                                                                     //Setting the child index in the array.
        if (value != null) {                                                                                            // Checking whether the node is null.
            BTreeNode child = retrieveNode(value);                                                                      // If true retrieve the child node.
            child.setParent(this.getPage());                                                                            // We set the child's parent to be this node.
            saveNode(child);                                                                                            // Adding the child node to the hashMap
        }
    }


//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for inserting a key in the node.
     * @param index The index where the key will be inserted.
     * @param key The key to be inserted at the index.
     * @param leftChild The left child index.
     * @param rightChild The right child index.
     */
    private void insertAt(int index, int key, int leftChild, int rightChild) {
        for (int i = this.getKeyCount() + 1; i > index; --i)                                                            // Moving the child values from end to the index value to create space.
            this.setChild(i, this.getValues(i - 1));                                                                    // Setting the child value accordingly.

        for (int i = this.getKeyCount(); i > index; --i)                                                                // Moving the keys from end to the index value to create space.
            this.setKey(i, this.getKey(i - 1));                                                                         // Setting the child value accordingly.

        this.setKey(index, key);                                                                                        // Setting the key value at the correct location in the array.
        this.setChild(index, leftChild);                                                                                // Setting the leftChild value at the correct location in the array.
        this.setChild(index + 1, rightChild);                                                                           // Setting the rightChild value at the correct location in the array.
        this.keyCount += 1;                                                                                             // Increasing the number of key by 1.
        saveNode(this);                                                                                                 // Adding the current node to hashTable.
    }

    /**
     * Method for searching for a key in the node.
     * @param key The key we are searching for.
     * @return The index of the key in the node if it exists.
     */
    public int search(int key) {
        int index;                                                                                                      // Creating a index variable.
        for (index = 0; index < this.getKeyCount(); ++index) {                                                          // Looping for as many keys as we have.
            int cmp = this.getKey(index).compareTo(key);                                                                // Comparing the current key we got from the file with the key we are looking for.

            if (cmp == 0)                                                                                               // Checking whether the result of the comparison is 0.
                return index + 1;                                                                                       // If true we return the next index value.
            else if (cmp > 0)                                                                                           // Else checking whether the result of the comparison bigger than 0.
                return index;                                                                                           // If true we return index value.
        }
        return index;                                                                                                   // Returning the index value.
    }

    /**
     * Method for deleting a key from the node.
     * @param index index of the node to be deleted
     */
    private void deleteAt(int index) {
        int i;                                                                                                          // Creating a index variable.
        for (i = index; i < this.getKeyCount() - 1; ++i) {                                                              // Moving the keys and the child values from index value to the end to create space.
            this.setKey(i, this.getKey(i + 1));                                                                         // Setting the key value accordingly.
            this.setChild(i + 1, this.getValues(i + 2));                                                                // Setting the child value accordingly.
        }
        this.setKey(i, null);                                                                                           // Setting the key value at the correct location in the array.
        this.setChild(i + 1, null);                                                                                     // Setting the leftChild value at the correct location in the array.
        --this.keyCount;                                                                                                // Decreasing the number of key by 1.
        saveNode(this);                                                                                                 // Adding the current node to hashMap.
    }

    /**
     * Method for splitting a node if we have overflow.
     * @return The new node.
     */
    protected BTreeNode split() {
        int midIndex = this.getKeyCount() / 2;                                                                          // Creating the midIndex value.

        BTreeInnerNode newRNode = new BTreeInnerNode(page_size,getNextPage(),fileHandler);                              // Creating a new BTreeInnerNode node in order to insert half of the keys.

        for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {                                                       // Setting the keys from midIndex and up.
            newRNode.setKey(i - midIndex - 1, this.getKey(i));                                                          // Setting the key value in the new node accordingly.
            this.setKey(i, -1);                                                                                         // Setting the key value in the current node equal to -1.
        }

        for (int i = midIndex + 1; i <= this.getKeyCount(); ++i) {                                                      // Setting the children from midIndex and up.
            newRNode.setChild(i - midIndex - 1, this.getValues(i));                                                     // Setting the  children value in the new node accordingly.

            BTreeNode retrieved_child = retrieveNode(newRNode.getValues(i - midIndex - 1));                             // Retrieving the child node from the file.
            retrieved_child.setParent(newRNode.getPage());                                                              // Setting the child parent accordingly.

            this.setChild(i, null);                                                                                     // Setting the child value in the current node equal to null.
            saveNode(retrieved_child);                                                                                  // Adding the retrieved child node to hashMap.
        }

        this.setKey(midIndex, null);                                                                                    // Setting the key value in the current node.
        newRNode.keyCount = this.getKeyCount() - midIndex - 1;                                                          // Setting the number of keys in the new node.
        this.keyCount = midIndex;                                                                                       // Setting the number of keys in the current node.

        saveNode(newRNode);                                                                                             // Adding the new node to hashMap.
        saveNode(this);                                                                                                 // Adding the current node to hashMap.

        return newRNode;                                                                                                // Returning the new node.
    }

    /**
     * Method for pushing up a key in the tree.
     * @param key The key to be pushed up.
     * @param leftChild The leftChild page index.
     * @param rightNode The rightChild page index.
     * @return The BTreeNode that now contains the key.
     */
    protected BTreeNode pushUpKey(int key, int leftChild, int rightNode) {
        int index = this.search(key);                                                                                   // Getting the index where the key will be inserted in the node.
        this.insertAt(index, key, leftChild, rightNode);                                                                // Inserting the key at the correct node.

        if (this.isOverflow())                                                                                          // Checking whether the node has overflow.
            return this.dealOverflow();                                                                                 // If true we return the returning argument of dealOverflow function.
        else return this.getParent() == -1 ? this : null;                                                               // Else returning the node if there is no parent or null if there is.
    }


    //______________________________________________ Child Methods _____________________________________________________
    /**
     * Method for transferring a child.
     * @param borrower The borrower node.
     * @param lender The lender node.
     * @param borrowIndex The borrow index value.
     */
    protected void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex) {
        int borrowerChildIndex = 0;                                                                                     // Creating the borrowerChildIndex variable.

        while (borrowerChildIndex < this.getKeyCount() + 1 && retrieveNode(this.getValues(borrowerChildIndex)) != borrower)
            ++borrowerChildIndex;                                                                                       // Increasing the index by 1 accordingly.

        int upKey;                                                                                                      // Creating the upKey variable.
        if (borrowIndex == 0) {                                                                                         // Checking whether the borrowIndex is equal to 0
            upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex), lender, borrowIndex);                 // If true we jump to transferFromSibling function.
            this.setKey(borrowerChildIndex, upKey);                                                                     // Setting the key in the current node accordingly.
        }else {                                                                                                         // Else
            upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex - 1), lender, borrowIndex);             // We jump to transferFromSibling function.
            this.setKey(borrowerChildIndex - 1, upKey);                                                                 // Setting the key in the current node accordingly.
        }
        saveNode(this);                                                                                                 // Adding the current node to hashMap.
    }

    /**
     * Method for child fusion.
     * @param leftChild The leftChild node
     * @param rightChild The rightChild node
     * @return return corresponding value according to the case.
     */
    protected BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild) {
        int index = 0;                                                                                                  // Creating the index variable.

        while (index < this.getKeyCount() && this.getValues(index) != leftChild.getPage())                              // Looping for as many time as we need.
            ++index;                                                                                                    // Increasing the index by 1 accordingly.

        int sinkKey = this.getKey(index);                                                                               // Creating the index variable.
        leftChild.fusionWithSibling(sinkKey, rightChild);                                                               // We jump to fusionWithSibling function.

        this.deleteAt(index);                                                                                           // Deleting the items at index value.

        if (this.isUnderflow()) {                                                                                       // Checking whether the node has underflow.
            if (this.getParent() == -1) {                                                                               // If true we check whether the parent value is equal to -1.
                if (this.getKeyCount() == 0) {                                                                          // If true we check whether the number of keys is equal to 0.
                    leftChild.setParent(-1);                                                                            // If true we set the parent value equal to -1.
                    saveNode(leftChild);                                                                                // Adding the leftChild node to hashMap.
                    return leftChild;                                                                                   // Returning the leftChild node.
                }
                else return null;                                                                                       // Else we return null.
            }
            saveNode(this);                                                                                             // Adding the current node to hashMap.
            return this.dealUnderflow();                                                                                // Return the returning argument of dealUnderflow function.
        }
        saveNode(this);                                                                                                 // Adding the current node to hashMap.
        return null;                                                                                                    // Return null.
    }


    //_____________________________________________ Sibling Methods ____________________________________________________
    /**
     *Method for transferring a sibling.
     * @param sinkKey The sinkKey value.
     * @param sibling The sibling node.
     * @param borrowIndex The borrow index value.
     * @return Return corresponding value according to the case.
     */
    protected int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex) {
        BTreeInnerNode siblingNode = (BTreeInnerNode)sibling;                                                           // Creating temporary BTreeInnerNode instance that holds the sibling node.
        int upKey;                                                                                                      // Creating the index variable.
        if (borrowIndex == 0) {                                                                                         // Checking whether the borrowIndex is equal to 0
            int index = this.getKeyCount();                                                                             // If true getting the number of keys.
            this.setKey(index, sinkKey);                                                                                // Setting the key value in the current node accordingly.
            this.setChild(index + 1, siblingNode.getValues(borrowIndex));                                               // Setting the child value in the current node accordingly.
            this.keyCount += 1;                                                                                         // Increasing the number of keys in the node.
            upKey = siblingNode.getKey(0);                                                                              // Updating the value of the upKey.
        } else {
            this.insertAt(0, sinkKey, siblingNode.getValues(borrowIndex + 1), this.getValues(0));                       // Else inserting the key at the corresponding place.
            upKey = siblingNode.getKey(borrowIndex);                                                                    // Updating the value of the upKey.
        }
        siblingNode.deleteAt(borrowIndex);                                                                              // Deleting the key from the siblingNode.
        saveNode(this);                                                                                                 // Adding the current node to hashMap.
        return upKey;                                                                                                   // Returning the upKey value.
    }

    /**
     * Method for sibling fusion.
     * @param sinkKey The sinkKey value.
     * @param rightSibling The rightSibling node.
     */
    protected void fusionWithSibling(int sinkKey, BTreeNode rightSibling) {
        BTreeInnerNode rightSiblingNode = (BTreeInnerNode)rightSibling;                                                 // Creating temporary BTreeInnerNode instance that holds the sibling node.

        int j = this.getKeyCount();                                                                                     // Initializing the j value.
        this.setKey(j++, sinkKey);                                                                                      // Setting the key accordingly.

        for (int i = 0; i < rightSiblingNode.getKeyCount(); ++i)                                                        // Looping for as many keys as we got.
            this.setKey(j + i, rightSiblingNode.getKey(i));                                                             // Setting the key value accordingly.

        for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; ++i)                                                    // Looping for as many keys as we got.
            this.setChild(j + i, rightSiblingNode.getValues(i));                                                        // Setting the child value accordingly.

        this.keyCount += 1 + rightSiblingNode.getKeyCount();                                                            // Increasing the currents node keyCount.
        this.setRightSibling(rightSiblingNode.getRightSibling());                                                       // Setting the rightSibling value of the current node accordingly.

        if (rightSiblingNode.getRightSibling() != -1){                                                                  // Checking whether the rightSibling has a right sibling.
            BTreeNode retrieved_sibling = retrieveNode(rightSiblingNode.getRightSibling());                             // If true we retrieve the right sibling node of the right sibling.
            retrieved_sibling.setLeftSibling(this.getPage());                                                           // We set the left sibling value accordingly.
            saveNode(retrieved_sibling);                                                                                // Adding the retrieved node to hashMap.
        }
        saveNode(this);                                                                                                 // Adding the current node to hashMap.
    }


}