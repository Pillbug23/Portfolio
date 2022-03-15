package gitlet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/** Shopping cart that holds items and empties it after it is checked out
 * The stage addition adds files to the staging area.
 * If a file is already staged, hence it is in our treemap, replace the existing
 * file with the contents from the new one.
 *
 * @source https://www.youtube.com/watch?v=GfmH9_8tM5w (Gitlet Intro - Part 2)
 * @source https://www.w3schools.com/java/java_arraylist.asp
 */
public class Stage implements Serializable {
    TreeMap<String, String> addAddition;
    /*Instance variables*/
    ArrayList<String> rmDeletion;
    TreeMap<String, String> branches;
    ArrayList<String> noLonger;
    Commit tracksStaged;
    String headStuff;
    String mainBranch;
    Integer hardCode = 1;

    /*Stage object*/
    public Stage() {
        addAddition = new TreeMap<>(); /*Stage for addition*/
        rmDeletion = new ArrayList<>(); /*Stage for removal*/
        noLonger = new ArrayList<>(); /*No longer staged for removal*/
        branches = new TreeMap<>(); /*New branch storage, put here cuz lazy*/
    }

    /*Switches our pointer to the current commit, we have n more commits*/
    public void changePointer(Commit tracks) {
        this.tracksStaged = tracks;
    }

    public void headFile(String identification) {
        this.headStuff = identification;
    }

    public void headBranch(String main) {
        this.mainBranch = main;
    }

    public String getFile() {
        return this.headStuff;
    }

    public String getBranch() {
        return this.mainBranch;
    }

    public void getCode(int context) {
        this.hardCode = context;
    }
}
