package com.dreaminginprod.cache;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CacheConfig(cacheNames = "books")
@Repository
class BookRepositoryImpl implements BookRepository {

    private final Map<String, Book> fakeRepository = new HashMap<>();

    @CachePut(key = "#result.id")
    @Override
    public Book create(NewBook newBook) {
        final Book book = new Book(
                UUID.randomUUID().toString(),
                newBook.author(),
                newBook.title()
        );
        fakeRepository.put(book.id(), book);
        System.out.println("Book created: [%s]".formatted(book));
        System.out.println("Current repository state: [%s]".formatted(fakeRepository));
        return book;
    }

    @Cacheable
    @Override
    public Optional<Book> read(String id) {
        emulateLongRunning();
        Book book = fakeRepository.get(id);
        System.out.println("Book retrieved without cache usage: [%s]".formatted(book));
        System.out.println("Current repository state: [%s]".formatted(fakeRepository));
        return Optional.ofNullable(book);
    }

    @CachePut(key = "#p0.id()")

    @Override
    public Book update(Book book) {
        if (fakeRepository.containsKey(book.id())) {
            fakeRepository.put(book.id(), book);
        } else {
            throw new IllegalStateException("Nothing to update");
        }
        System.out.println("Book with id: [%s] Updated".formatted(book.id()));
        System.out.println("Current repository state: [%s]".formatted(fakeRepository));
        return book;
    }

    @CacheEvict(key = "#p0")
    @Override
    public void delete(String id) {
        if (fakeRepository.containsKey(id)) {
            fakeRepository.remove(id);
        } else {
            throw new IllegalStateException("Nothing to delete");
        }
        System.out.println("Deleted book by id: [%s]".formatted(id));
        System.out.println("Current repository state: [%s]".formatted(fakeRepository));
    }

    private void emulateLongRunning() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
