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

For example, we added wug.txt for addition, then committed (see above images for reference), we should be able to see the sha-ID by searching up the specific commit message we entered, in this case I typed "added wug".

<a href="https://imgbb.com/"><img src="https://i.ibb.co/SfyyNYd/find.jpg" alt="find" border="0"></a></br>





