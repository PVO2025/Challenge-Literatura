package com.challengeLiteratura.ChallengeLiteratura.principal;

import com.challengeLiteratura.ChallengeLiteratura.model.Autor;
import com.challengeLiteratura.ChallengeLiteratura.model.Datos;
import com.challengeLiteratura.ChallengeLiteratura.model.Libros;
import com.challengeLiteratura.ChallengeLiteratura.repository.AutorRepository;
import com.challengeLiteratura.ChallengeLiteratura.repository.LibrosRepository;
import com.challengeLiteratura.ChallengeLiteratura.service.ConsumoApi;
import com.challengeLiteratura.ChallengeLiteratura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Principal implements CommandLineRunner {

    @Autowired
    private LibrosRepository librosRepository;
    @Autowired
    private AutorRepository autorRepository;

        private static final String URL_BASE = "https://gutendex.com/books/";
        private ConsumoApi consumoAPI = new ConsumoApi();

        private ConvierteDatos conversor = new ConvierteDatos();

        private Scanner teclado = new Scanner(System.in);

        public void muestraElMenu() {

            int opcion = -1;

            while (opcion != 0) {
                System.out.println("\n📚 Catálogo de Libros 📚");
                System.out.println("1 - Buscar libros populares");
                System.out.println("2 - Buscar libros por título");
                System.out.println("3 - Listar libros registrados");
                System.out.println("4 - Listar autores registrados");
                System.out.println("5 - Listar autores vivos en un determinado año");
                System.out.println("6 - Listar libros por idioma");
                System.out.println("7 - Salir");
                System.out.print("Seleccione una opción: ");
                opcion = teclado.nextInt();
                teclado.nextLine(); // limpia buffer

                switch (opcion) {
                    case 1 -> mostrarPopulares();
                    case 2 -> buscarPorTitulo();
                    case 3 -> listarLibrosRegistrados();
                    case 4 -> listarAutoresRegistrados();
                    case 5 -> listarAutoresVivos();
                    case 6 -> listarLibrosPorIdioma();
                    case 7 ->{
                        System.out.println("Saliendo...");
                        opcion = 0;
                        System.exit(0); // Forzar salida de la app

                    }

                    default -> System.out.println("Opción no válida.");

                }
            }
        }

    private void listarAutoresVivos() {
        System.out.print("Ingrese año para buscar autores vivos en ese año: ");
        int anio = teclado.nextInt();
        teclado.nextLine();

        var autores = autorRepository.findAll();
        autores.stream()
                .filter(a -> a.getAnioNacimiento() != null && a.getAnioNacimiento() <= anio &&
                        (a.getAnioMuerte() == null || a.getAnioMuerte() >= anio))
                .forEach(a -> System.out.println("👤 " + a.getNombre()));
    }

    private void listarAutoresRegistrados() {
        var autores = autorRepository.findAll();
        autores.forEach(autor -> {
            System.out.println("👤 " + autor.getNombre() +
                    " (Nac: " + autor.getAnioNacimiento() +
                    ", Muerte: " + autor.getAnioMuerte() + ")");
        });
    }

    private void listarLibrosPorIdioma() {
        System.out.print("Ingrese el código del idioma (ej: 'en', 'es', 'fr'): ");
        var idioma = teclado.nextLine();
        System.out.println("🔍 Buscando libros en idioma: " + idioma);

        var libros = librosRepository.findByIdioma(idioma);
        if (libros.isEmpty()) {
            System.out.println("❌ No hay libros registrados en ese idioma.");
        } else {
            libros.forEach(libro -> {
                System.out.println("📖 " + libro.getTitulo() + " (" + libro.getIdioma() + ")");
            });
        }
    }

    private void listarLibrosRegistrados() {
        var libros = librosRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("📭 No hay libros registrados en la base de datos.");
        } else {
            libros.forEach(libro -> {
                System.out.println("📖 Título: " + libro.getTitulo());
                System.out.println("🌍 Idioma: " + libro.getIdioma());
                System.out.println("⬇️ Descargas: " + libro.getNumeroDeDescargas());
                System.out.println("--------------------------");
            });
        }
    }


    private void mostrarPopulares() {
            //var json = consumoAPI.obtenerDatos(URL_BASE);
            //var datos = conversor.obtenerDatos(json, Datos.class);
            //datos.results().forEach(libro ->
              //      System.out.println("📖 " + libro.titulo() + " - Descargas: " + libro.numeroDeDescargas()));
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        datos.results().forEach(libroDto -> {
            // Obtener idioma correctamente o asignar "desconocido"

            //String idioma = (libroDto.idiomas() != null && !libroDto.idiomas().isEmpty())
              //      ? libroDto.idiomas().get(0)
                //    : "desconocido";

            String idioma;

            if (libroDto.idiomas() != null && !libroDto.idiomas().isEmpty()) {
                idioma = libroDto.idiomas().get(0);
            } else {
                idioma = "desconocido";
            }

            // Crear instancia del libro con setters (evita problemas con constructores)
            Libros libro = new Libros();
            libro.setTitulo(libroDto.titulo());
            libro.setIdioma(idioma);
            libro.setNumeroDeDescargas(libroDto.numeroDeDescargas());



            // Buscar o guardar autores
            Set<Autor> autoresEntidad = libroDto.autor().stream().map(autorDto -> {
                return autorRepository.findByNombre(autorDto.nombre())
                        .orElseGet(() -> {
                            Autor nuevo = new Autor(
                                    autorDto.nombre(),
                                    autorDto.anioNacimiento(),
                                    autorDto.anioMuerte()
                            );
                            return autorRepository.save(nuevo);
                        });
            }).collect(Collectors.toSet());

            libro.setAutores(autoresEntidad);

            // Guardar en la base de datos
            librosRepository.save(libro);
            System.out.println("✅ Guardado: " + libro.getTitulo());
        });


        }

        private void buscarPorTitulo() {
            System.out.print("Ingrese título a buscar: ");
            var titulo = teclado.nextLine();
            var url = URL_BASE + "?search=" + titulo.replace(" ", "+");
            var json = consumoAPI.obtenerDatos(url);
            var datos = conversor.obtenerDatos(json, Datos.class);

            datos.results().forEach(libro -> {
                System.out.println("\n📖 Título: " + libro.titulo());
                System.out.println("✍️ Autor(es): ");
                libro.autor().forEach(a -> System.out.println("   - " + a.nombre()));
                System.out.println("🌍 Idiomas: " + libro.idiomas());
                System.out.println("⬇️ Descargas: " + libro.numeroDeDescargas());


            });




        }

    @Override
    public void run(String... args) throws Exception {
        muestraElMenu();// <- Esto lanza el menú interactivo

    }
}


