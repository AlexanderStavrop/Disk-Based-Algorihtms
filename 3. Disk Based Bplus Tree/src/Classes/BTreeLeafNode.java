package Classes;

/**
 * BTreeNode class for leaf nodes.
 */
class BTreeLeafNode extends BTreeNode {
    /**
     * FileHandler instance that handles the files.
     */
    private final FileHandler fileHandler;


//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the BTreeLeafNode
     * @param fileHandler FileHandler instance that accesses the file.
     * @param page_size Size in bytes of every innerNode.
     * @param page Page number of the node.
     */
    public BTreeLeafNode(int page_size, int page, FileHandler fileHandler) {
        super(page_size, page, fileHandler);                                                                            // Passing the arguments to the super class.
        this.Values = new Integer[LEAF_ORDER + 1];                                                                      // Initializing the size of the values array.
        this.keys = new Integer[LEAF_ORDER + 1];                                                                        // Initializing the size of the keys array.
        this.fileHandler = fileHandler;                                                                                 // Initializing the fileHandler instance.
        this.nodeType = 1;                                                                                              // Setting the node type equal to 1.
    }


//_____________________________________________________ Getters ________________________________________________________
    /**
     * Getter for getting the node type of the leafNode node.
     * @return Returning 1 as it is an leaf node.
     */
    public int getNodeType() {
        return 1;                                                                                                       // Returning 1.
    }


//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for inserting a key in the node.
     * @param key The key to be inserted at the index.
     * @param value The value corresponding to the key.
     */
    public void insertKey(Integer key, Integer value) {
        int index = 0;                                                                                                  // Creating a index variable.

        while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)                                     // Looping for as long as we need.
            ++index;                                                                                                    // Increasing the value of the index by 1.

        this.insertAt(index, key, value);                                                                               // Jumping to insertAt function.
    }

    /**
     * Method for inserting a key in the node.
     * @param index The index where the key will be inserted.
     * @param key The key to be inserted at the index.
     * @param value The value to be inserted.
     */
    private void insertAt(int index, int key, Integer value) {
        for (int i = this.getKeyCount() - 1; i >= index; --i) {                                                         // Moving space for the key.
            this.setKey(i + 1, this.getKey(i));                                                                         // Moving the key value accordingly.
            this.setValues(i + 1, this.getValues(i));                                                                   // Moving the Values value accordingly.
        }
        this.setKey(index, key);                                                                                        // Setting the key value accordingly.
        this.setValues(index, value);                                                                                   // Moving the Values value accordingly.
        ++this.keyCount;                                                                                                // Increasing the number of keys by 1.
        saveNode(this);                                                                                                 // Adding the current node to hashTable.
    }

    /**
     * Method for searching for a key in the node.
     * @param key The key we are searching for.
     * @return The index of the key in the node if it exists.
     */
    public int search(int key) {
        for (int i = 0; i < this.getKeyCount(); ++i) {                                                                  // Looping for as many keys as we got.
            int cmp = this.getKey(i).compareTo(key);                                                                    // Comparing the current key to the one we are looking for.
            if (cmp == 0)                                                                                               // Checking whether the cmp value is equal to 0.
                return i;                                                                                               // If true we return i.
            else if (cmp > 0)                                                                                           // Else checking whether the cmp value is bigger than 0.
                return -1;                                                                                              // If true we return -1.
        }
        return -1;                                                                                                      // Returning -1.
    }

    /**
     * Method for deleting a key from the node.
     * @param key The key want to delete.
     * @return Returning true if the deletion was successful or false if it wasn't.
     */
    public boolean delete(int key) {
        int index = this.search(key);                                                                                   // Getting the correct index value.
        if (index == -1)                                                                                                // Checking whether the index is equal to -1.
            return false;                                                                                               // If true we return false.
        this.deleteAt(index);                                                                                           // Deleting the items at index value.
        return true;                                                                                                    // Returning true if the deletion was successful.
    }

    /**
     * Method for deleting a key from the node.
     * @param index the index in which the key is to be removed
     */
    private void deleteAt(int index) {
        int i;                                                                                                          // Creating an index variable.
        for (i = index; i < this.getKeyCount() - 1; ++i) {                                                              // Moving the keys.
            this.setKey(i, this.getKey(i + 1));                                                                         // Setting the keys accordingly.
            this.setValues(i, this.getValues(i + 1));                                                                   // Setting the value accordingly.
        }
        this.setKey(i, -1);                                                                                             // Setting the keys accordingly.
        this.setValues(i, -1);                                                                                          // Setting the keys accordingly.
        --this.keyCount;                                                                                                // Decreasing the number of keys in node by 1.
        saveNode(this);                                                                                                 // Adding the current node to hashTable.
    }

    /**
     * Method for splitting a node if we have overflow.
     * @return The new node.
     */
    protected BTreeNode split() {
        int midIndex = this.getKeyCount() / 2;                                                                          // Creating the midIndex value.

        BTreeLeafNode newRNode = new BTreeLeafNode(page_size, getNextPage(), fileHandler);                              // Creating a new BTreeLeafNode node in order to insert half of the keys.

        for (int i = midIndex; i < this.getKeyCount(); ++i) {                                                           // Setting the keys and the values from midIndex and up.
            newRNode.setKey(i - midIndex, this.getKey(i));                                                              // Setting the keys of the new node accordingly.
            newRNode.setValues(i - midIndex, this.getValues(i));                                                        // Setting the values of the new node accordingly.
            this.setKey(i, null);                                                                                       // Setting the keys of the current node accordingly.
            this.setValues(i, null);                                                                                    // Setting the values of the current node accordingly.
        }
        newRNode.keyCount = this.getKeyCount() - midIndex;                                                              // Setting the keyCount value of the new node.
        this.keyCount = midIndex;                                                                                       // Setting the keyCount value fo the current node.

        saveNode(this);                                                                                                 // Adding the current node to hashTable.
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
        throw new UnsupportedOperationException();                                                                      // This method is not supported.
    }


    //______________________________________________ Child Methods _____________________________________________________
    /**
     * Unsupported in leaf nodes
     * @param borrower borrower
     * @param lender lender
     * @param borrowIndex borrow index
     */
    protected void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex) {
        throw new UnsupportedOperationException();                                                                      // This method is not supported.
    }

    /**
     * Unsupported in leaf nodes
     * @param leftChild left child
     * @param rightChild right child
     * @return UnsupportedOperationException
     */
    protected BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild) {
        throw new UnsupportedOperationException();                                                                      // This method is not supported.
    }


    //_____________________________________________ Sibling Methods ____________________________________________________
    /**
     * Method for transferring from sibling.
     * @param sinkKey The sinkKey value.
     * @param sibling The sibling node.
     * @param borrowIndex The borrowIndex value.
     * @return The index according to the case.
     */
    protected int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex) {
        BTreeLeafNode siblingNode = (BTreeLeafNode)sibling;                                                             // Storing the sibling node.

        this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValues(borrowIndex));                            // Inserting a key to the current node.
        siblingNode.deleteAt(borrowIndex);                                                                              // Deleting a key from the sibling node.

        saveNode(this);                                                                                 // Adding the current node to hashTable.
        return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);                                                   // Returning the correct index.
    }

    /**
     * Method for child fusion.
     * @param sinkKey The sinkKey value.
     * @param rightSibling The rightSibling node.
     */
    public void fusionWithSibling(int sinkKey, BTreeNode rightSibling) {
        BTreeLeafNode siblingLeaf = (BTreeLeafNode) rightSibling;                                                       // Storing the sibling node.

        int j = this.getKeyCount();                                                                                     // Setting the j value.
        for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {                                                           // Looping for as many keys as we got.
            this.setKey(j + i, siblingLeaf.getKey(i));                                                                  // Setting the key value accordingly.
            this.setValues(j + i, siblingLeaf.getValues(i));                                                            // Setting the Values value accordingly.
        }

        this.keyCount += siblingLeaf.getKeyCount();                                                                     // Setting the keyCount value accordingly.
        this.setRightSibling(siblingLeaf.rightSibling);                                                                 // Setting the rightSibling value accordingly.

        if (siblingLeaf.rightSibling != -1) {                                                                           // Checking whether the siblings rightSibling is not -1.
            BTreeNode retrieved_sibling = retrieveNode(siblingLeaf.getRightSibling());                                  // If true we retrieve the sibling.
            retrieved_sibling.setLeftSibling(this.getPage());                                                           // Setting the retrieved sibling leftSibling value.
            saveNode(retrieved_sibling);                                                                                // Adding the retrieved sibling node to hashTable.
        }
        saveNode(this);                                                                                                 // Adding the current node to hashTable.
    }


}
