package Classes;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * FileHandler class that handles the files associated with the tree.
 */
public class FileHandler {
    /**
     *  hashTable that holds pages that have been changed.
     */
    Hashtable<Integer, BTreeNode> toUpdate = new Hashtable<>();
    /**
     * The size in bytes of every record in the data file.
     */
    private final int data_file_size  = 32;
    /**
     * Index file where the B+tree nodes are stored.
     */
    private final RandomAccessFile index;
    /**
     * Data file where the values associated with the keys in the B+ree.
     */
    private final RandomAccessFile data;
    /**
     * Offset in bytes between key and corresponding value.
     */
    private final int key_value_offset;
    /**
     * Offset between every value in the data file.
     */
    private final int dataByteOffset;
    /**
     *  Size in bytes of total bytes containing the information of the node.
     */
    private final int dataPage_size;
    /**
     *  Value that holds the next page index value.
     */
    private static int nextPage = 1;
    /**
     * Size in bytes of every value that holds information about the node.
     */
    private final int info_size;
    /**
     *  Size in bytes of a page where a node of the B+tree will be stored.
     */
    private final int page_size;
    /**
     *  Byte array variable where we store everything we read from the file.
     */
    private final byte[] buffer;
    /**
     * Size in bytes of key + value.
     */
    private final int rec_size;
    /**
     *  Size in bytes of a key stored in the B+Tree.
     */
    private final int key_size;
    /**
     * Variable that holds the total number of records the tree has.
     */
    private int totalRecs = 0;
    /**
     * Variable that holds the number of counts in every test.
     */
    private int counter = 0;


//___________________________________________________ Constructor ______________________________________________________
    /**
     * Constructor for the fileHandler.
     * @param page_size Size in bytes of every page representing a node.
     * @param info_size Size in bytes of the value that holds information about the node.
     * @param rec_size_index Size in byte of a key and its associated value.
     * @param rec_size_data Size in byte of a value in the data file.
     * @param key_size Size in byte of key stored in the node.
     * @param dataPage_size Size in byte of all the values containing the 4information of the node.
     * @param key_value_offset Offset between a key and its corresponding value.
     * @param index The index file containing the nodes.
     * @param data The data file containing the values associated with the keys.
     */
    public FileHandler(int page_size, int info_size, int rec_size_index, int rec_size_data, int key_size, int dataPage_size, int key_value_offset, RandomAccessFile index,  RandomAccessFile data) {
        this.key_value_offset = key_value_offset;                                                                       // Initializing the key_value_offset value.
        this.dataByteOffset = rec_size_data;                                                                            // Initializing the dataByteOffset value.
        this.dataPage_size = dataPage_size;                                                                             // Initializing the dataPage_size value.
        this.buffer = new byte[page_size];                                                                              // Initializing the buffer size.
        this.rec_size = rec_size_index;                                                                                 // Initializing the rec_size value.
        this.page_size = page_size;                                                                                     // Initializing the page_size value.
        this.info_size = info_size;                                                                                     // Initializing the info_size value.
        this.key_size = key_size;                                                                                       // Initializing the key_size value.
        this.index = index;                                                                                             // Initializing the index file.
        this.data = data;                                                                                               // Initializing the data file.
    }


//_____________________________________________________ Getters ________________________________________________________
    /**
     * Method for getting a value from the data file.
     * @param position The position from where we will read the value.
     * @return Returning the value we got from the file.
     */
    public int getData(int position){
        byte[] tmpArray = readBlock(data, position/page_size);                                                          // Reading a block from the data file.
        byte[] intArray = new byte[data_file_size];                                                                     // Creating a new byte array to store the record we get from the file.

        System.arraycopy(tmpArray, position % page_size, intArray, 0, data_file_size);                                  // Copying data_file_size bytes from tmpArray to intArray.
        return ByteBuffer.wrap(intArray).getInt();                                                                      // Returning the value.
    }

    /**
     * Method for getting a B+Tree node from the file.
     * @param page The page of the node we want to retrieve.
     * @return Returning the node we got from the file.
     */
    public BTreeNode getNode(int page) {
        if (toUpdate.containsKey(page))                                                                                 // Checking if the hashTable contains the node we want to retrieve.
            return toUpdate.get(page);                                                                                  // If true we return the BTreeNode from the hashTable.

        BTreeNode newNode;                                                                                              // Creating a new BTreeNode instance.
        byte[] tmp = new byte[page_size];                                                                               // Creating a byte array with size equal to one page.

        byte[] node = readBlock(index, page);                                                                           // Reading a block from the Index file.
        System.arraycopy(node, 0, tmp, 0, info_size);                                                                   // Copying to tmp the first 4 bytes (NodeType).
        int type = (ByteBuffer.wrap(tmp).getInt());                                                                     // Setting the type variable equal to the type of the node.

        newNode = (type == 0) ? new BTreeInnerNode(page_size, page, this) : new BTreeLeafNode(page_size, page, this);   // Checking if the node we read is inner of lead.

        for (int i = 1; i < dataPage_size / info_size; i++) {                                                           // Looping for as many information values as we got.
            System.arraycopy(node, i * info_size, tmp, 0, info_size);                                                   // Copying 16 bytes from node byte array to the tmp array, 4 bytes at a time.
            switch (i) {                                                                                                // Switch case for the type of information we retrieve from the file.
                case 1 -> newNode.setParent(ByteBuffer.wrap(tmp).getInt());                                             // The first value is the parentNode page value.
                case 2 -> newNode.setLeftSibling(ByteBuffer.wrap(tmp).getInt());                                        // The second value is the leftSibling page value.
                case 3 -> newNode.setRightSibling(ByteBuffer.wrap(tmp).getInt());                                       // The third value is the rightSibling page value.
                case 4 -> newNode.setKeyCount(ByteBuffer.wrap(tmp).getInt());                                           // The forth value is the keyCount value.
            }
        }

        int j;                                                                                                          // Creating an index variable j.
        for (j = 0; j < (page_size - dataPage_size) / rec_size; j++) {                                                  // Looping for as many keys and values as we got.
            System.arraycopy(node, j * info_size + dataPage_size, tmp, 0, key_size);                                    // Copying key_size bytes from the byte array we read from the file.
            newNode.setKey(j, ByteBuffer.wrap(tmp).getInt());                                                           // Setting the key value we got from the file.

            System.arraycopy(node, j * info_size + dataPage_size + key_value_offset, tmp, 0, key_size);                 // Copying key_size bytes from the byte array we read from the file.
            newNode.setValues(j, ByteBuffer.wrap(tmp).getInt());                                                        // Setting the key value we got from the file.
        }
        if (type == 0) {                                                                                                // Checking whether the node we are retrieving is an inner node.
            System.arraycopy(node, page_size - key_size, tmp, 0, key_size);                                             // If true we copy the last 4 bytes of the byte array we got from the file.
            newNode.setValues(j, ByteBuffer.wrap(tmp).getInt());                                                        // Setting the last value of the newNode.
        }
        return newNode;                                                                                                 // Returning the newNode.
    }

    /**
     * Getter for getting the next page to create a BTreeNode.
     * @return The next available page and then adding 1 to it.
     */
    public int getNextPage() {
        return nextPage++;                                                                                              // Returning the nextPage value and then adding 1 to it.
    }

    /**
     * Getter for the byte offset where we store a value corresponding to a key.
     * @return Returning the byte offset of the next data value.
     */
    public int getOffset(){
        return totalRecs * dataByteOffset;                                                                              // Returning the correct byte offset.
    }

    /**
     * Getter for the hashtable object.
     * @return Returning the hashtable associated with the hashTable.
     */
    public Hashtable<Integer, BTreeNode> getToUpdate() {
        return toUpdate;                                                                                                // Returning the hashTable object.
    }


//_____________________________________________________ Methods ________________________________________________________
    /**
     * Method for increasing the number of records we go in B+Tree.
     */
    public void increaseRecs(){
        this.totalRecs++;                                                                                               // Increasing the totalRecs variable by 1.
    }

    /**
     * Method for reading a block of bytes from the file.
     * @param file The file from which we are retrieving the block of bytes.
     * @param position The position in the file from where we are retrieving the block of bytes.
     * @return  Returning a byte array containing the block of bytes.
     */
    public byte[] readBlock(RandomAccessFile file, int position) {
        byte[] tmp = new byte[page_size];                                                                               // Creating a byte array that holds the block of bytes we are going to retrieve.
        try {                                                                                                           // Trying to access the file.
            file.seek((long) position * page_size);                                                                     // Moving the file cursor to the correct position.
            file.read(tmp);                                                                                             // Reading a block of data from the file to the buffer
            System.arraycopy(tmp, 0, buffer, 0, page_size);                                                             // Copying the tmp byte array to the FileHandler buffer.
            counter ++;                                                                                                 // Increasing the number of disk accesses by 1.
            return buffer;                                                                                              // Returning The buffer.
        } catch (IOException e) {                                                                                       // Catch in case the there was an error handling the file.
            System.out.println("Error handling the file");                                                              // Printing the error message.
            e.printStackTrace();                                                                                        // Printing the exception message.
            return null;                                                                                                // Returning null if there was an error.
        }
    }

    /**
     * Method for writing a block of byte to a file.
     * @param file The file in which we are writing the block of bytes.
     * @param buffer The byte array that contains the information we want to write.
     * @param position The position in the file where we are going to write the block of bytes.
     */
    public void writeBlock(RandomAccessFile file, byte[] buffer, int position) {
        try {                                                                                                           // Trying to access the file.
            file.seek((long) position * page_size);                                                                     // Moving the file cursor to the correct position.
            file.write(buffer);                                                                                         // Writing a block of information from the buffer to the file.
            counter ++;                                                                                                 // Increasing the number of disk accesses by 1.
        } catch (IOException e) {                                                                                       // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                                              // Printing the error message
            e.printStackTrace();                                                                                        // Printing the exception message
        }
    }

    /**
     * Method for writing data to the Data file.
     * @param value The value we are writing to the file.
     */
    public void updateData(int value){
        byte[] tmpArray;                                                                                                // Creating a byte array to store wha we read from the file.
        int index = ((totalRecs-1) % (page_size/data_file_size));                                                       // Setting the value of the index in the file.
        int page = (totalRecs-1)/(page_size/data_file_size);                                                            // Setting the value of the page where we are writing the information.

        tmpArray = readBlock(data, page);                                                                               // Reading a block from the data file.
        byte[] intArray = ByteBuffer.allocate(data_file_size).putInt(value).array();                                    // Converting the value we want to store into bytes.
        System.arraycopy(intArray, 0, tmpArray, index * data_file_size, data_file_size);                                // Copying the bytes of the value to the correct place in the page.
        writeBlock(data,tmpArray,page);                                                                                 // Writing the block of bytes to the file.
    }

    /**
     * Method for updating the index file.
     * @param node The node we are writing to the file.
     */
    public void updateIndex(BTreeNode node){
        byte[] tmpArray = new byte[page_size];                                                                          // Creating a byte array where we will store the values of the node.
        byte[] intArray = null;                                                                                         // Creating a byte array that will temporary hold the values in bytes.

        for (int i = 0; i < dataPage_size /info_size; i++) {                                                             // Looping for as many information values as we got.
            switch (i) {                                                                                                // Switch case for the type of information we are converting to byte.
                    case 0 -> intArray = ByteBuffer.allocate(info_size).putInt(node.getNodeType()).array();             // The first value is the nodeType of the node.
                    case 1 -> intArray = ByteBuffer.allocate(info_size).putInt(node.getParent()).array();               // The second value is the parent page value of the node.
                    case 2 -> intArray = ByteBuffer.allocate(info_size).putInt(node.getLeftSibling()).array();          // The third value is the leftSibling page value of the node.
                    case 3 -> intArray = ByteBuffer.allocate(info_size).putInt(node.getRightSibling()).array();         // The forth value is the rightSibling page value of the node.
                    case 4 -> intArray = ByteBuffer.allocate(info_size).putInt(node.getKeyCount()).array();             // The fifth value is the keyCount value of the node.
            }
            System.arraycopy(intArray, 0, tmpArray, info_size * i, info_size);                                          // Copying the intArray value to the corresponding place in the tmpArray.
        }

        int i;                                                                                                          // Creating an index variable.
        for (i = 0; i < node.getKeyCount(); i++) {                                                                      // Looping for as many keys as we got in the node.
            intArray = ByteBuffer.allocate(key_size).putInt(node.getKey(i)).array();                                    // Converting the key value to bytes.
            System.arraycopy(intArray, 0, tmpArray,  dataPage_size + i * key_size, key_size);                           // Copying the bytes to the tmpArray.

            intArray = ByteBuffer.allocate(key_size).putInt(node.getValues(i)).array();                                 // Converting the value associated with the key to bytes.
            System.arraycopy(intArray, 0, tmpArray, dataPage_size + key_value_offset + i * key_size, key_size);         // Copying the bytes to the tmpArray.
        }

        if (node.getNodeType() == 0){                                                                                   // Checking whether the node we are storing is an inner node.
            intArray = ByteBuffer.allocate(key_size).putInt(node.getValues(i)).array();                                 // If true we convert the last value to bytes.
            System.arraycopy(intArray, 0, tmpArray, dataPage_size + key_value_offset + (i) * key_size, key_size);       // Copying the bytes to tmpArray.
        }
        writeBlock(index,tmpArray,node.getPage());                                                                      // Writing the block of bytes to the file.
    }


    //_____________________________________________ HashMaps Methods ___________________________________________________
    /**
     * Method for putting a node in the hashTable to be updated.
     * @param node The node we are adding in the hashTable.
     */
    public void removeAndPut(BTreeNode node){
        toUpdate.remove(node.getPage());                                                                                // Removing the old instance of the node from the file.
        toUpdate.put(node.getPage(),node);                                                                              // Putting the new instance of the node in the hashTable.

        if(toUpdate.keySet().size() > 100){                                                                             // Checking whether our cache is full.
            for(Object key: toUpdate.keySet().toArray())                                                                // If true we loop for as many items as we got in the hashTable.
                updateAndRemove(toUpdate.get(key));                                                                     // Updating the associated page.
        }
    }

    /**
     * Method for updating the file and then removing the node from the hashTable.
     * @param node The node we are updating.
     */
    public void updateAndRemove(BTreeNode node){
        updateIndex(node);                                                                                              // Updating the index file
        toUpdate.remove(node.getPage());                                                                                // Removing the node instance from the hashTable.
    }


    //______________________________________________ Counter Methods ___________________________________________________
    /**
     * Method for initializing the counter to 0.
     */
    public void initialize_counter (){
        this.counter = 0;                                                                                               // Setting the counter value equal to 0.
    }

    /**
     * Method for getting the number of counts we measured.
     * @return Returning the number of counts.
     */
    public int getCounts(){
        return this.counter;                                                                                            // Returning the number of counts.
    }

    /**
     * Method for emptying the hashTable.
     */
    public void emptyHashTable(){
        toUpdate.clear();                                                                                               // Emptying the hastTable.
    }


}
