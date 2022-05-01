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








