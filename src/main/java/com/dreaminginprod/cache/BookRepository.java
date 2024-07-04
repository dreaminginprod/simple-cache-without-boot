package com.dreaminginprod.cache;


import java.util.Optional;

public interface BookRepository {

    Book create(NewBook book);


    Optional<Book> read(String id);


    Book update(Book book);

    void delete(String id);
}
