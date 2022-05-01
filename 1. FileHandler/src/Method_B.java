import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Method_B {
    private final int methodArecSize;
    private final int numOfPages;
    private final int page_size;
    private final int numOfRuns;
    private final int numOfKeys;
    private final int rec_size;
    private final int key_size;
    private int fileIndex = 0;
    private int listIndex = 0;
    List<Integer> listOfKeys;
    FileManager fileManager;
    List<Integer> usedKeys;


//_____________________________________________________ Constructor ____________________________________________________
    public Method_B(int page_size, int numOfKeys, int numOfRuns, int methodAreadSize, int key_size, List<Integer> listOfKeys, List<Integer> usedKeys) {
        this.methodArecSize = methodAreadSize;
        this.listOfKeys = listOfKeys;
        this.rec_size = 2 * key_size;
        this.page_size = page_size;
        this.numOfKeys = numOfKeys;
        this.numOfRuns = numOfRuns;
        this.key_size = key_size;
        this.usedKeys = usedKeys;
        this.numOfPages = numOfKeys/(page_size/ rec_size);
    }

//_______________________________________________________ Methods ______________________________________________________
    public int createFile(String fname) {                                                                   // Method for creating a new random file in the desired format
        String type = "2 files, 1 with 4 Keys and 4 indexed, 1 with 4 keys and 4 data in each block - Both Unsorted";
        fileManager = new FileManager(type, page_size, numOfKeys, numOfRuns);                               // Declaring a new File manager
        fileManager.createFile(fname);                                                                      // Creating a file for the keys and indexes

        for(int i=1; i <= numOfPages; i++) {
            writeKeysIndexesToBuffer();                                                                     // Writing as many keys and as many indexes to the buffer every time
            fileManager.writeNextBlock();                                                                   // Writing the buffer to the file
        }
        fileManager.setNumOfReads_create(fileManager.getNumOfReads());                                      // Setting the number of reads needed to create the file
        fileManager.setNumOfWrites_create(fileManager.getNumOfWrites());                                    // Setting the number of writes needed to create the file
        return 1;                                                                                           // Returning 1 if everything went ok
    }

    public int writeKeysIndexesToBuffer(){                                                                  // Method for writing as many keys followed by their page index to the buffer
        byte[] tmpArray = new byte[rec_size];                                                               // Declaring the array where we will save our chars each time
        byte[] intArray;                                                                                    // Declaring the array where we will save our chars each time
        for(int i=0; i < page_size/rec_size; i++) {
            fileIndex += (i % (page_size/methodArecSize) == 0) ? 1 : 0;                                     // Changing the value of index according to the page we are writing

            intArray = ByteBuffer.allocate(key_size).putInt(listOfKeys.get(listIndex)).array();             // Converting the key we got from createRandomKey method into a byteArray
            System.arraycopy(intArray, 0, tmpArray, 0, key_size);                                           // Copying the intArray to tmpArray

            intArray = ByteBuffer.allocate(key_size).putInt(fileIndex).array();                             // Converting the key we got from createRandomKey method into a byteArray
            System.arraycopy(intArray, 0, tmpArray, key_size, key_size);                                    // Copying the intArray to tmpArray

            fileManager.setBuffer(tmpArray,i);                                                              // Copying the tmpArray to the buffer so we car write it to the file
            listIndex += 1;                                                                                 // Increasing the listIndex value by 1
        }
        return 1;                                                                                           // Returning 1 if everything went ok
    }

    public int readMethod_B(String from, String to) {                                                       // Method for reading the content of the file created in this method
        List<Integer> used = new ArrayList<>();                                                             // Declaring a new ArrayList where we save the keys we searched for
        int reads = 0;                                                                                      // Declaring a new int to store the number of reads

        for (int i=0; i < numOfRuns; i++){
            int keyToFind = fileManager.getRandomKeyFromList(usedKeys,used);                                // Getting a key from the one the list of keys we searched in Method A
            int index = findIndex(keyToFind);                                                               // Getting the index of the pages
            reads += fileManager.getNumOfReads();                                                           // Getting the number of reads we made to the file with the indexes
            reads += findData(index,keyToFind,to,from);                                                     // Getting the number of reads we made to the file with the keys and data
        }
        fileManager.setNumOfReads(reads);                                                                   // Setting the number of read in average
        return 1;                                                                                           // Returning 1 if everything went ok
    }

    public int findIndex(int key) {                                                                         // Method for finding the index according the key
        byte[] buf = new byte[page_size];                                                                   // Declaring a new byte array where we will copy the buffer so we can extract the desired information
        fileManager.setCursor(page_size);                                                                   // Setting the cursor to the first page

        for (int j = 0; j < numOfPages; j++) {
            fileManager.readNextBlock();                                                                    // Reading the next block from the file
            System.arraycopy(fileManager.getBuffer(), 0, buf, 0, page_size);                                // Copying the buffer from file manager to the buf byte array

            for (int k = 0; k < page_size / rec_size; k++) {
                byte[] tmp = new byte[key_size];                                                            // Declaring a new byte array where we save the keys we read in each record
                System.arraycopy(buf, rec_size * k, tmp, 0, tmp.length);                                    // Copying the first 4 bytes of every record to the tmp byte array
                int curr_key = ByteBuffer.wrap(tmp).getInt();                                               // Storing the value of the key we read from the record

                if (curr_key == key){                                                                       // Checking if the key we got is the one we are looking for
                    System.arraycopy(buf, rec_size * k + key_size, tmp, 0, tmp.length);                     // Copying the first 4 bytes of every record to the tmp byte array
                    return ByteBuffer.wrap(tmp).getInt();                                                   // Returning the index of the key we are looking for
                }
            }
        }
        return 0;                                                                                           // Returning 0 if there was an error
    }

    public int findData(int index, int key, String from,  String to){                                       // Method for finding the data in the first file
        fileManager.closeFile();                                                                            // Closing the file with the keys and the indexes
        fileManager.openFile(to);                                                                           // Opening the file that has the keys and the data
        fileManager.setNumOfReads(0);                                                                       // Setting the number of read to zero so we don't make any arithmetic mistake
        fileManager.readBlock(index);                                                                       // Reading the correct block an looking for the key in that block

        for (int i = 0; i < page_size/ methodArecSize; i++){
            byte[] tmp = new byte[key_size];                                                                // Declaring a new byte array where we save the keys we read in each record
            System.arraycopy(fileManager.getBuffer(), methodArecSize * i, tmp, 0, key_size);                // Copying the first 4 bytes of every record to the tmp byte array
            int curr_key = ByteBuffer.wrap(tmp).getInt();                                                   // Storing the value of the key we read from the record

            if (curr_key == key){                                                                           // Checking if the key we got is the one we are looking for
                fileManager.closeFile();                                                                    // Closing the file with the keys and the indexes
                fileManager.openFile(from);                                                                 // Opening the first file that has the
                fileManager.setNumOfReads(0);                                                               // Setting the number of read to zero so we don't make any arithmetic mistake
                return fileManager.getNumOfReads();                                                         // Returning the number of reads
            }
        }
        return 0;                                                                                           // Returning 0 if there was an error
    }

    public void printInfo(){                                                                                // Method for printing the first block that contains the info of the method
        fileManager.printInfoBlock();                                                                       // Printing the information that is stored in page zero of the file in a nice format
    }

    public int endMethod(){                                                                                 // Method for closing the file corresponding to the current method
        fileManager.closeFile();                                                                            // Closing the file the fileManager currently handles
        return 1;                                                                                           // Returning 1 if everything went ok
    }

}
