package org.orbyta.queueabstractionpoc.queue;

import org.orbyta.queueabstractionpoc.dto.CreateBookDto;
import org.orbyta.queueabstractionpoc.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class MessageProducer {

    @Value("${spring.cloud.stream.bindings.output-out-0.destination}")
    private String outputTopic;

    private final StreamBridge streamBridge;

    public MessageProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    // Uncomment this function to obtain a supplier that generates new queue message in loop without any trigger.
    /*@Bean
    public Supplier<Book> output() {
        return () -> {
            Book book = new Book("Sample Title", "Andrea Casaluci");
            streamBridge.send(outputTopic, book);
            return book;
        };
    }*/

    public Book output(CreateBookDto bookDto) {
        Book book = new Book(bookDto.getTitle(), bookDto.getAuthor());
        streamBridge.send(outputTopic, book);
        return book;
    }
}

