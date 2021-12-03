package com.finops.spotprice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.finops.spotprice.model.Usuario;
import com.finops.spotprice.repository.UsuarioRepository;

@Controller
public class LoginController {
	
	Usuario user;
	
	@Autowired
	UsuarioRepository usuarioRepository;

	@GetMapping("/")
	public String telaLogin() {
		return "login/loginUsuario";
	}
	
	@RequestMapping (method = RequestMethod.GET, value="/loginUsuario")
	public String login() {
		return "login/loginUsuario";
	}
	
	@RequestMapping (method = RequestMethod.POST, value="/validarUsuario")
	public String validarUser(String login, String senha) {
		user = usuarioRepository.findByloginAndSenha(login, senha);
		if(user != null) {
			return "paginaPrincipal/tabelaSpot";
		}
		return "login/loginUsuario";
	}
	
}
