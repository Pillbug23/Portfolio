package gitlet;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  Commit utilizes the message, what you type in string, the parent tracking,
 *  the contents of the blob, and the SHA.
 *  does at a high level.
 *  TreeMap puts items in order so no need to worry about re-ordering.
 *  @source https://www.javatpoint.com/java-util-calendar
 *  @source https://compiler.javatpoint.com/opr/test.jsp?filename=CalendarExample1
 *  @source https://beginnersbook.com/2013/04/java-string-to-date-conversion/
 *  @source https://stackoverflow.com/questions/25739955/
 *  @source how-to-format-a-date-to-following-format-day-month-year
 *  @source https://stackoverflow.com/questions/2009207/java-unparseable-date-exception
 *  @source http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
 *
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * Instance Variables
     */
    private String message;
    private Date timestamp;
    private Commit tracksStaged;
    private TreeMap<String, Blobs> tracking;
    private Commit parent;
    private TreeMap<String, Blobs> compareStage;
    private TreeMap<String, Blobs> commitBlob; /*Commit blob */
    private String log;
    private String shaID; /*SHA 40CHAR ID*/
    private HashMap<String, String> commitContents; /*Tracks parent commit*/
    private String dateFormat;
    private String parentCommit;
    private Date calendar;
    private static String add = "";
    private static HashMap<String, Blobs> blobContents;
    private static String uniqueID;

    /**
     * Inside structure when calling commit, which sets message equal to log
     * time, parent tracking a copy of the commit file, tracking blob contents,
     * and lastly converts our file to SHA format.
     * commit  3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
     * Merge: 4975af1 2c1ead1
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * @source https://stackoverflow.com/questions/7895171/java-date0-is-not
     * -1-1-1970
     * @source https://www.java67.com/2013/01/how-to-format-date-in-java
     * -simpledateformat-example.html
     */
    public Commit(String message, HashMap<String, String> blob, String parent) {
        this.log = message;
        this.commitContents = blob;
        this.parentCommit = parent;
        if (message.equals("initial commit")) {
            calendar = new Date(0);
        } else {
            calendar = new Date();
        }
        SimpleDateFormat time = new SimpleDateFormat("E MMM d HH:mm:ss yyyy Z");
        this.dateFormat = time.format(calendar);
        this.shaID = convertSHA();
    }

    public String getID() {
        return this.shaID;
    }

    public String obtainDate() {
        return this.dateFormat;
    }

    public String getMessage() {
        return this.log;
    }

    public String getTimestamp() {
        return String.valueOf(this.timestamp);
    }

    public String getParent() {
        return this.parentCommit;
    }

    public HashMap<String, String> getContent() {
        return this.commitContents;
    }

    public String convertSHA() {
        byte[] convert = Utils.serialize(this); /*Returns byte array with contents*/
        String headSHA = Utils.sha1(convert); /*Returns SHA1 ID as string*/
        return headSHA;
    }
    /**
     * Every object–every blob and every commit in our case–has a unique
     * integer id that serves as a reference to the object. An interesting
     * feature of Git is that these ids are universal: unlike a typical Java
     * implementation, two objects with exactly the same content will have the
     * same id on all systems.
     * a SHA id is as follows: SHA-1 hash of the concatenation of VALS
     * be any mixture of byte arrays and Strings.
     * @source https://stackoverflow.com/questions/5293174/creating-a-uniqu
     * e-id-from-an-array-of-strings-in-javascript
     * @source https://stackoverflow.com/questions/40091771/sha-1-hashing-on
     * -java-and-c-sharp
     */
    public static boolean isFile(Commit a) {
        File file1 = new File(String.valueOf(a));
        if (file1.exists() && !file1.isDirectory()) {
            return true;
        }
        return false;
    }
}
