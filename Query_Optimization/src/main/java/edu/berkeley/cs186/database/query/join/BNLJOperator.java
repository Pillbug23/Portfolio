package edu.berkeley.cs186.database.query.join;

import edu.berkeley.cs186.database.TransactionContext;
import edu.berkeley.cs186.database.common.iterator.BacktrackingIterator;
import edu.berkeley.cs186.database.query.JoinOperator;
import edu.berkeley.cs186.database.query.QueryOperator;
import edu.berkeley.cs186.database.table.Record;
import edu.berkeley.cs186.database.table.Schema;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Performs an equijoin between two relations on leftColumnName and
 * rightColumnName respectively using the Block Nested Loop Join algorithm.
 */
public class BNLJOperator extends JoinOperator {
    protected int numBuffers;

    public BNLJOperator(QueryOperator leftSource,
                        QueryOperator rightSource,
                        String leftColumnName,
                        String rightColumnName,
                        TransactionContext transaction) {
        super(leftSource, materialize(rightSource, transaction),
                leftColumnName, rightColumnName, transaction, JoinType.BNLJ
        );
        this.numBuffers = transaction.getWorkMemSize();
        this.stats = this.estimateStats();
    }

    @Override
    public Iterator<Record> iterator() {
        return new BNLJIterator();
    }

    @Override
    public int estimateIOCost() {
        //This method implements the IO cost estimation of the Block Nested Loop Join
        int usableBuffers = numBuffers - 2;
        int numLeftPages = getLeftSource().estimateStats().getNumPages();
        int numRightPages = getRightSource().estimateIOCost();
        return ((int) Math.ceil((double) numLeftPages / (double) usableBuffers)) * numRightPages +
               getLeftSource().estimateIOCost();
    }

    /**
     * A record iterator that executes the logic for a simple nested loop join.
     * Look over the implementation in SNLJOperator if you want to get a feel
     * for the fetchNextRecord() logic.
     */
    private class BNLJIterator implements Iterator<Record>{
        // Iterator over all the records of the left source
        private Iterator<Record> leftSourceIterator;
        // Iterator over all the records of the right source
        private BacktrackingIterator<Record> rightSourceIterator;
        // Iterator over records in the current block of left pages
        private BacktrackingIterator<Record> leftBlockIterator;
        // Iterator over records in the current right page
        private BacktrackingIterator<Record> rightPageIterator;
        // The current record from the left relation
        private Record leftRecord;
        // The next record to return
        private Record nextRecord;


        private BNLJIterator() {
            super();
            this.leftSourceIterator = getLeftSource().iterator();
            this.fetchNextLeftBlock();

            this.rightSourceIterator = getRightSource().backtrackingIterator();
            this.rightSourceIterator.markNext();
            this.fetchNextRightPage();

            this.nextRecord = null;
        }

        /**
         * Fetch the next block of records from the left source.
         * leftBlockIterator should be set to a backtracking iterator over up to
         * B-2 pages of records from the left source, and leftRecord should be
         * set to the first record in this block.
         *
         * If there are no more records in the left source, this method should
         * do nothing.
         *
         * You may find QueryOperator#getBlockIterator useful here.
         *
         *      * markPrev() marks the last returned value of the iterator, which is the last
         *      * returned value of next().
         *      *
         *      * Calling markPrev() on an iterator that has not yielded a record yet,
         *      * or that has not yielded a record since the last reset() call does nothing.
         *      *
         *      * markNext() marks the next returned value of the iterator, which is the
         *      * value returned by the next call of next().
         *      *
         *      * Calling markNext() on an iterator that has no records left does nothing.
         *      *
         *      *
         *      * reset() resets the iterator to the last marked location. The subsequent
         *      * call to next() should return the value that was marked. If nothing has
         *      * been marked, reset() does nothing. You may reset() to the same point as
         *      * many times as desired until a new mark is set.
         *      *
         *  iterator with the values [1,2,3]:
         *  * BackTrackingIterator<Integer> iter = new BackTrackingIteratorImplementation();
         *  * iter.next();     // returns 1
         *  * iter.next();     // returns 2
         *  * iter.markPrev(); // marks the previously returned value, 2
         *  * iter.next();     // returns 3
         *  * iter.hasNext();  // returns false
         *  * iter.reset();    // reset to the marked value (line 5)
         *  * iter.hasNext();  // returns true
         *  * iter.next();     // returns 2
         *  * iter.markNext(); // mark the value to be returned next, 3
         *  * iter.next();     // returns 3
         *  * iter.hasNext();  // returns false
         *  * iter.reset();    // reset to the marked value (line 11)
         *  * iter.hasNext();  // returns true
         *  * iter.next();     // returns 3
         *  GetBlockIterator 3 parameters --
         *  * @param records an iterator of records
         *  * @param schema the schema of the records yielded from `records`
         *  * @param maxPages the maximum number of pages worth of records to consume
         *  */
        private void fetchNextLeftBlock() {
            // TODO(proj3_part1): implement
            if (!this.leftSourceIterator.hasNext()) { /*Check the next block if empty*/
                return;
            }
            /*Converts source input to block*/
            this.leftBlockIterator = getBlockIterator(this.leftSourceIterator, getLeftSource().getSchema(), numBuffers - 2);
             /*Marknext, saves spot when resetting*/
            this.leftBlockIterator.markNext();
            this.leftRecord = this.leftBlockIterator.next(); /*Read first record from block, assign it to LR*/
            /*Do nothing if block is out of pages*/
        }

        /**
         * Fetch the next page of records from the right source.
         * rightPageIterator should be set to a backtracking iterator over up to
         * one page of records from the right source.
         *
         * If there are no more records in the right source, this method should
         * do nothing.
         *
         * You may find QueryOperator#getBlockIterator useful here.
         */
        private void fetchNextRightPage() {
            // TODO(proj3_part1): implement
            if (!this.rightSourceIterator.hasNext()) {
                return;
            }
            int upToOne = 1;
            this.rightPageIterator = QueryOperator.getBlockIterator(this.rightSourceIterator, getRightSource().getSchema(), 1);
            this.rightPageIterator.markNext();
            /*Do nothing if block is out of pages*/
        }

        /**
         * Returns the next record that should be yielded from this join,
         * or null if there are no more records to join.
         *
         * You may find JoinOperator#compare useful here. (You can call compare
         * function directly from this file, since BNLJOperator is a subclass
         * of JoinOperator).
         */
        private Record fetchNextRecord() {
            // TODO(proj3_part1): implement
            if (this.leftBlockIterator == null) { /*Check if we are done iterating over block*/
                // The left source was empty, nothing to fetch
                return null;
            }
            nextRecord = null;
            while (this.nextRecord == null) {
                if (this.rightPageIterator.hasNext()) { /*Case 1:Right Page still has more pages*/
                    // there's a next right record, join it if there's a match
                    Record rightRecord = this.rightPageIterator.next(); /*Iterate thru each record*/
                    if (compare(this.leftRecord, rightRecord) == 0) {
                        this.nextRecord = this.leftRecord.concat(rightRecord);
                    }
                } else {
                    if (this.leftBlockIterator.hasNext()) { /*Case 2:R no more,L has values*/
                        // there's no more right records but there's still left
                        // records. Advance left and reset right
                        this.leftRecord = this.leftBlockIterator.next();
                        this.rightPageIterator.reset();
                    } else {
                        fetchNextRightPage();
                        if (this.rightPageIterator.hasNext()) { /*Case 3: Neither RP,LB have values to yield, but theres still p*/
                            /*Were done with the current right page, and the current block left has values left in the block*/
                            this.leftBlockIterator.reset(); /*Resets back to page 0*/
                            this.leftRecord = this.leftBlockIterator.next();
                        } else {
                            fetchNextLeftBlock();
                            if (this.leftBlockIterator.hasNext()) {
                                this.rightSourceIterator.reset();
                                fetchNextRightPage();
                            } else {
                                return nextRecord;
                            }
                        }
                    }
                }
            }
            return nextRecord;
        }

        /**
         * @return true if this iterator has another record to yield, otherwise
         * false
         */
        @Override
        public boolean hasNext() {
            if (this.nextRecord == null) this.nextRecord = fetchNextRecord();
            return this.nextRecord != null;
        }

        /**
         * @return the next record from this iterator
         * @throws NoSuchElementException if there are no more records to yield
         */
        @Override
        public Record next() {
            if (!this.hasNext()) throw new NoSuchElementException();
            Record nextRecord = this.nextRecord;
            this.nextRecord = null;
            return nextRecord;
        }
    }
}
