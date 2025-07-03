package com.challengeLiteratura.ChallengeLiteratura.model;



import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer anioNacimiento;
    private Integer anioMuerte;

    @ManyToMany(mappedBy = "autores")
    private Set<Libros> libros = new HashSet<>();

    public Autor() {}

    public Autor(String nombre, Integer anioNacimiento, Integer anioMuerte) {
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.anioMuerte = anioMuerte;
    }

    // Getters y setters

    public Long getId() { return id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getAnioNacimiento() { return anioNacimiento; }

    public void setAnioNacimiento(Integer anioNacimiento) { this.anioNacimiento = anioNacimiento; }

    public Integer getAnioMuerte() { return anioMuerte; }

    public void setAnioMuerte(Integer anioMuerte) { this.anioMuerte = anioMuerte; }

    public Set<Libros> getLibros() { return libros; }

    public void setLibros(Set<Libros> libros) { this.libros = libros; }

    public void setId(Long id) {
        this.id = id;
    }
}

