package br.com.simob.api.resource;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import br.com.simob.api.dto.AtualizaPerfilDTO;
import br.com.simob.api.dto.AtualizaStatusDTO;
import br.com.simob.api.dto.UsuarioDTO;
import br.com.simob.exception.ErroAutenticacao;
import br.com.simob.exception.RegraNegocioException;
import br.com.simob.model.entity.Usuario;
import br.com.simob.model.enums.PerfilAcesso;
import br.com.simob.model.enums.StatusUsuario;
import br.com.simob.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	private final UsuarioService service;
	
	private Usuario converter(UsuarioDTO dto) {
		Usuario usuario = new Usuario();
		usuario.setId(dto.getId());
		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(dto.getSenha());
		if(dto.getPerfil() != null) {
			usuario.setPerfil(PerfilAcesso.valueOf(dto.getPerfil()));
		}
		if(dto.getStatus() != null) {
			usuario.setStatus(StatusUsuario.valueOf(dto.getStatus()));
		}
		
		return usuario;			
	}
	
	private UsuarioDTO converter(Usuario usuario) {
		return UsuarioDTO.builder()
					.id(usuario.getId())
					.nome(usuario.getNome())
					.email(usuario.getEmail())
					.senha(usuario.getSenha())
					.perfil(usuario.getPerfil().name())
					.status(usuario.getStatus().name())
					.build();
	}
	
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		try{
			Usuario entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
		
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar( @PathVariable("id") Long id ) {
		return service.obterPorId(id)
					.map( usuario -> new ResponseEntity(converter(usuario), HttpStatus.OK) )
					.orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND) );
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody UsuarioDTO dto) {
		return service.obterPorId(id).map( entidade -> {
			try {
				Usuario usuario = converter(dto);
				usuario.setId(entidade.getId());
				service.atualizar(usuario);
				return ResponseEntity.ok(usuario);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			}).orElseGet( () ->
			new ResponseEntity("Usuário não localizado.", HttpStatus.BAD_REQUEST) );
	}
	
	@PutMapping("{id}/atualiza-perfil")
	public ResponseEntity atualizarStatus( @PathVariable("id") Long id , @RequestBody AtualizaPerfilDTO dto ) {
		return service.obterPorId(id).map( entity -> {
			PerfilAcesso perfilSelecionado = PerfilAcesso.valueOf(dto.getPerfil());
			
			if(perfilSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o perfil do usuário, envie um perfil válido.");
			}
			
			try {
				entity.setPerfil(perfilSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		
		}).orElseGet( () ->
		new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus( @PathVariable("id") Long id , @RequestBody AtualizaStatusDTO dto ) {
		return service.obterPorId(id).map( entity -> {
			StatusUsuario statusSelecionado = StatusUsuario.valueOf(dto.getStatus());
			
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do usuário, envie um status válido.");
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		
		}).orElseGet( () ->
		new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST) );
	}
}
