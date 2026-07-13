package com.example.bookapp.controller;

import com.example.bookapp.model.Book;
import com.example.bookapp.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookControllerTest {

    private BookController bookController;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        bookController = new BookController(bookService);
    }

    @Test
    void getAllBooks_returnsListFromService() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");

        when(bookService.getAllBooks()).thenReturn(List.of(book1, book2));

        // Act
        List<Book> result = bookController.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_returnsBookWhenFound() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        when(bookService.getBookById(1L)).thenReturn(java.util.Optional.of(book));

        // Act
        var result = bookController.getBookById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Book", result.get().getTitle());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_returnsEmptyWhenNotFound() {
        // Arrange
        when(bookService.getBookById(999L)).thenReturn(java.util.Optional.empty());

        // Act
        var result = bookController.getBookById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(bookService, times(1)).getBookById(999L);
    }

    @Test
    void createBook_savesAndReturnsBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor("New Author");
        book.setIsbn("999999");

        when(bookService.saveBook(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookController.createBook(book);

        // Assert
        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        verify(bookService, times(1)).saveBook(book);
    }

    @Test
    void updateBook_updatesBookWhenFound() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old Title");
        existingBook.setAuthor("Old Author");
        existingBook.setIsbn("111111");

        Book updatedBook = new Book();
        updatedBook.setTitle("New Title");
        updatedBook.setAuthor("New Author");
        updatedBook.setIsbn("222222");

        when(bookService.getBookById(1L)).thenReturn(java.util.Optional.of(existingBook));
        when(bookService.saveBook(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(existingBook.getId());
            return book;
        });

        // Act
        Book result = bookController.updateBook(1L, updatedBook);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        verify(bookService, times(1)).getBookById(1L);
        verify(bookService, times(1)).saveBook(any(Book.class));
    }

    @Test
    void updateBook_throwsExceptionWhenBookNotFound() {
        // Arrange
        when(bookService.getBookById(999L)).thenReturn(java.util.Optional.empty());

        // Act
        assertThrows(RuntimeException.class, () -> bookController.updateBook(999L, new Book()));

        // Assert
        verify(bookService, times(1)).getBookById(999L);
        verify(bookService, never()).saveBook(any(Book.class));
    }

    @Test
    void deleteBook_deletesBookById() {
        // Arrange
        Long bookId = 1L;

        // Act
        bookController.deleteBook(bookId);

        // Assert
        verify(bookService, times(1)).deleteBook(bookId);
    }
}
