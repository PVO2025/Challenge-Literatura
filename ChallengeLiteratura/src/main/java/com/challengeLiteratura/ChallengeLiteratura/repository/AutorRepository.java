package com.challengeLiteratura.ChallengeLiteratura.repository;

import com.challengeLiteratura.ChallengeLiteratura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);
}


