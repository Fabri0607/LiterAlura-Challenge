package com.alura.literalura.service;

import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroServicio {
    @Autowired
    private LibroRepository repository;

    public List<LibroDTO> obtenerTodosLosLibros() {
        return convierteDatos(repository.findAll());
    }

    public List<LibroDTO> convierteDatos(List<Libro> libro) {
        return libro.stream()
                .map(l -> new LibroDTO(
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getIdioma(),
                        l.getDescargas()
                ))
                .collect(Collectors.toList());
    }
}
