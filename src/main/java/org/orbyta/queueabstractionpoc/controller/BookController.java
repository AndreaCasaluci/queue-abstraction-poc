package org.orbyta.queueabstractionpoc.controller;

import jakarta.validation.Valid;
import org.orbyta.queueabstractionpoc.dto.CreateBookDto;
import org.orbyta.queueabstractionpoc.model.Book;
import org.orbyta.queueabstractionpoc.queue.MessageProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final MessageProducer messageProducer;

    public BookController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping("/new")
    public Book createNewBook(@Valid @RequestBody CreateBookDto bookDto) {
        return messageProducer.output(bookDto);
    }
}
