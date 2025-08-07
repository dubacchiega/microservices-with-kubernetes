package br.com.bacchiega.cambio_service.controller;

import br.com.bacchiega.cambio_service.environment.InstanceInformationService;
import br.com.bacchiega.cambio_service.model.Cambio;
import br.com.bacchiega.cambio_service.repository.CambioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Tag(name = "Cambio Endpoint", description = "Endpoints for managing currency exchange rates")
@RestController
@RequestMapping("/cambio-service")
public class CambioController {

    private Logger logger = LoggerFactory.getLogger(CambioController.class);

    @Autowired
    private InstanceInformationService informationService;

//    private Environment environment; posso pegar a porta do servidor aqui

    @Autowired
    private CambioRepository cambioRepository;

    @Operation(description = "Get currency exchange rate")
    @GetMapping("/{amount}/{from}/{to}")
    public Cambio getCambio(@PathVariable BigDecimal amount, @PathVariable String from, @PathVariable String to){

        logger.info("getCambio is called with -> {}, {} and {}", amount, from, to); // para logar no zipkin usando o Span Id. Vai ser visível no log do Spring quando ele tiver subindo
        Cambio cambio = cambioRepository.findByFromAndTo(from, to);

        if (cambio == null) {
            throw new RuntimeException("Currency Unsupported");
        }
//        String port = environment.getProperty("local.server.port"); // posso pegar a porta do servidor (jeito antigo)
        String port = informationService.retrieveServerPort();
        String host = informationService.retrieveInstanceInfo();

        BigDecimal conversionFactor = cambio.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);

        // setScale(2, RoundingMode.CEILING) arredonda o valor para cima (CEILING) com duas casas decimais
        cambio.setConvertedValue(convertedValue.setScale(2, RoundingMode.CEILING));
        cambio.setEnvironment(host + " VERSION: kube-v1 PORT:" + port);
        return cambio;
    }
}
