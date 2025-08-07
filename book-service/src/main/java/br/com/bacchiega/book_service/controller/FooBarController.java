package br.com.bacchiega.book_service.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//@Slf4j -> é uma anotação do Lombok que cria um logger associado a esta classe
@Tag(name = "FooBar Endpoint", description = "Endpoints for FooBar operations")
@RestController
@RequestMapping("/book-service")
public class FooBarController {

    // criando um logger associado a esta classe
    private Logger logger = LoggerFactory.getLogger(FooBarController.class);

    @Operation(summary = "FooBar Endpoint")
    @GetMapping("/foo-bar")
    // ele tenta mandar a requisição novamente após um numero de vezes que a chamada será tentada em caso de falha
//    @Retry(name = "foo-bar", fallbackMethod = "fallbackMethod")

    // enquanto o tudo está indo bem, ele segue o fluxo normal e fica fechado. A partir do momento que ocorre uma taxa de falhas ele abre
    // depois que ele abre ele não aceita mais requisições, porém ele pode ir para o estado de meio-aberto e tentar uma nova requisição
    // caso essa nova requisição falhe, ele volta para o estado aberto, caso ela seja bem sucedida, ele volta para o estado fechado
    // isso é feito para evitar que o sistema fique sobrecarregado com requisições que estão falhando
//    @CircuitBreaker(name = "default", fallbackMethod = "fallbackMethod")
//    @RateLimiter(name = "default") // limita a quantidade de requisições que podem ser feitas em um determinado tempo
    @Bulkhead(name = "default") // limita a quantidade de requisições que podem ser feitas em um determinado tempo, mas não retorna erro se a quantidade de requisições for excedida
    public String fooBar() {
        logger.info("Request foo-bar is received!");
        // RestTemplate é usada para fazer uma chamada HTTP
        // .getForEntity faz uma requisição GET para a url informada e espera uma resposta do tipo String
        var response = new RestTemplate().getForEntity("http://localhost:8080/foo-bar", String.class);
        return response.getBody();
    }

    // Metodo de fallback que será chamado se a chamada para foo-bar falhar
    public String fallbackMethod(Exception ex){
        return "Fallback response: foo-bar service is currently unavailable. Please try again later.";
    }
}
