package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import domain.Book;

class testBookClass {

	@Test
	void test() {
        Book book = new Book("Cukur", "Yousef Hajeer", "1234567890");

        assertEquals("Cukur", book.getTitle());
        assertEquals("Yousef Hajeer", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertFalse(book.isBorrowed());

        book.setBorrowed(true);
        assertTrue(book.isBorrowed());

        book.setBorrowed(false);
        assertFalse(book.isBorrowed());
	}

}
