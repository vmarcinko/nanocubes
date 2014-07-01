package vmarcinko.nanocubes.temporal;

import vmarcinko.nanocubes.Content;

import java.util.ArrayList;
import java.util.List;

public class TemporalTable implements Content {
    private final List<Bin> list = new ArrayList<>();

    public void registerDataPoint(long dataPointBinTimestamp) {
        // let's go reverse until
        boolean exactFound = false;
        int previousTimestampBinIndex = -1;
        for (int i = list.size() - 1; i >= 0; i--) {
            Bin bin = list.get(i);
            if (bin.getTimestamp() < dataPointBinTimestamp) {
                previousTimestampBinIndex = i;
                break;

            } else if (bin.getTimestamp() == dataPointBinTimestamp) {
                exactFound = true;
            }
            bin.incrementCount();
        }

        if (!exactFound) {
            long exactBinCount = previousTimestampBinIndex == -1 ? 0 : list.get(previousTimestampBinIndex).getCount();
            Bin exactBin = new Bin(dataPointBinTimestamp, exactBinCount);
            exactBin.incrementCount();
            list.add(previousTimestampBinIndex + 1, exactBin);
        }
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

        for (int i = 0; i < bucketCount; i++) {
            long bucketStartTime = startTime + i * bucketLength;

            long startBinEventCount = (previousEndBinEventCount == -1) ? getBinEventCount(findLowerThanTarget(bucketStartTime, false)) : previousEndBinEventCount;

            long bucketEndTime = bucketStartTime + bucketLength - 1;
            int endBucketBinIndex = findLowerThanTarget(bucketEndTime, true);
            long endBinEventCount = getBinEventCount(endBucketBinIndex);
            previousEndBinEventCount = endBinEventCount;

            long bucketEventCount = endBinEventCount - startBinEventCount;
            result.add(new Bin(bucketStartTime, bucketEventCount));
        }
        return result;
    }

    private long getBinEventCount(int binIndex) {
        if (binIndex == -1) {
            return 0;
        }
        return list.get(binIndex).getCount();
    }

    private int findLowerThanTarget(long targetBinTimestamp, boolean acceptExactMatch) {
        int low = -1;
        int high = list.size() - 1;

        while (low != high) {
            int sum = low + high;
            int addition = sum < 0 ? 0 : sum % 2;
            int midBinIndex = (sum / 2) + addition;
            long midBinTimestamp = list.get(midBinIndex).getTimestamp();

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
        return new TemporalTable();
    }

    @Override
    public void appendPrettyPrint(StringBuilder sb, int depth) {
        sb.append("<TEMPORAL_TABLE>");
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
