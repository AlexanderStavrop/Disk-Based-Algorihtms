public class main_class {
    public static void main(String[] args) {
        int numOfKeys = 10000;
        int page_size = 128;
        int numOfRuns = 20;
        int rec_size = 32;
        int key_size = 4;

        System.out.println("|-------------------------------------------------------------------------------------------------------");
        System.out.println("|                                     General Information                                               ");
        System.out.println("| Keys: " + key_size + " Byte integers");
        System.out.println("| Data: " + (rec_size - key_size) + " Byte strings");
        System.out.println("| Indexes: " + key_size + " Byte integers");
        System.out.println("| Number of keys: " + numOfKeys);
        System.out.println("| Number of random keys to find in file: " + numOfRuns);
        System.out.println("|-------------------------------------------------------------------------------------------------------");

//_________________________________________________ Method A ___________________________________________________________
        Method_A method_a = new Method_A(page_size, numOfKeys, numOfRuns, rec_size, key_size);
        method_a.createFile("Method_A");                                                        // Creating the file
        method_a.readMethod_A("Method_A");                                                      // Searching for the random keys
        method_a.printInfo();                                                                   // Printing the first page of the file
        method_a.endMethod();                                                                   // Closing the file

//_________________________________________________ Method B ___________________________________________________________
        Method_B method_b = new Method_B(page_size, numOfKeys, numOfRuns, method_a.getRec_size(), key_size, method_a.getListOfKeys(), method_a.getUsedKeys());
        method_b.createFile("Method_B");                                                        // Creating the file
        method_b.readMethod_B("Method_B","Method_A");                                           // Searching for the random key in the first file and searching with the index in the second file
        method_b.printInfo();                                                                   // Printing the first page of the file
        method_b.endMethod();                                                                   // Closing the file

//_________________________________________________ Method C ___________________________________________________________
        Method_C method_c = new Method_C(page_size, numOfKeys, numOfRuns, method_a.getRec_size(), key_size, method_a.getUsedKeys());
        method_c.sortFile("Method_A");                                                          // Sorting the file
        method_c.createFile("Method_C");                                                        // Creating the file
        method_c.readMethod_C("Method_C");                                                      // Searching for the random keys
        method_c.printInfo();                                                                   // Printing the first page of the file
        method_c.endMethod();                                                                   // Closing the file

//_________________________________________________ Method D ___________________________________________________________
        Method_D method_d = new Method_D(page_size, numOfKeys, numOfRuns, key_size,method_a.getRec_size(), method_a.getUsedKeys());
        method_d.sortFile("Method_B");                                                          // Sorting the file
        method_d.createFile("Method_D");                                                        // Creating the file
        method_d.readMethod_D("Method_D","Method_A");                                           // Searching for the random key in the first file and searching with the index in the second file
        method_d.printInfo();                                                                   // Printing the first page of the file
        method_d.endMethod();                                                                   // Closing the file
    }
}
