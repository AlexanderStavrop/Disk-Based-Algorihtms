import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Method_D {
    private final String type = "2 files, Sorting the file from method B using Merge sort, file from method A - One sorted";
    private final int methodArecSize;
    private final int totalLength;
    private final int numOfPages;
    private final int page_size;
    private final int numOfKeys;
    private final int numOfRuns;
    private final int rec_size;
    private final int key_size;
    FileManager fileManager;
    List<Integer> usedKeys;
    private int sortReads;
    MergeSort mergeSort;
    byte[] masterArray;

//_____________________________________________________ Constructor ____________________________________________________
    public Method_D(int page_size, int numOfKeys, int numOfRuns, int key_size, int methodArecSize, List<Integer> usedKeys) {
        this.rec_size = 2 * key_size;
        this.methodArecSize = methodArecSize;
        this.numOfPages = numOfKeys / (page_size / rec_size);
        this.totalLength = numOfPages * page_size;
        this.page_size = page_size;
        this.numOfKeys = numOfKeys;
        this.numOfRuns = numOfRuns;
        this.usedKeys = usedKeys;
        this.key_size = key_size;
        this.sortReads = 0;
    }

//_______________________________________________________ Methods ______________________________________________________
    public int sortFile(String from){                                                                           // Method for sorting an array
        fileManager = new FileManager(type,page_size,numOfKeys,numOfRuns);                                      // Declaring a new FileManager object
        mergeSort = new MergeSort(totalLength,numOfPages,page_size,rec_size,key_size,fileManager);              // Declaring a new MergeSort Object
        masterArray = mergeSort.loadFile(from);                                                                 // Loading everything there is in the file we read from
        mergeSort.sort(masterArray,0,masterArray.length/rec_size - 1);                                          // Sorting the array according to the value of the keys
        return 1;                                                                                               // Returning 1 if everything went ok
    }

    public int createFile(String fname) {                                                                       // Method for creating a new random file in the desired format
        fileManager = new FileManager(type, page_size, numOfKeys, numOfRuns);                                   // Declaring a new File manager
        fileManager.createFile(fname);                                                                          // Creating a file for the keys and indexes
        fileManager.setCursor(page_size);                                                                       // Setting the cursor to point at the first page
        writeToBuffer(masterArray);                                                                             // Writing the master array to the buffer
        fileManager.setNumOfReads_sort(mergeSort.getSortReads());                                               // Setting the number of reads needed to sort the file
        return 1;                                                                                               // Returning 1 if everything went ok
    }

    public void writeToBuffer(byte[] arr){                                                                      // Method for writing the array we sorted back to the file
        for (int i=0; i < numOfPages; i++){
            fileManager.setBuffer(arr,i,page_size);                                                             // Copying a part of the array to the buffer
            fileManager.writeBlock((i + 1));                                                                    // Writing the buffer to the file
        }
        fileManager.setNumOfWrites_create(fileManager.getNumOfWrites());                                        // Setting the number of writes needed to create the file
        fileManager.setNumOfReads_sort(sortReads);                                                              // Setting the number of reads needed to create the file
    }

    public int readMethod_D(String from, String to) {                                                           // Method for reading the content of the file created in this method
        List<Integer> used = new ArrayList<>();                                                                 // Declaring a new ArrayList where we save the keys we searched for
        int index = 0;
        int reads = 0;                                                                                          // Declaring a new int to store the number of reads

        for (int i=0; i < numOfRuns; i++){
            int keyToFind = fileManager.getRandomKeyFromList(usedKeys,used);                                    // Getting a random key from the list in order to search it in the file
            BinarySearch binarySearch = new BinarySearch(page_size,rec_size,key_size,fileManager);              // Declaring a new BinarySearch object
            index = binarySearch.binarySearch(1, numOfPages,keyToFind);                                         // Using the binary search method to find the desired key
            reads += fileManager.getNumOfReads();
            reads += findData(index,keyToFind,from,to);
        }
        fileManager.setNumOfReads(reads);
        return 1;                                                                                               // Returning 1 if everything went ok
    }

    public int findData(int index, int key, String from,  String to){                                           // Method for finding the data in the first file
        fileManager.closeFile();                                                                                // Closing the file with the keys and the indexes
        fileManager.openFile(to);                                                                               // Opening the file that has the keys and the data
        fileManager.setNumOfReads(0);                                                                           // Setting the number of read to zero so we don't make any arithmetic mistake
        fileManager.readBlock(index);                                                                           // Reading the correct block an looking for the key in that block

        for (int i = 0; i < page_size/ methodArecSize; i++){
            byte[] tmp = new byte[key_size];                                                                    // Declaring a new byte array where we save the keys we read in each record
            System.arraycopy(fileManager.getBuffer(), methodArecSize * i, tmp, 0, key_size);                    // Copying the first 4 bytes of every record to the tmp byte array
            int curr_key = ByteBuffer.wrap(tmp).getInt();                                                       // Storing the value of the key we read from the record

            if (curr_key == key){                                                                               // Checking if the key we got is the one we are looking for
                int reads = fileManager.getNumOfReads();
                fileManager.closeFile();                                                                        // Closing the file with the keys and the indexes
                fileManager.openFile(from);                                                                     // Opening the first file that has the
                fileManager.setNumOfReads(0);                                                                   // Setting the number of read to zero so we don't make any arithmetic mistake
                return reads;                                                                                   // Returning the number of reads
            }
        }
        return 0;                                                                                               // Returning 0 if there was an error
    }

    public void printInfo(){                                                                                    // Method for printing the first block that contains the info of the method
        fileManager.printInfoBlock();                                                                           // Printing the information that is stored in page zero of the file in a nice format
    }

    public int endMethod(){                                                                                     // Method for closing the file corresponding to the current method
        fileManager.closeFile();                                                                                // Closing the file the fileManager currently handles
        return 1;                                                                                               // Returning 1 if everything went ok
    }

}

