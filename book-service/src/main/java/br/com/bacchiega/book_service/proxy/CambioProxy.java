package br.com.bacchiega.book_service.proxy;

import br.com.bacchiega.book_service.dto.Cambio;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "cambio-service") // nome do serviço que será chamado (Sem Kubernetes)
@FeignClient(name = "cambio-service", url = "${CAMBIO_SERVICE_SERVICE_HOST:http://host.docker.internal}:8000")
public interface CambioProxy {

    @GetMapping(value = "/cambio-service/{amount}/{from}/{to}") // mapeia o endpoint do serviço de câmbio
    public Cambio getCambio(
            @PathVariable Double amount,
            @PathVariable String from,
            @PathVariable String to
    );
}
// conecta o BookService com o CambioService usando o Feign Client

// o Kubernetes por padrão pega o nome do serviço que eu estou chamando (cambio-service) e concatena SERVICE_HOST
// porem o Kubernetes entende em UpperCase, então ficaria como CAMBIO_SERVICE_SERVICE_HOST
// qualquer outro serviço vai vir com o SERVICE_HOST em maiúsculo

// @FeignClient(name = "cambio-service", url = "${CAMBIO_SERVICE_SERVICE_HOST:http://host.docker.internal}:8000") ->
// quando eu for rodar localmente, ele vai pegar o host.docker.internal, que é o IP do meu host (máquina local) dentro do container Docker
// quando for em um cluster Kubernetes, ele vai pegar a variável de ambiente CAMBIO_SERVICE_SERVICE_HOST
