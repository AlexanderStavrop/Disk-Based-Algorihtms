import java.io.*;
import java.util.List;
import java.util.Random;

public class FileManager {
    private final int buf_size2 = 92;
    private final int buf_size1 = 8;
    private RandomAccessFile file;
    private final int numOfRuns;
    private final int numOfKeys;
    private final int page_size;
    private final byte[] buffer;
    private int numOfReads_create;
    private int numOfWrites_create;
    private int numOfReads_sort;
    private int numOfWrites_sort;
    private int numOfWrites;
    private int numOfReads;
    private int numOfPages;
    private String fname;
    private String type;
    private int cursor;

//_____________________________________________________ Constructor ____________________________________________________
    public FileManager(String type, int page_size, int numOfKeys, int numOfRuns) {             // Constructor
        this.buffer = new byte[page_size];
        this.page_size = page_size;
        this.numOfKeys = numOfKeys;
        this.numOfRuns = numOfRuns;
        this.type = type;
    }

//_______________________________________________________ Getters ______________________________________________________
    public int getNumOfWrites() {
    return numOfWrites;
}

    public int getNumOfReads() {                                                            // Getter for the number of reads
        return numOfReads;
    }

    public byte[] getBuffer() {                                                             // Getter for the byte Array buffer
        return buffer;                                                                      // Returning the buffer
    }

//_______________________________________________________ Setters ______________________________________________________
    public void setNumOfWrites_create(int numOfWrites_create) {
        this.numOfWrites_create = numOfWrites_create;
    }

    public void setNumOfReads_create(int numOfReads_create) {
        this.numOfReads_create = numOfReads_create;
    }

    public void setNumOfReads_sort(int numOfReads_sort) {
        this.numOfReads_sort = numOfReads_sort;
    }

    public void setBuffer(byte[] buf, int position, int length) {                           // Setter for the byte Array buffer
        System.arraycopy(buf, position * page_size, buffer, 0, length);                     // Copying the charArray to tmpArray
    }

    public void setBuffer(byte[] buf, int position ) {                                      // Setter for the byte Array buffer
        System.arraycopy(buf, 0, buffer, position*buf.length, buf.length);                  // Copying the charArray to tmpArray
    }

    public void setNumOfReads(int numOfReads) {                                             // Setter for number of reads performed
        this.numOfReads = numOfReads;                                                       // Setting the number of pages
    }

    public void setCursor(int cursor) {                                                     // Setter for the cursor(file pointer)
        this.cursor = cursor;                                                               // Setting the value of the cursor
    }

//_______________________________________________________ Methods ______________________________________________________
    public int FileHandle() {                                                               // Method for updating the first page of the file
        ByteArrayOutputStream bos = new ByteArrayOutputStream();                            // Declaring ByteArrayOutputStream
        DataOutputStream out = new DataOutputStream(bos);                                   // Declaring DataOutputStream

        byte[] src = fname.getBytes();                                                      // Declaring a byte array with the file name
        byte[] dst = new byte[buf_size1];                                                   // Declaring a temporary byte array to copy src
        System.arraycopy(src, 0, dst, 0, src.length);                                       // Copying the src to dst

        src = type.getBytes();                                                              // Declaring a byte array with the file type
        byte[] dst2 = new byte[buf_size2];                                                  // Declaring a temporary byte array to copy src
        System.arraycopy(src, 0, dst2, 0, src.length);                                      // Copying the src to dst2

        try {
            out.write(dst);                                                                 // Writing the file name
            out.writeInt(numOfPages);                                                       // Writing the number of pages
            out.writeInt(numOfReads_create);                                                // Writing the number of read to create the file
            out.writeInt(numOfWrites_create);                                               // Writing the number of writes to create the file
            out.writeInt(numOfReads_sort);                                                  // Writing the number of read to sort the file
            out.writeInt(numOfWrites_sort);                                                 // Writing the number of writes to sort the file
            out.writeInt(numOfReads/numOfRuns);                                             // Writing the number of reads performed in average (reads/runs)
            out.write(dst2);                                                                // Writing the file type
            out.close();                                                                    // Closing the DataOutputStream
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }

        byte[] buf = bos.toByteArray();                                                     // Getting the ByteArrayOutputStream to bytes
        byte[] WriteDataPage = new byte[page_size];                                         // Declaring an array at page_size
        System.arraycopy(buf, 0, WriteDataPage, 0, buf.length);                             // Copying buf data to writeDataPage

        try {
            bos.close();                                                                    // Closing ByteArrayOutputStream
            file.seek(0);                                                                   // Moving the file pointer at the start of the file
            file.write(WriteDataPage);                                                      // Writing the writeDataPage to the file
            cursor = page_size;                                                             // Setting the cursor equal to 1 page_size
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int createFile(String fname) {                                                   // Method for creating a file
        try {
            this.file = new RandomAccessFile(fname, "rw");                                  // Creating the file
            this.numOfWrites_create = 0;                                                    // Setting the number of writes performed for creating the file to 0
            this.numOfReads_create = 0;                                                     // Setting the number of reads performed for creating the file to 0
            this.numOfWrites_sort = 0;                                                      // Setting the number of writes performed for sorting the file to 0
            this.numOfReads_sort = 0;                                                       // Setting the number of reads performed for sorting the file to 0
            this.numOfWrites = 0;                                                           // Setting the number of file writes to 0
            this.numOfReads = 0;                                                            // Setting the number of file reads to 0
            this.numOfPages = 0;                                                            // Setting the number of pages to 0
            this.fname = fname;                                                             // Setting the name equal to the input
            this.cursor = 0;                                                                // Setting the cursor to 0
            FileHandle();                                                                   // Updating the first page
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (FileNotFoundException e) {                                                 // Catch in case the file couldn't be found
            System.out.println("Error creating the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int openFile(String fname){                                                      // Method for opening a file
        try {
            this.file = new RandomAccessFile(fname,"rw");                                   // Opening a file with the name fname
            this.fname = fname;                                                             // Setting the fname equal to fname
            return numOfPages;                                                              // Retuning the number of pages if everything went ok
        } catch (FileNotFoundException e) {                                                 // Catch in case the file couldn't be found
            System.out.println("Error opening the file");                                   // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int readBlock(int position) {                                                    // Method for reading a block from a file and storing it to the buffer
        try {
            cursor = position * page_size;                                                  // Changing the value of the cursor
            file.seek(cursor);                                                              // Moving the cursor to the desired position
            file.read(buffer);                                                              // Reading a block of data from the file to the buffer
            cursor += page_size;                                                            // Increasing the value of the cursor by one page
            numOfReads += 1;                                                                // Increasing the number of file accesses by 1 (Reads)
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int readNextBlock() {                                                            // Method for reading the block the cursor currently points to
        try {
            file.seek(cursor);                                                              // Moving the cursor to the next page
            file.read(buffer);                                                              // Reading a block of data from the file to the buffer
            cursor += page_size;                                                            // Increasing the value of the cursor by 1 page
            numOfReads += 1;                                                                // Increasing the number of file accesses by 1 (Reads)
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int writeBlock(int position) {                                                   // Method for writing a block from buffer to the file
        try {
            cursor = position * page_size;                                                  // Changing the value of the cursor variable
            file.seek(cursor);                                                              // Moving the file pointer at the cursor value
            file.write(buffer);                                                             // Writing a block of information from the buffer to the file
            numOfPages += ((cursor / page_size) > numOfPages) ? 1 : 0;                      // Increasing the number of pages by 1 if we write to a new page
            cursor += page_size;                                                            // Increasing the value of the cursor by 1 page
            numOfWrites += 1;                                                               // Increasing the number of file accesses by 1 (Writes)
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int writeNextBlock(){                                                            // Method for writing a block from buffer to the file at the next block
        try {
            file.seek(cursor);                                                              // Moving the file pointer at the cursor value
            file.write(buffer);                                                             // Writing a block of information from the buffer to the file
            numOfPages += ((cursor / page_size) > numOfPages) ? 1 : 0;                      // Increasing the number of pages by 1 if we write to a new page
            cursor += page_size;                                                            // Increasing the value of the cursor by 1 page
            numOfWrites += 1;                                                               // Increasing the number of file accesses by 1 (Writes)
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int appendBlock() {                                                              // Method for writing a block from the buffer at the end of the file
        cursor = numOfPages * page_size + page_size;                                        // Changing the cursor variable to the correct one
        try {
            file.seek(cursor);                                                              // Moving the file pointer at the cursor value
            file.write(buffer);                                                             // Writing a block of information from the buffer to the file
            cursor += page_size;                                                            // Increasing the value of the cursor by 1 page
            numOfWrites += 1;                                                               // Increasing the number of file accesses by 1 (Writes)
            numOfPages += 1;                                                                // Increasing the number of pages by 1
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int deleteBlock(int position) {                                                  // Method for deleting a block
        if(position > numOfPages) {                                                         // Checking if there is not a block at the given position
            System.out.println("Invalid position");                                         // Printing the error message
            return 0;                                                                       // Returning 0 if there was an error
        }
        if(position < numOfPages) {                                                         // Checking if the block is not the last one
            readBlock(numOfPages);                                                          // We read the last block to the buffer
            writeBlock(position);                                                           // We write the last block from the buffer to the given position
        }

        numOfPages -= 1;                                                                    // Even if the block is the last block of the file, we reduce the number of pages by 1
        try {
            file.setLength((long)numOfPages*page_size + page_size);                         // Setting the length of the file to be one block less (adding one page because numOfPages doesn't include page zero
            FileHandle();                                                                   // Updating the first page
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public int closeFile() {                                                                // Method for closing the file we are handling
        FileHandle();                                                                       // Updating the first page
        try {
            file.close();                                                                   // Closing the file
            return 1;                                                                       // Returning 1 if everything went ok
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return 0;                                                                       // Returning 0 if there was an error
        }
    }

    public void printInfoBlock(){                                                           // Method for printing the information that is stored in page zero of the file in a nice format
        byte[] ReadDataPage = new byte[page_size];                                          // Declaring ReadDataPage
        try {
            FileHandle();                                                                   // Updating the page zero of the file
            file.seek(0);                                                                   // Moving the cursor to page zero
            file.read(ReadDataPage);                                                        // Reading a block of data from the file
        } catch (IOException e) {                                                           // Catch in case the there was an error handling the file
            System.out.println("Error handling the file");                                  // Printing the error message
            e.printStackTrace();                                                            // Printing the exception message
            return;                                                                         // Returning if there was an error
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);                  // Declaring ByteArrayInputStream
        DataInputStream din = new DataInputStream(bis);                                     // Declaring DataInputStream
        byte[] strBuf = new byte[buf_size1];                                                // Declaring strBuf byte array
        byte[] strBuf2 = new byte[buf_size2];                                               // Declaring strBuf byte array
        try {
            din.read(strBuf);
            System.out.println();
            System.out.println("|-------------------------------------------------------------------------------------------------------");
            System.out.println("| Name of file: " + new String(strBuf));
            System.out.println("| Number of pages: " + din.readInt());
            System.out.println("| -Creating the file");
            System.out.println("| Number of read : " + din.readInt());
            System.out.println("| Number of writes: " + din.readInt());
            System.out.println("| -Sorting the file");
            System.out.println("| Number of read (Sorting): " + din.readInt());
            System.out.println("| Number of writes (Sorting): " + din.readInt());
            System.out.println("| -Searching for the keys");
            System.out.println("| Number of Reads performed (in average): " + din.readInt());
            din.read(strBuf2);
            System.out.println("| Type: " + new String(strBuf2));
            System.out.println("|-------------------------------------------------------------------------------------------------------");
        } catch (IOException e) {
            System.out.println("Error handling the file");                                                  // Printing the error message
            e.printStackTrace();                                                                            // Printing the exception message
            return;                                                                                         // Returning if there was an error
        }
    }

    public int getRandomKeyFromList(List<Integer> usedKeys, List<Integer> used){                            // Method for getting a random key from (Duplicate from Method_A)
        Random rand = new Random();                                                                         // Declaring the random variable
        while(true) {
            int randomKey = usedKeys.get(rand.nextInt(usedKeys.size()));                                    // Getting a random key from the list of keys
            if (!used.contains(randomKey)) {                                                                // Checking if the key has been used before
                used.add(randomKey);                                                                        // If we haven't used the key before, we add it to the new list
                return randomKey;                                                                           // Returning the new key so it can be searched
            }
        }
    }

}
