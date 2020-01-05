## Heapsort (WIP)

## 1.Definition

Heap is an array object but logically viewed as a binary tree. This tree ‘s nodes are filled at all level except ones at the very bottom of the tree.

## Attributes:

   Length: number of elements in the array containing the heap
    
-   Heap-size: number of element in the heap.
    
-   Height of a node in heap: edges count from root to node.
    

## Type:

Max and min heap

-   Max: parent > children
    
-   Min: children < parent
    

## 2. Basic operation

## Traversing:

Given i as current index: (indexing from 1)

-   Find i ‘s parent: (i - i %2)/2
    
-   Find i ‘s left child: 2i
    
-   Find i ’s right child: 2i + 1
    

## Operation within data structure

   Heapify: maintain the property of the heap mentioned above
    
-   Build heap: turn unordered array into a heap liked array
    
-   Heapsort: given a heap, sort using heap ‘s property
    
-   Insert: Enter new element into heap maintaining heap property
    
-   Extract: take out the max (min) item of the heap maintaining heap property
    
-   Increase priority: set an item ‘s priority to new value maintaining heap property
    

## 2.1. Heapify (max - heap)

Given an item at index i. From i to the rest of array is not yet a max heap, but from i’s children max heap they are (Important condition) -> Make i also a max heap:

-   Between i, 2i and 2i + 1, find [largest].
    
-   If [largest] not i: exchange [largest] and i. Heapify again [largest]

Heapification can be done through recursion or loop. Loop should save more space.
    

### Space complexity 

 - #### Case recursive

Each recursive stack: 3 

Number of recursion: log(n)

1 + 3log(n) => O(logn)

 - #### Case iteration

log(1) TBD

### Running time:

Worst case happens when we need to heapify the tree to the very bottom of the heap. The recurrence from a n-element heap to 1 and k element heap ( with k the maximum number of elements the sub heap can have).

Demonstration:

k is maximized when n is the number of element of a half full heap (the left side of heap is one level higher than the right side)

=> left = k and right = (k - 1)/2 (provable)

Thus n = left + right + 1 (left side + right side + root node)

= (3k + 1)/2

=> k = (2n -1)/3

=> T(n) <= T(2n/3) + Θ(1)

According to master theorem:

a = 1, b = 3/2. logba = log3/21 = 0

f(n)=Θ(nlogba) = Θ(1) => case 2:

=> T(n) = O(lgn)

=> T(n) = O(h) with h height of the node being heapify

## 2.2. Build heap

Heapify from bottom to top, starting from node heap-size /2 to 1.

=> make sure always heapify a heaped tree.

### Running time: O(n)

h=0⌊lgn⌋⌈n2h+1⌉O(h)

-   H:height of a mini heap created by the traversing of nodes from bottom to root node=> h from 0 to lg(n). Since each heapification dependent on the height of current mini heap=> O(h)
    
-   ⌈n2h+1⌉: elements per level of the heap counted by n and h. Representing times to repeat the operation of heapify at certain level.
    

### Space complexity:
Input: n

Initial function stack: length + half length + i: 3

Recursive function stack of heapify functions:

i = 0⌊n2⌋(Space complexity of heapification) =i = 0⌊n2⌋O(1) ( if use iteration instead of recursion)

= O(n)

=> If heapify function is not called but directly executed inside the loop, stacks are not stored in memory => O(1)

## 2.3. Heapsort:

### Space complexity

Assume that heapify is defined inline inside the loop => stack is not stored and the array is not cloned

Space taken: i in outer loop, left, right and largest in heapify operation = 4

=> O(1)
### Runtime complexity

Given an unordered array A with values and priorities, sort using heap:

Build heap for A ( max heap for example)

Sort in place:

Throw the first (max) element to the end of array, decrease A length, heapify new A without the thrown away element and do again until A ‘s length reach 1 element.

=> O(nlogn)

## 3. Implementation:

 - Heapsorting is not widely used because good quicksort is more
   preferred.
   
 - Heapsort data structure is good for building priority queues

   

 - When heapsorting is more preferred:

   
   Alternatives of heapsort can be considered as quick sort and
   mergesort. Quick sort in practice is more widely used, but has worst
   case time complexity of n^2.
   
   Mergesort has the same worst case time complexity as heapsort, but
   takes extra memory (n)
   
   => Heapsort is used when stability is concerned more than speed.

# 4.Interview questions

## 4.1. Sorting a k-sorted array ( Max )

K-sorted array is a nearly sorted array with each element is falsely positioned for at most k index around itself.

Using priority queue:

Because k is given, we get k + 1 first items of the array and put in the heap. By extracting max this heap => surely gain max item of the whole value. Insert new item from array into the queue when finished. Repeat until the end.

## 4.2. Find a kth largest element in array

Only apply if k is smaller than n/2

Build a min priority queue with items are the kth first elements of the array.

For loop from the k +1 index til the end of array, check if current value is larger than the min of priority queue, if yes, replace the root with this current value and heapify the queue.

The final queue will contains kth largest item of the initial array inside it.

## 4.3. Convert max heap into min heap

Like building a new heap in linear time

## 4.4. Merge m sorted lists with different lengths but with total number of n element.

Build priority queue with M items containing first elements of M lists.

This item contains data about its source and its index in its source.

Iteration until the queue is exausted. In each loop, pop the max item, trace its source and its index then fill the found item into the result array. If its is not the end of its source, insert its next element into the heap.

## 4.5. Find the smallest range with at-least one element coming from each of m sorted lists

Algorithm is applicable for min and max heap.

Maintain queue of m first items of m lists. Iterate until one of the list has its final element inserted into the queue. Keep pop out the queue and inserting until the loop breaks.

The queue has the range needed .

## 4.6. External merge sort algorithm
TBD
