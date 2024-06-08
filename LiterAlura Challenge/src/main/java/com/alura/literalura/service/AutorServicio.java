package com.alura.literalura.service;

import com.alura.literalura.dto.AutorDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorServicio {
    @Autowired
    private AutorRepository repository;

    public List<AutorDTO> obtenerTodosLosAutores() {
        return convierteDatos(repository.findAll());
    }

    public List<AutorDTO> convierteDatos(List<Autor> autor) {
        return autor.stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNombre(),
                        a.getNacimiento(),
                        a.getDeceso()
                ))
                .collect(Collectors.toList());
    }
}
