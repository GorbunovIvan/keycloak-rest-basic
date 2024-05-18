package org.example.service;

import org.example.model.Book;

import java.util.List;

public interface BookService {
    List<Book> getAll();
    Book getById(Integer id);
    Book create(Book book);
    void delete(Integer id);
}
