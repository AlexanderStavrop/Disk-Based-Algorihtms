import java.nio.ByteBuffer;

public class MergeSort {
    private final int totalLength;
    private final int numOfPages;
    private final int page_size;
    private final int rec_size;
    private final int key_size;
    FileManager fileManager;
    private int sortReads;

    public int getSortReads() {
        return sortReads;
    }

    public MergeSort(int totalLength, int numOfPage, int page_size, int rec_size, int key_size, FileManager fileManager) {
        this.fileManager = fileManager;
        this.totalLength = totalLength;
        this.numOfPages = numOfPage;
        this.page_size = page_size;
        this.rec_size = rec_size;
        this.key_size = key_size;
    }

    public byte[] loadFile(String from){                                                                                    // Method for loading every page of the file to an array so it can be sorted
        byte[] masterArray = new byte[totalLength];                                                                         // Declaring a new byte array to store all the information in the file
        fileManager.openFile(from);                                                                                         // Opening the file we want to read the date from(Method_A)
        fileManager.setNumOfReads(0);                                                                                       // Setting the number of reads to 0
        for (int i=1; i <= numOfPages; i++){
            fileManager.readBlock(i);                                                                                       // Reading a block to the buffer
            System.arraycopy(fileManager.getBuffer(), 0, masterArray, (i - 1) * page_size, page_size);                      // Copying the buffer to the masterArray array
        }
        sortReads = fileManager.getNumOfReads();                                                                            // Storing the number or writes needed for sorting the file
        fileManager.closeFile();                                                                                            // Closing the file
        return masterArray;                                                                                                 // Returning the array
    }

    public void sort(byte[] arr, int left, int right) {                                                                     // Method for sorting the a byte array recursively
        if (left < right) {                                                                                                 // Checking whether the left index is smaller than the right one
            int middle = left + (right - left) / 2;                                                                         // If so we find the middle value

            sort(arr, left, middle);                                                                                        // Sorting the first half of the array, (array[0, m])
            sort(arr, (middle + 1), right);                                                                                 // Sorting the second half of the array, (array[m+1, right)

            merge(arr, left, middle, right);                                                                                // Merging two arrays
        }
    }

    public void merge (byte[] arr, int left, int middle, int right){                                                        // Method for merging two unordered parts of an array into the array but ordered
        int leftIndex = 0, rightIndex = 0, arrIndex = 0, rightKey, leftKey;                                                 // Declaring the index values we will need for this method

        int n1 = middle - left + 1;                                                                                         // Calculation the size of the first array
        int n2 = right - middle;                                                                                            // Calculation the size of the second array

        byte[] leftArray = new byte[n1 * rec_size];                                                                         // Declaring a temporary array for the first array
        byte[] rightArray = new byte[n2 * rec_size];                                                                        // Declaring a temporary array for the second array

        System.arraycopy(arr, left * rec_size, leftArray, 0,n1*rec_size);                                                   // Copying the first part to the left array
        System.arraycopy(arr,(middle + 1) * rec_size, rightArray, 0,n2 * rec_size);                                         // Copying the second part to the right array

        while (leftIndex < n1 && rightIndex < n2) {
            leftKey = getKey(leftArray, leftIndex);                                                                         // Getting the key from the left array
            rightKey = getKey(rightArray, rightIndex);                                                                      // Getting the key from the right array

            if (leftKey < rightKey) {                                                                                       // Checking whether the left key is smaller than the right one
                System.arraycopy(leftArray, leftIndex * rec_size, arr, left * rec_size + arrIndex, rec_size);               // Copying the first part to the array
                arrIndex += rec_size;                                                                                       // Increasing the arrayIndex by a rec_size
                leftIndex += 1;                                                                                             // Increasing the leftIndex value by 1
            }

            else{
                System.arraycopy(rightArray, rightIndex * rec_size, arr,left * rec_size + arrIndex,rec_size);               // Copying the second part to the array
                arrIndex += rec_size;                                                                                       // Increasing the arrayIndex by a rec_size
                rightIndex += 1;                                                                                            // Increasing the rightIndex value by 1
            }
        }

        while (leftIndex < n1) {
            System.arraycopy(leftArray, leftIndex * rec_size, arr,left * rec_size + arrIndex,rec_size);                     // Copying the first part to the array
            arrIndex += rec_size;                                                                                           // Increasing the arrayIndex by a rec_size
            leftIndex += 1;                                                                                                 // Increasing the leftIndex value by 1
        }

        while (rightIndex < n2) {
            System.arraycopy(rightArray, rightIndex * rec_size, arr,left * rec_size + arrIndex,rec_size);                   // Copying the second part to the array
            arrIndex += rec_size;                                                                                           // Increasing the arrayIndex value by a rec_size
            rightIndex += 1;                                                                                                // Increasing the rightIndex value by 1
        }
    }

    public int getKey(byte[] arr, int index){                                                                               // Method for getting the key value from the masterArray
        byte[] tmp = new byte[key_size];                                                                                    // Declaring a new byte array to save the key bytes
        System.arraycopy(arr,index * rec_size, tmp,0,tmp.length);                                                           // Copying the key_size bytes to the tmp array
        return ByteBuffer.wrap(tmp).getInt();                                                                               // Returning the key as an integer
    }

}
