package pe.gob.mtpe.sivice.externo.integracion.api;

import java.io.File;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Archivos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.EncargadoRegion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Entidades;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoDocumentos;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ArchivoUtilitarioService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.EncargadoRegionService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RequestMapping({"/api/encargadoregion"})
public class ControladorEncargadoRegion {
	
	@Autowired
	private EncargadoRegionService encargadoRegionService;
	
	@Autowired
	private ArchivoUtilitarioService archivoUtilitarioService;

	@Value("${rutaArchivo}")
	private String rutaRaiz;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ApiOperation(value = "Busca encaragdos de la region")
	@PostMapping("/buscar")
	public List<EncargadoRegion> informacion(
			@RequestParam(value = "regionpk")           Long    regionpk,
			@RequestParam(value = "nombre") 			String  nombre, 
			@RequestParam(value = "numerodocumento")    String  numerodocumento,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
		
		EncargadoRegion generico = new EncargadoRegion();
		List<EncargadoRegion> lsregiones= new ArrayList<EncargadoRegion>();
		
		try {
 
			Regiones region = new Regiones(); 
			region.setrEgionidpk(idRegion);
			
			generico.setRegion(region);
			generico.setRegionpk(regionpk);
			generico.setvNombre(nombre);
			generico.setvNumdocaprobacion(numerodocumento);
		
			lsregiones = encargadoRegionService.buscar(generico);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
		
		return lsregiones;
	}
	
	
	@ApiOperation(value = "Lista encargados de la regiones")
	@GetMapping("/")
	public List<EncargadoRegion> informacion(
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		
		List<EncargadoRegion> generico= new ArrayList<EncargadoRegion>();
		
		try {
			if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
				generico = encargadoRegionService.listar();
			}else {
				
				Regiones region = new Regiones(); 
				region.setrEgionidpk(idRegion);
				generico = encargadoRegionService.listarFiltrado(region);
			}
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("Fin ***************************************************************");
		}
 
		
		
		return generico;
	}
	
	
	
	@ApiOperation(value = "Registra un encargado por region")
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(
			          @RequestParam(value = "regionpk")            Long          regionpk,
			          @RequestParam(value = "entidad")             Long          entidad, 
			          @RequestParam(value = "nombre") 			   String        nombre, 
			          @RequestParam(value = "apellidopaterno")     String        apellidopaterno, 
			          @RequestParam(value = "apellidomaterno")     String        apellidomaterno, 
			          @RequestParam(value = "tipodoc",required = false)             Long          tipodoc, 
			          @RequestParam(value = "numerodocumento",required = false)    String        numerodocumento, 
			          @RequestParam(value = "numerocelular")       String        numerocelular,  
			          @RequestParam(value = "fechadocaprobacion",required = false)  String        fechadocaprobacion, 
			          @RequestParam(value = "numdocaprobacion")  String        numdocaprobacion,
			          @RequestParam(value="docaprob",required = false)              MultipartFile docaprob,
			          @RequestHeader(name = "id_usuario", required = true) Long idUsuario,
					  @RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
					  @RequestHeader(name = "info_rol", required = true) String nombreRol
	 ) {
		
		EncargadoRegion  generico = new EncargadoRegion();
		Map<String, Object> response = new HashMap<>();
		try {
			
			if(docaprob!=null && docaprob.getSize()>0) {
				Archivos archivo = new Archivos();
				archivo = archivoUtilitarioService.cargarArchivo(docaprob, ConstantesUtil.C_ENCARGADO_REGION);
				if (archivo.isVerificarCarga() == true && archivo.isVerificarCarga() == true) {
					generico.setvNombreArchivo(archivo.getNombre());
					generico.setvUbicacionarchivo(archivo.getUbicacion());
					generico.setvExtension(archivo.getExtension());
				}
				
			}
			
			Entidades ventidad = new Entidades();
			ventidad.seteNtidadidpk(entidad);
			
			TipoDocumentos tipodocumentos = new TipoDocumentos();
			tipodocumentos.settPdocumentoidpk(tipodoc);
			
			Regiones region = new Regiones();
			region.setrEgionidpk(regionpk);
			
			 
			generico.setRegion(region);
			generico.setEntidades(ventidad);
			generico.setvNombre(nombre);
			generico.setvApellidopaterno(apellidopaterno);
			generico.setvApellidomaterno(apellidomaterno);
			generico.setTipoDocumentos(tipodocumentos);
			generico.setvNumdocumento(numerodocumento);
			generico.setvNumerocelular(numerocelular);
			generico.setdFechaprobacion((fechadocaprobacion!=null)? FechasUtil.convertStringToDate(fechadocaprobacion) : null  );
			generico.setvNumdocaprobacion(numdocaprobacion);
			generico = encargadoRegionService.registrar(generico);
			
			
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
		
		return new ResponseEntity<EncargadoRegion>(generico,HttpStatus.OK);
	}
	
	
	
	@ApiOperation(value = "Actualizar informacion del encargado de la region")
	@PostMapping("/actualizar")
	public ResponseEntity<?> actualizar(
			          @RequestParam(value = "eNcargadoregionidpk") Long          eNcargadoregionidpk,
			          @RequestParam(value = "regionpk")            Long          regionpk,
			          @RequestParam(value = "entidad")             Long          entidad, 
			          @RequestParam(value = "nombre") 			   String        nombre, 
			          @RequestParam(value = "apellidopaterno")     String        apellidopaterno, 
			          @RequestParam(value = "apellidomaterno")     String        apellidomaterno, 
			          @RequestParam(value = "tipodoc")             Long          tipodoc, 
			          @RequestParam(value = "numerodocumento")    String        numerodocumento, 
			          @RequestParam(value = "numerocelular")       String        numerocelular,  
			          @RequestParam(value = "fechadocaprobacion")  String        fechadocaprobacion, 
			          @RequestParam(value = "numdocaprobacion")  String        numdocaprobacion,
			          @RequestParam(value="docaprob", required = false)              MultipartFile docaprob,
			          @RequestHeader(name = "id_usuario", required = true) Long idUsuario,
					  @RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
					  @RequestHeader(name = "info_rol", required = true) String nombreRol
	 ) {
		
		EncargadoRegion  generico = new EncargadoRegion();
		Map<String, Object> response = new HashMap<>();
		try {
			
			
			generico.seteNcargadoregionidpk(eNcargadoregionidpk);
			generico = encargadoRegionService.buscarPorId(generico);
			
			if(docaprob!=null && docaprob.getSize()>0) {
				Archivos archivo = new Archivos();
				archivo = archivoUtilitarioService.cargarArchivo(docaprob, ConstantesUtil.C_ENCARGADO_REGION);
				if (archivo.isVerificarCarga() == true && archivo.isVerificarCarga() == true) {
					generico.setvNombreArchivo(archivo.getNombre());
					generico.setvUbicacionarchivo(archivo.getUbicacion());
					generico.setvExtension(archivo.getExtension());
				}
				
			}
			
			
			 
			Entidades ventidad = new Entidades();
			ventidad.seteNtidadidpk(entidad);
			
			TipoDocumentos tipodocumentos = new TipoDocumentos();
			tipodocumentos.settPdocumentoidpk(tipodoc);
			
			Regiones region = new Regiones();
			region.setrEgionidpk(regionpk);
			
			generico.setRegion(region);
			generico.setEntidades(ventidad);
			generico.setvNombre(nombre);
			generico.setvApellidopaterno(apellidopaterno);
			generico.setvApellidomaterno(apellidomaterno);
			generico.setTipoDocumentos(tipodocumentos);
			generico.setvNumdocumento( (numerodocumento.equals("null"))? null  : numerodocumento );
			generico.setvNumerocelular(numerocelular);
			generico.setdFechaprobacion((fechadocaprobacion!=null)? FechasUtil.convertStringToDate(fechadocaprobacion) : null  );
			generico.setvNumdocaprobacion( (numdocaprobacion.equals("null"))? null  : numdocaprobacion );
			generico = encargadoRegionService.actualizar(generico);
			
			
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
		
		return new ResponseEntity<EncargadoRegion>(generico,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Muestra la informacion del encargado de la region")
	@GetMapping("/{idencargadoregion}")
	public ResponseEntity<?> informacion(
			@PathVariable Long idencargadoregion,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		
		EncargadoRegion  generico = new EncargadoRegion();
		generico.seteNcargadoregionidpk(idencargadoregion);
		Map<String, Object> response = new HashMap<>();
		try {
			generico = encargadoRegionService.buscarPorId(generico);
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
		
		return new ResponseEntity<EncargadoRegion>(generico,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Elimina al encargado de la region")
	@DeleteMapping("/{idencargadoregion}")
	public ResponseEntity<?> eliminar(
			@PathVariable Long idencargadoregion,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		
		EncargadoRegion  generico = new EncargadoRegion();
		generico.seteNcargadoregionidpk(idencargadoregion);
		Map<String, Object> response = new HashMap<>();
		try {
			
			generico = encargadoRegionService.buscarPorId(generico);
			generico = encargadoRegionService.Eliminar(generico);
			
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
		
		return new ResponseEntity<EncargadoRegion>(generico,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "descarga el archivo ")
	@GetMapping("/descargar/{id}")
	public ResponseEntity<?>   descargar(
			@PathVariable Long id, 
			HttpServletResponse res) {
		EncargadoRegion generico = new EncargadoRegion(); 
		String ruta = "";
		Map<String, Object> response = new HashMap<>();
		try {
			generico.seteNcargadoregionidpk(id);
			generico = encargadoRegionService.buscarPorId(generico);
			if(generico==null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.TEMA_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.TEMA_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			ruta = rutaRaiz + generico.obtenerRutaAbsolutaArchivo();
			File fichero = new File(ruta);
			if(!fichero.exists()) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.C_ARCHIVO_MENSAJE);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.C_ARCHIVO_ERROR_MENSAJE);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} 
			res.setHeader("Content-Disposition", "attachment; filename=" + generico.getvNombreArchivo()+"."+generico.getvExtension());
			res.getOutputStream().write(Files.readAllBytes(Paths.get(ruta)));
		} catch (Exception e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMessage()));
			log.error("Fin ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<EncargadoRegion>(generico, HttpStatus.OK);
	}
	
	

}
