package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Book;
import com.aeon.library.entity.Copy;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookRepository;
import com.aeon.library.repo.CopyRepository;
import com.aeon.library.util.BookUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CopyRepository copyRepository;

    @Mock
    private BookUtil bookUtil;

    @InjectMocks
    BookServiceImpl bookService;

    private CreateBookReq validRequest;
    private CreateBookReq invalidRequest;
    private GetBookReq request;

    private Book existingBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create valid request
        validRequest = new CreateBookReq();
        validRequest.setIsbn("0545162076");
        validRequest.setAuthor("John Doe");
        validRequest.setTitle("Sample Book");

        // Create invalid request
        invalidRequest = new CreateBookReq();
        invalidRequest.setIsbn("invalid_isbn");
        invalidRequest.setAuthor("Jane Doe");
        invalidRequest.setTitle("Another Book");

        // Create existing book
        existingBook = new Book();
        existingBook.setIsbn("0545162076");
        existingBook.setAuthor("John Doe");
        existingBook.setTitle("Sample Book");

        request = new GetBookReq();
        request.setIsbn("0545162076");
        request.setAuthor("John Doe");
        request.setTitle("Sample Book");
        request.setPageNo(1);
        request.setPageSize(10);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testRegisterBook_withValidISBNAndNewBook() throws GeneralException {
        // Arrange
        when(bookRepository.findById(validRequest.getIsbn())).thenReturn(Optional.empty());

        // Act
        CreateBookRes response = bookService.registerBook(validRequest);

        // Assert
        assertEquals(validRequest.getIsbn(), response.getIsbn());
        assertEquals(validRequest.getAuthor(), response.getAuthor());
        assertEquals(validRequest.getTitle(), response.getTitle());
        assertFalse(response.isBorrowed());

        verify(bookRepository, times(1)).save(any(Book.class));
        verify(copyRepository, times(1)).save(any(Copy.class));
    }

    @Test
    public void testRegisterBook_withInvalidISBN() {
        // Act & Assert
        assertThrows(GeneralException.class, () -> bookService.registerBook(invalidRequest), "Invalid ISBN");

        verify(bookRepository, times(0)).findById(anyString());
        verify(bookRepository, times(0)).save(any(Book.class));
        verify(copyRepository, times(0)).save(any(Copy.class));
    }

    @Test
    public void testRegisterBook_withExistingBookAndMatchingDetails() throws GeneralException {
        // Arrange
        when(bookRepository.findById(validRequest.getIsbn())).thenReturn(Optional.of(existingBook));

        // Act
        CreateBookRes response = bookService.registerBook(validRequest);

        // Assert
        assertEquals(existingBook.getIsbn(), response.getIsbn());
        assertEquals(existingBook.getAuthor(), response.getAuthor());
        assertEquals(existingBook.getTitle(), response.getTitle());
        assertFalse(response.isBorrowed());

        verify(bookRepository, times(0)).save(any(Book.class));  // No new book should be saved
        verify(copyRepository, times(1)).save(any(Copy.class));  // Only copy should be saved
    }

    @Test
    public void testRegisterBook_withExistingBookButNonMatchingDetails() {
        // Arrange
        when(bookRepository.findById(validRequest.getIsbn())).thenReturn(Optional.of(existingBook));

        validRequest.setAuthor("Different Author");
        validRequest.setTitle("Different Title");

        // Act & Assert
        assertThrows(GeneralException.class, () -> bookService.registerBook(validRequest), "Author and/or Title does not match existing ISBN");

        verify(copyRepository, times(0)).save(any(Copy.class));  // Copy should not be saved
    }

    @Test
    public void testGetBooks_withValidSearchCriteria() {
        // Arrange
        Book book1 = new Book();
        book1.setIsbn("0545162076");
        book1.setAuthor("John Doe");
        book1.setTitle("Sample Book");

        Copy copy1 = new Copy();
        copy1.setId(1L);
        copy1.setBorrowed(false);

        book1.setCopies(Arrays.asList(copy1));

        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book1), PageRequest.of(0, 10), 1);

        when(bookRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(bookPage);

        // Act
        GetBookRes response = bookService.getBooks(request);

        // Assert
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getCurrentPage());
        assertEquals(1, response.getBookList().size());

        BookDto bookDto = response.getBookList().get(0);
        assertEquals("0545162076", bookDto.getIsbn());
        assertEquals("John Doe", bookDto.getAuthor());
        assertEquals("Sample Book", bookDto.getTitle());
        assertEquals(1, bookDto.getCopies().size());

        CopyDto copyDto = bookDto.getCopies().get(0);
        assertEquals(1L, copyDto.getId());
        assertEquals(false, copyDto.isBorrowed());
    }

    @Test
    public void testGetBooks_withNoResults() {
        // Arrange
        Page<Book> bookPage = new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);

        when(bookRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(bookPage);

        // Act
        GetBookRes response = bookService.getBooks(request);

        // Assert
        assertEquals(0, response.getTotalPages());
        assertEquals(1, response.getCurrentPage());
        assertEquals(0, response.getBookList().size());
    }

    @Test
    public void testGetBooks_withPagination() {
        // Arrange
        Book book1 = new Book();
        book1.setIsbn("0545162076");
        book1.setAuthor("John Doe");
        book1.setTitle("Sample Book");

        Copy copy1 = new Copy();
        copy1.setId(1L);
        copy1.setBorrowed(false);

        book1.setCopies(Arrays.asList(copy1));

        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book1), PageRequest.of(1, 10), 2);

        when(bookRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(bookPage);

        // Act
        request.setPageNo(2);  // 2nd page (pageNo starts from 1 in request)
        GetBookRes response = bookService.getBooks(request);

        // Assert
        assertEquals(2, response.getTotalPages());
        assertEquals(2, response.getCurrentPage());
        assertEquals(1, response.getBookList().size());

        BookDto bookDto = response.getBookList().get(0);
        assertEquals("0545162076", bookDto.getIsbn());
        assertEquals("John Doe", bookDto.getAuthor());
        assertEquals("Sample Book", bookDto.getTitle());
        assertEquals(1, bookDto.getCopies().size());

        CopyDto copyDto = bookDto.getCopies().get(0);
        assertEquals(1L, copyDto.getId());
        assertEquals(false, copyDto.isBorrowed());
    }
}