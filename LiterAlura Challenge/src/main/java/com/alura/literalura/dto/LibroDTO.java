package com.alura.literalura.dto;

import com.alura.literalura.model.Autor;

public record LibroDTO(
        Long Id,
        String titulo,
        Autor autor,
        String idioma,
        Double descargas

) {
}
