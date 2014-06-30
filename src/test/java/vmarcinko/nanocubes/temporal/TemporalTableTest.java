package vmarcinko.nanocubes.temporal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TemporalTableTest {
    private static final long BIN_DURATION = 1000 * 60 * 60;

    public static void main(String[] args) {
        TemporalTable table = new TemporalTable();
        table.registerEvent(newBinTimestamp("20140101 0102"));
        table.registerEvent(newBinTimestamp("20140101 0103"));
        table.registerEvent(newBinTimestamp("20140101 0301"));
        table.registerEvent(newBinTimestamp("20140101 0503"));

        System.out.println("table = " + table);

/*
        long startTime = newBinTimestamp("20130101 0503");

        List<Bin> result1 = table.queryCounts(startTime, newBinTimestamp("20150101 0503") - startTime, 1);
        System.out.println("result1 = " + result1);

        List<Bin> result2 = table.queryCounts(startTime, newBinTimestamp("20140101 0301") - startTime, 1);
        System.out.println("result2 = " + result2);

        List<Bin> result3 = table.queryCounts(startTime, newBinTimestamp("20140101 0503") - startTime, 1);
        System.out.println("result3 = " + result3);

        long startTime1 = newBinTimestamp("20140101 0301");
        System.out.println("startTime1 = " + startTime1);
        long bucketLength = newBinTimestamp("20140101 0503") - newBinTimestamp("20140101 0301");
        System.out.println("bucketLength = " + bucketLength);

        List<Bin> result4 = table.queryCounts(startTime1, bucketLength, 1);
        System.out.println("result4 = " + result4);
*/

        List<Bin> result5 = table.queryCounts(newBinTimestamp("20140101 0102"), 1, 10);
        System.out.println("result5 = " + result5);
    }

    private static long newBinTimestamp(String text) {
        try {
            Date date = new SimpleDateFormat("yyyyMMdd HHmm").parse(text);
            return date.getTime() / BIN_DURATION;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
