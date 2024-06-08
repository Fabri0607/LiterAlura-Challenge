package com.alura.literalura;

import com.alura.literalura.principal.Principal;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class literAluraApplication implements CommandLineRunner {
	@Autowired
	private LibroRepository repository;

	@Autowired
	private AutorRepository repository2;

	public static void main(String[] args) {
		SpringApplication.run(literAluraApplication.class, args);

	}
	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository, repository2);
		principal.muestraElMenu();

	}


}
