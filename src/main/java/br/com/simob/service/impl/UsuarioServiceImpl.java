package br.com.simob.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.simob.exception.ErroAutenticacao;
import br.com.simob.exception.RegraNegocioException;
import br.com.simob.model.entity.Usuario;
import br.com.simob.model.enums.PerfilAcesso;
import br.com.simob.model.enums.StatusUsuario;
import br.com.simob.model.repository.UsuarioRepository;
import br.com.simob.service.UsuarioService;
import br.com.simob.util.SegurancaUtil;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}	
	
	
	@Override
	@Transactional
	public Usuario autenticar(String email, String senha) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não localizado para o email informado.");
		}
		
		if(!usuario.get().getSenha().equals(SegurancaUtil.criptografaSenha(senha))) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário para o email informado");
		}
		
	}

	@Override
	@Transactional
	public Usuario salvar(Usuario usuario) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		validarEmail(usuario.getEmail());
		usuario.setPerfil(PerfilAcesso.PADRAO);
		usuario.setStatus(StatusUsuario.ATIVO);
		usuario.setDataCadastro(Calendar.getInstance().getTime());
		usuario.setSenha(SegurancaUtil.criptografaSenha(usuario.getSenha()));
		return repository.save(usuario);
	}

	@Override
	@Transactional
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional
	public Usuario atualizar(Usuario usuario) {
		Objects.requireNonNull(usuario.getId());
		return repository.save(usuario);
	}

	@Override
	@Transactional
	public void atualizarPerfil(Usuario usuario, PerfilAcesso perfil) {
		usuario.setPerfil(PerfilAcesso.PADRAO);
		atualizar(usuario);
	}

	@Override
	@Transactional
	public void atualizarStatus(Usuario usuario, StatusUsuario status) {
		usuario.setStatus(StatusUsuario.ATIVO);
		atualizar(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> buscar(Usuario usuarioFiltro) {
		Example example = Example.of(usuarioFiltro, ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));

		return repository.findAll(example);
		}

}
