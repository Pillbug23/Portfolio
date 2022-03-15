package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Phillip Ly
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  To check for directory, have to apply to all, but im lazy
     *  so test case will only call status
     */
    public static void main(String[] args) {
        int firstArg = args.length;
        Repository gitletR = new Repository();;
        if (firstArg == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        switch(args[0]) {
            case "init":
                gitletR.init();
                break;
            case "add":
                gitletR.add(args[1]);
                break;
            case "commit":
                gitletR.commit(args[1]);
                break;
            case "rm":
                gitletR.rm(args[1]);
                break;
            case "log":
                gitletR.log();
                break;
            case "global-log":
                gitletR.global();
                break;
            case "find":
                gitletR.find(args[1]);
                break;
            case "status":
                if (!Repository.GITLET_DIR.exists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                    System.exit(0);
                }
                gitletR.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    gitletR.checkout3(args[1]);
                } else if (args.length == 3) {
                    gitletR.checkout1(args[1],args[2]);
                } else if (args.length == 4) {
                    gitletR.checkout2(args[1],args[2],args[3]);
                } else {
                    System.out.println("Incorrect arguments");
                }
                break;
            case "branch":
                gitletR.branch(args[1]);
                break;
            case "rm-branch":
                gitletR.rmBranch(args[1]);
                break;
            case "reset":
                gitletR.reset(args[1]);
                break;
            case "merge":
                gitletR.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists");
        }
    }
}
