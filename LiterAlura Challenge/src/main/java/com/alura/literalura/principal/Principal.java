package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.DatosAutor;
import com.alura.literalura.model.DatosLibro;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.service.ConvierteDatosAutor;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private ConvierteDatosAutor conversorAutor = new ConvierteDatosAutor();
    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner teclado = new Scanner(System.in);
    private LibroRepository repositorio;
    private AutorRepository repositorio2;
    private List<Libro> libros;
    private List<Autor> autores;
    private Optional<Autor> autorBuscado;
    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Seleccione la opción deseada
                    1- buscar libro por título 
                    2- listar libros registrados
                    3- listar autores registrados
                    4- listar autores vivos en un determinado año
                    5- listar libros por idioma                             
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    mostrarAutorxsRegistradxs();
                    break;
                case 4:
                    mostrarAutorxsVivxsEnUnDeterminadoAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
        System.exit(0);
    }
    public Principal(LibroRepository repository, AutorRepository repository2) {
        this.repositorio = repository;
        this.repositorio2 = repository2;
    }
    private DatosLibro getDatosLibro(String nombreLibro) {
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search="+ nombreLibro.replace(" ", "+"));
        DatosLibro datos = conversor.obtenerDatos(json, DatosLibro.class);
        return datos;
    }
    private DatosAutor getDatosAutor(String nombreLibro) {
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search="+ nombreLibro.replace(" ", "+"));
        DatosAutor datos = conversorAutor.obtenerDatos(json, DatosAutor.class);
        return datos;
    }
private String pregunta() {
    System.out.println("Escribe el nombre del libro que deseas buscar");
    var nombreLibro = teclado.nextLine();
    return nombreLibro;
    }
    private void mostrarLibrosBuscados(){
        try{
            List<Libro> libros = repositorio.findAll();
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas))
                    .forEach(System.out::println);
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
            libros = new ArrayList<>();
        }
    }

    public void buscarLibroPorTitulo() {
        mostrarLibrosBuscados();
        String libroBuscado = pregunta();

        libros = libros != null ? libros : new ArrayList<>();

        Optional<Libro> broli = libros.stream()
                .filter(l -> l.getTitulo().toLowerCase()
                        .contains(libroBuscado.toLowerCase()))
                .findFirst();

        if(broli.isPresent()) {
            var libroEncontrado = broli.get();
            System.out.println(libroEncontrado);
            System.out.println("El libro ya fue cargado, pruebe con otro");
        }else{
            try {
                DatosLibro datosLibro = getDatosLibro(libroBuscado);
                System.out.println(datosLibro);

                if (datosLibro != null) {
                    DatosAutor datosAutor = getDatosAutor(libroBuscado);
                    if (datosAutor != null) {
                        List<Autor> autores = repositorio2.findAll();
                        autores = autores != null ? autores : new ArrayList<>();

                        Optional<Autor> pueta = autores.stream()
                                .filter(a -> datosAutor.nombre() != null &&
                                        a.getNombre().toLowerCase().contains(datosAutor.nombre().toLowerCase()))
                                .findFirst();

                        Autor autor;
                        if (pueta.isPresent()) {
                            autor = pueta.get();
                        } else {
                            autor = new Autor(
                                    datosAutor.nombre(),
                                    datosAutor.nacimiento(),
                                    datosAutor.deceso()
                            );
                            repositorio2.save(autor);
                        }

                        Libro libro = new Libro(
                                datosLibro.titulo(),
                                autor,
                                datosLibro.idioma() != null ? datosLibro.idioma() : Collections.emptyList(),
                                datosLibro.descargas()
                        );

                        libros.add(libro);
                        autor.setLibros(libros);

                        System.out.println(libro);
                        repositorio.save(libro);

                        System.out.println("Libro guardado exitosamente");
                    } else {
                        System.out.println("No se encontró el autor para el libro");
                    }

                } else {
                    System.out.println("No se encontró el libro");
                }
            } catch (Exception e) {
                System.out.println("excepción: " + e.getMessage());
            }
        }
}

    public void mostrarAutorxsRegistradxs(){
        autores = repositorio2.findAll();
        autores.stream()
                .forEach(System.out::println);
    }

    public void mostrarAutorxsVivxsEnUnDeterminadoAno(){
        System.out.println("Ingrese un año");
        int anio = teclado.nextInt();
        autores = repositorio2.findAll();
        List<String> autoresNombre = autores.stream()
                .filter(a-> (a.getDeceso() >= anio) && (a.getNacimiento() <= anio))
                .map(a->a.getNombre())
                .collect(Collectors.toList());
        autoresNombre.forEach(System.out::println);
    }

    public void listarLibrosPorIdioma(){
        libros = repositorio.findAll();
        List<String> idiomasUnicos = libros.stream()
                .map(Libro::getIdioma)
                .distinct()
                .collect(Collectors.toList());
        idiomasUnicos.forEach(idioma -> {
            switch (idioma){
                case "en":
                    System.out.println("en - english");
                    break;
                case "es":
                    System.out.println("es - español");
                    break;
            }
        });
        System.out.println("");
        System.out.println("Ingrese el idioma del que desea buscar los libros");
        String idiomaBuscado = teclado.nextLine();
        List<Libro> librosBuscados = libros.stream()
                .filter(l->l.getIdioma().contains(idiomaBuscado))
                .collect(Collectors.toList());
        librosBuscados.forEach(System.out::println);

    }


}
