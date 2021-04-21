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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoDocumentos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.UsuarioRol;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Usuarios;
import pe.gob.mtpe.sivice.externo.core.negocio.service.UsuarioRolService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.UsuarioService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil; 

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RequestMapping({ "/api/seguridad" })
public class ControllerSeguridad {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRolService usuarioRolService;
	
	@Autowired
	private BCryptPasswordEncoder encriptarclave;
	 
	Logger log = LoggerFactory.getLogger(this.getClass()); 
	
	@ApiOperation(value = "Muestra informacion de un usuario por su codigo")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
		
		 Usuarios usuario = new Usuarios();
		 Map<String, Object> response = new HashMap<>();
		 try {
			 usuario.setuSuarioidpk(id);
			 usuario = usuarioService.buscarPorId(usuario);
			 
		} catch (DataAccessException e) {
			SQLException sqle = (SQLException) e.getCause();
			log.error(e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Error code: " + sqle.getErrorCode());
			log.error("SQL state: " + sqle.getSQLState());
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuario);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		 
		return new ResponseEntity<Usuarios>(usuario, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Lista los usuarios")
	@GetMapping("/")
	public List<Usuarios> listar( 
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
 
		List<Usuarios> lista = new ArrayList<Usuarios>();
		List<Usuarios> listFinal = new ArrayList<Usuarios>();
		
		
	    try {
	    	Usuarios usuario = new Usuarios();
		     usuario.setVrol(nombreRol);
		     
		     Regiones region  = new Regiones();
		     region.setrEgionidpk(idRegion);
		     
		     usuario.setRegiones(region);
		     lista= usuarioService.listar(usuario);
		     
		     
		     //MOSTRAMOS EL ROL 
		     UsuarioRol usuarioRol= new UsuarioRol();
		     
		     for(Usuarios item: lista) {
		    	  
		    	 usuarioRol= usuarioRolService.buscarNombreRolPorIdusuario(item.getuSuarioidpk()); 
		    	 if(usuarioRol!=null) {
		    		 item.setVrol(usuarioRol.getRoles().getvDescripcion());
		    	 }
		    	 
		    	 listFinal.add(item);
 
		     }
		     
		     
		     
		} catch (DataAccessException e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		
		return listFinal;

	}
	
	
	@ApiOperation(value = "Busca usuarios por criterio de busqueda")
	@PostMapping(value = "/buscar")
	public List<Usuarios> buscar(
		    @RequestParam("tipodocumento")    Long tipodocumento,
		    @RequestParam("numerodocumento")  String numerodocumento,
		    @RequestParam("nombre")           String nombre,
		    @RequestParam("apellidopaterno")  String apellidopaterno,
		    @RequestParam("apellidomaterno")  String apellidomaterno,
		    @RequestParam("region")           Long region
		 ){
		
		List<Usuarios> lista = new ArrayList<Usuarios>();
		List<Usuarios> listFinal = new ArrayList<Usuarios>();
		
		try {
			Usuarios usuario = new Usuarios();
			TipoDocumentos vtipodocumentos= new TipoDocumentos();
			Regiones vregion = new Regiones();
			
			if(tipodocumento!=null) {
				vtipodocumentos.settPdocumentoidpk(tipodocumento);
			}
			
			if(vregion!=null) {
				vregion.setrEgionidpk(region);
			}
			
			usuario.setTipodocumento(vtipodocumentos);
			usuario.setRegiones(vregion);
			usuario.setvNumdocumento(numerodocumento);
			usuario.setvNombre(nombre);
			usuario.setvAppaterno(apellidopaterno);
			usuario.setvApmaterno(apellidomaterno);
			lista = usuarioService.buscar(usuario);
			
			//MOSTRAMOS EL ROL 
		     UsuarioRol usuarioRol= new UsuarioRol();
		     
		     for(Usuarios item: lista) {
		    	  
		    	 usuarioRol= usuarioRolService.buscarNombreRolPorIdusuario(item.getuSuarioidpk()); 
		    	 if(usuarioRol!=null) {
		    		 item.setVrol(usuarioRol.getRoles().getvDescripcion());
		    	 }
		    	 
		    	 listFinal.add(item);

		     }
			
			
		} catch (DataAccessException e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
 
		return listFinal;
	}
	
	
 
	@ApiOperation(value = "Registra un usuario")
	@PostMapping(value = "/registrar")
	public ResponseEntity<?> registra(@RequestParam("tipodocumento") Long tipodocumento,
			@RequestParam("vNombre") String vNombre, @RequestParam("vAppaterno") String vAppaterno,
			@RequestParam("vApmaterno") String vApmaterno, @RequestParam("vNumdocumento") String vNumdocumento,
			@RequestParam("vCorreo") String vCorreo, @RequestParam("vClave") String vClave,
			@RequestParam("region") Long vregion) {
		Usuarios usuario = new Usuarios();
		Map<String, Object> response = new HashMap<>();
		Usuarios usuariobuscar = new Usuarios();

		try {

			TipoDocumentos tipoDocumentos = new TipoDocumentos();
			tipoDocumentos.settPdocumentoidpk(tipodocumento);
			
			Regiones region = new Regiones();
			region.setrEgionidpk(vregion);
			
			usuario.setvNombre(vNombre);
			usuario.setvAppaterno(vAppaterno);
			usuario.setvApmaterno(vApmaterno);
			usuario.setvNumdocumento(vNumdocumento);
			usuario.setUsername(vCorreo);
			usuario.setPassword(encriptarclave.encode(vClave));
			usuario.setTipodocumento(tipoDocumentos);
			usuario.setRegiones(region);

			usuariobuscar = usuarioService.buscarPorCorreo(usuario);

			if (usuariobuscar == null) {
				usuariobuscar = usuarioService.Registrar(usuario);

			} else {
				if (!"1".equals(usuariobuscar.getEnabled())) {
					usuariobuscar.setEnabled("1");
					usuariobuscar.setcFlgeliminado("1");
					usuariobuscar = usuarioService.Actualizar(usuariobuscar);
				}else { 
					response.put(ConstantesUtil.X_MENSAJE," EL USUARIO("+usuariobuscar.getUsername()+") "+ConstantesUtil.C_CORREO_DUPLICADO_MSG_CONSEJEROS);
					response.put(ConstantesUtil.X_ERROR, ConstantesUtil.C_CORREO_DUPLICADO_ERROR_CONSEJEROS);
					response.put(ConstantesUtil.X_ENTIDAD, usuariobuscar);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				}

			}

		} catch (DataAccessException e) {
			
			SQLException sqle = (SQLException) e.getCause();
			log.error(e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Error code: " + sqle.getErrorCode());
			log.error("SQL state: " + sqle.getSQLState());
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuario);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Usuarios>(usuariobuscar, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Actualiza un usuario")
	@PostMapping(value = "/actualizar")
	public ResponseEntity<?> actualizar(
			@RequestParam("idusuario") Long idusuario,
			@RequestParam("tipodocumento") Long tipodocumento,
			@RequestParam("vNombre") String vNombre, @RequestParam("vAppaterno") String vAppaterno,
			@RequestParam("vApmaterno") String vApmaterno, @RequestParam("vNumdocumento") String vNumdocumento,
			@RequestParam("vCorreo") String vCorreo, @RequestParam("vClave") String vClave,
			@RequestParam("region") Long vregion) {
		Usuarios usuario = new Usuarios();
		Map<String, Object> response = new HashMap<>();
		Usuarios usuariobuscar = new Usuarios();

		try {
			

			TipoDocumentos tipoDocumentos = new TipoDocumentos();
			tipoDocumentos.settPdocumentoidpk(tipodocumento);
			
			Regiones region = new Regiones();
			region.setrEgionidpk(vregion);
			
			usuario.setuSuarioidpk(idusuario);
			usuario.setvNombre(vNombre);
			usuario.setvAppaterno(vAppaterno);
			usuario.setvApmaterno(vApmaterno);
			usuario.setvNumdocumento(vNumdocumento);
			usuario.setUsername(vCorreo.trim());
			//usuario.setPassword(vClave);
			usuario.setTipodocumento(tipoDocumentos);
			usuario.setRegiones(region);

			usuariobuscar = usuarioService.buscarPorId(usuario);
			
			vClave = vClave.trim();
            if(!"".equals(vClave)) {
            	 usuario.setPassword(encriptarclave.encode(vClave));	
            }else {
            	usuario.setPassword(usuariobuscar.getPassword());
            }
 
			usuario.setcFlgeliminado(usuariobuscar.getcFlgeliminado());
			usuario.setEnabled(usuariobuscar.getEnabled());
			usuario.setUsername(usuariobuscar.getUsername());
			usuario.setdFecreg(usuariobuscar.getdFecreg());
			usuariobuscar = usuarioService.Actualizar(usuario);
			


		} catch (DataAccessException e) {
			SQLException sqle = (SQLException) e.getCause();
			log.error(e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Error code: " + sqle.getErrorCode());
			log.error("SQL state: " + sqle.getSQLState());
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuario);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Usuarios>(usuariobuscar, HttpStatus.OK);
	}
	
	
	
	@ApiOperation(value = "Elimina un usuario")
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		 Usuarios usuario = new Usuarios();
		 Map<String, Object> response = new HashMap<>();
		 try {
			 usuario.setuSuarioidpk(id);
			 
			 usuario = usuarioService.buscarPorId(usuario);
			 usuario =usuarioService.Eliminar(usuario);
		} catch (DataAccessException e) {
			SQLException sqle = (SQLException) e.getCause();
			log.error(e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Error code: " + sqle.getErrorCode());
			log.error("SQL state: " + sqle.getSQLState());
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, usuario);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		 
		return new ResponseEntity<Usuarios>(usuario, HttpStatus.OK);
	}
	


}
