package br.com.bacchiega.cambio_service.repository;

import br.com.bacchiega.cambio_service.model.Cambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CambioRepository extends JpaRepository<Cambio, Long> {

    // SELECT * FROM cambio WHERE from_currency = ? AND to_currency = ?
    Cambio findByFromAndTo(String from, String to);
}
