#include "hashtable.h"
#include <stdlib.h>
#include <stdio.h>

/*
 * This creates a new hash table of the specified size and with
 * the given hash function and comparison function.
 */
HashTable *createHashTable(int size, unsigned int (*hashFunction)(void *),
                           int (*equalFunction)(void *, void *)) {
  int i = 0;
  HashTable *newTable = malloc(sizeof(HashTable));
  if (NULL == newTable) {
    fprintf(stderr, "malloc failed \n");
    exit(1);
  }
  newTable->size = size;
  newTable->data = malloc(sizeof(HashBucket *) * size);
  if (NULL == newTable->data) {
    fprintf(stderr, "malloc failed \n");
    exit(1);
  }
  for (i = 0; i < size; i++) {
    newTable->data[i] = NULL;
  }
  newTable->hashFunction = hashFunction;
  newTable->equalFunction = equalFunction;
  return newTable;
}

/*
 * This inserts a key/data pair into a hash table.  To use this
 * to store strings, simply cast the char * to a void * (e.g., to store
 * the string referred to by the declaration char *string, you would
 * call insertData(someHashTable, (void *) string, (void *) string).
 */
void insertData(HashTable *table, void *key, void *data) {
  // -- TODO --
  // HINT:
  // 1. Find the right hash bucket location with table->hashFunction.
  // 2. Allocate a new hash bucket struct.
  // @source CS61C Lecture 4 Slides
  // 3. Append to the linked list or create it if it does not yet exist.
  /*Hash Bucket location determined with our function*/
  /*Divide by size of table @source CS61B HashTable slides*/
  unsigned int rightHashBucketLocation = table->hashFunction(key) % table->size;
  struct HashBucket *newHashBucket = (struct HashBucket *) malloc(sizeof(struct HashBucket));
 if (table->data[rightHashBucketLocation] == NULL) {
 	table->data[rightHashBucketLocation] = newHashBucket;
	newHashBucket->key = key;
	newHashBucket->data = data;
	newHashBucket->next = NULL;
 } else {
 	struct HashBucket *iterate = table->data[rightHashBucketLocation];
	while (iterate->next != NULL) {
		iterate = iterate->next;
	}
	iterate->next = newHashBucket;
	newHashBucket->key = key;
	newHashBucket->data = data;
	newHashBucket->next = NULL;
 	}	 
}

/*
 * This returns the corresponding data for a given key.
 * It returns NULL if the key is not found. 
 */
void *findData(HashTable *table, void *key) {
  // -- TODO --
  // HINT:
  // 1. Find the right hash bucket with table->hashFunction.
  // 2. Walk the linked list and check for equality with table->equalFunction.
  unsigned int rightHashBucketLocation = table->hashFunction(key) % table->size;
  struct HashBucket *traverse = table->data[rightHashBucketLocation];
  while (traverse->next != NULL) {
  	if (table->equalFunction(key, traverse->key)) {
		return traverse->data;
	}
	traverse = traverse->next;
  }
  return NULL;
}
