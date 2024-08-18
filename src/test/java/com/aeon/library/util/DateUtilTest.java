package com.aeon.library.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testStringToDate_withValidDate() {
        // Arrange
        String validDateStr = "2024-08-16";
        SimpleDateFormat formatter = DateUtil.YYYY_MM_DD;

        // Act
        Date date = DateUtil.stringToDate(validDateStr, formatter);

        // Assert
        assertNotNull(date, "Date should not be null");
        assertEquals(Date.valueOf("2024-08-16"), date);
    }

    @Test
    public void testStringToDate_withDifferentFormat() {
        // Arrange
        String validDateStr = "16/08/2024";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Act
        Date date = DateUtil.stringToDate(validDateStr, formatter);

        // Assert
        assertNotNull(date, "Date should not be null");
        assertEquals(Date.valueOf("2024-08-16"), date);
    }

    @Test
    public void testStringToDate_withInvalidDate() {
        // Arrange
        String invalidDateStr = "invalid-date";
        SimpleDateFormat formatter = DateUtil.YYYY_MM_DD;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            DateUtil.stringToDate(invalidDateStr, formatter);
        }, "Expected a RuntimeException for invalid date format");
    }

    @Test
    public void testStringToDate_withNullDate() {
        // Arrange
        String nullDateStr = null;
        SimpleDateFormat formatter = DateUtil.YYYY_MM_DD;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            DateUtil.stringToDate(nullDateStr, formatter);
        }, "Expected a NullPointerException for null date string");
    }
}