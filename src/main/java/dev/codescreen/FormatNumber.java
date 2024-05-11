package dev.codescreen;

import java.text.DecimalFormat;

public class FormatNumber {

    /**
     * Transform integer or one-digit decimal into two-digit decimal.
     * @param num Input variable type is String.
     * @return return a formatted string.
     */

    public static String formatNumber(String num) {
        DecimalFormat formatter = new DecimalFormat("0.00"); // Ensures minimum of two decimal places

        try {
            // Attempt to parse the string to a double
            double value = Double.parseDouble(num);

            // Check if the number already contains decimal places beyond just zeros
            if (num.contains(".") && !num.matches("\\d+\\.0+$")) {
                return num; // Return as is, if it already contains specific decimal places like 100.23
            }

            // Otherwise, format it to have two decimal places
            return formatter.format(value);
        } catch (NumberFormatException e) {
            // Handle the case where the input string is not a valid number
            System.err.println("Invalid number format: " + num);
            return num;
        }
    }
}