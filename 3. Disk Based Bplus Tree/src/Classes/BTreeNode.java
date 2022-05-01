package Classes;

/**
 * BTreeNode abstract class for every type of node in a B+Tree
 */
abstract class BTreeNode{
    /**
     * FileHandler associated with the BTreeNode.
     */
    private final FileHandler fileHandler;
    /**
     *  Number of keys and values an inner node can contain.
     */
    protected static int INNER_ORDER = 29;
    /**
     *  Number of keys and values an leaf node can contain.
     */
    protected static int LEAF_ORDER = 29;
    /**
     *  Array that contains the values the node contains.
     */
    protected Integer[] Values;
    /**
     *  The right sibling index of this node.
     */
    protected int rightSibling;
    /**
     *  The left sibling index of this node.
     */
    protected int leftSibling;
    /**
     *  Array that contains the keys the node contains.
     */
    protected Integer[] keys;
    /**
     *  The parent index of this node.
     */
    protected int parentNode;
    /**
     *  The page size of the node.
     */
    protected int page_size;
    /**
     *  The index of the current node.
     */
    private final int page;
    /**
     *  The value that holds the type of the node.
     */
    protected int nodeType;
    /**
     *  The number of keys contained in the node.
     */
    protected int keyCount;


//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the BTreeNode.
     * @param fileHandler FileHandler instance that accesses the file.
     * @param page_size Size in bytes of every innerNode.
     * @param page Page number of the node.
     */
    protected BTreeNode(int page_size, int page, FileHandler fileHandler) {
        this.fileHandler = fileHandler;                                                                                 // Setting up the fileHandler instance.
        this.page_size = page_size;                                                                                     // Setting the page_size value.
        this.rightSibling = -1;                                                                                         // Initializing the rightSibling value to -1.
        this.leftSibling = -1;                                                                                          // Initializing the leftSibling value to -1.
        this.parentNode = -1;                                                                                           // Initializing the parent value to -1.
        this.keyCount = 0;                                                                                              // Initializing the keyCount value to 0.
        this.page = page;                                                                                               // Setting the page value accordingly.
    }


//_____________________________________________________ Getters ________________________________________________________
    /**
     * Getter for the value at index in the nodes array.
     * @param index The index we use in the array.
     * @return The item from the array.
     */
    public Integer getValues(int index) {
        return this.Values[index];                                                                                      // Returning the specific value.
    }

    /**
     * Getter for the page index of the rightSibling.
     * @return The item from the array.
     */
    public Integer getRightSibling() {
        return this.rightSibling;                                                                                       // Returning the index.
    }

    /**
     * Getter for the page index of the leftSibling.
     * @return The item from the array.
     */
    public Integer getLeftSibling() {
        return this.leftSibling;                                                                                        // Returning the index.
    }

    /**
     * Getter for the key at a specific index in the node
     * @param index The index we use in the array.
     * @return The item from the array if it exists else -1 if it doesn't.
     */
    public Integer getKey(int index) {
        return this.keys[index];                                                                                    // If true we return it.
    }

    /**
     * Getter for the page index of the parentNode.
     * @return The item from the array.
     */
    public Integer getParent() {
        return this.parentNode;                                                                                         // Returning the index.
    }

    /**
     * Getter for the number of keys in the node.
     * @return The number of keys.
     */
    public int getKeyCount() {
        return this.keyCount;                                                                                           // Returning the index.
    }

    /**
     * Getter for the next available page to create a node.
     * @return The next available page.
     */
    public int getNextPage(){
        return fileHandler.getNextPage();                                                                               // Returning the next available page.
    }

    /**
     * Getter for the number of keys in the node.
     * @return The page index.
     */
    public int getPage() {
        return page;                                                                                                    // Returning the page of the node.
    }


//_____________________________________________________ Setters ________________________________________________________
    /**
     * Setter for a value in the array.
     * @param index The index we use in the array.
     * @param value The value we are setting in the array.
     */
    public void setValues(int index, Integer value) {
        this.Values[index] = value;                                                                                     // Setting the value in the array.
    }

    /**
     * Setter for the right sibling value of the node.
     * @param sibling The page index of the right sibling.
     */
    public void setRightSibling(Integer sibling) {
        this.rightSibling = sibling;                                                                                    // Setting the rightSibling value.
    }

    /**
     * Setter for the left sibling value of the node.
     * @param sibling The page index of the right sibling.
     */
    public void setLeftSibling(Integer sibling) {
        this.leftSibling = sibling;                                                                                     // Setting the leftSibling value.
    }

    /**
     * Setter for a key in the array.
     * @param index The index we use in the array.
     * @param key The value of the key.
     */
    public void setKey(int index, Integer key) {
        this.keys[index] = key;                                                                                         // Setting the key in the array.
    }

    /**
     * Setter for the number of keys in the node.
     * @param keyCount The number of keys in the node.
     */
    public void setKeyCount(int keyCount) {
        this.keyCount = keyCount;                                                                                       // Setting the keyCount value.
    }

    /**
     * Setter for the parent value of the node.
     * @param parent The page index of the parent node.
     */
    public void setParent(int parent) {
        this.parentNode = parent;                                                                                       // Setting the parent node value.
    }


//_________________________________________________ Abstract Methods ___________________________________________________
    /**
     * Method for children transfer.
     * @param borrower The borrower node.
     * @param lender The lender node.
     * @param borrowIndex The borrowIndex index.
     */
    protected abstract void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex);

    /**
     * Method for children fusion.
     * @param leftChild The leftChild node.
     * @param rightChild The rightChild node.
     * @return The processed node.
     */
    protected abstract BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild);

    /**
     * Method for sibling transfer.
     * @param sinkKey The sinkKey value.
     * @param sibling The sibling node.
     * @param borrowIndex The borrowIndex value.
     * @return The processed node.
     */
    protected abstract int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex);

    /**
     * Method for sibling fusion.
     * @param sinkKey The sinkKey value.
     * @param rightSibling The rightSibling node.
     */
    protected abstract void fusionWithSibling(int sinkKey, BTreeNode rightSibling);

    /**
     * Method for pushing a key to upper nodes.
     * @param key The key we want to push up.
     * @param leftChild The page index of the leftChild.
     * @param rightNode The page index of the rightChild.
     * @return The processed node.
     */
    protected abstract BTreeNode pushUpKey(int key, int leftChild, int rightNode);

    /**
     * Method for splitting a node if we have overflow.
     * @return The new node.
     */
    protected abstract BTreeNode split();

    /**
     * Method for searching for a key in the node.
     * @param key The key we are searching for.
     * @return The index of the key in the node if it exists.
     */
    public abstract int search(int key);

    /**
     * Getter for getting the node type of the leafNode node.
     * @return Returning 1 as it is an leaf node.
     */
    public abstract int getNodeType();


//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for checking if the node can lend a key.
     * @return Returning true if the node can and false if it can't.
     */
    public boolean canLendAKey() {
        return this.getKeyCount() > (this.keys.length / 2);                                                             // Returning the boolean value.
    }

    /**
     * Method for retrieving a node from the index file.
     * @param page The page of the target node.
     * @return Returning the BtreeNode instance we retrieved.
     */
    protected BTreeNode retrieveNode(int page){
        return fileHandler.getNode(page);                                                                               // Retrieving and returning the node at given page.
    }

    /**
     * Method for saving a node to the hashTable.
     * @param node The node we want to save.
     */
    protected void saveNode(BTreeNode node){
        fileHandler.removeAndPut(node);                                                                                 // Adding the node to the hashTable.
    }


    //_____________________________________________ Overflow Methods ___________________________________________________
    /**
     * Method that checks if the node has overflow.
     * @return Returning true if the node is indeed overflowed.
     * */
    public boolean isOverflow() {
        return this.getKeyCount() == this.keys.length;                                                                  // Returning the boolean value.
    }

    /**
     * Method for dealing with the overflow in node.
     * @return Returning the return argument of pushUpKey.
     */
    public BTreeNode dealOverflow() {
        int midIndex = this.getKeyCount() / 2;                                                                          // Creating the midIndex variable.
        int upKey = this.getKey(midIndex);                                                                              // Creating the upKey variable.

        BTreeNode newRNode = this.split();                                                                              // Splitting the node.

        if (this.getParent() == -1) {                                                                                   // Checking whether the node doesn't have a parent.
            BTreeNode inner = new BTreeInnerNode(page_size,getNextPage(),fileHandler);                                  // If true we create a new innerNode.
            this.setParent(inner.getPage());                                                                            // Setting the parent value of the current node.
            saveNode(inner);                                                                                            // Adding the inner node to the hashTable.
        }

        newRNode.setLeftSibling(this.getPage());                                                                        // Setting the left sibling value of the new node.
        newRNode.setParent(this.getParent());                                                                           // Setting the parent value of the new node.
        newRNode.setRightSibling(this.getRightSibling());                                                               // Setting the right sibling value of the new node.

        if (this.getRightSibling() != -1) {                                                                             // Checking whether the current node doesn't have a right sibling.
            BTreeNode retrieved_sibling = retrieveNode(this.getRightSibling());                                         // If true we retrieve the left sibling node.
            retrieved_sibling.setLeftSibling(newRNode.getPage());                                                       // Setting the left sibling value of the retrieved node.
            saveNode(retrieved_sibling);                                                                                // Adding the retrieved node to the hashTable.
        }

        this.setRightSibling(newRNode.getPage());                                                                       // Setting the right sibling value of the current node.
        saveNode(newRNode);                                                                                             // Adding the new node to the hashTable.
        saveNode(this);                                                                                                 // Adding the current node to the hashTable.

        BTreeNode retrieved_parent = retrieveNode(this.getParent());                                                    // Retrieving the parent node of the current node.
        return retrieved_parent.pushUpKey(upKey,this.getPage(), newRNode.getPage());                                    // Returning the return value of pushUpKey function.
    }


    //_____________________________________________ Underflow Methods __________________________________________________
    /**
     * Method that checks if the node has underflow.
     * @return Returning true if the node is indeed underflow.
     * */
    public boolean isUnderflow() {
        return this.getKeyCount() < (this.keys.length / 2);                                                             // Returning the boolean value.
    }

    /**
     * Method for dealing with the overflow in node.
     * @return Returning the corresponding value of each case.
     */
    public BTreeNode dealUnderflow() {
        if (this.getParent() == -1)                                                                                     // Checking whether the parent is equal to -1.
            return null;                                                                                                // If true we return null.

        int leftSibling = this.getLeftSibling();                                                                        // Setting the leftSibling value variable.
        if (leftSibling != -1 && retrieveNode(leftSibling).canLendAKey()) {                                             // Checking if there is a left sibling an if it can lend a key.
            BTreeNode retrieved_parent = retrieveNode(this.getParent());                                                // If true we retrieve the parent.
            BTreeNode retrieved_sibling = retrieveNode(leftSibling);                                                    // Retrieving the left sibling.
            retrieved_parent.processChildrenTransfer(this, retrieved_sibling, retrieved_sibling.getKeyCount() - 1);     // Jumping to processChildrenTransfer function.
            saveNode(retrieved_parent);                                                                                 // Adding the retrieved parent node to the hashTable.
            return null;                                                                                                // Returning null.
        }

        int rightSibling = this.getRightSibling();                                                                      // Setting the rightSibling value variable.
        if (rightSibling != -1 && retrieveNode(rightSibling).canLendAKey()) {                                           // Checking if there is a right sibling an if it can lend a key.
            BTreeNode retrieved_parent = retrieveNode(this.getParent());                                                // If true we retrieve the parent.
            BTreeNode retrieved_sibling = retrieveNode(rightSibling);                                                   // Retrieving the right sibling.
            retrieved_parent.processChildrenTransfer(this, retrieved_sibling, 0);                                       // Jumping to processChildrenTransfer function.
            saveNode(retrieved_parent);                                                                                 // Adding the retrieved parent node to the hashTable.
            return null;                                                                                                // Returning null.
        }

        BTreeNode retrieved_parent = retrieveNode(this.getParent());                                                    // Retrieving the parent node.
        if (leftSibling != -1)                                                                                          // Checking if there is a left sibling.
            return retrieved_parent.processChildrenFusion(retrieveNode(leftSibling), this);                             // If true we return the returning argument of processChildrenFusion.
        else
            return retrieved_parent.processChildrenFusion(this, retrieveNode(this.getRightSibling()));                  // Else we return the returning argument of processChildrenFusion.
    }


}