

# Queue Abstraction POC

In this project, we leverage the [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) framework, which allows interaction with various types of queues while remaining agnostic to their implementations.

## Requirements


Below are the elements required for the correct functioning of the project:

- **RabbitMQ:** A local instance of RabbitMQ
    - **Exchange:**
        - An exchange named **"pocinput"** of type **"topic"**
        - An exchange named **"pocoutput"** of type **"topic"**
    - **Queue:**
        - A queue named **"pocqueue"**
    - **Bindings:**
        - A binding that links the **"pocinput"** exchange to the **"pocqueue"** queue with the *routing key* **"testkey"**
        - A binding that links the **"pocoutput"** exchange to the **"pocqueue"** queue with the *routing key* **"testkey"**

This configuration reflects the project, with a reading component (*Consumer*) and a writing component (*Producer*) operating on the same queue.


## Application Properties


In this section, the various properties present in the `application.properties` file are explained.

### General

- `spring.application.name=Queue-Abstraction-POC`: Application name.
- `spring.cloud.function.definition=input;output`: Specifies the names of the methods that take part in queue operations:
    - `input` : Method related to the *Consumer*.
    - `output` : Method related to the *Producer*.

### Input Binding Configuration

- `spring.cloud.stream.bindings.input-in-0.destination=pocinput`: Destination to which the channel associated with the `input` method is connected.
- `spring.cloud.stream.bindings.input-in-0.group=pocqueue`: Name of the queue associated with the `input` method.

### Output Binding Configuration

- `spring.cloud.stream.bindings.output-out-0.destination=pocoutput`: Destination to which the channel associated with the `output` method is connected.
- `spring.cloud.stream.bindings.output-out-0.group=pocqueue`: Name of the queue associated with the `output` method.
- `spring.cloud.stream.bindings.output-in-0.destination=pocoutput`: Input channel destination for the *Producer* on the same *Exchange* as the output channel.
- `spring.cloud.stream.bindings.output-in-0.group=pocqueue`: Queue name for the *Producer*'s input channel.

### Configuration File Inclusion

- `spring.config.import=optional:config/rabbitmq.properties`: Imports the configuration file with properties specific to RabbitMQ.
- `spring.config.import=optional:config/kafka.properties`: Imports the configuration file with properties specific to Kafka (alternative to RabbitMQ).

---


### Example of `application.properties`

```properties  
spring.application.name=Queue-Abstraction-POC  
spring.cloud.function.definition=input;output  
  
# Input binding configuration  
spring.cloud.stream.bindings.input-in-0.destination=pocinput  
spring.cloud.stream.bindings.input-in-0.group=pocqueue  
  
# Output binding configuration  
spring.cloud.stream.bindings.output-out-0.destination=pocoutput  
spring.cloud.stream.bindings.output-out-0.group=pocqueue  
spring.cloud.stream.bindings.output-in-0.destination=pocoutput  
spring.cloud.stream.bindings.output-in-0.group=pocqueue  
  
# Include RabbitMQ properties  
spring.config.import=optional:config/rabbitmq.properties  
  
# Include Kafka properties  
#spring.config.import=optional:config/kafka.properties  
```  

## RabbitMQ Properties

Here are the specific properties for the RabbitMQ configuration. This configuration file allows setting the necessary parameters to correctly connect the application to the RabbitMQ queue.

- **Host and Port:**
    - `spring.rabbitmq.host=localhost`: Specifies the queue host.
    - `spring.rabbitmq.port=5672`: Specifies the queue port.
- **Credentials:**
    - `spring.rabbitmq.username=guest`: Specifies the username to access the queue.
    - `spring.rabbitmq.password=guest`: Specifies the password to access the queue.
- **Producer Configuration:**
    - `spring.cloud.stream.rabbit.bindings.pocoutput.producer.bind-queue=true`: Specifies that the producer should bind the queue.
    - `spring.cloud.stream.rabbit.bindings.pocoutput.producer.declare-exchange=false`: Specifies that the producer should not declare the exchange.
    - `spring.cloud.stream.rabbit.bindings.pocoutput.producer.queue-name-group-only=true`: Specifies that the queue name is determined only by the group.
    - `spring.cloud.stream.rabbit.bindings.pocoutput.producer.routing-key=testkey`: Specifies the routing key to use for the producer binding.
    - `spring.cloud.stream.rabbit.bindings.pocoutput.producer.binding-routing-key-delimiter=,`: Specifies the delimiter to use for multiple routing keys.
- **Output Consumer Configuration:**
    - `spring.cloud.stream.rabbit.bindings.output-in-0.consumer.bind-queue=false`: Specifies that the consumer should not bind the queue.
    - `spring.cloud.stream.rabbit.bindings.output-in-0.consumer.queue-name-group-only=true`: Specifies that the queue name is determined only by the group.
- **Input Consumer Configuration:**
    - `spring.cloud.stream.rabbit.bindings.input-in-0.consumer.bind-queue=true`: Specifies that the consumer should bind the queue.
    - `spring.cloud.stream.rabbit.bindings.input-in-0.consumer.queue-name-group-only=true`: Specifies that the queue name is determined only by the group.
    - `spring.cloud.stream.rabbit.bindings.input-in-0.consumer.binding-routing-key=testkey`: Specifies the routing key to use for the consumer binding.
    - `spring.cloud.stream.rabbit.bindings.input-in-0.consumer.binding-routing-key-delimiter=,`: Specifies the delimiter to use for multiple routing keys.


## Project Functionality

The project is a simple example that demonstrates how to use Spring Cloud Stream to implement a messaging system with RabbitMQ. Below is an explanation of the functionality of the main components of the project:

### MessageConsumer Class

The `MessageConsumer` class is configured to read messages from the queue.


```java  
package org.orbyta.queueabstractionpoc.queue;  
  
import org.orbyta.queueabstractionpoc.model.Book;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
  
import java.util.function.Consumer;  
  
@Configuration  
public class MessageConsumer {  
  
 @Bean public Consumer<Book> input() { return book -> System.out.println("I'm the Consumer and that's what I've read from the queue: \n" + book.toString()); }}  
```  

- **Description:** The `MessageConsumer` class contains a method `input()` annotated with @Bean, which defines a *Consumer* of type *Book*. When a *Book* type message arrives in the queue, the *Consumer* prints the content of the book to the console.

### MessageProducer Class

The `MessageProducer` class is configured to send messages to the queue.


```java  
package org.orbyta.queueabstractionpoc.queue;  
  
import org.orbyta.queueabstractionpoc.dto.CreateBookDto;  
import org.orbyta.queueabstractionpoc.model.Book;  
import org.springframework.beans.factory.annotation.Value;  
import org.springframework.cloud.stream.function.StreamBridge;  
import org.springframework.stereotype.Component;  
  
@Component  
public class MessageProducer {  
  
 @Value("${spring.cloud.stream.bindings.output-out-0.destination}") private String outputTopic;  
 private final StreamBridge streamBridge;  
 public MessageProducer(StreamBridge streamBridge) { this.streamBridge = streamBridge; }  
 // Uncomment this function to obtain a supplier that generates new queue message in loop without any trigger.  
/*
@Bean public Supplier<Book> output() { return () -> { Book book = new Book("Sample Title", "Andrea Casaluci"); streamBridge.send(outputTopic, book); return book; };}
*/
 public Book output(CreateBookDto bookDto) { Book book = new Book(bookDto.getTitle(), bookDto.getAuthor()); streamBridge.send(outputTopic, book); return book; }}  
```  

- **Description:** The `MessageProducer` class uses `StreamBridge` to send *Book* type messages to the queue. The `output()` method creates a *Book* object from a *CreateBookDto*, sends the *Book* to the *output topic*, and returns the *Book* object.
- **Supplier:** There is a function disabled by comments. By removing the comment block, you would get a *Supplier* that creates and writes messages in a loop to the queue, and each of these messages is received and read by the *Consumer*.

### Book Model

The `Book` model represents the message that is sent and received from the queue.


```java  
package org.orbyta.queueabstractionpoc.model;  
  
import java.util.UUID;  
  
public class Book {  
 private String id; private String title; private String author;  
 // Constructor public Book(String title, String author) { this.id = UUID.randomUUID().toString(); this.title = title; this.author = author; }  
 // Getters and setters public String getId() { return id; }  
 public void setId(String id) { this.id = id; }  
 public String getTitle() { return title; }  
 public void setTitle(String title) { this.title = title; }  
 public String getAuthor() { return author; }  
 public void setAuthor(String author) { this.author = author; }  
 @Override public String toString() { return "Book{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", author='" + author + '\'' + '}'; }}  
```  

- **Description:** The `Book` class contains fields `id`, `title`, and `author`, along with their constructor, getters, and setters. The `id` field is automatically generated with a `UUID`. The `toString()` method provides a readable representation of the *Book* object.

### CreateBookDto

The DTO `CreateBookDto` is used to transfer the data necessary for creating a new *Book*.


```java  
package org.orbyta.queueabstractionpoc.dto;  
  
public class CreateBookDto {  
 private String title; private String author;  
 public CreateBookDto(String title, String author) { this.title = title; this.author = author; }  
 public String getTitle() { return title; }  
 public void setTitle(String title) { this.title = title; }  
 public String getAuthor() { return author; }  
 public void setAuthor(String author) { this.author = author; }}  
```  


- **Description:** The `CreateBookDto` class contains the fields `title` and `author`, along with their respective constructor, getter, and setter methods.

### Book Controller
The `BookController` handles HTTP requests to create new books.



```java  
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
 public BookController(MessageProducer messageProducer) { this.messageProducer = messageProducer; }  
 @PostMapping("/new") public Book createNewBook(@Valid @RequestBody CreateBookDto bookDto) { return messageProducer.output(bookDto); }}  
```  
- **Description:** The `BookController` class contains an endpoint `/books/new` that accepts a POST request with a `CreateBookDto` in the body. This endpoint uses the `MessageProducer` to create a new Book and send it to the queue.

