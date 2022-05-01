import java.util.ArrayList;
import java.util.List;

public class Method_C {
    private final String type = "Sorting the 1st file using Merge Sort - Sorted";
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
    public Method_C(int page_size, int numOfKeys, int numOfRuns, int rec_size, int key_size, List<Integer> usedKeys) {
        this.numOfPages = numOfKeys/(page_size/rec_size);
        this.totalLength = numOfPages * page_size;
        this.numOfRuns = numOfRuns;
        this.numOfKeys = numOfKeys;
        this.page_size = page_size;
        this.usedKeys = usedKeys;
        this.rec_size = rec_size;
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
        fileManager = new FileManager(type, page_size, numOfKeys, numOfRuns);                                   // Declaring a new FileManager object
        fileManager.createFile(fname);                                                                          // Creating a file for the keys and indexes
        fileManager.setCursor(page_size);                                                                       // Setting the cursor to point at the first page
        writeToBuffer(masterArray);                                                                             // Writing the masterArray to the buffer
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

    public int readMethod_C(String from) {                                                                      // Method for reading the content of the file created in this method
        fileManager.closeFile();                                                                                // Closing the file we are currently handling
        fileManager.openFile(from);                                                                             // Opening the file we got as input
        List<Integer> used = new ArrayList<>();                                                                 // Declaring a new ArrayList where we save the keys we searched for

        for (int i=0; i < numOfRuns; i++){
            int keyToFind = fileManager.getRandomKeyFromList(usedKeys,used);                                    // Getting a random key from the list in order to search it in the file
            BinarySearch binarySearch = new BinarySearch(page_size,rec_size,key_size,fileManager);              // Declaring a new BinarySearch object
            binarySearch.binarySearch(1, numOfPages,keyToFind);                                                 // Using the binary search method to find the desired key
        }
        fileManager.setNumOfReads(fileManager.getNumOfReads());                                                 // Setting the number of reads needed to find the desired keys
        return 1;                                                                                               // Returning 1 if everything went ok
    }

    public void printInfo(){                                                                                    // Method for printing the first block that contains the info of the method
        fileManager.printInfoBlock();                                                                           // Printing the information that is stored in page zero of the file in a nice format
    }

    public int endMethod(){                                                                                     // Method for closing the file corresponding to the current method
        fileManager.closeFile();                                                                                // Closing the file the fileManager currently handles
        return 1;                                                                                               // Returning 1 if everything went ok
    }
}
