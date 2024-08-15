package com.aeon.library.util;

public class IsbnUtil {
    public static boolean validateISBN10(String isbn) {
        isbn = isbn.replace("-", "");
        if (isbn.length() != 10) {
            return false;
        }

        try {
            int total = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                total += digit * (10 - i);
            }

            char checksum = isbn.charAt(9);
            if (checksum == 'X') {
                total += 10;
            } else {
                total += Integer.parseInt(String.valueOf(checksum));
            }

            return total % 11 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
