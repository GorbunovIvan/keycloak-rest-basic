package org.example.service;

import org.example.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BookService {

    private final List<Book> books;

    public BookService() {
        this.books = new ArrayList<>();
    }

    public List<Book> getAll() {
        return new ArrayList<>(books);
    }

    public Book getById(Integer id) {
        return books.stream()
                .filter(b -> Objects.equals(b.getId(), id))
                .findAny()
                .orElse(null);
    }

    public Book create(Book book) {
        var newId = getNextId();
        book.setId(newId);
        books.add(book);
        return book;
    }

    public void delete(Integer id) {
        var book = getById(id);
        if (book != null) {
            books.remove(book);
        }
    }

    private Integer getNextId() {
        return books.stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0) + 1;
    }
}
