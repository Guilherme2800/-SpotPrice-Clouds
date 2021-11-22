package com.finops.spotprice.service;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.finops.spotprice.SpotPriceApplicationTests;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ConexaoMariaDbTest extends SpotPriceApplicationTests {

	@Autowired
	ConexaoMariaDb mariaDb;

	static Connection conexao;

	@BeforeAll
	public static void init() {
		conexao = null;
	}

	@Test
	public void testConexaoAceita() {

		conexao = mariaDb.conectar();
		assertNotNull(conexao);

	}

	@AfterAll
	public void deconect() {
		mariaDb.desconectar();
	}

}
