package edu.berkeley.cs186.database.concurrency;

import java.util.ArrayList;

/**
 * Utility methods to track the relationships between different lock types.
 */
public enum LockType {
    S,   // shared
    X,   // exclusive
    IS,  // intention shared
    IX,  // intention exclusive
    SIX, // shared intention exclusive
    NL;  // no lock held

    /**
     * This method checks whether lock types A and B are compatible with
     * each other. If a transaction can hold lock type A on a resource
     * at the same time another transaction holds lock type B on the same
     * resource, the lock types are compatible.
     * X locks are incompatible with all other locks, except NL.
     * <p>
     * // X compatibility sanity checks
     * assertFalse(LockType.compatible(LockType.X, LockType.IS));
     * assertFalse(LockType.compatible(LockType.X, LockType.IX));
     * assertFalse(LockType.compatible(LockType.X, LockType.SIX));
     * assertFalse(LockType.compatible(LockType.IS, LockType.X));
     * assertFalse(LockType.compatible(LockType.IX, LockType.X));
     * assertFalse(LockType.compatible(LockType.SIX, LockType.X));
     * <p>
     * // IS compatibility sanity checks
     * assertTrue(LockType.compatible(LockType.IS, LockType.SIX));
     * assertTrue(LockType.compatible(LockType.SIX, LockType.IS));
     * assertFalse(LockType.compatible(LockType.IX, LockType.SIX));
     * assertFalse(LockType.compatible(LockType.SIX, LockType.IX));
     * assertFalse(LockType.compatible(LockType.SIX, LockType.SIX));
     */
    public static boolean compatible(LockType a, LockType b) {
        if (a == null || b == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        String value = compatibleMatrix(a.toString(), b.toString());
        if (value == "T") {
            return true;
        }
        return false;
    }

    /**
     * * Compatibility Matrix
     * * (Boolean value in cell answers is `left` compatible with `top`?)
     * *
     * *     | NL  | IS  | IX  |  S  | SIX |  X
     * * ----+-----+-----+-----+-----+-----+-----
     * * NL  |  T  |  T  |  T  |  T  |  T  |  T    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * * IS  |  T  |  T  |  T  |  T  |  T  |  F    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * * IX  |  T  |  T  |  T  |  F  |  F  |  F    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * * S   |  T  |  T  |  F  |  T  |  F  |  F    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * * SIX |  T  |  T  |  F  |  F  |  F  |  F    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * * X   |  T  |  F  |  F  |  F  |  F  |  F    Done
     * * ----+-----+-----+-----+-----+-----+-----
     * *
     * * The filled in cells are covered by the public tests.
     * * You can expect the blank cells to be covered by the hidden tests!
     * * Hint: I bet the notes might have something useful for this...
     *  @param first
     * @param second
     * @return
     */
    private static String compatibleMatrix(String first, String second) {
        int i = 0;
        int j = 0;
        int doAllSix = 0;
        ArrayList<String> theSix = new ArrayList<String>();
        theSix.add("NL");
        theSix.add("IS");
        theSix.add("IX");
        theSix.add("S");
        theSix.add("SIX");
        theSix.add("X");
        while (doAllSix < 6) {
            if (first == theSix.get(doAllSix)) {
                i = doAllSix;
            }
            if (second == theSix.get(doAllSix)) {
                j = doAllSix;
            }
            doAllSix += 1;
        }
        final String[][] matrix = {
                {"T", "T", "T", "T", "T", "T"},
                {"T", "T", "T", "T", "T", "F"},
                {"T", "T", "T", "F", "F", "F"},
                {"T", "T", "F", "T", "F", "F"},
                {"T", "T", "F", "F", "F", "F"},
                {"T", "F", "F", "F", "F", "F"}
        };
        return matrix[i][j];
    }

    /**
     * This method returns the lock on the parent resource
     * that should be requested for a lock of type A to be granted.
     */
    public static LockType parentLock(LockType a) {
        if (a == null) {
            throw new NullPointerException("null lock type");
        }
        switch (a) {
        case S: return IS;
        case X: return IX;
        case IS: return IS;
        case IX: return IX;
        case SIX: return IX;
        case NL: return NL;
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }

    /**
     * This method returns if parentLockType has permissions to grant a childLockType
     * on a child.
     */
    public static boolean canBeParentLock(LockType parentLockType, LockType childLockType) {
        if (parentLockType == null || childLockType == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        String value = compatibleMatrix2(parentLockType.toString(), childLockType.toString());
        if (value == "T") {
            return true;
        }
        return false;
    }
    /**
     * Parent Matrix
     * (Boolean value in cell answers can `left` be the parent of `top`?)
     *
     *     | NL  | IS  | IX  |  S  | SIX |  X
     * ----+-----+-----+-----+-----+-----+-----
     * NL  |  T  |  F  |  F  |  F  |  F  |  F    Done
     * ----+-----+-----+-----+-----+-----+-----
     * IS  |  T  |  T  |  F  |  T  |  F  |  F    Done
     * ----+-----+-----+-----+-----+-----+-----
     * IX  |  T  |  T  |  T  |  T  |  T  |  T    Done
     * ----+-----+-----+-----+-----+-----+-----
     * S   |  T  |  F  |  F  |  F  |  F  |  F    Done
     * ----+-----+-----+-----+-----+-----+-----
     * SIX |  T  |  F  |  T  |  F  |  F  |  T    Done
     * ----+-----+-----+-----+-----+-----+-----
     * X   |  T  |  F  |  F  |  F  |  F  |  F    Done
     * ----+-----+-----+-----+-----+-----+-----
     *
     * The filled in cells are covered by the public test.
     * You can expect the blank cells to be covered by the hidden tests!
     */
    private static String compatibleMatrix2(String first, String second) {
        int i = 0;
        int j = 0;
        int doAllSix = 0;
        ArrayList<String> theSix = new ArrayList<String>();
        theSix.add("NL");
        theSix.add("IS");
        theSix.add("IX");
        theSix.add("S");
        theSix.add("SIX");
        theSix.add("X");
        while (doAllSix < 6) {
            if (first == theSix.get(doAllSix)) {
                i = doAllSix;
            }
            if (second == theSix.get(doAllSix)) {
                j = doAllSix;
            }
            doAllSix += 1;
        }
        final String[][] matrix = {
                {"T", "F", "F", "F", "F", "F"},
                {"T", "T", "F", "T", "F", "F"},
                {"T", "T", "T", "T", "T", "T"},
                {"T", "F", "F", "F", "F", "F"},
                {"T", "F", "T", "F", "F", "T"},
                {"T", "F", "F", "F", "F", "F"}
        };
        return matrix[i][j];
    }

    /**
     * This method returns whether a lock can be used for a situation
     * requiring another lock (e.g. an S lock can be substituted with
     * an X lock, because an X lock allows the transaction to do everything
     * the S lock allowed it to do).
     */
    public static boolean substitutable(LockType substitute, LockType required) {
        if (required == null || substitute == null) {
            throw new NullPointerException("null lock type");
        }
        // TODO(proj4_part1): implement
        String value = compatibleMatrix3(substitute.toString(), required.toString());
        if (value == "T") {
            return true;
        }
        return false;
    }

    /**
     * Substitutability Matrix
     * (Values along left are `substitute`, values along top are `required`)
     *
     *     | NL  | IS  | IX  |  S  | SIX |  X
     * ----+-----+-----+-----+-----+-----+-----
     * NL  |  T  |  F  |  F  |  F  |  F  |  F   Done
     * ----+-----+-----+-----+-----+-----+-----
     * IS  |  T  |  T  |  F  |  F  |  F  |  F   Done
     * ----+-----+-----+-----+-----+-----+-----
     * IX  |  T  |  T  |  T  |  F  |  F  |  F   Done
     * ----+-----+-----+-----+-----+-----+-----
     * S   |  T  |  F  |  F  |  T  |  F  |  F   Done
     * ----+-----+-----+-----+-----+-----+-----
     * SIX |  T  |  F  |  F  |  T  |  T  |  F   Done
     * ----+-----+-----+-----+-----+-----+-----
     * X   |  T  |  F  |  F  |  T  |  F  |  T   Done
     * ----+-----+-----+-----+-----+-----+-----
     *
     * The filled in cells are covered by the public test.
     * You can expect the blank cells to be covered by the hidden tests!
     *
     * The boolean value in the cell answers the question:
     * "Can `left` substitute `top`?"
     *
     * or alternatively:
     * "Are the privileges of `left` a superset of those of `top`?"
     */
    private static String compatibleMatrix3(String first, String second) {
        int i = 0;
        int j = 0;
        int doAllSix = 0;
        ArrayList<String> theSix = new ArrayList<String>();
        theSix.add("NL");
        theSix.add("IS");
        theSix.add("IX");
        theSix.add("S");
        theSix.add("SIX");
        theSix.add("X");
        while (doAllSix < 6) {
            if (first == theSix.get(doAllSix)) {
                i = doAllSix;
            }
            if (second == theSix.get(doAllSix)) {
                j = doAllSix;
            }
            doAllSix += 1;
        }
        final String[][] matrix = {
                {"T", "F", "F", "F", "F", "F"},
                {"T", "T", "F", "F", "F", "F"},
                {"T", "T", "T", "F", "F", "F"},
                {"T", "F", "F", "T", "F", "F"},
                {"T", "F", "F", "T", "T", "F"},
                {"T", "F", "F", "T", "F", "T"}
        };
        return matrix[i][j];
    }

    /**
     * @return True if this lock is IX, IS, or SIX. False otherwise.
     */
    public boolean isIntent() {
        return this == LockType.IX || this == LockType.IS || this == LockType.SIX;
    }

    @Override
    public String toString() {
        switch (this) {
        case S: return "S";
        case X: return "X";
        case IS: return "IS";
        case IX: return "IX";
        case SIX: return "SIX";
        case NL: return "NL";
        default: throw new UnsupportedOperationException("bad lock type");
        }
    }
}

