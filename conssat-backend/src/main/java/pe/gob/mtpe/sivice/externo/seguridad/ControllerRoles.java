package pe.gob.mtpe.sivice.externo.seguridad;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Roles;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.UsuarioRol;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Usuarios;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.UsuarioRolService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.UsuarioService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RequestMapping({ "/api/roles" })
public class ControllerRoles {

	@Autowired
	private UsuarioRolService usuarioRolService;
	
	@Autowired
	private  FijasService fijasService;
	
	@Autowired
	private UsuarioService  usuarioService;

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ApiOperation(value = "Registra un rol")
	@PostMapping(value = "/registrar")
	public ResponseEntity<?> registra(
			@RequestParam("idusuario") Long idusuario, 
			@RequestParam("idrol")     Long idrol,
			@RequestHeader(name = "id_usuario", required = true)        Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true)          String nombreRol) {

		UsuarioRol usuarioRol = new UsuarioRol();
		UsuarioRol usuarioRolbuscar = new UsuarioRol();

		Map<String, Object> response = new HashMap<>();
		try {

			Usuarios usuario = new Usuarios();
			usuario.setuSuarioidpk(idusuario);

			Roles rol = new Roles();
			rol.setrOlidpk(idrol);

			usuarioRol.setUsuario(usuario);
			usuarioRol.setRoles(rol);
			
			usuarioRolbuscar = usuarioRolService.buscarPorRol(usuarioRol);
			
			// VALIDAMOS QUE SOLO SEA UN ROL CONSSAT, ROLE_OPECONSSAT 
			if(usuarioRolbuscar==null) {
				if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
					
					Usuarios Infousuario = new Usuarios();
					Infousuario =  usuarioService.buscarPorId(usuario);
					
					Roles Inforol = new Roles();
					Inforol = fijasService.buscaRoles(rol);
					
					if(ConstantesUtil.C_ROLE_CONSSAT.equals(Inforol.getvDesnombre()) ||  ConstantesUtil.C_ROLE_OPECONSSAT.equals(Inforol.getvDesnombre()) ) {
						if(!ConstantesUtil.C_NOMBRE_REGION_LIMA.equals(Infousuario.getRegiones().getvDesnombre())) {
							response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.C_ROL_MSG_ERROR);
							response.put(ConstantesUtil.X_ERROR, ConstantesUtil.C_ROL_ERROR_MENSAJE);
							response.put(ConstantesUtil.X_ENTIDAD, usuarioRolbuscar);
							return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
						}
						
					}
							
					
				}	
				
			}
			
			
			if (usuarioRolbuscar != null) {
				usuarioRol=usuarioRolService.deshabilitarrol(usuarioRolbuscar);
			} else {
				usuarioRol=usuarioRolService.Registrar(usuarioRol);
			}

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuarioRol);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		

		return new ResponseEntity<UsuarioRol>(usuarioRol, HttpStatus.OK);
	}
	
	
	
	@ApiOperation(value = "Activa un usuario")
	@GetMapping(value = "/activar/{idusuariorol}")
	public ResponseEntity<?>  activarRol(@PathVariable Long idusuariorol){
		UsuarioRol usuarioRol = new UsuarioRol();
		Map<String, Object> response = new HashMap<>();
		try {
			usuarioRol.setuSuariorolidpk(idusuariorol);
			usuarioRol = usuarioRolService.buscarPorId(usuarioRol);
			usuarioRol = usuarioRolService.Actualizar(usuarioRol);
 
		} catch (DataAccessException e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuarioRol);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<UsuarioRol>(usuarioRol, HttpStatus.OK);
	}
	
	
	
	
	@ApiOperation(value = "desactiva un usuario")
	@GetMapping(value = "/desactivar/{idusuariorol}")
	public ResponseEntity<?>  desactivarRol(@PathVariable Long idusuariorol){
		UsuarioRol usuarioRol = new UsuarioRol();
		Map<String, Object> response = new HashMap<>();
		try {
			usuarioRol.setuSuariorolidpk(idusuariorol);
			usuarioRol = usuarioRolService.buscarPorId(usuarioRol);
			usuarioRol = usuarioRolService.deshabilitarrol(usuarioRol);
			
		} catch (DataAccessException e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuarioRol);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<UsuarioRol>(usuarioRol, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Lista los roles por usuario")
	@GetMapping(value="/{idusuario}")
	public List<UsuarioRol> listaRolesPorUsuario(@PathVariable Long idusuario){
		
		List<UsuarioRol> listar = new ArrayList<UsuarioRol>();
		try {
			Usuarios usuarios = new Usuarios();
			usuarios.setuSuarioidpk(idusuario);
			
			UsuarioRol usuarioRol = new UsuarioRol();
			usuarioRol.setUsuario(usuarios);
			listar = usuarioRolService.buscar(usuarioRol);
		} catch (Exception e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMessage()));
			log.error("Fin ***************************************************************");
		}
 
		
		return listar;
	}

}
