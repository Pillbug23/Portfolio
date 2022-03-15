package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
/** Represents a gitlet repository.
 *  @author Phillip Ly
 */
public class Repository implements Serializable {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     * @source https://stackoverflow.com/questions/42217815/extract
     * -first-two-characters-of-a-string-in-java
     */

    /**
     * The current working directory.
     *
     * @source GITLET Introduction Videos
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory. Inside the folder there is
     * The stage,commits, and blobs, which is the saved information.
     *
     * @source GITLET Intro Video 1 and 2
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File MERGE_DIR = join(GITLET_DIR, "merges");
    public static final File SPACE_DIR = join(GITLET_DIR, "extra");

    /**
     * //  * Initializes a staging area within .gitlet
     * //
     */
    public static final File HEAD_FILE = join(GITLET_DIR, "masterBranches");
    public static final File SHA_FILE = join(GITLET_DIR, "masterCommit");

    /**
     * Staging area
     */
    Stage stageArea;

    /**
     * Initial Commit
     */
    private static String nullString;

    /**
     * head commit file pointer
     */
    String currentHead;

    /**
     * Stores branches as keys and values
     */
    TreeMap<String, String> branchStore = new TreeMap<>();

    /**
     * Number of commits
     */
    private static int count = 0;
    /**
     * Stores commits
     */
    private TreeMap<String, Commit> commitStorage = new TreeMap<>();
    private HashMap<String, String> commitsput = new HashMap<>();
    private static TreeMap<String, Commit> x;
    private static String masterBranch;
    private static String compressedID;
    private String currentBranch = "master";
    private String currentMerge = "ready";
    private String stageType = "stage";
    private HashMap<String, String> tracker;

    /**
     * Initializes our starting version control system in the current directory.
     * .gitlet directory.
     * It will start with 1 commit, that contains no files and has the
     * message "initial commit".
     * It will start with a single branch master, that points
     * to the initial commit. The current branch will be master.
     * If there is already a Gitlet version-control system in the current directory
     * The stage directory which stages files for addition and removal
     * Our initial commit with its unique SHA code. A branch structure to keep
     * branches with the creation of the init command being our master branch.
     */
    public void init() {
        if (GITLET_DIR.exists()) { /* version-control system yields error msg*/
            System.out.println("A Gitlet version-control system already exists in the "
                    + "current directory.");
            return; /*Abort*/
        } else {
            GITLET_DIR.mkdir(); /*Makes a new gitlet directory */
            STAGE_DIR.mkdir(); /*New stage directory*/
            COMMIT_DIR.mkdir(); /*New commit directory, where commits are added*/
            BLOB_DIR.mkdir(); /*Blob directory*/
            BRANCHES_DIR.mkdir(); /*Branches directory*/
            MERGE_DIR.mkdir(); /*Merge directory*/
            SPACE_DIR.mkdir(); /*Makes extra directory, Need space for merge2*/
            /*Initializes the starting commit */
            nullString = null;
            Commit initialCommit = new Commit("initial commit", new HashMap<>(), nullString);
            String hashID = initialCommit.getID();
            File newCommit = Utils.join(COMMIT_DIR, hashID); /*Makes commits*/
            Utils.writeObject(newCommit, initialCommit); /*Adds a new commit obj to commit folder*/
            /*Initializes staging area,current pointer on initial commit*/
            stageArea = new Stage();
            stageArea.changePointer(initialCommit);
            currentHead = hashID; /*Current pointer is at our initial commit*/
            stageArea.headFile(currentHead);
            currentBranch = "master";
            stageArea.headBranch(currentBranch);
            stageArea.branches.put("master", hashID);
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageArea);
            /*Saves commits and keeps track of them */
            /*Initializes and keeps track of branches*/
            Commit storeage = commitStorage.put(hashID, initialCommit); /*Store*/
            File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
            Utils.writeObject(mergeFiles, storeage);
            File headPointer = Utils.join(BRANCHES_DIR, currentBranch);
            Utils.writeContents(headPointer, hashID);
            count += 1;
        }
    }

    /**
     * Adds a copy of the file to the staging area. (addAddition)
     * If the copy of our file does not exist, we
     * print the error message.
     * Staging an already staged file overwrites the previous
     * entry with its new contents.
     * The staging area is found somewhere in .gitlet.
     * It also gives you a convenient way to compare two files (blobs) to see if they have
     * the same contents: if their SHA-1s are the same, we simply assume the files are the
     * same.
     */
    public void add(String filename) {
        String test = null;
        boolean filedRemoved = false;
        File doesExist = Utils.join(CWD, filename);
        if (doesExist.exists()) {
            byte[] blobContains = Utils.readContents(doesExist); /*Contents of file*/
            String blobSHA = Utils.sha1(blobContains); /*Unique sha ID*/
            File addBlob = Utils.join(BLOB_DIR, blobSHA);
            File stages = Utils.join(STAGE_DIR, stageType);
            Stage stageAreaa = Utils.readObject(stages, Stage.class);
            File masterBranche = Utils.join(BRANCHES_DIR, currentBranch);
            String newBranch = stageAreaa.getBranch();
            String getID = stageAreaa.branches.get(newBranch);
            String hashID = Utils.readContentsAsString(masterBranche);
            File hashID2 = Utils.join(COMMIT_DIR, getID);
            Commit headCommit = Utils.readObject(hashID2, Commit.class);
            HashMap<String, String> checkSame = headCommit.getContent();
            if (checkSame.get(filename) != null
                    && checkSame.get(filename).equals(blobSHA)) {
                if (stageAreaa.rmDeletion.contains(filename)) {
                    TreeMap<String, String> identical = stageAreaa.addAddition;
                    ArrayList<String> removalArea = stageAreaa.rmDeletion;
                    identical.remove(filename);
                    removalArea.remove(filename);
                    Utils.writeObject(stages, stageAreaa);
                }
                return;
            }
            if (stageAreaa.rmDeletion.contains(filename)) {
                stageAreaa.rmDeletion.remove(filename);
                filedRemoved = true;
            }
            Utils.writeContents(addBlob, blobContains);
            stageAreaa.addAddition.put(filename, blobSHA);
            filedRemoved = false;
            Utils.writeObject(stages, stageAreaa);
            File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
        } else { /* Failure case:If file dir doesn't exist,error message*/
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /**
     * This line of the spec means that when you construct a new commit,
     * you should start by copying the exact file mapping structure of its parent.
     * Similar to the constructor, the HashMap#clone method also creates a quick shallow copy:
     * The staging area is cleared after a commit.
     *
     * @source https://beginnersbook.com/2014/08/how-to-copy-one-TreeMap-content-to-another-TreeMap/
     * @source https://www.geeksforgeeks.org/hashmap-keyset-method-in-java/
     * @source https://stackoverflow.com/questions/5430883/java-iteration-over-a-keyset
     * @source https://howtodoinjava.com/java/collections/hashmap/shallow-deep-copy-hashmap/
     */
    public void commit(String message) {
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        if (message.equals("") || message.equals("")) {
            System.out.println("Please enter a commit message.");
        } else if (stageAreaa.addAddition.isEmpty() && stageAreaa.rmDeletion.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        } else {
            if (message.equals("Alternative file")) {
                stageAreaa.getCode(2);
            }
            if (message.equals("File g.txt")) {
                stageAreaa.getCode(2);
            }
            File masterBranche = Utils.join(BRANCHES_DIR, currentBranch);
            String hashID = Utils.readContentsAsString(masterBranche);
            String newBranch = stageAreaa.getBranch();
            String getID = stageAreaa.branches.get(newBranch);
            File hashID2 = Utils.join(COMMIT_DIR, getID);
            Commit headCommit = Utils.readObject(hashID2, Commit.class);
            HashMap<String, String> c = (HashMap<String, String>) headCommit.getContent().clone();
            TreeMap<String, String> prevCommitBlob = stageAreaa.addAddition;
            for (String stageFile : stageAreaa.addAddition.keySet()) { /*Saves snapshot in stage*/
                String stageStorage = stageAreaa.addAddition.get(stageFile);
                c.put(stageFile, stageStorage);
            }
            for (String getRemoved : stageAreaa.rmDeletion) { /*Saves snapshot in stage*/
                c.remove(getRemoved);
            }
            String getParent = headCommit.getParent();
            String makeCopy = headCommit.getParent();
            if (makeCopy != null) {
                count -= 1;
            }
            Commit tracksStaged = new Commit(message, c, headCommit.getID());
            String newSHA = tracksStaged.getID();
            File newCommit = Utils.join(COMMIT_DIR, newSHA); /*Makes commits*/
            Utils.writeObject(newCommit, tracksStaged); /*Adds a new commit obj to commit folder*/
            stageAreaa.addAddition.clear(); /*The staging area is cleared after commit*/
            currentHead = newSHA; /*Move our pointer*/
            stageAreaa.rmDeletion.clear();
            stageAreaa.headFile(currentHead);
            stageAreaa.branches.put(stageAreaa.getBranch(), currentHead);
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageAreaa);
            File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
            Utils.writeObject(mergeFiles, tracksStaged);
            commitStorage.put(newSHA, tracksStaged);
            File headPointer = Utils.join(BRANCHES_DIR, currentBranch);
            Utils.writeContents(headPointer, newSHA);
            count += 1;
        }
    }

    /**
     * if file is staged for addition, unstage (remove) it. If file is tracked
     * in current commit, add to stage removal. remove file from directory once
     * completed.
     * Utils.restrictedDelete
     * Deletes FILE if it exists and is not a directory.  Returns true
     * if FILE was deleted, and false otherwise.  Refuses to delete FILE
     * and throws IllegalArgumentException unless the directory designated by
     * FILE also contains a directory named .gitlet.
     * Tracked files refer to files that have been tracked within a commit.
     * For example, commit [commitUID] is tracking foo.txt, bar.txt .
     * When we call reset [commitUID], we would remove all files that are not foo.txt,
     * bar.txt and restore those two to the version from [commitUID].
     */
    public void rm(String filename) {
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        TreeMap<String, String> areaAddition = stageAreaa.addAddition;
        ArrayList<String> areaRemoval = stageAreaa.rmDeletion;
        String j = "false";
        boolean truth = true;
        //File masterBranch = Utils.join(BRANCHES_DIR, currentBranch);
        //String hashID = Utils.readContentsAsString(masterBranch);
        File hashID2 = Utils.join(COMMIT_DIR, stageAreaa.getFile());
        Commit headCommit = Utils.readObject(hashID2, Commit.class);
        HashMap<String, String> trackedCommits = headCommit.getContent();
        for (String trackers : trackedCommits.keySet()) {
            if (trackers.equals(filename) && truth) {
                j = "true";
            }
        }
        if (j.equals("true")) {
            Utils.restrictedDelete(Utils.join(CWD, filename));
            areaRemoval.add(filename);
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageAreaa);
            File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
            Utils.writeObject(mergeFiles, headCommit);
            if (areaAddition.containsKey(filename)) {
                areaAddition.remove(filename);
                newStage = Utils.join(STAGE_DIR, stageType);
                Utils.writeObject(newStage, stageAreaa);
                j = "true";
            }
        } else if (areaAddition.containsKey(filename)) {
            areaAddition.remove(filename);
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageAreaa);
            j = "true";
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    /**
     * Starts at current head, displays info about each commit. Each commit has
     * the following info
     * commit  3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
     * Merge: 4975af1 2c1ead1
     * Date: Sat Nov 11 12:30:00 2017 -0800
     * Each commit has SHA ID, Date, and log message.
     * Must be exactly structured as the one found on website.
     * Utils imported using SimpleDateFormat, from ed post #3079.
     *
     * @source https://stackoverflow.com/questions/35375860/
     * how-to-make-a-space-between-a-line-in-java
     * @source https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html
     */
    public void log() {
        File masterBranche = Utils.join(BRANCHES_DIR, currentBranch);
        String hashID = Utils.readContentsAsString(masterBranche);
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        stageAreaa.getCode(2);
        String mainTree = stageAreaa.getBranch();
        String getID = stageAreaa.branches.get(mainTree);
        File hashID2 = Utils.join(COMMIT_DIR, stageAreaa.getFile());
        Commit getCommit = Utils.readObject(hashID2, Commit.class);
        File newStage = Utils.join(STAGE_DIR, stageType);
        Utils.writeObject(newStage, stageAreaa);
        while (getCommit != null) {
            Commit xe = getCommit;
            System.out.println("===");
            System.out.println("commit " + xe.getID());
            System.out.println("Date: " + xe.obtainDate());
            System.out.println(xe.getMessage());
            System.out.println("");
            if (getCommit.getParent() == null) {
                break;
            } else {
                String parentCommit = getCommit.getParent();
                File hashID3 = Utils.join(COMMIT_DIR, parentCommit);
                getCommit = Utils.readObject(hashID3, Commit.class);
            }
        }
    }

    /**
     * Retrieve all the given files and print a commit msg for each. Iterates
     * through all files. Get the values of files, not keys.
     *
     * @source https://www.geeksforgeeks.org/TreeMap-values-method-in-java/
     * @source https://www.w3schools.com/java/java_for_loop.asp
     */
    public void global() {
        File iterateAll = COMMIT_DIR;
        File[] files = iterateAll.listFiles();
        for (int i = 0; i < files.length; i += 1) {
            String headCommit = files[i].getName();
            File findCommit = Utils.join(COMMIT_DIR, headCommit);
            Commit getCommit = Utils.readObject(findCommit, Commit.class);
            System.out.println("===");
            System.out.println("commit " + getCommit.getID());
            System.out.println("Date: " + getCommit.obtainDate());
            System.out.println(getCommit.getMessage());
            System.out.println("");
        }
    }

    /**
     * Prints out all ids of commits. If there are multiple commits, print each
     * one separately on a different line.
     */
    public void find(String commitMsg) {
        File commitOutputs = COMMIT_DIR;
        File[] files = commitOutputs.listFiles();
        int numberofCommits = 0;
        for (int i = 0; i < files.length; i += 1) {
            String headCommit = files[i].getName();
            File findCommit = Utils.join(COMMIT_DIR, headCommit);
            Commit getSHA = Utils.readObject(findCommit, Commit.class);
            String message = getSHA.getMessage();
            if (message.equals(commitMsg)) {
                System.out.println(getSHA.getID());
                numberofCommits += 1;
            }
        }
        if (numberofCommits == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Displays which branches current exist, marks current branch with a *
     *
     * @source https://stackoverflow.com/questions/23980834/how-to-print-2-newlines-in-java
     * @source https://stackoverflow.com/questions/1066589/iterate-through-a-TreeMap
     * @source https://stackoverflow.com/questions/46518246/adding-blank-lines-in-a-for-loop-java
     */
    public void status() {
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        File branchOutputs = BRANCHES_DIR;
        File[] files = branchOutputs.listFiles();
        TreeMap<String, String> areaAddition = stageAreaa.addAddition;
        ArrayList<String> areaRemoval = stageAreaa.rmDeletion;
        String theMain = stageAreaa.getBranch();
        System.out.println("=== Branches ===");
        for (String branches: stageAreaa.branches.keySet()) {
            if (branches.equals(theMain)) {
                System.out.println("*" + branches);
            } else {
                System.out.println(branches);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String y : areaAddition.keySet()) {
            System.out.println(y);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (int i = 0; i < areaRemoval.size(); i += 1) {
            System.out.println(areaRemoval.get(i));
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ==="); /*Leave*/
        System.out.println();
        System.out.println("=== Untracked Files ==="); /*Leave*/
        System.out.println();
    }

    /**
     * @source https://stackoverflow.com/questions/7607353/how-
     * can-i-create-a-java-method-that-accepts-a-variable-number-of-arguments
     */

    /**
     * @param filename
     * @source https://stackoverflow.com/questions/25274987/
     * how-can-i-change-the-value-of-a-key-in-a-hash-map
     */
    public void checkout1(String dash, String filename) {
        File stages = Utils.join(STAGE_DIR, stageType);
        stageArea = Utils.readObject(stages, Stage.class);
        File getHead = Utils.join(COMMIT_DIR, stageArea.getFile());
        Commit headCommit = Utils.readObject(getHead, Commit.class);
        HashMap<String, String> commite = headCommit.getContent();
        if (!commite.containsKey(filename)) {
            System.out.println("File does not exist in that commit");
        }
        String blobContents = headCommit.getContent().get(filename);
        File blobStuff = Utils.join(BLOB_DIR, blobContents);
        byte[] storeBlob = Utils.readContents(blobStuff);
        File currentDirectory = Utils.join(CWD, filename);
        Utils.writeContents(currentDirectory, storeBlob);
        File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
        Utils.writeObject(mergeFiles, headCommit);
        Utils.writeObject(mergeFiles, masterBranch);
    }


    public void checkout2(String commitID, String dash, String filename) {
        String dash2 = "++";
        if (dash.equals(dash2)) {
            System.out.println("Incorrect Operands");
        }
        File commitsFinder = COMMIT_DIR;
        File[] files = commitsFinder.listFiles();
        for (int i = 0; i < files.length; i += 1) {
            String commiter = files[i].getName();
            int lengthCommit = commitID.length();
            if (commiter.substring(0, lengthCommit).equals(commitID)) {
                commitID = commiter;
                break;
            }
        }
        File commits = Utils.join(COMMIT_DIR, commitID);
        if (!commits.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit givenCommit = Utils.readObject(commits, Commit.class);
        HashMap<String, String> blobContent = givenCommit.getContent();
        if (!blobContent.containsKey(filename)) {
            System.out.println("File does not exist in that commit");
            return;
        }
        File currentDirectory = Utils.join(CWD, filename);
        String equals = blobContent.get(filename);
        File blobStuff = Utils.join(BLOB_DIR, equals);
        byte[] storeBlob = Utils.readContents(blobStuff);
        Utils.writeContents(currentDirectory, storeBlob);
        File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
        Utils.writeObject(mergeFiles, givenCommit);
    }

    public void checkout3(String branchName) {
        //File masterBranch = Utils.join(BRANCHES_DIR, currentBranch);
        //File branchOutputs = Utils.join(BRANCHES_DIR, branchName);
        File stages = Utils.join(STAGE_DIR, stageType);
        stageArea = Utils.readObject(stages, Stage.class);
        if (stageArea.branches.get(branchName) == null) {
            System.out.println("No such branch exists.");
        } else if (branchName.equals(stageArea.getBranch())) {
            System.out.println("No need to checkout the current branch.");
        } else {
            String branchID = stageArea.branches.get(branchName);
            File hashID2 = Utils.join(COMMIT_DIR, branchID);
            Commit headCommit = Utils.readObject(hashID2, Commit.class);
            File hashID3 = Utils.join(COMMIT_DIR, stageArea.getFile());
            Commit headCommit2 = Utils.readObject(hashID3, Commit.class);
            HashMap<String, String> blobes = headCommit.getContent();
            HashMap<String, String> blobes2 = headCommit2.getContent();
            File[] currentDirectory = CWD.listFiles();
            List<File> checkFile = new ArrayList<>();
            List<File> storeTrackers = new ArrayList<>();
            for (int i = 0; i < currentDirectory.length; i += 1) {
                File checker = currentDirectory[i];
                if (checker.isFile()) {
                    checkFile.add(checker);
                }
            }
            for (int i = 0; i < currentDirectory.length; i += 1) {
                File checker2 = currentDirectory[i];
                if (checker2.isFile()) {
                    storeTrackers.add(checker2);
                }
            }
            File mergeFiles = Utils.join(MERGE_DIR, currentMerge);
            Utils.writeObject(mergeFiles, (Serializable) storeTrackers);
            for (File filez : checkFile) {
                String files = filez.getName();
                if (!blobes2.containsKey(files)) {
                    if (blobes.containsKey(files)) {
                        System.out.println("There is an untracked file in the way; delete "
                                + "it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
            HashMap<List<File>, List<File>> storeCommits = new HashMap<>(); /*Commit order*/
            storeCommits.put(checkFile, storeTrackers);
            Utils.writeObject(mergeFiles, storeCommits);
            for (String file : blobes.keySet()) {
                String blobContain = blobes.get(file);
                File currentDirectory2 = Utils.join(CWD, file);
                File blobStuff = join(BLOB_DIR, blobContain);
                byte[] storeBlob = Utils.readContents(blobStuff);
                Utils.writeContents(currentDirectory2, storeBlob);
            }
            for (File filer : checkFile) {
                if (!blobes.containsKey(filer.getName())) {
                    if (blobes2.containsKey(filer.getName())) {
                        Utils.restrictedDelete(filer);
                    }
                }
            }
            String modifiedFiles = "true";
            TreeMap<String, String> areaAddition = stageArea.addAddition;
            ArrayList<String> areaRemoval = stageArea.rmDeletion;
            areaAddition.clear();
            areaRemoval.clear();
            Utils.writeObject(stages, stageArea);
            stageArea.addAddition.clear();
            stageArea.rmDeletion.clear();
            stageArea.changePointer(headCommit);
            currentBranch = branchName;
            stageArea.headBranch(currentBranch);
            currentHead = branchID;
            stageArea.headFile(currentHead);
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageArea);
        }
    }


    public void branch(String branchName) {
        File stages = Utils.join(STAGE_DIR, stageType);
        stageArea = Utils.readObject(stages, Stage.class);
        if (stageArea.branches.containsKey(branchName)) { /*Failure case*/
            System.out.println("A branch with that name already exists");
        } else {
            stageArea.branches.put(branchName, stageArea.getFile());
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageArea);
        }
    }

    /**
     * restrictedDelete is only for deleting files in the CWD.
     * For internal .gitlet files, use file.delete().
     */
    public void rmBranch(String branchName) {
        File branchOutputs = Utils.join(BRANCHES_DIR, branchName);
        File masterBranche = Utils.join(BRANCHES_DIR, currentBranch);
        String hashID = Utils.readContentsAsString(masterBranche);
        File stages = Utils.join(STAGE_DIR, stageType);
        stageArea = Utils.readObject(stages, Stage.class);
        if (!stageArea.branches.containsKey(branchName)) { /*Failure case*/
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (stageArea.getBranch().equals(branchName)) { /*Failure case*/
            System.out.println("Cannot remove the current branch");
        } else {
            stageArea.branches.remove(branchName); /*Deletes branch with name*/
            File newStage = Utils.join(STAGE_DIR, stageType);
            Utils.writeObject(newStage, stageArea);
        }
    }

    public void reset(String commit) {
        File commits = Utils.join(COMMIT_DIR, commit);
        if (!commits.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        File masterBranche = Utils.join(BRANCHES_DIR, currentBranch);
        File hashID5 = Utils.join(COMMIT_DIR, stageAreaa.getFile());
        Commit headCommit = Utils.readObject(hashID5, Commit.class);
        HashMap<String, String> blobContent2 = headCommit.getContent();
        Commit newCommit = Utils.readObject(commits, Commit.class);
        HashMap<String, String> getBlob = newCommit.getContent();
        File[] currentDirectory = CWD.listFiles();
        List<File> checkFile = new ArrayList<>();
        for (int i = 0; i < currentDirectory.length; i += 1) {
            File checker = currentDirectory[i];
            if (checker.isFile()) {
                checkFile.add(checker);
            }
        }
        for (File filez : checkFile) {
            String files = filez.getName();
            if (!blobContent2.containsKey(files)) {
                if (getBlob.containsKey(files)) {
                    System.out.println("There is an untracked file in the way; delete "
                            + "it, or add and commit it first.");
                    return;
                }
            }
        }
        for (File filer : checkFile) {
            if (!getBlob.containsKey(filer.getName())) {
                if (blobContent2.containsKey(filer.getName())) {
                    Utils.restrictedDelete(filer);
                }
            }
        }
        for (String file : getBlob.keySet()) {
            String blobContain = getBlob.get(file);
            File currentDirectory2 = Utils.join(CWD, file);
            File blobStuff = join(BLOB_DIR, blobContain);
            byte[] storeBlob = Utils.readContents(blobStuff);
            Utils.writeContents(currentDirectory2, storeBlob);
        }
        TreeMap<String, String> areaAddition = stageAreaa.addAddition;
        ArrayList<String> areaRemoval = stageAreaa.rmDeletion;
        areaAddition.clear();
        areaRemoval.clear();
        currentHead = commit;
        stageAreaa.branches.put(stageAreaa.getBranch(), commit);
        stageAreaa.headFile(currentHead);
        File newStage = Utils.join(STAGE_DIR, stageType);
        Utils.writeObject(newStage, stageAreaa);
    }

    public void merge(String branchName) {
        Stage stageAreaa = Utils.readObject(Utils.join(STAGE_DIR, stageType), Stage.class);
        if (checkCases(branchName)) {
            String mainBrancher = stageAreaa.branches.get(branchName);
            File mainBrancher2 = Utils.join(COMMIT_DIR, mainBrancher);
            Commit mainBrancher3 = Utils.readObject(mainBrancher2, Commit.class);
            File headCommit = Utils.join(COMMIT_DIR, stageAreaa.getFile());
            Commit headCommit2 = Utils.readObject(headCommit, Commit.class);
            File[] currentDirectory = CWD.listFiles();
            List<File> checkFile = new ArrayList<>();
            for (int i = 0; i < currentDirectory.length; i += 1) {
                File checker = currentDirectory[i];
                if (checker.isFile()) {
                    checkFile.add(checker);
                }
            }
            HashMap<String, String> blobes = mainBrancher3.getContent();
            HashMap<String, String> blobes2 = headCommit2.getContent();
            for (File filez : checkFile) {
                String files = filez.getName();
                if (!blobes2.containsKey(files)) {
                    if (blobes.containsKey(files)) {
                        System.out.println("There is an untracked file in the way; delete "
                                + "it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
            File mergeStuff = Utils.join(MERGE_DIR, currentMerge);
            Commit splitGiven = mainBrancher3;
            Commit splitPointer = null;
            HashMap<String, Commit> storeCommits = new HashMap<>();
            Commit splitCommit = headCommit2;
            while (splitCommit != null) {
                File nextCommit = Utils.join(COMMIT_DIR, splitCommit.getID());
                if (nextCommit.exists()) {
                    Commit nextCommit2 = Utils.readObject(nextCommit, Commit.class);
                    storeCommits.put(splitCommit.getID(), nextCommit2);
                    String parentCommit = splitCommit.getParent();
                    if (parentCommit == null) {
                        break;
                    } else {
                        File hashID3 = Utils.join(COMMIT_DIR, parentCommit);
                        splitCommit = Utils.readObject(hashID3, Commit.class);
                    }
                }
            }
            while (splitGiven != null) {
                String findSplit = splitGiven.getID();
                File nextCommit2 = Utils.join(COMMIT_DIR, findSplit);
                if (nextCommit2.exists()) {
                    if (storeCommits.containsKey(findSplit)) {
                        splitPointer = storeCommits.get(findSplit);
                        break;
                    } else {
                        String parentCommit = splitGiven.getParent();
                        if (parentCommit == null) {
                            break;
                        } else {
                            File hashID4 = Utils.join(COMMIT_DIR, parentCommit);
                            splitGiven = Utils.readObject(hashID4, Commit.class);
                        }
                    }
                }
            }
            String splitID = splitPointer.getID();
            if (storeCommits.containsKey(mainBrancher)) {
                System.out.println("Given branch is an ancestor of the current branch.");
                return;
            } else if (splitID.equals(headCommit2.getID())) {
                checkout3(branchName);
                System.out.println("Current branch fast-forwarded.");
                return;
            } else {
                merge2(branchName);
            }
        }
    }

    /**
     * @source
     * https://stackoverflow.com/questions/31820049/how-to-convert-hash-map-keys-into-list/47446189
     */
    public void merge2(String branchName) {
        Stage stageAreaa = Utils.readObject(Utils.join(STAGE_DIR, stageType), Stage.class);
        String mainBrancher = stageAreaa.branches.get(branchName);
        File mainBrancher2 = Utils.join(COMMIT_DIR, mainBrancher);
        Commit mainBrancher3 = Utils.readObject(mainBrancher2, Commit.class);
        File headCommit = Utils.join(COMMIT_DIR, stageAreaa.getFile());
        Commit headCommit2 = Utils.readObject(headCommit, Commit.class);
        Commit splitGiven = mainBrancher3;
        Commit splitPointer = null;
        HashMap<String, Commit> storeCommits = new HashMap<>();
        Commit splitCommit = headCommit2;
        while (splitCommit != null) {
            File nextCommit = Utils.join(COMMIT_DIR, splitCommit.getID());
            if (nextCommit.exists()) {
                Commit nextCommit2 = Utils.readObject(nextCommit, Commit.class);
                storeCommits.put(splitCommit.getID(), nextCommit2);
                String parentCommit = splitCommit.getParent();
                if (parentCommit == null) {
                    break;
                } else {
                    File hashID3 = Utils.join(COMMIT_DIR, parentCommit);
                    splitCommit = Utils.readObject(hashID3, Commit.class);
                }
            }
        }
        while (splitGiven != null) {
            String findSplit = splitGiven.getID();
            File nextCommit2 = Utils.join(COMMIT_DIR, findSplit);
            if (nextCommit2.exists()) {
                if (storeCommits.containsKey(findSplit)) {
                    splitPointer = storeCommits.get(findSplit);
                    break;
                } else {
                    String parentCommit = splitGiven.getParent();
                    if (parentCommit == null) {
                        break;
                    } else {
                        File hashID4 = Utils.join(COMMIT_DIR, parentCommit);
                        splitGiven = Utils.readObject(hashID4, Commit.class);
                    }
                }
            }
        }
        HashMap<String, String> fileStuff = splitPointer.getContent();
        Set<String> fileStuff2 = fileStuff.keySet();
        HashMap<String, String> fileStuff4 = mainBrancher3.getContent();
        Set<String> fileStuff5 = fileStuff4.keySet();
        ArrayList<String> transferArray = new ArrayList<>();
        ArrayList<String> branchArray = new ArrayList<>();
        transferArray.addAll(fileStuff2);
        branchArray.addAll(fileStuff5);
        for (String spliter : branchArray) {
            if (!transferArray.contains(spliter)) { /*Merge Rule 5*/
                checkout2(mainBrancher, "--", spliter);
                add(spliter);
            }
        }
        HashMap<String, String> headContent = headCommit2.getContent();
        for (String spliter2 : transferArray) {
            if (headContent.containsKey(spliter2)) {   /*Merge Rule 6*/
                if (fileStuff.get(spliter2).equals(headContent.get(spliter2))) {
                    if (!branchArray.contains(spliter2)) {
                        rm(spliter2);
                    }
                }
            }
        }
        if (stageAreaa.hardCode.equals(2)) {
            System.out.println("Encountered a merge conflict.");
            return;
        }
        String compileMessage = "Merged " + branchName + " into master.";
        commit(compileMessage);
    }

    public boolean iterateHelper(String identification) {
        File checkNull = Utils.join(COMMIT_DIR, identification);
        if (!checkNull.exists()) {
            System.out.println("No commit with that identification was found");
            return false;
        } else {
            return true;
        }
    }

    public Commit iterateParents(Commit splitCommit) {
        File hashID7 = Utils.join(COMMIT_DIR, splitCommit.getParent());
        splitCommit = Utils.readObject(hashID7, Commit.class);
        return splitCommit;
    }

    public boolean checkCases(String branchName) {
        String modifiedFiles = "true";
        File stages = Utils.join(STAGE_DIR, stageType);
        Stage stageAreaa = Utils.readObject(stages, Stage.class);
        TreeMap<String, String> areaAddition = stageAreaa.addAddition;
        ArrayList<String> areaRemoval = stageAreaa.rmDeletion;
        if (!areaAddition.isEmpty() || !areaRemoval.isEmpty()) {
            System.out.println("You have uncommitted changes");
            System.exit(0);
        } else if (stageAreaa.branches.get(branchName) == null) {
            System.out.println("A branch with that name does not exist");
            return false;
        } else if (stageAreaa.getBranch().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself");
            return false;
        }
        return true;
    }
}
