package com.finops.spotprice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Usuario {

	@Id
	@Column (name="cod_usuario")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codUsuario;

	private String login;

	private String senha;
}