package com.example.bookapp.service;

import com.example.bookapp.model.Book;
import com.example.bookapp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookServiceImplOwnershipTest {

    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    void saveBook_persistsBookWithOwnerId() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("123456");
        book.setOwnerId(1L);

        bookService.saveBook(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getBookById_returnsBookWhenFound() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setOwnerId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Book", result.get().getTitle());
        assertEquals(1L, result.get().getOwnerId());
    }

    @Test
    void getBookById_returnsEmptyWhenNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllBooks_returnsAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setOwnerId(1L);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setOwnerId(2L);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Book 1", result.get(0).getTitle());
        assertEquals("Book 2", result.get(1).getTitle());
        assertEquals(1L, result.get(0).getOwnerId());
        assertEquals(2L, result.get(1).getOwnerId());
    }

    @Test
    void deleteBook_deletesBookById() {
        Long bookId = 1L;

        bookService.deleteBook(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void saveBook_setsOwnerIdCorrectly() {
        Book book = new Book();
        book.setTitle("My Book");
        book.setAuthor("Me");
        book.setIsbn("999");
        book.setOwnerId(42L);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("My Book");
        savedBook.setAuthor("Me");
        savedBook.setIsbn("999");
        savedBook.setOwnerId(42L);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book saved = bookService.saveBook(book);

        assertNotNull(saved);
        assertEquals(42L, saved.getOwnerId());
        verify(bookRepository, times(1)).save(book);
    }
}
