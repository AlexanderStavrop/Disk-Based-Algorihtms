# FileHandler
## Measurements
A program dedicated to measure the diffrence between the number of disc accesses required to find random keys. The keys and the corresponding data are stored 
in four different ways. The data from the files is retrieved in pages of 128 bit.

### Method A
- A record is created containing a key (4 bits) and a character (28 bits), thus in every page there are 4 records.
- The keys are stored in a random order and so the search is sereal, reading one block at the time.
- The theroretical complexity of serial search is O(n).

### Method B
- A record is created containing a key (4 bits) and an index pointing to the page where the corresponding data is stored (4 bits), thus in every paga there 
are 16 records.
- The keys are stored in a random order again but due to the larger number of keys in each page, the search is much more efficient.
- The theoretical complextiy of serial search is O(n).

### Method C
- The file of method A is sorted using the MergeSort sorting algorithm.
- The keys are searched using the BinarySearch search algorithm.
- The theoretical complexity of BinarySearch is O(logn).

### Method D
- The file of method D is sorted using the MergeSort sorting algorithm.
- The keys are searched using the BinarySearch search algorithm.
- The theoretical complexity of BinarySearch is O(logn).

## Results
There are 10^4 random and unique keys created in range on 1 to 10^6. We search for 20 keys that exists in the file and find the average number of disk accesses is measured.
