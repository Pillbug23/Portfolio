package gitlet;

import java.io.File;
import java.io.Serializable;

/** Saved contents of files, each blob tracked to a different commit, a blob in
 * summary is what the file contents, what is specifically inside it. Blob is what
 * is added when we call add and staged for addition
 * @source https://www.youtube.com/watch?v=GfmH9_8tM5w (Gitlet Intro-Part 2)
 * @source http://gitlet.maryrosecook.com/docs/gitlet.html for description of what a
 * blob is.
 * --A blob object stores the content of a file. For example, if a file called numbers.txt
 * that contains first is added to the index, a blob called hash(first) will be created
 * containing "first".
 * --A tree object stores a list of files and directories in a directory in the repository.
 * Entries in the list for files point to blob objects. Entries in the list for directories
 * point at other tree objects.
 * --A commit object stores a pointer to a tree object and a message. It represents the state
 * of the repository after a commit.
 */
public class Blobs implements Serializable {
    /**
     * Each blob is a reference to some file which contains contents
     * The file contents has a version number, which must be the same as file commit
     * Each blob has a unique SHA-ID that can be derived
     */
    public Blobs(String filename, String directoryArea) {
        String file = filename; /*Initialize String name of file*/
        String file2 = filename;
        File fileDirectory = Utils.join(directoryArea, file);
    }

    /**
     * Handles cases if the blob already exists or the directory
     * is already created
     */
    private boolean blobHelper(File fileDirectory) {
        if (!fileDirectory.exists()) {
            return true;
        }
        return false;
    }
}
