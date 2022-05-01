import java.nio.ByteBuffer;

public class BinarySearch {
    private final int page_size;
    private final int rec_size;
    private final int key_size;
    FileManager fileManager;
    private int rightKey;
    private int leftKey;

//_____________________________________________________ Constructor ____________________________________________________
    public BinarySearch(int page_size, int rec_size, int key_size, FileManager fileManager) {
        this.fileManager = fileManager;
        this.page_size = page_size;
        this.rec_size = rec_size;
        this.key_size = key_size;
    }

//_______________________________________________________ Methods ______________________________________________________
    public int binarySearch(int leftIndex, int length, int key){                                                    // Method for searching in an array using the binary search method
        if (length >= 1){
            int middle = leftIndex + (length - leftIndex) / 2;                                                      // Calculating the value of the middle index
            getKey(middle);                                                                                         // Getting the key that is in the middle and the one that is the 4th in that block

            if(leftKey == key)                                                                                      // Checking whether the key in the middle is the one we are looking for
                return getIndex(0);                                                                                 // If we found the key, we return it

            if(leftKey > key)                                                                                       // Checking whether the key in the middle bigger than the one we are looking for
                return binarySearch(leftIndex, middle - 1, key);                                                    // If so, we search in the left half of the array we were searched before, again using binary search
            else if(rightKey >= key){                                                                               // Checking whether the key in the end of the block is smaller or equal to the one we are looking for
                for(int i=0; i < page_size/rec_size; i++){
                    byte[] tmp = new byte[key_size];                                                                // Declaring a new byte array to save the key bytes
                    System.arraycopy(fileManager.getBuffer(),i * rec_size, tmp,0,tmp.length);                       // Copying the first 4 bytes of every rec in the page the key is in
                    if (ByteBuffer.wrap(tmp).getInt() == key)                                                       // Checking whether the key we got from the block is the one we are looking for
                        return getIndex(i);                                                                         // If we found the key, we return it
                }
            }
            else{
                return binarySearch( middle + 1, length, key);                                                       // Else we search for the key in the right half of the array we were searched before, again using binary search
            }
        }
        return 0;                                                                                                   // Returning 0 if we didn't find the key we were looking for
    }

    public void getKey(int index){                                                                                  // Method for getting the key value from the masterArray
        byte[] tmp = new byte[key_size];                                                                            // Declaring a new byte array to save the key bytes
        fileManager.readBlock(index);                                                                               // Reading a block from the file

        System.arraycopy(fileManager.getBuffer(),0, tmp,0,tmp.length);                                              // Copying the key_size bytes to the tmp array
        leftKey = ByteBuffer.wrap(tmp).getInt();                                                                    // Setting the value of left key

        System.arraycopy(fileManager.getBuffer(),((page_size/rec_size - 1) * rec_size), tmp,0,tmp.length);          // Copying the key_size bytes to the tmp array
        rightKey = ByteBuffer.wrap(tmp).getInt();                                                                   // Setting the value of right key
    }

    public int getIndex(int index){                                                                                 // Method for getting the index when we have found the key
        byte[] tmp = new byte[key_size];                                                                            // Declaring a tmp byte array to store the key

        System.arraycopy(fileManager.getBuffer(),index * rec_size + key_size, tmp,0,tmp.length);                    // Copying the next 4 bytes of the rec the key is in
        return ByteBuffer.wrap(tmp).getInt();                                                                       // Returning the value key
        }

}
