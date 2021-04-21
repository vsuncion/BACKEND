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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Archivos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.ComiConsej;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Tipoconsejero;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ArchivoUtilitarioService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ComisionConsejeroService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;


@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({ "/api/comisiconsej" })
public class ControladorComisionConsejero {
 
	
	@Autowired
	private ComisionConsejeroService ComisionConsejeroService;
	
	@Autowired
	private FijasService fijasService;
	
	@Autowired
	private ArchivoUtilitarioService archivoUtilitarioService;

	@Value("${rutaArchivo}")
	private String rutaRaiz;

	Logger log = LoggerFactory.getLogger(this.getClass());
	/*
	@GetMapping("/")
	public List<ComiConsej> listar() {
		return ComisionConsejeroService.listar();
	}
	
	@PostMapping("/comisiconsej")
	public ResponseEntity<?> registrar(@RequestBody ComiConsej generico) {
		logger.info("========== INGRESO A GRABAR BOLETINES=============== ");
		Map<String, Object> response = new HashMap<>();
		try {
			generico = ComisionConsejeroService.Registrar(generico);
		} catch (DataAccessException e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ComiConsej>(generico,HttpStatus.CREATED);
	}
	
	@PostMapping("/buscar")
	public List<ComiConsej> buscar(@RequestBody ComiConsej buscar) {
		return null;
	}

	
	*/

	
	@ApiOperation(value = "Mostrar informacion del consejero por su codigo identificador")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		ComiConsej generico = new ComiConsej();
		generico.setcOmiconsidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {
			generico = ComisionConsejeroService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.COMISION_CONSEJERO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.COMISION_CONSEJERO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("FIN ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<ComiConsej>(generico,HttpStatus.OK);
	}

	
	
	@ApiOperation(value = "Registra o Listar los consejeros por comision")
	@GetMapping("/consejeroscomision/{idcomision}")
	public List<ComiConsej> listarConsejerosComision(
			@PathVariable Long idcomision,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		
		// ******* REGISTRAR O LISTAR CONSEJEROS ASIGNADOS POR COMISION
 
		List<ComiConsej> listaconsejero = new ArrayList<ComiConsej>();
		try {
			listaconsejero = ComisionConsejeroService.buscar(idcomision,idRegion,idUsuario);
		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("FIN ***************************************************************");
		}
		
		return listaconsejero;
	}
	

	@ApiOperation(value = "Actualizar datos del consejero")
	@PostMapping("/actualizar")
	public ResponseEntity<?> actualizar(
			@RequestParam(value="codcomiconsejero" ) Long codcomiconsejero,
			@RequestParam(value="vtipoconsejero",required = true )   Long vtipoconsejero,
			@RequestParam(value="vfechainicio" )     String vfechainicio,
			@RequestParam(value="vfechafin" )        String vfechafin,
			@RequestParam(value="vnumerodocumento") String vnumerodocumento,
			@RequestParam(value="vdocumento", required = false )       MultipartFile  vdocumento,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			) {
		ComiConsej generico = new ComiConsej(); 
		Archivos archivoDocumento = new Archivos();
		Map<String, Object> response = new HashMap<>();
		try {
 
			  generico.setcOmiconsidpk(codcomiconsejero);
 
			  generico = ComisionConsejeroService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.COMISION_CONSEJERO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.COMISION_CONSEJERO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
 
			//*****  DATOS DE USUARIO DE INICIO DE SESION **********
			 Long codigoconsejo=fijasService.BuscarConsejoPorNombre(nombreRol); // CONSSAT
			//*******************************************************
			
			if(vdocumento!=null && vdocumento.getSize()>0) {
				archivoDocumento = archivoUtilitarioService.cargarArchivo(vdocumento, ConstantesUtil.C_DOCCOMISIONCONSEJERO);
				generico.setvNombrearchivo(archivoDocumento.getNombre());
			    generico.setvUbicacion(archivoDocumento.getUbicacion());
			    generico.setvExtension(archivoDocumento.getExtension());
			}
			
			
			Tipoconsejero tipoconsejero= new Tipoconsejero();
            tipoconsejero.settPconsejeroidpk(vtipoconsejero);
            
            generico.setTipoconsejero(tipoconsejero);
            generico.setTipoconsejero(tipoconsejero);
            generico.setdFecinicio(  (vfechainicio!=null)? FechasUtil.convertStringToDate(vfechainicio) : null  );
            generico.setdFecfin(     (vfechafin!=null)? FechasUtil.convertStringToDate(vfechafin) : null );
            generico.setvNumdocumento(vnumerodocumento);
            generico.setnUsureg(codigoconsejo); 
            generico = ComisionConsejeroService.Actualizar(generico);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("FIN ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		
		return new ResponseEntity<ComiConsej>(generico,HttpStatus.OK);
	}

	
	
	
	@ApiOperation(value = "Eliminar un consejero")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) { 
		ComiConsej generico = new ComiConsej();
		generico.setcOmiconsidpk(id);
		Map<String, Object> response = new HashMap<>();
		try {
			
			generico = ComisionConsejeroService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.COMISION_CONSEJERO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.COMISION_CONSEJERO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} 
			generico = ComisionConsejeroService.Eliminar(generico);

		} catch (DataAccessException e) {
			log.error("INICIA CODIGO REGION="+idRegion.toString()+"***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			log.error("FIN ***************************************************************");
			
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR, e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<ComiConsej>(generico,HttpStatus.OK);

	}
	
	
	@ApiOperation(value = "descargar archivo")
	@GetMapping("/descargar/{id}")
	public void descargarArchivo(
			@PathVariable Long id,
			HttpServletResponse res) {
		ComiConsej generico = new ComiConsej();
		generico.setcOmiconsidpk(id);
		String ruta = "";
		try {
			generico = ComisionConsejeroService.buscarPorId(generico);
			ruta = rutaRaiz + generico.obtenerRutaAbsoluta();
			res.setHeader("Content-Disposition", "attachment; filename=" + generico.getvNombrearchivo()+"."+generico.getvExtension());
			res.getOutputStream().write(Files.readAllBytes(Paths.get(ruta)));
		} catch (Exception e) {
			log.error("INICIA  ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMessage()));
			log.error("FIN ***************************************************************");
		}

	}
	
	

}
