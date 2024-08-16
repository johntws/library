package com.aeon.library.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testValidateISBN10_withValidISBN() {
        // Arrange
        String validIsbn = "0471958697";

        // Act
        boolean result = BookUtil.validateISBN10(validIsbn);

        // Assert
        assertTrue(result, "Valid ISBN-10 should return true");
    }

    @Test
    public void testValidateISBN10_withValidISBNEndingWithX() {
        // Arrange
        String validIsbnWithX = "080442957X";

        // Act
        boolean result = BookUtil.validateISBN10(validIsbnWithX);

        // Assert
        assertTrue(result, "Valid ISBN-10 ending with 'X' should return true");
    }

    @Test
    public void testValidateISBN10_withInvalidISBN() {
        // Arrange
        String invalidIsbn = "0471958698";

        // Act
        boolean result = BookUtil.validateISBN10(invalidIsbn);

        // Assert
        assertFalse(result, "Invalid ISBN-10 should return false");
    }

    @Test
    public void testValidateISBN10_withNonNumericISBN() {
        // Arrange
        String nonNumericIsbn = "04719586X9";

        // Act
        boolean result = BookUtil.validateISBN10(nonNumericIsbn);

        // Assert
        assertFalse(result, "Non-numeric ISBN-10 should return false");
    }

    @Test
    public void testValidateISBN10_withInvalidLength() {
        // Arrange
        String invalidLengthIsbn = "123456789";

        // Act
        boolean result = BookUtil.validateISBN10(invalidLengthIsbn);

        // Assert
        assertFalse(result, "ISBN-10 with invalid length should return false");
    }

    @Test
    public void testValidateISBN10_withHyphenatedISBN() {
        // Arrange
        String hyphenatedIsbn = "0-471-95869-7";

        // Act
        boolean result = BookUtil.validateISBN10(hyphenatedIsbn);

        // Assert
        assertTrue(result, "Hyphenated ISBN-10 should return true");
    }

    @Test
    public void testValidateISBN10_withInvalidChecksum() {
        // Arrange
        String invalidChecksumIsbn = "0306406153";  // valid checksum should be 2

        // Act
        boolean result = BookUtil.validateISBN10(invalidChecksumIsbn);

        // Assert
        assertFalse(result, "ISBN-10 with invalid checksum should return false");
    }
}