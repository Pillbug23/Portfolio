/*
 * Include the provided hash table library.
 */
#include "hashtable.h"

/*
 * Include the header file.
 */
#include "philphix.h"

/*
 * Standard IO and file routines.
 */
#include <stdio.h>

/*
 * General utility routines (including malloc()).
 */
#include <stdlib.h>

/*
 * Character utility routines.
 */
#include <ctype.h>

/*
 * String utility routines.
 */
#include <string.h>

/*
 * This hash table stores the dictionary.
 */
HashTable *dictionary;

/*
 * The MAIN routine.  You can safely print debugging information
 * to standard error (stderr) as shown and it will be ignored in 
 * the grading process.
 */
int main(int argc, char **argv) {
  if (argc != 2) {
    fprintf(stderr, "Specify a dictionary\n");
    return 1;
  }
  /*
   * Allocate a hash table to store the dictionary.
   */
  fprintf(stderr, "Creating hashtable\n");
  dictionary = createHashTable(0x61C, &stringHash, &stringEquals);

  fprintf(stderr, "Loading dictionary %s\n", argv[1]);
  readDictionary(argv[1]);
  fprintf(stderr, "Dictionary loaded\n");

  fprintf(stderr, "Processing stdin\n");
  processInput();

  /*
   * The MAIN function in C should always return 0 as a way of telling
   * whatever program invoked this that everything went OK.
   */
  return 0;
}

/*
 * This should hash a string to a bucket index.  void *s can be safely cast
 * to a char * (null terminated string)
 */
unsigned int stringHash(void *s) {
  // -- TODO --
  //fprintf(stderr, "need to implement stringHash\n");
  //Source on hash function provided ED/spec source cited
  //@source https://stackoverflow.com/questions/7666509/hash-function-for-string
  //Source of explanation on djb2 Hash function by Dan Bernstein
  //@source http://www.cse.yorku.ca/~oz/hash.html
  /* To suppress compiler warning until you implement this function, */
  /*Cast void safely to char*/
  char *z = (char*) s;
  int c;
  /*5381 is chosen because it is 1.) odd 2.) prime 3.) deficient */
  unsigned long hash = 5381;
  while ((c = *z++)) {
  	hash = ((hash << 5) + hash) + c;
  }
  return hash;
}

/*
 * This should return a nonzero value if the two strings are identical 
 * (case sensitive comparison) and 0 otherwise.
 */
int stringEquals(void *s1, void *s2) {
  // -- TODO --
  //fprintf(stderr, "You need to implement stringEquals");
  // Source to check if two strings are equal
  // @source https://stackoverflow.com/questions/14232990/comparing-two-strings-  // in-c
  // Source on explanation of how to cast void * to char*
  // @source https://stackoverflow.com/questions/7067927/how-do-you-convert-void-pointer-to-char-pointer-in-c
/* To suppress compiler warning until you implement this function */
  char *x = (char *)s1;
  char *y = (char *)s2;
  if (strcmp(x,y) == 0) {
  	return 1;
  } else {	
  	return 0;
  }
}

/*
 * This function should read in every word and replacement from the dictionary
 * and store it in the hash table.  You should first open the file specified,
 * then read the words one at a time and insert them into the dictionary.
 * Once the file is read in completely, return.  You will need to allocate
 * (using malloc()) space for each word.  As described in the spec, you
 * can initially assume that no word is longer than 60 characters.  However,
 * for the final bit of your grade, you cannot assumed that words have a bounded
 * length.  You CANNOT assume that the specified file exists.  If the file does
 * NOT exist, you should print some message to standard error and call exit(61)
 * to cleanly exit the program.
 */
void readDictionary(char *dictName) {
  // -- TODO --
  //fprintf(stderr, "You need to implement readDictionary\n");
  // How to open a specified file
  //@source https://www.programiz.com/c-programming/c-file-input-output
  // How to check if file exists
  // @source https://stackoverflow.com/questions/230062/whats-the-best-way-to-check-if-a-file-exists-in-c
  // Explanation of stderr
  // @source https://stackoverflow.com/questions/39002052/how-i-can-print-to-stderr-in-c
  // Explanation of scanf/fscanf and how to use it
  // @source https://www.tutorialspoint.com/c_standard_library/c_function_fscanf.htm
  // Explanation of how to exit end of file using EOF
  // @source   // How to check if file exists
  // @source https://stackoverflow.com/questions/230062/whats-the-best-way-to-check-if-a-file-exists-in-c
  // Explanation of stderr
  // @source https://stackoverflow.com/questions/39002052/how-i-can-print-to-stderr-in-c
  // Explanation of scanf/fscanf and how to use it
  // @source https://www.tutorialspoint.com/c_standard_library/c_function_fscanf.htm
  // Explanation of how to exit end of file using EOF
  // @source https://stackoverflow.com/questions/1428911/detecting-eof-in-c
/*Select the file you want to open, then select the opening mode r=reading*/
  FILE *filpoint;
  filpoint = fopen(dictName,"r");
  /*Checks if file exists or not*/
  if (filpoint == NULL){
        fprintf(stderr, "File no existo\n");
        exit(61);
  } else {
	/*Keeps track of length of word*/
	int i;
	/*Char length is 60 or less*/
	char name[60];
	/*Reads each word of a given file, assuming word doesnt exceed 60 char*/  	
	while (fscanf(filpoint,"%s", name) != EOF){
		int length = 0;
		for (i = 0; name[i] != '\0'; i++){
			length+= 1;
		}	
		/*Allocating space using malloc()*/
		/*Size of char is 1 so sizeof is not needed optional*/
		/*You need an extra + 1 to handle the null character at end*/
		char *allocateWord = (char*) malloc((length+1)*sizeof(char));
		/*Note we allocated space before we strcpy*/
		strcpy(allocateWord,name);
		insertData(dictionary,allocateWord,allocateWord);
	}
  }
}

/*
 * This should process standard input (stdin) and perform replacements as 
 * described by the replacement set then print either the original text or 
 * the replacement to standard output (stdout) as specified in the spec (e.g., 
 * if a replacement set of `taest test\n` was used and the string "this is 
 * a taest of  this-proGram" was given to stdin, the output to stdout should be 
 * "this is a test of  this-proGram").  All words should be checked
 * against the replacement set as they are input, then with all but the first
 * letter converted to lowercase, and finally with all letters converted
 * to lowercase.  Only if all 3 cases are not in the replacement set should 
 * it report the original word.
 *
 * Since we care about preserving whitespace and pass through all non alphabet
 * characters untouched, scanf() is probably insufficent (since it only considers
 * whitespace as breaking strings), meaning you will probably have
 * to get characters from stdin one at a time.
 *
 * Do note that even under the initial assumption that no word is longer than 60
 * characters, you may still encounter strings of non-alphabetic characters (e.g.,
 * numbers and punctuation) which are longer than 60 characters. Again, for the 
 * final bit of your grade, you cannot assume words have a bounded length.
 */
void processInput() {
  // -- TODO --
  //fprintf(stderr, "You need to implement processInput\n");
  // Source on explanation of stdin, stdout and how to use
  // @source https://www.tutorialspoint.com/cprogramming/c_input_output.htm
  // @source https://stackoverflow.com/questions/16430108/what-does-it-mean-to-write-to-stdout-in-c
  char c;
  int i = 0;
  int n;
  char name[60];
  int maxWordLength = 60;
}
