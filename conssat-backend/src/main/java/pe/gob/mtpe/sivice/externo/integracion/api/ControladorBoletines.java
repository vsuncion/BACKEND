package pe.gob.mtpe.sivice.externo.integracion.api;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Archivos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Boletines;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Consejos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ArchivoUtilitarioService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.BoletinService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;


@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({"api/boletines"})
public class ControladorBoletines { 

	@Autowired
	private BoletinService boletinService;
	
	@Autowired
	private ArchivoUtilitarioService archivoUtilitarioService;
	
	@Autowired
	private  FijasService fijasService;

	@Value("${rutaArchivo}")
	private String rutaRaiz;
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	
	@ApiOperation(value = "Lista los boletines")
	@GetMapping("/")
	public List<Boletines> listarBoletines(
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario, 
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		 
		Boletines boletines = new Boletines();
		List<Boletines> lista = new ArrayList<Boletines>();
		
		try {
			
			Regiones region = new Regiones();
			region.setrEgionidpk(idRegion);
			boletines.setRegion(region);
			
			Consejos consejos = new Consejos();
			consejos.setcOnsejoidpk(fijasService.BuscarConsejoPorNombre(nombreRol));
			boletines.setConsejo(consejos);
			
			lista = boletinService.listar(boletines);
			
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

  
	
	@ApiOperation(value = "Registrar un boletin")
	@PostMapping("/registrar")
	public ResponseEntity<?> registrarBoletin(
			@RequestParam("archivoboletin") MultipartFile archivoboletin, 
			@RequestParam("fecha") String fecha_boletin,
			@RequestParam("comision") String comision, 
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		 
		
		// *****************  INFORMACION DEL USUARIO LOGEADO ***************
		   Long idconsejo = 0L;  
		   idconsejo = fijasService.BuscarConsejoPorNombre(nombreRol);
		// ******************************************************************
		   
		Boletines generico = new Boletines();
		Archivos archivo = new Archivos();
		
		Map<String, Object> response = new HashMap<>();
		try {
			
			archivo = archivoUtilitarioService.cargarArchivo(archivoboletin, ConstantesUtil.BOLETIN_ALIAS_CORRELATIVO);

			if (archivo.isVerificarCarga() == true && archivo.isVerificarCarga() == true) { 
				
				//*****  DATOS DE USUARIO DE INICIO DE SESION **********
				Consejos consejo = new Consejos();
				consejo.setcOnsejoidpk(idconsejo);
				
				Regiones region = new Regiones();
				region.setrEgionidpk(idRegion);
				//*******************************************************
				
				generico.setdFecboletin(FechasUtil.convertStringToDate(fecha_boletin));
				generico.setvNombrearchivo(archivo.getNombre());
				generico.setvArchivoextension(archivo.getExtension());
				generico.setvUbiarch(archivo.getUbicacion());
				generico.setConsejo(consejo);
				generico.setRegion(region);
				generico.setnUsureg(idUsuario);
				generico.setvComision(comision);
				generico = boletinService.Registrar(generico);
			} else {
				response.put(ConstantesUtil.X_MENSAJE, "ARCHIVO NO ADJUNTO");
				response.put(ConstantesUtil.X_ERROR, "NO SE ENCONTRO EL ARCHIVO ADJUNTO");
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
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Boletines>(generico,HttpStatus.CREATED);
	}
	
	
	@ApiOperation(value = "Actualizar un boletin",notes = "Actualizar un boletin de los Consejos" )
	@PostMapping("/actualizar")
	public ResponseEntity<?> actualizarBoletin(
			@RequestParam(value="codigoboletin")                                 Long codigoboletin,
			@RequestParam(value="archivoboletin",required = false) MultipartFile archivoboletin, 
			@RequestParam(value="fecha")                                         String fecha_boletin,
			@RequestParam("comision") String comision,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			) { 
		
		Archivos archivo = new Archivos();
		Boletines generico = new Boletines();
		Map<String, Object> response = new HashMap<>();
		try {
			generico.setbOletinidpk(codigoboletin);
			generico = boletinService.buscarPorId(generico);
			
			if(archivoboletin!=null && archivoboletin.getSize()>0) {
				archivo = archivoUtilitarioService.cargarArchivo(archivoboletin, ConstantesUtil.C_BOLETINES);
				generico.setvUbiarch(archivo.getUbicacion());
				generico.setvNombrearchivo((archivo.getNombre()));
				generico.setvArchivoextension(archivo.getExtension());
			}
			
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.BOLETIN_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.BOLETIN_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
			if(fecha_boletin!=null) {
				generico.setdFecboletin(FechasUtil.convertStringToDate(fecha_boletin));
			}
 
			generico.setnUsumodifica(idUsuario);
			generico.setvComision(comision);
			generico = boletinService.Actualizar(generico);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}  
		
		return new ResponseEntity<Boletines>(generico,HttpStatus.OK);
	}

	@ApiOperation(value = "Eliminar un boletin")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminarBoletin(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		
		Boletines generico = new Boletines();
		generico.setbOletinidpk(id);
		Map<String, Object> response = new HashMap<>();
		try { 
			
			generico = boletinService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.BOLETIN_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.BOLETIN_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
			generico.setnUsuelimina(idUsuario);
			generico = boletinService.Eliminar(generico);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.BOLETIN_MSG_EXITO_ELIMINAR);
		response.put(ConstantesUtil.X_ENTIDAD, generico);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	
	@ApiOperation(value = "Mostrar informacion del boletin por su identificador")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorId(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		
		Boletines generico = new Boletines();
		generico.setbOletinidpk(id);
		Map<String, Object> response = new HashMap<>();
		try { 
			
			generico = boletinService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.BOLETIN_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.BOLETIN_ERROR_BUSCAR);
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
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		 
		response.put(ConstantesUtil.X_ENTIDAD, generico);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	
	
	@ApiOperation(value = "Buscar boletines por los criterios de busqueda")
	@PostMapping("/buscar")
	public List<Boletines> buscar(
			@RequestBody Boletines buscar,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		
		Regiones region = new Regiones();
		
		try {
			region.setrEgionidpk(idRegion);
			buscar.setRegion(region);
			buscar.setnUsureg(idUsuario);
			
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		
		return boletinService.buscarBoletines(buscar);
	}
	
	
	
	@ApiOperation(value = "descargar archivo del boletin")
	@GetMapping("/descargar/{id}")
	public void descargarArchivo(
			@PathVariable Long id, HttpServletResponse res) {
		Boletines generico = new Boletines();
		generico.setbOletinidpk(id);
		String ruta = "";
		try {
			generico = boletinService.buscarPorId(generico);
			ruta = rutaRaiz + generico.obtenerRutaAbsoluta();
			res.setHeader("Content-Disposition", "attachment; filename=" + generico.getvNombrearchivo()+"."+generico.getvArchivoextension());
			res.getOutputStream().write(Files.readAllBytes(Paths.get(ruta)));
		} catch (Exception e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMessage()));
			log.error("Fin ***************************************************************");
		}

	}
	
 

}
