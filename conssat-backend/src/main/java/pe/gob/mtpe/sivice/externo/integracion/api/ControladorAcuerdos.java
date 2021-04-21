package pe.gob.mtpe.sivice.externo.integracion.api;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Actas;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Acuerdos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.BandejaActas;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Sesiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoSesiones;
import pe.gob.mtpe.sivice.externo.core.negocio.service.AcuerdoService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({ "/api/acuerdos" })
public class ControladorAcuerdos {

	private static final Logger logger = LoggerFactory.getLogger(ControladorAcuerdos.class);

	@Autowired
	private AcuerdoService acuerdoService;
	
	@Value("${rutaArchivo}")
	private String rutaRaiz;
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/")
	public List<Acuerdos> listar() {
		return acuerdoService.listar();
	}

	@ApiOperation(value = "Muestra informacion del acuerdo por su identificador")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorIdAcuerdo(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Acuerdos generico = new Acuerdos();
		generico.setaCuerdoidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {

			generico = acuerdoService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.ACUERDO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.ACUERDO_ERROR_BUSCAR);
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
			response.put(ConstantesUtil.X_ERROR,
					e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Acuerdos>(generico, HttpStatus.OK);

	}

	@ApiOperation(value = "Busca los acuerdos por sesion")
	@PostMapping("/buscaracuerdosporsesion")
	public List<BandejaActas> buscar(
			@RequestParam("codigosesion") String codigosesion,
			@RequestParam("tiposesion") Long tiposesion, 
			@RequestParam("fechainicio") String fechainicio,
			@RequestParam("fechafin") String fechafin,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {

		Sesiones sesionbuscar = new Sesiones();
		List<BandejaActas> listarBandejaActas = new ArrayList<BandejaActas>();
		try {
			if (tiposesion != null) {
				TipoSesiones tiposesiones = new TipoSesiones();
				tiposesiones.settIposesionidpk(tiposesion);
			}

			sesionbuscar.setvCodsesion(codigosesion);
			sesionbuscar.setdFechaInicio(FechasUtil.convertStringToDate(fechainicio));
			sesionbuscar.setdFechaFin(FechasUtil.convertStringToDate(fechafin));

			listarBandejaActas = acuerdoService.buscarAcuerdosPorSesion(sesionbuscar);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		return listarBandejaActas;

	}

	@ApiOperation(value = "Registrar un acuerdo")
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(
			@RequestBody Acuerdos acuerdos,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		logger.info("========== INGRESO A GRABAR BOLETINES=============== ");
		Map<String, Object> response = new HashMap<>();
		try {
			acuerdos = acuerdoService.Registrar(acuerdos);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, acuerdos);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Acuerdos>(acuerdos, HttpStatus.CREATED);
	}

	@ApiOperation(value = "Actualizar un acuerdo")
	@PostMapping("/acuerdos")
	public ResponseEntity<?> actualizar(
			@RequestBody Acuerdos acuerdos,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Acuerdos generico = null;
		Map<String, Object> response = new HashMap<>();
		try {
			generico = acuerdoService.buscarPorId(acuerdos);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.ACUERDO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.ACUERDO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, acuerdos);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}

			acuerdos.setnUsureg(generico.getnUsureg());
			acuerdos.setdFecreg(generico.getdFecreg());
			generico = acuerdoService.Actualizar(acuerdos);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, acuerdos);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Acuerdos>(generico, HttpStatus.OK);
	}

	@ApiOperation(value = "Eliminar un acuerdo")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		logger.info("========== INGRESO A   eliminar=============== ");
		Acuerdos generico = new Acuerdos();
		generico.setaCuerdoidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {

			generico = acuerdoService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.ACUERDO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.ACUERDO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}

			generico.setnUsureg(generico.getnUsureg());
			generico.setdFecreg(generico.getdFecreg());
			generico = acuerdoService.Eliminar(generico);

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

		return new ResponseEntity<Acuerdos>(generico, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Listar acuerdos por acta")
	@GetMapping("/acuerdosporacta/{idacta}")
	public List<Acuerdos> listarAcuerdosPorActas(
			@PathVariable Long idacta,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Actas acta = new Actas();
		List<Acuerdos> lista = new ArrayList<>();
		try {
			acta.setaCtaidpk(idacta);
			lista = acuerdoService.listarAcuerdosPorActa(acta);
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
	

	 

}
