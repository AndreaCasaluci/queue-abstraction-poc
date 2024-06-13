package org.orbyta.queueabstractionpoc.queue;
import org.orbyta.queueabstractionpoc.model.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageConsumer {

    @Bean
    public Consumer<Book> input() {
        return book -> System.out.println("I'm the Consumer and that's what I've read from the queue: \n"+book.toString());
    }
}
