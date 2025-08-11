package br.com.bacchiega.book_service.controller;

import br.com.bacchiega.book_service.environment.InstanceInformationService;
import br.com.bacchiega.book_service.model.Book;
import br.com.bacchiega.book_service.proxy.CambioProxy;
import br.com.bacchiega.book_service.repository.BookRepository;
import br.com.bacchiega.book_service.dto.Cambio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book Endpoint", description = "Endpoints for managing books")
@RestController
@RequestMapping("/book-service")
public class BookController {

    private Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    InstanceInformationService informationService;
//    Environment environment;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CambioProxy cambioProxy;

    @Operation(summary = "Find a book by ID and currency")
    @GetMapping("/{id}/{currency}")
    public Book findBook(@PathVariable Long id, @PathVariable String currency) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        logger.info("Calculating the converted price of the book from {} USD to {} ", book.getPrice(), currency);

        Cambio cambio = cambioProxy.getCambio(book.getPrice(), "USD", currency); // Usando o Feign Client para chamar o serviço de câmbio passando o valor a ser convertido, a moeda em que o livro foi salvo e a moeda convertida
//        String port = environment.getProperty("local.server.port");
        String port = informationService.retrieveServerPort();
        String host = informationService.retrieveInstanceInfo();

        book.setEnvironment("BOOK HOST: " + host + " PORT: " + port + "VERSION: kube-v2" + " CAMBIO PORT: " + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue()); // Seto o preço do livro com o valor convertido retornado pelo serviço de câmbio
        book.setCurrency(currency);
        return book;
    }

// SEM O FEIGN CLIENT
/*    @GetMapping("/{id}/{currency}")
    public Book findBook(@PathVariable Long id, @PathVariable String currency) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        HashMap<String, String> params = new HashMap<>();
        params.put("amount", book.getPrice().toString());
        params.put("from", "USD");
        params.put("to", currency);

        ResponseEntity<Cambio> response = new RestTemplate()
                .getForEntity("http://localhost:8000/cambio-service/{amount}/{from}/{to}", Cambio.class, params);
        Cambio cambio = response.getBody();

        String port = environment.getProperty("local.server.port");

        book.setEnvironment(port);
        book.setPrice(cambio.getConvertedValue());

        return book;
    }*/
}
