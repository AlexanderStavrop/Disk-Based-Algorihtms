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

<br></br>
# Binary-and-Threaded-Binary-Trees-Performance

A program dedicated to test the performance of a binary search tree and a threaed binary search tree in comparisson with an ordered array. The performance is measured upon the assignments and the comparissons happening when inserting the keys and when search for them.

## Binary Search Tree (BST)
A binary search tree is created by modifying the code found in <a href="https://www.geeksforgeeks.org/binary-search-tree-data-structure/">This</a> tutorial.To represent the tree we use an Nx3 matrix, where N is the number of keys we want in our tree. 
  - The first column of the matrix has the key value.
  - The second column of the matrix has the pointers for the line where the left tree-leaf of the key is. 
  - The third column of the matrix has the pointers for the line where the right tree-leaf of the key is.


Every search starts from the trees root, and by comparing the key value to the current root, we search the left or the right sub-tree acordingly. If the key is not present in the tree, we get to an empty sub-tree (where the current root  has value equal to -1) and we start searching for the next key. 

The complexity of the BST is O(log2(N))

## Threaded Binary Search Tree (TBST)
A threaded binary search tree is created by modifying the code found in <a href="https://www.geeksforgeeks.org/threaded-binary-tree/">This</a> tutorial. To represent the tree we use an Nx5 matrix, where N is the number of keys we want in our tree.
  - The first three columns are the same as the binary search thee.
  - The forth column of the matrix has the left thread of the tree. 
  - The forth column of the matrix has the right thread of the tree.

The threads point to the next smaller(left thread) or the next bigger(right thread) key in case a sub-tree has no leafs.

The complexity of the TBST is O(log2(N))

## Sorted Array
An array in created where the keys are in ascending order.

For searching, we perform binary search, using the code found in <a href="hhttps://www.geeksforgeeks.org/binary-search/">This</a> tutorial.

## Results
- There are 10^5 random and unique keys inserted in the trees. For testing, we perforom 100  single key searches, 100 random ranged searches with range of [K, K+100] and 100 random ranged searches with range of [K, K+1000] and calculating the average number comparisons happend. For the single key searches, we check if the key is present in the tree and for the ranged searches, we search for the lower bound of the range and then perform inorder search until we find the uper bound of the range.

<br></br>
# Disk-Based-Bplus-Tree










