package dev.codescreen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    private static FormatNumber formatNumber = new FormatNumber();

    /**
     * Parse the CSV-structured file
     *
     * @param filePath The path to the files
     * @return a list of transaction objects
     */

    public static List<SampleTestTransaction> parseCsv(String filePath) throws Exception {
        List<SampleTestTransaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                SampleTestTransaction transaction = new SampleTestTransaction(
                        values[0],  // action
                        values[1],  // msgId
                        values[2],  // userId
                        values[3],  // debitOrCredit
                        formatNumber.formatNumber(values[4]),  // amount
                        values[5],  // responseCode
                        values[6]   // final balance
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

}
