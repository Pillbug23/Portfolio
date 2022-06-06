# Gitlet
A custom version-control system similiar to git. Functionality includes intialziation of a version control system, adding and committing files, removing files, log information, checking out files, branching, and merging.

# Methods
* git init 

```
java gitlet.Main init
```

Creates a new version-control system in the current directory. It starts with with an initial commit, a single branch which points to the initial commit named "master". A stage directory is intialized here; think of it as a shopping cart holds items and empties it after it is checked out. Each commit is identified by its unique SHA-code, a 160-bit integer hash from any sequence of bytes; think of it as a unique code for each given commit. If a control system already exists, an error message will be printed.

<a href="https://imgbb.com/"><img src="https://i.ibb.co/k8rDBvw/init-intialized-already.jpg" alt="init-intialized-already" border="0"></a>
s
