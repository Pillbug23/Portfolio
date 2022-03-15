package edu.berkeley.cs186.database.recovery;

import edu.berkeley.cs186.database.Transaction;
import edu.berkeley.cs186.database.common.Pair;
import edu.berkeley.cs186.database.concurrency.DummyLockContext;
import edu.berkeley.cs186.database.io.DiskSpaceManager;
import edu.berkeley.cs186.database.memory.BufferManager;
import edu.berkeley.cs186.database.memory.Page;
import edu.berkeley.cs186.database.recovery.records.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implementation of ARIES.
 */
public class ARIESRecoveryManager implements RecoveryManager {
    // Disk space manager.
    DiskSpaceManager diskSpaceManager;
    // Buffer manager.
    BufferManager bufferManager;

    // Function to create a new transaction for recovery with a given
    // transaction number.
    private Function<Long, Transaction> newTransaction;

    // Log manager
    LogManager logManager;
    // Dirty page table (page number -> recLSN).
    Map<Long, Long> dirtyPageTable = new ConcurrentHashMap<>();
    // Transaction table (transaction number -> entry).
    Map<Long, TransactionTableEntry> transactionTable = new ConcurrentHashMap<>();
    // true if redo phase of restart has terminated, false otherwise. Used
    // to prevent DPT entries from being flushed during restartRedo.
    boolean redoComplete;

    public ARIESRecoveryManager(Function<Long, Transaction> newTransaction) {
        this.newTransaction = newTransaction;
    }

    /**
     * Initializes the log; only called the first time the database is set up.
     * The master record should be added to the log, and a checkpoint should be
     * taken.
     */
    @Override
    public void initialize() {
        this.logManager.appendToLog(new MasterLogRecord(0));
        this.checkpoint();
    }

    /**
     * Sets the buffer/disk managers. This is not part of the constructor
     * because of the cyclic dependency between the buffer manager and recovery
     * manager (the buffer manager must interface with the recovery manager to
     * block page evictions until the log has been flushed, but the recovery
     * manager needs to interface with the buffer manager to write the log and
     * redo changes).
     * @param diskSpaceManager disk space manager
     * @param bufferManager buffer manager
     */
    @Override
    public void setManagers(DiskSpaceManager diskSpaceManager, BufferManager bufferManager) {
        this.diskSpaceManager = diskSpaceManager;
        this.bufferManager = bufferManager;
        this.logManager = new LogManager(bufferManager);
    }

    // Forward Processing //////////////////////////////////////////////////////

    /**
     * Called when a new transaction is started.
     *
     * The transaction should be added to the transaction table.
     *
     * @param transaction new transaction
     */
    @Override
    public synchronized void startTransaction(Transaction transaction) {
        this.transactionTable.put(transaction.getTransNum(), new TransactionTableEntry(transaction));
    }

    /**
     * Called when a transaction is about to start committing.
     *
     * A commit record should be appended, the log should be flushed,
     * and the transaction table and the transaction status should be updated.
     *
     * @param transNum transaction being committed
     * @return LSN of the commit record
     */
    @Override
    public long commit(long transNum) {
        // TODO(proj5): implement
        //Transaction table (transaction number -> entry)
        //Each num corresponds to a specific entry
        TransactionTableEntry commitEntry = transactionTable.get(transNum);
        //Transaction object for the transaction.
        Transaction transObj = commitEntry.transaction;
        CommitTransactionLogRecord commitRecord = new CommitTransactionLogRecord(transNum, commitEntry.lastLSN);
        //A commit record should be appended
        long LSNcommit = logManager.appendToLog(commitRecord);
        //the log should be flushed
        logManager.flushToLSN(LSNcommit);
        //transaction status should be updated... can you transaction.status to determine status
        transObj.setStatus(Transaction.Status.COMMITTING);
        //transaction table should be updated... update the lastLSN refer to notes (self)
        //Set the lastLSN of the transaction to the LSN of the record you are on
        commitEntry.lastLSN = LSNcommit;
        return LSNcommit;
    }

    /**
     * Called when a transaction is set to be aborted.
     *
     * An abort record should be appended, and the transaction table and
     * transaction status should be updated. Calling this function should not
     * perform any rollbacks.
     *
     * @param transNum transaction being aborted
     * @return LSN of the abort record
     */
    @Override
    public long abort(long transNum) {
        // TODO(proj5): implement
        //Transaction table (transaction number -> entry)
        //Each num corresponds to a specific entry
        TransactionTableEntry abortEntry = transactionTable.get(transNum);
        //Transaction object for the transaction.
        Transaction transObj = abortEntry.transaction;
        AbortTransactionLogRecord abortRecord = new AbortTransactionLogRecord(transNum, abortEntry.lastLSN);
        //An abort record should be appended
        long LSNabort = logManager.appendToLog(abortRecord);
        //transaction status should be updated... can you transaction.status to determine status
        transObj.setStatus(Transaction.Status.ABORTING);
        //transaction table should be updated... update the lastLSN refer to notes (self)
        //Set the lastLSN of the transaction to the LSN of the record you are on
        abortEntry.lastLSN = LSNabort;
        return LSNabort;
    }

    /**
     * Called when a transaction is cleaning up; this should roll back
     * changes if the transaction is aborting (see the rollbackToLSN helper
     * function below).
     *
     * Any changes that need to be undone should be undone, the transaction should
     * be removed from the transaction table, the end record should be appended,
     * and the transaction status should be updated.
     *
     * @param transNum transaction to end
     * @return LSN of the end record
     */
    @Override
    public long end(long transNum) {
        // TODO(proj5): implement
        //Transaction table (transaction number -> entry)
        //Each num corresponds to a specific entry
        TransactionTableEntry endEntry = transactionTable.get(transNum);
        //Transaction object for the transaction.
        Transaction transObj = endEntry.transaction;
        Transaction.Status transStatus = transObj.getStatus();
        LogRecord rollbackLSN = logManager.fetchLogRecord(endEntry.lastLSN);
        //this should roll back changes if the transaction is aborting
        if (transStatus.equals(Transaction.Status.ABORTING)) {
            rollbackToLSN(transNum, 0);
            //rollbackToLSN(transNum, );
        }
        //the transaction should be removed from the transaction table
        transactionTable.remove(transObj);
        //the end record should be appended
        EndTransactionLogRecord endRecord = new EndTransactionLogRecord(transNum, endEntry.lastLSN);
        long LSNend = logManager.appendToLog(endRecord);
        //and the transaction status should be updated
        transObj.setStatus(Transaction.Status.COMPLETE);
        return LSNend;
    }

    /**
     * Recommended helper function: performs a rollback of all of a
     * transaction's actions, up to (but not including) a certain LSN.
     * Starting with the LSN of the most recent record that hasn't been undone:
     * - while the current LSN is greater than the LSN we're rolling back to:
     *    - if the record at the current LSN is undoable:
     *       - Get a compensation log record (CLR) by calling undo on the record
     *       - Append the CLR
     *       - Call redo on the CLR to perform the undo
     *    - update the current LSN to that of the next record to undo
     *
     * Note above that calling .undo() on a record does not perform the undo, it
     * just creates the compensation log record.
     *
     * @param transNum transaction to perform a rollback for
     * @param LSN LSN to which we should rollback
     */
    private void rollbackToLSN(long transNum, long LSN) {
        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        LogRecord lastRecord = logManager.fetchLogRecord(transactionEntry.lastLSN);
        long lastRecordLSN = lastRecord.getLSN();
        // Small optimization: if the last record is a CLR we can start rolling
        // back from the next record that hasn't yet been undone.
        long currentLSN = lastRecord.getUndoNextLSN().orElse(lastRecordLSN);
        // TODO(proj5) implement the rollback logic described above
        //Starting with the LSN of the most recent record that hasn't been undone:
        //while the current LSN is greater than the LSN we're rolling back to:
        //long currentLSN2 = currentLSN;
        //int count = 0;

        while (currentLSN >= LSN) {
            LogRecord curr = logManager.fetchLogRecord(currentLSN);
            //LogRecord curr = logManager.fetchLogRecord(currentLSN);
            //System.out.println(currentLSN);
            //if the record at the current LSN is undoable
            //if (lastRecord.isUndoable()) {
            //System.out.println(curr.isUndoable());
            //System.out.println(lastRecord.isUndoable());
            if (curr.isUndoable()) {
                //System.out.println("check");
                //count += 1;
                //Get a compensation log record (CLR) by calling undo on the record
                LogRecord CLRnew = curr.undo(transactionEntry.lastLSN);
                //Append the CLR
                //logManager.appendToLog(CLRnew);
                transactionEntry.lastLSN = logManager.appendToLog(CLRnew);
                //Call redo on the CLR to perform the undo
                CLRnew.redo(this, diskSpaceManager, bufferManager);
            }
            if(curr.getPrevLSN().isPresent()) {
                currentLSN = curr.getPrevLSN().get();
            }
            else {
                transactionTable.clear();
                break;
            }
            //transactionTable.clear();
            /*} else {
                transactionTable.clear();
                break;
            }

             */

            //update the current LSN to that of the next record to undo
            /*if (curr.getPrevLSN().isPresent()) {
                transactionEntry.lastLSN = lastRecord.getPrevLSN().get();
                //currentLSN = curr.getUndoNextLSN().orElse(curr.getLSN());
            } else {
                transactionTable.clear();
                break;
            }
            lastRecord = logManager.fetchLogRecord(transactionEntry.lastLSN);
            lastRecordLSN = lastRecord.getLSN();
            //currentLSN2 = currentLSN;
            if (count >= 1) {
                currentLSN = currentLSN + 40000;
            }*/
        }
        //transactionTable.clear();
    }

    /**
     * Called before a page is flushed from the buffer cache. This
     * method is never called on a log page.
     *
     * The log should be as far as necessary.
     *
     * @param pageLSN pageLSN of page about to be flushed
     */
    @Override
    public void pageFlushHook(long pageLSN) {
        logManager.flushToLSN(pageLSN);
    }

    /**
     * Called when a page has been updated on disk.
     *
     * As the page is no longer dirty, it should be removed from the
     * dirty page table.
     *
     * @param pageNum page number of page updated on disk
     */
    @Override
    public void diskIOHook(long pageNum) {
        if (redoComplete) dirtyPageTable.remove(pageNum);
    }

    /**
     * Called when a write to a page happens.
     *
     * This method is never called on a log page. Arguments to the before and after params
     * are guaranteed to be the same length.
     *
     * The appropriate log record should be appended, and the transaction table
     * and dirty page table should be updated accordingly.
     *
     * @param transNum transaction performing the write
     * @param pageNum page number of page being written
     * @param pageOffset offset into page where write begins
     * @param before bytes starting at pageOffset before the write
     * @param after bytes starting at pageOffset after the write
     * @return LSN of last record written to log
     */
    @Override
    public long logPageWrite(long transNum, long pageNum, short pageOffset, byte[] before,
                             byte[] after) {
        assert (before.length == after.length);
        assert (before.length <= BufferManager.EFFECTIVE_PAGE_SIZE / 2);
        // TODO(proj5): implement
        //Transaction table (transaction number -> entry)
        //Each num corresponds to a specific entry
        TransactionTableEntry logEntry = transactionTable.get(transNum);
        long previousLSN = logEntry.lastLSN;
        //Update the appropriate log
        UpdatePageLogRecord newUpdate = new UpdatePageLogRecord(transNum,pageNum,previousLSN,pageOffset,before,after);
        //The appropriate log record should be appended
        //and the transaction table updated
        previousLSN = logManager.appendToLog(newUpdate);
        logEntry.lastLSN = previousLSN;
        //dirty page table updated
        //dirtyPageTable.put(pageNum,appendLog);
        dirtyPageTable.putIfAbsent(pageNum,previousLSN);
        return previousLSN;
    }

    /**
     * Called when a new partition is allocated. A log flush is necessary,
     * since changes are visible on disk immediately after this returns.
     *
     * This method should return -1 if the partition is the log partition.
     *
     * The appropriate log record should be appended, and the log flushed.
     * The transaction table should be updated accordingly.
     *
     * @param transNum transaction requesting the allocation
     * @param partNum partition number of the new partition
     * @return LSN of record or -1 if log partition
     */
    @Override
    public long logAllocPart(long transNum, int partNum) {
        // Ignore if part of the log.
        if (partNum == 0) return -1L;
        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);

        long prevLSN = transactionEntry.lastLSN;
        LogRecord record = new AllocPartLogRecord(transNum, partNum, prevLSN);
        long LSN = logManager.appendToLog(record);
        // Update lastLSN
        transactionEntry.lastLSN = LSN;
        // Flush log
        logManager.flushToLSN(LSN);
        return LSN;
    }

    /**
     * Called when a partition is freed. A log flush is necessary,
     * since changes are visible on disk immediately after this returns.
     *
     * This method should return -1 if the partition is the log partition.
     *
     * The appropriate log record should be appended, and the log flushed.
     * The transaction table should be updated accordingly.
     *
     * @param transNum transaction requesting the partition be freed
     * @param partNum partition number of the partition being freed
     * @return LSN of record or -1 if log partition
     */
    @Override
    public long logFreePart(long transNum, int partNum) {
        // Ignore if part of the log.
        if (partNum == 0) return -1L;

        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);

        long prevLSN = transactionEntry.lastLSN;
        LogRecord record = new FreePartLogRecord(transNum, partNum, prevLSN);
        long LSN = logManager.appendToLog(record);
        // Update lastLSN
        transactionEntry.lastLSN = LSN;
        // Flush log
        logManager.flushToLSN(LSN);
        return LSN;
    }

    /**
     * Called when a new page is allocated. A log flush is necessary,
     * since changes are visible on disk immediately after this returns.
     *
     * This method should return -1 if the page is in the log partition.
     *
     * The appropriate log record should be appended, and the log flushed.
     * The transaction table should be updated accordingly.
     *
     * @param transNum transaction requesting the allocation
     * @param pageNum page number of the new page
     * @return LSN of record or -1 if log partition
     */
    @Override
    public long logAllocPage(long transNum, long pageNum) {
        // Ignore if part of the log.
        if (DiskSpaceManager.getPartNum(pageNum) == 0) return -1L;

        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);

        long prevLSN = transactionEntry.lastLSN;
        LogRecord record = new AllocPageLogRecord(transNum, pageNum, prevLSN);
        long LSN = logManager.appendToLog(record);
        // Update lastLSN
        transactionEntry.lastLSN = LSN;
        // Flush log
        logManager.flushToLSN(LSN);
        return LSN;
    }

    /**
     * Called when a page is freed. A log flush is necessary,
     * since changes are visible on disk immediately after this returns.
     *
     * This method should return -1 if the page is in the log partition.
     *
     * The appropriate log record should be appended, and the log flushed.
     * The transaction table should be updated accordingly.
     *
     * @param transNum transaction requesting the page be freed
     * @param pageNum page number of the page being freed
     * @return LSN of record or -1 if log partition
     */
    @Override
    public long logFreePage(long transNum, long pageNum) {
        // Ignore if part of the log.
        if (DiskSpaceManager.getPartNum(pageNum) == 0) return -1L;

        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);

        long prevLSN = transactionEntry.lastLSN;
        LogRecord record = new FreePageLogRecord(transNum, pageNum, prevLSN);
        long LSN = logManager.appendToLog(record);
        // Update lastLSN
        transactionEntry.lastLSN = LSN;
        dirtyPageTable.remove(pageNum);
        // Flush log
        logManager.flushToLSN(LSN);
        return LSN;
    }

    /**
     * Creates a savepoint for a transaction. Creating a savepoint with
     * the same name as an existing savepoint for the transaction should
     * delete the old savepoint.
     *
     * The appropriate LSN should be recorded so that a partial rollback
     * is possible later.
     *
     * @param transNum transaction to make savepoint for
     * @param name name of savepoint
     */
    @Override
    public void savepoint(long transNum, String name) {
        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);
        transactionEntry.addSavepoint(name);
    }

    /**
     * Releases (deletes) a savepoint for a transaction.
     * @param transNum transaction to delete savepoint for
     * @param name name of savepoint
     */
    @Override
    public void releaseSavepoint(long transNum, String name) {
        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);
        transactionEntry.deleteSavepoint(name);
    }

    /**
     * Rolls back transaction to a savepoint.
     *
     * All changes done by the transaction since the savepoint should be undone,
     * in reverse order, with the appropriate CLRs written to log. The transaction
     * status should remain unchanged.
     *
     * @param transNum transaction to partially rollback
     * @param name name of savepoint
     */
    @Override
    public void rollbackToSavepoint(long transNum, String name) {
        TransactionTableEntry transactionEntry = transactionTable.get(transNum);
        assert (transactionEntry != null);

        // All of the transaction's changes strictly after the record at LSN should be undone.
        long savepointLSN = transactionEntry.getSavepoint(name);

        // TODO(proj5): implement
        LogRecord updateLog = logManager.fetchLogRecord(transactionEntry.lastLSN);
        rollbackToLSN(transNum,transactionEntry.lastLSN);
        return;
    }

    /**
     * Create a checkpoint.
     *
     * First, a begin checkpoint record should be written. Done
     *
     * Then, end checkpoint records should be filled up as much as possible first
     * using recLSNs from the DPT, then status/lastLSNs from the transactions
     * table, and written when full (or when nothing is left to be written).
     * You may find the method EndCheckpointLogRecord#fitsInOneRecord here to
     * figure out when to write an end checkpoint record.
     *
     * Finally, the master record should be rewritten with the LSN of the
     * begin checkpoint record.
     */
    @Override
    public synchronized void checkpoint() {
        // Create begin checkpoint log record and write to log
        LogRecord beginRecord = new BeginCheckpointLogRecord();
        long beginLSN = logManager.appendToLog(beginRecord);

        Map<Long, Long> chkptDPT = new HashMap<>();
        Map<Long, Pair<Transaction.Status, Long>> chkptTxnTable = new HashMap<>();

        // TODO(proj5): generate end checkpoint record(s) for DPT and transaction table
        //Then, end checkpoint records should be filled up as much as possible first
        //using recLSNs from the DPT,
        //boolean checkEnd = EndCheckpointLogRecord.fitsInOneRecord(0, 1);
        //Endcheckpoint: parameters
        //the number of dirty page table entries stored in the record
        //the number of transaction number/status/lastLSN entries stored in the record
        //returns whether the record would fit in one page
        for (Map.Entry<Long,Long> recLSN: dirtyPageTable.entrySet()) {
            boolean checkEnd = EndCheckpointLogRecord.fitsInOneRecord(chkptDPT.size()+1, 0);
            if (!checkEnd) {
                //Write record
                EndCheckpointLogRecord writeCheckpoint = new EndCheckpointLogRecord(chkptDPT, chkptTxnTable);
                //The appropriate log record should be appended
                logManager.appendToLog(writeCheckpoint);
                chkptDPT.clear();
            }
            Long key = recLSN.getKey();
            Long value = recLSN.getValue();
            chkptDPT.put(key, value);
        }
        //then status/lastLSNs from the transactions table
        for (Map.Entry<Long,TransactionTableEntry> status: transactionTable.entrySet()) {
            boolean checkEnd2 = EndCheckpointLogRecord.fitsInOneRecord(chkptDPT.size(), chkptTxnTable.size()+1);
            if (!checkEnd2) {
                //Write record
                EndCheckpointLogRecord writeCheckpoint = new EndCheckpointLogRecord(chkptDPT, chkptTxnTable);
                //The appropriate log record should be appended
                logManager.appendToLog(writeCheckpoint);
                chkptDPT.clear();
                chkptTxnTable.clear();
            }
            Long key2 = status.getKey();
            TransactionTableEntry value2 = status.getValue();
            Transaction.Status currentStatus = value2.transaction.getStatus();
            long lastLSN = value2.lastLSN;
            Pair<Transaction.Status, Long> createPair = new Pair<Transaction.Status, Long>(currentStatus, lastLSN);
            chkptTxnTable.put(key2, createPair);
        }

        // Last end checkpoint record
        LogRecord endRecord = new EndCheckpointLogRecord(chkptDPT, chkptTxnTable);
        logManager.appendToLog(endRecord);
        // Ensure checkpoint is fully flushed before updating the master record
        flushToLSN(endRecord.getLSN());

        // Update master record
        MasterLogRecord masterRecord = new MasterLogRecord(beginLSN);
        logManager.rewriteMasterRecord(masterRecord);
    }

    /**
     * Flushes the log to at least the specified record,
     * essentially flushing up to and including the page
     * that contains the record specified by the LSN.
     *
     * @param LSN LSN up to which the log should be flushed
     */
    @Override
    public void flushToLSN(long LSN) {
        this.logManager.flushToLSN(LSN);
    }

    @Override
    public void dirtyPage(long pageNum, long LSN) {
        dirtyPageTable.putIfAbsent(pageNum, LSN);
        // Handle race condition where earlier log is beaten to the insertion by
        // a later log.
        dirtyPageTable.computeIfPresent(pageNum, (k, v) -> Math.min(LSN,v));
    }

    @Override
    public void close() {
        this.checkpoint();
        this.logManager.close();
    }

    // Restart Recovery ////////////////////////////////////////////////////////

    /**
     * Called whenever the database starts up, and performs restart recovery.
     * Recovery is complete when the Runnable returned is run to termination.
     * New transactions may be started once this method returns.
     *
     * This should perform the three phases of recovery, and also clean the
     * dirty page table of non-dirty pages (pages that aren't dirty in the
     * buffer manager) between redo and undo, and perform a checkpoint after
     * undo.
     */
    @Override
    public void restart() {
        this.restartAnalysis();
        this.restartRedo();
        this.redoComplete = true;
        this.cleanDPT();
        this.restartUndo();
        this.checkpoint();
    }

    /**
     * This method performs the analysis pass of restart recovery.
     *
     * First, the master record should be read (LSN 0). The master record contains
     * one piece of information: the LSN of the last successful checkpoint.
     *
     * We then begin scanning log records, starting at the beginning of the
     * last successful checkpoint.
     *
     * If the log record is for a transaction operation (getTransNum is present)
     * - update the transaction table
     *
     * If the log record is page-related (getPageNum is present), update the dpt
     *   - update/undoupdate page will dirty pages
     *   - free/undoalloc page always flush changes to disk
     *   - no action needed for alloc/undofree page
     *
     * If the log record is for a change in transaction status:
     * - update transaction status to COMMITTING/RECOVERY_ABORTING/COMPLETE
     * - update the transaction table
     * - if END_TRANSACTION: clean up transaction (Transaction#cleanup), remove
     *   from txn table, and add to endedTransactions
     *
     * If the log record is an end_checkpoint record:
     * - Copy all entries of checkpoint DPT (replace existing entries if any)
     * - Skip txn table entries for transactions that have already ended
     * - Add to transaction table if not already present
     * - Update lastLSN to be the larger of the existing entry's (if any) and
     *   the checkpoint's
     * - The status's in the transaction table should be updated if it is possible
     *   to transition from the status in the table to the status in the
     *   checkpoint. For example, running -> aborting is a possible transition,
     *   but aborting -> running is not.
     *
     * After all records in the log are processed, for each ttable entry:
     *  - if COMMITTING: clean up the transaction, change status to COMPLETE,
     *    remove from the ttable, and append an end record
     *  - if RUNNING: change status to RECOVERY_ABORTING, and append an abort
     *    record
     *  - if RECOVERY_ABORTING: no action needed
     */
    void restartAnalysis() {
        // Read master record
        LogRecord record = logManager.fetchLogRecord(0L);
        // Type checking
        assert (record != null && record.getType() == LogType.MASTER);
        MasterLogRecord masterRecord = (MasterLogRecord) record;
        // Get start checkpoint LSN
        long LSN = masterRecord.lastCheckpointLSN;
        // Set of transactions that have completed
        Set<Long> endedTransactions = new HashSet<>();
        // TODO(proj5): implement
        // We then begin scanning log records, starting at the beginning of the
        // last successful checkpoint.
        Iterator<LogRecord> scanCheckpoint = logManager.scanFrom(LSN);
        // Checks the iterator if it has a next
        while (scanCheckpoint.hasNext()) {
            //If the log record is for a transaction operation (getTransNum is present)
            //update the transaction table.
            LogRecord nextLog = scanCheckpoint.next();
            boolean presentLog = nextLog.getTransNum().isPresent();
            //The following applies to any record with a non-empty result for LogRecord#getTransNum()
            if (presentLog) {
                Long transNum = nextLog.getTransNum().get();
                //If the transaction is not in the transaction table, it should be added
                // to the table (the newTransaction function object can be used to create
                // a Transaction object, which can be passed to startTransaction).
                if (!transactionTable.containsKey(transNum)) {
                    startTransaction(newTransaction.apply(transNum));
                }
                TransactionTableEntry updateEntry = transactionTable.get(transNum);
                updateEntry.lastLSN = nextLog.LSN;
                //If the log record is for a change in transaction status:
                //- update transaction status to COMMITTING/RECOVERY_ABORTING/COMPLETE
                //- update the transaction table
                //- if END_TRANSACTION: clean up transaction (Transaction#cleanup), remove
                //from txn table, and add to endedTransactions
                if (nextLog.type.equals(LogType.COMMIT_TRANSACTION)) {
                    updateEntry.transaction.setStatus(Transaction.Status.COMMITTING);
                } else if (nextLog.type.equals(LogType.ABORT_TRANSACTION)) {
                    updateEntry.transaction.setStatus(Transaction.Status.RECOVERY_ABORTING);
                } else if (nextLog.type.equals(LogType.END_TRANSACTION)) {
                    updateEntry.transaction.cleanup();
                    updateEntry.transaction.setStatus(Transaction.Status.COMPLETE);
                    transactionTable.remove(transNum);
                    endedTransactions.add(transNum);
                }
                //If the log record is page-related (getPageNum is present),
                //Update the dpt
                //- update/undoupdate page will dirty pages
                //- free/undoalloc page always flush changes to disk
                //- no action needed for alloc/undofree page
                boolean presentPage = nextLog.getPageNum().isPresent();
                if (presentPage) {
                    Long pgNumber = nextLog.getPageNum().get();
                    if (nextLog.type.equals(LogType.UPDATE_PAGE) || nextLog.type.equals(LogType.UNDO_UPDATE_PAGE)) {
                        dirtyPageTable.putIfAbsent(pgNumber, nextLog.LSN);
                    } else if (nextLog.type.equals(LogType.FREE_PAGE) || (nextLog.type.equals(LogType.UNDO_ALLOC_PAGE))) {
                        //FreePage/UndoAllocPage both make their changes visible on disk
                        // immediately, and can be seen as flushing the freed page to disk
                        // (remove page from DPT)
                        logManager.flushToLSN(nextLog.LSN);
                        dirtyPageTable.remove(pgNumber);
                    }
                }
            }
                //If the log record is an end_checkpoint record:
                //     * - Copy all entries of checkpoint DPT (replace existing entries if any)
                //     * - Skip txn table entries for transactions that have already ended
                //     * - Add to transaction table if not already present
                //     * - Update lastLSN to be the larger of the existing entry's (if any) and
                //     *   the checkpoint's
                //     * - The status's in the transaction table should be updated if it is possible
                //     *   to transition from the status in the table to the status in the
                //     *   checkpoint. For example, running -> aborting is a possible transition,
                //     *   but aborting -> running is not.
                if (nextLog.type.equals(LogType.END_CHECKPOINT)) {
                    Map<Long, Long> copyTable = nextLog.getDirtyPageTable();
                    Map<Long, Pair<Transaction.Status, Long>> skipEntries = nextLog.getTransactionTable();
                    for (Map.Entry<Long,Long> eachEntry: copyTable.entrySet()) {
                        Long key = eachEntry.getKey();
                        Long value = eachEntry.getValue();
                        dirtyPageTable.put(key,value);
                    }
                    for (Map.Entry<Long, Pair<Transaction.Status, Long>> skipTXN : skipEntries.entrySet()) {
                        Long key = skipTXN.getKey();
                        Pair<Transaction.Status, Long> value = skipTXN.getValue();
                        if (transactionTable.containsKey(key)) { //Skip Entries
                            if (value.getSecond() > transactionTable.get(key).lastLSN) {
                                transactionTable.get(key).lastLSN = value.getSecond();
                            }
                        } else if (!endedTransactions.contains(key)) {
                            startTransaction(newTransaction.apply(key)); //add
                            if (transactionTable.get(key).transaction.getStatus().equals(Transaction.Status.COMPLETE)) {
                                transactionTable.remove(key);
                            }
                            if (transactionTable.get(key).transaction.getStatus().equals(Transaction.Status.COMMITTING)) {
                                transactionTable.remove(key);
                            }
                        }
                    }

            }
        }
        System.out.println("Done");
        //After all records in the log are processed, for each ttable entry:
        //- if COMMITTING: clean up the transaction, change status to COMPLETE,
        //remove from the ttable, and append an end record
        //- if RUNNING: change status to RECOVERY_ABORTING, and append an abort
        //record
        //- if RECOVERY_ABORTING: no action needed
        for (Map.Entry<Long,TransactionTableEntry> eachEntry: transactionTable.entrySet()) {
            TransactionTableEntry eachValue = eachEntry.getValue();
            Long eachKey = eachEntry.getKey();
            Transaction.Status statusUp = eachValue.transaction.getStatus();
            if (statusUp.equals(Transaction.Status.COMMITTING)) {
                eachValue.transaction.cleanup();
                eachValue.transaction.setStatus(Transaction.Status.COMPLETE);
                EndTransactionLogRecord newLog = new EndTransactionLogRecord(eachKey, eachValue.lastLSN);
                eachValue.lastLSN = logManager.appendToLog(newLog);
                transactionTable.remove(eachKey);
                endedTransactions.add(eachKey);
            } else if (statusUp.equals(Transaction.Status.RUNNING)) {
                eachValue.transaction.setStatus(Transaction.Status.RECOVERY_ABORTING);
                AbortTransactionLogRecord newLog = new AbortTransactionLogRecord(eachKey, eachValue.lastLSN);
                eachValue.lastLSN = logManager.appendToLog(newLog);
                endedTransactions.add(eachKey);
            }
        }
    }

    /**
     * This method performs the redo pass of restart recovery.
     *
     * First, determine the starting point for REDO from the dirty page table.
     *
     * Then, scanning from the starting point, if the record is redoable and
     * - partition-related (Alloc/Free/UndoAlloc/UndoFree..Part), always redo it
     * - allocates a page (AllocPage/UndoFreePage), always redo it
     * - modifies a page (Update/UndoUpdate/Free/UndoAlloc....Page) in
     *   the dirty page table with LSN >= recLSN, the page is fetched from disk,
     *   the pageLSN is checked, and the record is redone if needed.
     */
    void restartRedo() {
        // TODO(proj5): implement
        //@source https://www.tutorialspoint.com/find-minimum-element-of-hashset-in-java#:~:
        // text=To%20get%20the%20minimum%20element,min()%20method.
        //First, determine the starting point for REDO from the dirty page table.
        Collection<Long> startingPoint = dirtyPageTable.values();
        Long earliestPoint = Collections.min(startingPoint);
        Iterator<LogRecord> formRecord = logManager.scanFrom(earliestPoint);
        while (formRecord.hasNext()) {
            LogRecord nextRec = formRecord.next();
            boolean checkRedo = nextRec.isRedoable();
            //If record is redoable and
            //partition-related (Alloc/Free/UndoAlloc/UndoFree..Part), always redo it
            if (checkRedo && nextRec.getType().equals(LogType.ALLOC_PART)) {
                    nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
            } if (checkRedo && nextRec.getType().equals(LogType.FREE_PART)) {
                    nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
            } if (checkRedo && nextRec.getType().equals(LogType.UNDO_ALLOC_PART)) {
                    nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
            } if (checkRedo && nextRec.getType().equals(LogType.UNDO_FREE_PART)) {
                    nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
            }
            //- allocates a page (AllocPage/UndoFreePage), always redo it
            if (checkRedo && nextRec.getType().equals(LogType.ALLOC_PAGE)) {
                nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
            } if (checkRedo && nextRec.getType().equals(LogType.UNDO_FREE_PAGE)) {
                nextRec.redo(this, this.diskSpaceManager, this.bufferManager);
            }
            //modifies a page (Update/UndoUpdate/Free/UndoAlloc....Page) in
            //  the dirty page table with LSN >= recLSN, the page is fetched from disk,
            //  the pageLSN is checked, and the record is redone if needed.
            if (checkRedo && nextRec.getType().equals(LogType.UPDATE_PAGE)) {
                Long pageNum = nextRec.getPageNum().get();
                if (dirtyPageTable.containsKey(pageNum)) {
                    Long recLSN = dirtyPageTable.get(pageNum);
                    if (nextRec.LSN >= recLSN) {
                        Page page = bufferManager.fetchPage(new DummyLockContext(), pageNum);
                        try {
                            //the pageLSN on the page itself is strictly less than
                            // the LSN of the record.
                            long pageLSN = page.getPageLSN();
                            if (pageLSN < nextRec.LSN) {
                                nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
                            }
                        } finally {
                            page.unpin();
                        }
                    }
                }
            }
            if (checkRedo && nextRec.getType().equals(LogType.UNDO_UPDATE_PAGE)) {
                Long pageNum = nextRec.getPageNum().get();
                if (dirtyPageTable.containsKey(pageNum)) {
                    Long recLSN = dirtyPageTable.get(pageNum);
                    if (nextRec.LSN >= recLSN) {
                        Page page = bufferManager.fetchPage(new DummyLockContext(), pageNum);
                        try {
                            //the pageLSN on the page itself is strictly less than
                            // the LSN of the record.
                            long pageLSN = page.getPageLSN();
                            if (pageLSN < nextRec.LSN) {
                                nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
                            }
                        } finally {
                            page.unpin();
                        }
                    }
                }
            }
            if (checkRedo && nextRec.getType().equals(LogType.FREE_PAGE)) {
                Long pageNum = nextRec.getPageNum().get();
                if (dirtyPageTable.containsKey(pageNum)) {
                    Long recLSN = dirtyPageTable.get(pageNum);
                    if (nextRec.LSN >= recLSN) {
                        Page page = bufferManager.fetchPage(new DummyLockContext(), pageNum);
                        try {
                            //the pageLSN on the page itself is strictly less than
                            // the LSN of the record.
                            long pageLSN = page.getPageLSN();
                            if (pageLSN < nextRec.LSN) {
                                nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
                            }
                        } finally {
                            page.unpin();
                        }
                    }
                }
            }
            if (checkRedo && nextRec.getType().equals(LogType.UNDO_ALLOC_PAGE)) {
                Long pageNum = nextRec.getPageNum().get();
                if (dirtyPageTable.containsKey(pageNum)) {
                    Long recLSN = dirtyPageTable.get(pageNum);
                    if (nextRec.LSN >= recLSN) {
                        Page page = bufferManager.fetchPage(new DummyLockContext(), pageNum);
                        try {
                            //the pageLSN on the page itself is strictly less than
                            // the LSN of the record.
                            long pageLSN = page.getPageLSN();
                            if (pageLSN < nextRec.LSN) {
                                nextRec.redo(this,this.diskSpaceManager,this.bufferManager);
                            }
                        } finally {
                            page.unpin();
                        }
                    }
                }
            }
        }
    }

    /**
     * This method performs the undo pass of restart recovery.

     * First, a priority queue is created sorted on lastLSN of all aborting
     * transactions.
     *
     * Then, always working on the largest LSN in the priority queue until we are done,
     * - if the record is undoable, undo it, and append the appropriate CLR
     * - replace the entry with a new one, using the undoNextLSN if available,
     *   if the prevLSN otherwise.
     * - if the new LSN is 0, clean up the transaction, set the status to complete,
     *   and remove from transaction table.
     */
    void restartUndo() {
        // TODO(proj5): implement
        //@source https://stackoverflow.com/questions/11003155/change-priorityqueue-to-max-priorityqueue
        PriorityQueue<Long> prioQueue = new PriorityQueue<>(Collections.reverseOrder());
        //First, a priority queue is created sorted on lastLSN of all aborting
        //transactions.
        Long getCLRNew = null;
        for (Map.Entry<Long,TransactionTableEntry> eachEntry: transactionTable.entrySet()) {
            TransactionTableEntry getsValue = eachEntry.getValue();
            //The undo phase begins with the set of lastLSN of each of the aborting
            // transactions (in the RECOVERY_ABORTING state)
            if (getsValue.transaction.getStatus().equals(Transaction.Status.RECOVERY_ABORTING)) {
                prioQueue.add(getsValue.lastLSN);
            }
        }
        //Then, always working on the largest LSN in the priority queue until we are done,
        //Note we have already sorted from largest to smallest
        while (!prioQueue.isEmpty()) {
            //if the record is undoable, undo it, and append the appropriate CLR
            Long headValue = prioQueue.poll();
            LogRecord nextRec = logManager.fetchLogRecord(headValue);
            if (nextRec.isUndoable()) {
                //In Undo the previous value before the change is made
                Long transNum = nextRec.getTransNum().get();
                TransactionTableEntry prevValue = transactionTable.get(transNum);
                long previousUndo = prevValue.lastLSN;
                LogRecord CLR = nextRec.undo(previousUndo);
                long appendLSN = logManager.appendToLog(CLR);
                prevValue.lastLSN = appendLSN;
                //The undo method of LogRecord does not actually undo changes -
                // it instead returns the compensation log record. To actually undo
                // changes, you will need to append the returned CLR and then call redo on it.
                CLR.redo(this,diskSpaceManager,this.bufferManager);
                //replace the entry with a new one, using the undoNextLSN if available,
                //if the prevLSN otherwise.
            }
            if (nextRec.getUndoNextLSN().isPresent()) {
                getCLRNew = nextRec.getUndoNextLSN().get();
            } else {
                getCLRNew = nextRec.getPrevLSN().get();
            }
            // if the new LSN is 0, clean up the transaction, set the status to complete,
            // and remove from transaction table.
            if (getCLRNew == null) {
                return;
            }
            //(cleanup(), state set to COMPLETE, end transaction record written,
            // and removed from the transaction table).
            if (getCLRNew == 0) {
                Long transNum = nextRec.getTransNum().get();
                TransactionTableEntry prevValue = transactionTable.get(transNum);
                prevValue.transaction.cleanup();
                prevValue.transaction.setStatus(Transaction.Status.COMPLETE);
                end(nextRec.getTransNum().get());
                transactionTable.remove(prevValue.transaction.getTransNum());
            } else {
                prioQueue.add(getCLRNew);
            }
        }
    }

    /**
     * Removes pages from the DPT that are not dirty in the buffer manager.
     * This is slow and should only be used during recovery.
     */
    void cleanDPT() {
        Set<Long> dirtyPages = new HashSet<>();
        bufferManager.iterPageNums((pageNum, dirty) -> {
            if (dirty) dirtyPages.add(pageNum);
        });
        Map<Long, Long> oldDPT = new HashMap<>(dirtyPageTable);
        dirtyPageTable.clear();
        for (long pageNum : dirtyPages) {
            if (oldDPT.containsKey(pageNum)) {
                dirtyPageTable.put(pageNum, oldDPT.get(pageNum));
            }
        }
    }

    // Helpers /////////////////////////////////////////////////////////////////
    /**
     * Comparator for Pair<A, B> comparing only on the first element (type A),
     * in reverse order.
     */
    private static class PairFirstReverseComparator<A extends Comparable<A>, B> implements
            Comparator<Pair<A, B>> {
        @Override
        public int compare(Pair<A, B> p0, Pair<A, B> p1) {
            return p1.getFirst().compareTo(p0.getFirst());
        }
    }
}
