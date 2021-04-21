package pe.gob.mtpe.sivice.externo.integracion.api;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Consejos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Entidades;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Profesiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Roles;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Seleccion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoComisiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoDocumentos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoSesiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoTemas;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Tipoconsejero;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({"/api/fijas"})
public class ControladorFijas {
	
	@Autowired
	private FijasService fijasService;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	// ==================    PROFESIONES          ===========================	
	@ApiOperation(value = "Lista las profesiones")
	@GetMapping({"/listarprofesiones"})
	public List<Profesiones> listarProfesiones() {
		
		List<Profesiones> lista = new ArrayList<Profesiones>();
		try {
			lista = fijasService.listarProfesiones();
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	@ApiOperation(value = "Buscar profesion por codigo")
	@GetMapping("/buscarprofesion/{id}")
    public ResponseEntity<?> buscarPorIdProfesion(@PathVariable Long id) { 
		Profesiones generico = new Profesiones();
		generico.setpRofesionidpk(id);
		
    	Map<String,Object> response = new HashMap<>();
    	try {
    		generico =fijasService.buscarPorCodigoProfesion(generico);
    		  
    		  if(generico==null) {
    	    		response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.FPROFESION_MSG_ERROR_BUSCAR);
    				response.put(ConstantesUtil.X_ERROR,ConstantesUtil.FPROFESION_ERROR_BUSCAR);
    				response.put(ConstantesUtil.X_ENTIDAD, generico);
    	    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
    	    	}
    		  
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    	return new ResponseEntity<Profesiones>(generico,HttpStatus.OK);
    	 
    }
	
	
	// ==================    TIPOS DE DOCUMENTOS  ===========================
	@ApiOperation(value = "Lista los tipo de documentos")
	@GetMapping({"/listartipodocumentos"})
	public List<TipoDocumentos> listarTipoDocumentos() { 
		
		List<TipoDocumentos> lista = new ArrayList<TipoDocumentos>();
		try {
			lista = fijasService.listarTipoDocumentos();
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	@ApiOperation(value = "Busca el tipo de documento por su codigo")
	@GetMapping("/buscartipodocumento/{id}")
    public ResponseEntity<?> buscarPorIdTipoDocumento(@PathVariable Long id) { 
		TipoDocumentos generico = new TipoDocumentos();
		generico.settPdocumentoidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		generico =fijasService.buscarPorCodigoTipoDocumento(generico);
 
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPODOCUMENTO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPODOCUMENTO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    	return new ResponseEntity<TipoDocumentos>(generico,HttpStatus.OK);
    }
	
	
	// ==================    TIPOS DE CONSEJEROS  ===========================
	@ApiOperation(value = "Lista los tipos de consejeros")
	@GetMapping({"/listartipoconsejeros"})
	public List<Tipoconsejero> listarConsejeros() { 
		return fijasService.listarTipoConsejeros();
	} 
	
	
	@ApiOperation(value = "Buscar consejero por su codigo")
	@GetMapping("/buscartipoconsejero/{id}")
    public ResponseEntity<?> buscarPorIdConsejero(@PathVariable Long id) { 
		Tipoconsejero generico = new Tipoconsejero();
		generico.settPconsejeroidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoTipoConsejero(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPOCONSEJERO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPOCONSEJERO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    	return new ResponseEntity<Tipoconsejero>(generico,HttpStatus.OK);
    }

	// ==================    TIPOS DE REGIONES    ===========================
	@ApiOperation(value = "Lista las regiones")
	@GetMapping({"/listaregiones"})
	public List<Regiones> listarRegiones(
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		
		List<Regiones>  lstregiones=  new ArrayList<Regiones>();
		
		try {
 
			if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
				lstregiones = fijasService.listarTipoRegiones();
			}else {
				Regiones regiones= new Regiones();
				regiones.setrEgionidpk(idRegion);
				lstregiones = fijasService.listarTipoRegiones(regiones);
			}
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		
		return lstregiones;
	} 
	
	
	
	@ApiOperation(value = "Busca region por su codigo")
	@GetMapping("/buscaregion/{id}")
    public ResponseEntity<?> buscarPorIdRegion(@PathVariable Long id) { 
		Regiones generico = new Regiones();
		generico.setrEgionidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoRegion(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.REGION_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.REGION_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    	return new ResponseEntity<Regiones>(generico,HttpStatus.OK);
    }
	
	// ==================   listar tipo CONSEJO     ===========================
	@ApiOperation(value = "Lista los tipos de consejo")
	@GetMapping({"/listartipoconsejo"})
	public List<Consejos> listarConsejos() { 
		
		List<Consejos> lista = new ArrayList<Consejos>(); 
		try {
			lista =fijasService.listarConsejos();
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	@ApiOperation(value = "Busca consejo por su codigo")
	@GetMapping("/buscarconsejo/{id}")
    public ResponseEntity<?> buscarPorIdConsejo(@PathVariable Long id) { 
		Consejos generico = new Consejos();
		generico.setcOnsejoidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoConsejo(generico);
    		 
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPO_CONSEJO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPO_CONSEJO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	 
    	return new ResponseEntity<Consejos>(generico,HttpStatus.OK);
    }
	
	
	
	// ==================    TIPOS DE COMISIONES  ===========================
	@ApiOperation(value = "Lista los tipo de comisiones")
	@GetMapping({"/listartipocomisiones"})
	public List<TipoComisiones> listarTipoComisiones() { 
		
		List<TipoComisiones> lista = new ArrayList<TipoComisiones>();
		try {
			lista = fijasService.listarTipoComisiones();
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	@ApiOperation(value = "Busca comision por su codigo")
	@GetMapping("/buscartipocomision/{id}")
    public ResponseEntity<?> buscarPorIdTipoComisiones(@PathVariable Long id) { 
		TipoComisiones generico = new TipoComisiones();
		generico.settIpocomsidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoTipoComision(generico);

			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPO_COMISION_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPO_COMISION_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
    		
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	 
    	return new ResponseEntity<TipoComisiones>(generico,HttpStatus.OK);
    }
	
	// ==================    TIPOS DE SESION      ===========================
	@ApiOperation(value = "Lista los tipo de sesion")
	@GetMapping({"/listartiposesion"})
	public List<TipoSesiones> listarTipoSesion() {
		
		 List<TipoSesiones> lista = new ArrayList<TipoSesiones>();
		
		try {
			lista = fijasService.listarTipoSesion();
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	@ApiOperation(value = "Lista informacion de una sesion por su codigo")
	@GetMapping("/buscarsesion/{id}")
    public ResponseEntity<?> buscarPorCodigoTipoSesion(@PathVariable Long id) { 
		TipoSesiones generico = new TipoSesiones();
		generico.settIposesionidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoTipoSesion(generico);
    		 
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPO_SESION_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPO_SESION_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    		
    	return new ResponseEntity<TipoSesiones>(generico,HttpStatus.OK);
    	 
    }
	
	
	// ==================    TIPOS DE TEMAS       ===========================
	@ApiOperation(value = "Lista los tipo de temas")
	@GetMapping({"/listartemas"})
	public List<TipoTemas> listarTipoTemas() { 
		
		List<TipoTemas>  lista =  new ArrayList<TipoTemas>();
		try {
			lista = fijasService.listarTipoTemas();
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	@ApiOperation(value = "Busca un tema por su codigo")
	@GetMapping("/buscartema/{id}")
    public ResponseEntity<?> buscarPorIdTipoTema(@PathVariable Long id) { 
		TipoTemas generico = new TipoTemas();
		generico.settIpotemaidpk(id);
    	Map<String,Object> response = new HashMap<>();
    	try {
    		
    		generico =fijasService.buscarPorCodigoTipoTema(generico); 
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TIPO_TEMA_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TIPO_TEMA_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
    	 
    	return new ResponseEntity<TipoTemas>(generico,HttpStatus.OK);
    	 
    }
	
	
	
	// ==================    ENTIDADES       ===========================
	@ApiOperation(value = "Lista las entidades")
	@GetMapping({"/listarentidades"})
	public List<Entidades> listarEntidades() { 
		List<Entidades> lista =  new ArrayList<Entidades>();
		try {
			lista = fijasService.listarEntidades();	
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	} 
	
	
	
	
	@ApiOperation(value = "Busca entidad por su codigo")
	@GetMapping("/buscarentidad/{id}")
    public ResponseEntity<?> buscarPorIdEntidad(@PathVariable Long id) {
		Entidades entidad = new Entidades();
		entidad.seteNtidadidpk(id);
		Map<String,Object> response = new HashMap<>();
		try {
			entidad = fijasService.buscarPorEntidad(entidad);
			
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, entidad);
    		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Entidades>(entidad,HttpStatus.OK);
	}
	  
	// ==================    ENTIDADES       ===========================
	@ApiOperation(value = "Lista los roles")
	@GetMapping({"/listaroles"})
	public List<Roles> listarRoles(
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			) { 
		List<Roles> listaRoles = new ArrayList<Roles>();
		
		try {
			if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
				listaRoles = fijasService.listaRoles();
			}else {
				listaRoles =  fijasService.listaRolesCorssat();
			}
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		
		
		return listaRoles;
	} 
	
	
	@ApiOperation(value = "Lista Consejo")
	@GetMapping({"/listarconsejo/{idregion}"})
	 public List<Seleccion> listaConsejo(
			 @PathVariable int idregion,
			 @RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			 @RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			 @RequestHeader(name = "info_rol", required = true) String nombreRol
			 ){
		
		 List<Seleccion> lista = new ArrayList<Seleccion>();
		
		try {
			lista = fijasService.listaConsejo(idregion);
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		 return lista;
	 }
	
	
	/*
	@ApiOperation(value = "Lista Modulos")
	@GetMapping({"/listarmodulo/{idconsejo}"})
	 public List<Seleccion> listaModulo(
			 @PathVariable int idconsejo,
			 @RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			 @RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			 @RequestHeader(name = "info_rol", required = true) String nombreRol
			 ){
		
		 List<Seleccion> lista = new ArrayList<Seleccion>();
		
		try {
			lista = fijasService.listaModulo(idconsejo);
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		 return lista;
	 }
	*/
	
	
	@ApiOperation(value = "Lista Tipo Modulo")
	@GetMapping({"/listartipomodulo/"})
	 public List<Seleccion> listaTipoModulo(
			 @RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			 @RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			 @RequestHeader(name = "info_rol", required = true) String nombreRol
			 ){
		
		 List<Seleccion> lista = new ArrayList<Seleccion>();
		
		try {
			lista = fijasService.listaTipoModulo();
		} catch (DataAccessException e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		 return lista;
	 }
 
	
}
