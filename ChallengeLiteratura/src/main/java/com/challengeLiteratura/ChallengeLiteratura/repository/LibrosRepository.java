package com.challengeLiteratura.ChallengeLiteratura.repository;

import com.challengeLiteratura.ChallengeLiteratura.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibrosRepository extends JpaRepository<Libros, Long> {
    List<Libros> findByIdioma(String idioma);
}

