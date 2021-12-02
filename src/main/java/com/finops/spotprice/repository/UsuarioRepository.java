package com.finops.spotprice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finops.spotprice.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Usuario findByloginAndSenha(String login, String senha);
	
}
