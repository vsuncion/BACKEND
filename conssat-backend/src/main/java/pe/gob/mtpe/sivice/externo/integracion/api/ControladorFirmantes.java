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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Firmantes;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FirmanteService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({ "/api/firmantes" })
public class ControladorFirmantes {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FirmanteService firmanteService;

	
	@ApiOperation(value = "Listar los firmantes")
	@GetMapping("/")
	public List<Firmantes> listar(
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		
		List<Firmantes> lista = new ArrayList<Firmantes>();
		
		try {
			
			lista = firmanteService.listar();
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return lista;
	}

	
	
	@ApiOperation(value = "Buscar firmante por su codigo")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Firmantes generico = new Firmantes();
		generico.setfIrmanteidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {
			generico = firmanteService.buscarPorId(generico); 
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.FIRMANTES_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.FIRMANTES_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Firmantes>(generico,HttpStatus.OK);
	}

 
	
	
	@ApiOperation(value = "Registrar un firmante")
	@PostMapping("/firmantes")
	public ResponseEntity<?> registrar(
			@RequestBody Firmantes generico,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		Map<String, Object> response = new HashMap<>();
		try {
			generico = firmanteService.Registrar(generico);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Firmantes>(generico,HttpStatus.CREATED);
	}

	
	/*
	@ApiOperation(value = "Actualizar un firmante")
	@PostMapping("/firmantes")
	public ResponseEntity<?> actualizar(
			@RequestBody Firmantes firmantes,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Firmantes generico = null;
		Map<String, Object> response = new HashMap<>();
		try {
			generico = firmanteService.buscarPorId(firmantes);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.FIRMANTES_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.FIRMANTES_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, firmantes);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}

			//firmantes.setnUsureg(generico.getnUsureg());
			firmantes.setdFecreg(generico.getdFecreg());
			generico = firmanteService.Actualizar(firmantes);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, firmantes);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}  
		
		return new ResponseEntity<Firmantes>(generico,HttpStatus.OK);
	}
*/
	
	
	@ApiOperation(value = "Eliminar un firmante")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		Firmantes generico = new Firmantes();
		generico.setfIrmanteidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {

			generico = firmanteService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.FIRMANTES_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.FIRMANTES_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		 
			generico = firmanteService.Eliminar(generico);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Firmantes>(generico,HttpStatus.OK);

	}

}
