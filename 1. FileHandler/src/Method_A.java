import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Method_A {
    List<Integer> listOfKeys = new ArrayList<>();
    List<Integer> usedKeys = new ArrayList<>();
    private final int numOfPages;
    private final int numOfRuns;
    private final int numOfKeys;
    private final int page_size;
    private final int rec_size;
    private final int key_size;
    FileManager fileManager;

//_____________________________________________________ Constructor ____________________________________________________
    public Method_A(int page_size, int numOfKeys,int numOfRuns,int rec_size,int key_size) {     // Constructor
        this.numOfPages = numOfKeys/(page_size/rec_size);
        this.numOfRuns = numOfRuns;
        this.page_size = page_size;
        this.numOfKeys = numOfKeys;
        this.rec_size = rec_size;
        this.key_size = key_size;
    }

//_______________________________________________________ Getters ______________________________________________________
    public List<Integer> getListOfKeys() {                                                      // Getter for getting the list of keys
        return listOfKeys;                                                                      // Returning listOfKeys list
    }

    public List<Integer> getUsedKeys() {                                                        // Getter for getting the list of keys we searched for
        return usedKeys;                                                                        // Returning usedKeys list
    }

    public int getRec_size() {                                                                  // Getter for getting the rec_size
        return rec_size;                                                                        // Returning the rec_size value
    }

//_______________________________________________________ Methods ______________________________________________________
    public int createFile(String fname) {                                                       // Method for creating a new random file in the desired format
        String type = "1 file containing 4 Keys and 4 String of data in each block - Unsorted";
        fileManager = new FileManager(type,page_size,numOfKeys,numOfRuns);                      // Creating a new instance of FileManager
        fileManager.createFile(fname);                                                          // Creating a new file with the name given as input

        for(int i=1; i <= numOfPages; i++) {
            writeInfoToBuffer();                                                                // Writing a block of info to the buffer using the writeInfoToBuffer method
            fileManager.writeBlock(i);                                                          // Writing a block from the buffer to the file
        }
        fileManager.setNumOfReads_create(fileManager.getNumOfReads());                          // Setting the number of reads needed to create the file
        fileManager.setNumOfWrites_create(fileManager.getNumOfWrites());                        // Setting the number of writes needed to create the file
        return 1;                                                                               // Returning 1 if everything went ok
    }

    public int createRandomKey(){                                                               // Method for creating a random and unique key
        while(true){
            int key = (int)(Math.random() * (1000000 - 1 + 1) + 1);                             // Generating a key from 1 to 10^6
            if (!listOfKeys.contains(key)){                                                     // If the key doesn't exists already
                listOfKeys.add(key);                                                            // We add the new key to the ArrayList
                return key;                                                                     // We return the key so it can be stored
            }
        }
    }

    public char createRandomChar() {                                                            // Method for creating a random character
            return (char) (Math.random() * (26 - 1 + 1) + 1 + 96);                              // Returning a number between 1 and 26 plus 96 so we get the corresponding ascii value
    }

    public int writeInfoToBuffer() {                                                            // Method for writing a block to the buffer
        byte[] tmpArray = new byte[rec_size];                                                   // Declaring the array where we will save our recs each time
        byte[] charArray;                                                                       // Declaring the array where we will save our keys each time
        byte[] intArray;                                                                        // Declaring the array where we will save our chars each time

        for(int i=0; i < page_size/rec_size; i++){                                              // Doing as many records as we can according to the page_size
            intArray = ByteBuffer.allocate(key_size).putInt(createRandomKey()).array();         // Converting the key we got from createRandomKey method into a byteArray
            System.arraycopy(intArray, 0, tmpArray, 0, key_size);                               // Copying the intArray to tmpArray

            int char_size = rec_size - key_size;
            charArray = ByteBuffer.allocate(char_size).putChar(createRandomChar()).array();     // Converting the char we got from createRandomChar into byteArray
            System.arraycopy(charArray, 0, tmpArray, key_size, char_size);                      // Copying the charArray to tmpArray

            fileManager.setBuffer(tmpArray,i % (page_size/rec_size));                           // Copying the tmpArray to the buffer so we car write it to the file
        }
        return 1;                                                                               // Returning 1 if everything went ok
    }

    public int getRandomKeyFromList(List<Integer> used){                                        // Method for getting a random key from
        Random rand = new Random();                                                             // Declaring the random variable
        while(true) {
            int randomKey = listOfKeys.get(rand.nextInt(listOfKeys.size()));                    // Getting a random key from the list of keys
            if (!used.contains(randomKey)) {                                                    // Checking if the key has been used before
                used.add(randomKey);                                                            // If we haven't used the key before, we add it to the new list
                return randomKey;                                                               // Returning the new key so it can be searched
            }
        }
    }

    public int readMethod_A(String from) {                                                      // Method for reading the content of the file created in this method
        byte[] buf = new byte[page_size];                                                       // Declaring a new byte array where we will copy the buffer so we can extract the desired information
        fileManager.closeFile();                                                                // Closing the file we are currently handling
        fileManager.openFile(from);                                                             // Opening the file we got as input
        fileManager.setCursor(page_size);                                                       // Moving the cursor at the start of the file (page 1)

        for (int i = 0; i < numOfRuns; i++){                                                
            int keyToFind = getRandomKeyFromList(usedKeys);                                     // Getting a random key from the list in order to search it in the file
            fileManager.setCursor(page_size);                                                   // Setting the cursor to the first page
            boolean found = false;                                                              // Declaring boolean flag that indicates if we found the key or not

            for (int j=0; j < numOfPages; j++){
                fileManager.readNextBlock();                                                    // Reading the next block from the file
                System.arraycopy(fileManager.getBuffer(), 0, buf, 0, page_size);                // Copying the buffer from file manager to the buf byte array

                for (int k=0; k < page_size/rec_size; k++){
                    byte[] tmp = new byte[key_size];                                            // Declaring a new byte array where we save the keys we read in each record
                    System.arraycopy(buf, rec_size * k, tmp, 0, tmp.length);                    // Copying the first 4 bytes of every record to the tmp byte array
                    int curr_key = ByteBuffer.wrap(tmp).getInt();                               // Storing the value of the key we read from the record

                    if (curr_key == keyToFind){                                                 // Checking if the key we got is the one we are looking for
                        found = true;                                                           // If we found the key we want, we change the variable "found" to true
                        break;                                                                  // Breaking to exit the current loop
                    }
                }
                if (found)                                                                      // Checking whether we have found the key we were looking for
                    break;                                                                      // If we found the key we want, we exit the current loop and look for another one
            }
        }
        return 1;                                                                               // Returning 1 if everything went ok
    }

    public void printInfo(){                                                                    // Method for printing the first block that contains the info of the method
        fileManager.printInfoBlock();                                                           // Printing the information that is stored in page zero of the file in a nice format
    }

    public int endMethod(){                                                                     // Method for closing the file corresponding to the current method
        fileManager.closeFile();                                                                // Closing the file the fileManager handles
        return 1;                                                                               // Returning 1 if everything went ok
    }

}
