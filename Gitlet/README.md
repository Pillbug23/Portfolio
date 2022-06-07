# Gitlet
A custom version-control system similiar to git. Functionality includes intialziation of a version control system, adding and committing files, removing files, log information, checking out files, branching, and merging.

# Methods
* git init 

```
java gitlet.Main init
```

Creates a new version-control system in the current directory. It starts with with an initial commit, a single branch which points to the initial commit named "master". A stage directory is intialized here; think of it as a shopping cart holds items and empties it after it is checked out. Each commit is identified by its unique SHA-code, a 160-bit integer hash from any sequence of bytes; think of it as a unique code for each given commit. 

<a href="https://imgbb.com/"><img src="https://i.ibb.co/RCM5DY8/intiailziation.jpg" alt="intiailziation" border="0"></a><br />

Gitlet stores copies of files and other metadata in our directory .gitlet.

If a control system already exists, an error message will be printed.

<a href="https://imgbb.com/"><img src="https://i.ibb.co/k8rDBvw/init-intialized-already.jpg" alt="init-intialized-already" border="0"></a><br/>


* git add 

```
java gitlet.Main add [file name]
```

Adds a copy of the file to the staging area. If the file is already staged for addition, it overwrites the previous entry with the new contents. 


* git commit

```
java gitlet.Main commit [message]
```

Each commit contains specific metadata information (the message entered when making a commit, the date, 
and references to parent commits, sha-ID. Each commit also contains references to blobs (saved contents of files, each being tracked in a different commit). A commit has the same file contents as its parents. Files that were staged for addition/removal (using git add or git rm) are those updates to the commit. The new commit becomes the "current" commit with a head pointer that points to it. The now previous commit is our current commits parent. The staging area is cleared.

* git log

```
java gitlet.Main log
```

Starting at the current head commit(our current commit), displays information about each commit back to the earliest commit (the initial one we created when first intiailizing our version control system using init. Each commit contains its id, date of the commit (besides our initial commit which is “The (Unix) Epoch” time), and the commit message.

<a href="https://imgbb.com/"><img src="https://i.ibb.co/BKL9v1h/log.jpg" alt="log" border="0"></a></br>


```
java gitlet.Main find [commit message]
```

Custom method, allows user to find out the ids of all commits that have the given commit message. Multiple commits will be printed out on separate lines. The given message must be in quotations, if no such commit exists an error message will be printed.

For example, we added wug.txt for addition, then committed (see above images for reference), we should be able to see the sha-ID by searching up the specific commit message we entered, in this case I typed "added wug". The id matches what we have committed earlier.

<a href="https://imgbb.com/"><img src="https://i.ibb.co/SfyyNYd/find.jpg" alt="find" border="0"></a></br>


* git rm

```
java gitlet.Main rm [file name]
```

Unstages a file as it was staged for addition. If the file is tracked (any files in the current commit), we stage for removal. 


* git status

```
java gitlet.Main status
```

Displays what branches exist, in which our current branch we are at represented with an "*". Shows which files are staged for addition or removal.

Example below shows one branch(our current branch master initialized using init), and a file staged for removal (Our current commit reference to files).

<a href="https://imgbb.com/"><img src="https://i.ibb.co/85bJMCk/status.jpg" alt="status" border="0"></a><br />


* git checkout

```
1.) java gitlet.Main checkout -- [file name]
2.) java gitlet.Main checkout [commit id] -- [file name]
3.) java gitlet.Main checkout [branch name]
```

There are three possible use-cases for checkout. 

1.) Takes the version of a file if it exists in our current commit and puts it in our working directory (our repository), and overwrities the version of the file of it is there.

2.) The same as 1, but takes a commit id instead.

3.) The same as 1, but with an additional effect that the given branch will be considered the new HEAD (or current branch). 


* git branch

```
java gitlet.Main branch [branch name]
```

Creates a new branch with the desired name. Our new branch will have the added feature of pointing to our head commit (current commit), but does not automatically switch over to our newly created branch.

Suppose we want to add new features to a given file. We create a new branch that points to the same commit our current branch does. We can switch to the new branch using java gitlet.Main checkout [new-branch], modify the files then add and commit again. Going back to our previous branch and modifying,adding, and comitting there will allow us this branch structure. Essentially we are given a new pointer, with one of them being the current pointer indicated by "*". Each time we modify and commit we actively add a new child commit to the active current commit, allowing us to have multiple children. Below shows a visual example:

<a href="https://ibb.co/fnfj5Xx"><img src="https://i.ibb.co/JyVD4cs/branched.png" alt="branched" border="0"></a>
<a href="https://ibb.co/09DfvmZ"><img src="https://i.ibb.co/6tn4Lrb/checkout-master.png" alt="checkout-master" border="0"></a>
<a href="https://ibb.co/yWPsHgz"><img src="https://i.ibb.co/86s81Py/commit-on-branch.png" alt="commit-on-branch" border="0"></a>
<a href="https://imgbb.com/"><img src="https://i.ibb.co/Ct7TYmr/just-switched-branch.png" alt="just-switched-branch" border="0"></a>
<a href="https://imgbb.com/"><img src="https://i.ibb.co/XDRh1Km/just-called-branch.png" alt="just-called-branch" border="0"></a>

* git rm-branch

```
java gitlet.Main rm-branch [branch name]
```

Deletes the branch with the specified name. Only deletes the pointer associated with the branch, commits created under branch remain. Allows us to delete pointers to branching paths, in turn making it impossible to reach the commits we would have created in our new branched path.

<a href="https://ibb.co/gVnhH1Y"><img src="https://i.ibb.co/8chtT3k/removed.jpg" alt="removed" border="0"></a>


* git reset

```
java gitlet.Main reset [commit id]
```

Checks out all the files tracked by the given commit, removes tracked files not present in that commit, and shifts our current branch HEAD back to that commit with the given id. 


