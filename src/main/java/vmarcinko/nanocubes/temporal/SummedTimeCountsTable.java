package vmarcinko.nanocubes.temporal;

import vmarcinko.nanocubes.Content;

import java.util.ArrayList;
import java.util.List;

public class SummedTimeCountsTable implements Content {
    private final List<Bin> bins = new ArrayList<>();

    public void registerDataPoint(long dataPointBinTimestamp) {
        // let's go reverse until
        boolean exactMatchFound = false;
        int previousTimestampBinIndex = -1;
        for (int i = bins.size() - 1; i >= 0; i--) {
            Bin bin = bins.get(i);
            if (bin.getTimestamp() < dataPointBinTimestamp) {
                previousTimestampBinIndex = i;
                break;

            } else if (bin.getTimestamp() == dataPointBinTimestamp) {
                exactMatchFound = true;
            }
            bin.incrementCount();
        }

        if (!exactMatchFound) {
            long exactBinCount = previousTimestampBinIndex == -1 ? 0 : bins.get(previousTimestampBinIndex).getCount();
            Bin exactBin = new Bin(dataPointBinTimestamp, exactBinCount);
            exactBin.incrementCount();
            bins.add(previousTimestampBinIndex + 1, exactBin);
        }
    }

    public long queryTotalCount() {
        List<Bin> globalBin = queryCounts(Long.MIN_VALUE / 2, Long.MAX_VALUE, 1);
        return globalBin.get(0).getCount();
    }

    public List<Bin> queryCounts(long startTime, long bucketLength, long bucketCount) {
        if (bucketLength < 1) {
            throw new IllegalArgumentException("Bucket length cannot be smaller than 1, but is " + bucketLength);
        }
        if (bucketCount < 1) {
            throw new IllegalArgumentException("Bucket count cannot be smaller than 1, but is " + bucketCount);
        }

        List<Bin> result = new ArrayList<>();

        long previousEndBinEventCount = -1;

        // TODO: optimize to not search for out-of-range buckets ...
        for (int i = 0; i < bucketCount; i++) {
            long bucketStartTime = startTime + i * bucketLength;

            long startBinEventCount = (previousEndBinEventCount == -1) ? getBinEventCount(findExactMatchOrPredecessor(bucketStartTime, false)) : previousEndBinEventCount;

            long bucketEndTime = bucketStartTime + bucketLength - 1;
            int endBucketBinIndex = findExactMatchOrPredecessor(bucketEndTime, true);
            long endBinEventCount = getBinEventCount(endBucketBinIndex);
            previousEndBinEventCount = endBinEventCount;

            long bucketEventCount = endBinEventCount - startBinEventCount;
            if (bucketEventCount > 0) {
                result.add(new Bin(bucketStartTime, bucketEventCount));
            }
        }
        return result;
    }

    private long getBinEventCount(int binIndex) {
        if (binIndex == -1) {
            return 0;
        }
        return bins.get(binIndex).getCount();
    }

    private int findExactMatchOrPredecessor(long targetBinTimestamp, boolean acceptExactMatch) {
        int low = -1;
        int high = bins.size() - 1;

        while (low != high) {
            int sum = low + high;
            int addition = sum < 0 ? 0 : sum % 2;
            int midBinIndex = (sum / 2) + addition;
            long midBinTimestamp = bins.get(midBinIndex).getTimestamp();

            if (acceptExactMatch && (midBinTimestamp == targetBinTimestamp)) {
                return midBinIndex;
            } else if (midBinTimestamp >= targetBinTimestamp) {
                high = midBinIndex - 1;
            } else {
                low = midBinIndex;
            }
        }
        /* Now, low and high both point to the element in question. */
        return low;
    }

    @Override
    public Content shallowCopy() {
        return new SummedTimeCountsTable();
    }

    @Override
    public void appendPrettyPrint(StringBuilder sb, int depth) {
        sb.append("<TEMPORAL_TABLE>");
    }

    @Override
    public String toString() {
        return bins.toString();
    }
}
