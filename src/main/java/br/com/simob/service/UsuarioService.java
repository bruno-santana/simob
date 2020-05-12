package br.com.simob.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import br.com.simob.model.entity.Usuario;
import br.com.simob.model.enums.PerfilAcesso;
import br.com.simob.model.enums.StatusUsuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
	void validarEmail(String email);
	
	Usuario salvar(Usuario usuario) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
	Optional<Usuario> obterPorId(Long id);
	
	Usuario atualizar(Usuario usuario);
	
	void atualizarPerfil(Usuario usuario, PerfilAcesso perfil);
	
	void atualizarStatus(Usuario usuario, StatusUsuario status);
	
	List<Usuario> buscar(Usuario usuarioFiltro);
}
