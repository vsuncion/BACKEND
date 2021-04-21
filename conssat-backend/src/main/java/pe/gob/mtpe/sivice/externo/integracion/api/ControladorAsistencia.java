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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.AsistenciaConsejeros;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Asistencias;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.AsistenciasArchivos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Entidades;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Invitados;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Sesiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoDocumentos;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ArchivoUtilitarioService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.AsistenciaService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.AsistenciasArchivoService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.InvitadoService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;

@CrossOrigin(origins = { "http://localhost:4200", "*" })
@RestController
@RequestMapping({ "/api/asistencia" })
public class ControladorAsistencia {

	private static final Logger logger = LoggerFactory.getLogger(ControladorActas.class);

	@Autowired
	private AsistenciaService asistenciaService;

	@Autowired
	private FijasService fijasService;

	@Autowired
	private InvitadoService invitadoService;

	@Autowired
	private ArchivoUtilitarioService archivoUtilitarioService;

	@Autowired
	private AsistenciasArchivoService asistenciasArchivoService;

	
	@Value("${rutaArchivo}")
	private String rutaRaiz;
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ApiOperation(value = "Mostrar informacion de la sesion")
	@GetMapping("/{idsesion}")
	public ResponseEntity<?> buscarPorIdAsistencias(
			@PathVariable Long idsesion,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		// ONTENEMOS LOS DATOS DE LA SESSION
		Sesiones sesion = new Sesiones();
		Map<String, Object> response = new HashMap<>();
		try {
			sesion.setsEsionidpk(idsesion);
			sesion = asistenciaService.buscarSesion(sesion);
			 

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
			response.put(ConstantesUtil.X_ENTIDAD, sesion);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Sesiones>(sesion, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Listar asistentes por sesion")
	@GetMapping("/listarasistentes/{idsesion}")
	public List<AsistenciaConsejeros> buscarAsistencias(
			@PathVariable Long idsesion, 
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		   List<AsistenciaConsejeros> lista = new ArrayList<AsistenciaConsejeros>();
            try {
            	lista = asistenciaService.listarConsejerosAsistencia(idsesion);
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

	
	@ApiOperation(value = "Marcar la asistencia")
	@PostMapping("/actualizar")
	public ResponseEntity<?> actualizarHoraAsistencia(
			@RequestParam(value = "idAsistencia") Long idAsistencia,
			@RequestParam(value = "asistio")      String cFlgasistio,
			@RequestParam(value = "horaEntrada")  String vHoentrada,
			@RequestParam(value = "horaSalida")   String vHosalida,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {

		Map<String, Object> response = new HashMap<>();
		Asistencias generico = new Asistencias();
		try {
			generico.setaSistenciaidpk(idAsistencia);
			generico = asistenciaService.buscarPorId(generico);
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CALENDARIO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CALENDARIO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			generico.setcFlgasistio(cFlgasistio);
			generico.setvHoentrada(vHoentrada);
			generico.setvHosalida(vHosalida);
			generico = asistenciaService.Actualizar(generico);
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
		return new ResponseEntity<Asistencias>(generico, HttpStatus.OK);
	}

	
	
	@ApiOperation(value = "Mostra informacion de la asistencia por el codigo")
	@GetMapping("/info/{idasistencia}")
	public ResponseEntity<?> infoAsistente(
			@PathVariable Long idasistencia,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {

		Map<String, Object> response = new HashMap<>();
		AsistenciaConsejeros infoAsistencia = new AsistenciaConsejeros();
		Asistencias generico = new Asistencias();
 
		try {
			generico.setaSistenciaidpk(idasistencia);
			generico = asistenciaService.buscarPorId(generico);

			if (generico != null) {
				// AsistenciaConsejeros fila = new AsistenciaConsejeros();
				infoAsistencia.setIdAsistencia(generico.getaSistenciaidpk());
				infoAsistencia.setAsistio(generico.getcFlgasistio());

				infoAsistencia.setHoraEntrada(generico.getvHoentrada());
				infoAsistencia.setHoraSalida(generico.getvHosalida());

				if (generico.getConsejero() == null) {
					logger.info("=========== BUSCAMOS AL INVITADO =============");
					Invitados invitados = new Invitados();
					invitados.setiNvitadosidpk(generico.getiNvitadosfk());
					invitados = invitadoService.buscarPorId(invitados);
					infoAsistencia.setEntidad(invitados.getEntidad().getvDesnombre());
					infoAsistencia.setTipoDocumento(invitados.getTipodocumento().getvDesabreviado());
					infoAsistencia.setNumeroDocumento(invitados.getvNumerodocumento());
					infoAsistencia.setApellidosNombre(invitados.getvNombre() + "," + invitados.getvApellido_paterno()
							+ " " + invitados.getvApellido_materno());
				} else {
					infoAsistencia.setEntidad(generico.getConsejero().getEntidad().getvDesnombre());
					infoAsistencia.setTipoDocumento(generico.getConsejero().getEntidad().getvDesnombre());
					infoAsistencia.setNumeroDocumento(generico.getConsejero().getvNumdocumento());
					infoAsistencia.setApellidosNombre( generico.getConsejero().getvDesnombre() + "," + generico.getConsejero().getvDesappaterno()
									+ " " + generico.getConsejero().getvDesapmaterno());
				}

			} else {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CALENDARIO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CALENDARIO_ERROR_BUSCAR);
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
			response.put(ConstantesUtil.X_ENTIDAD, infoAsistencia);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
 
		return new ResponseEntity<AsistenciaConsejeros>(infoAsistencia, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Registrar un invitado")
	@PostMapping("/registrarinvitado")
	public ResponseEntity<?> grabarInvitado(
			@RequestParam(value = "entidad") Long entidad,
			@RequestParam(value = "sEsionfk")          Long sEsionfk, 
			@RequestParam(value = "tipodocumento")     Long tipodocumento,
			@RequestParam(value = "vNumerodocumento")  String vNumerodocumento,
			@RequestParam(value = "vNombre")           String vNombre,
			@RequestParam(value = "vApellido_paterno") String vApellido_paterno,
			@RequestParam(value = "vApellido_materno") String vApellido_materno,
			@RequestParam(value = "vNumerocelular")    String vNumerocelular,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {
		Invitados generico = new Invitados();
		Map<String, Object> response = new HashMap<>();
		try {

			Entidades entidades = new Entidades();
			entidades.seteNtidadidpk(entidad);
			entidades = fijasService.buscarPorEntidad(entidades);

			TipoDocumentos tipoDocumentos = new TipoDocumentos();
			tipoDocumentos.settPdocumentoidpk(tipodocumento);
			tipoDocumentos = fijasService.buscarPorCodigoTipoDocumento(tipoDocumentos);

			generico.setEntidad(entidades);
			generico.setsEsionfk(sEsionfk);
			generico.setTipodocumento(tipoDocumentos);
			generico.setvNumerodocumento(vNumerodocumento);
			generico.setvNombre(vNombre);
			generico.setvApellido_paterno(vApellido_paterno);
			generico.setvApellido_materno(vApellido_materno);
			generico.setvNumerocelular(vNumerocelular);
			generico.setnUsureg(idUsuario);
			generico = asistenciaService.RegistrarInvitados(generico);

			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CALENDARIO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CALENDARIO_ERROR_BUSCAR);
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

		return new ResponseEntity<Invitados>(generico, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Cargar archivo de asistencia")
	@PostMapping("/cargardocasistencia")
	public ResponseEntity<?> cargarArchivoAsistencias(
			@RequestParam(value = "docasistencia") MultipartFile docasistencia,
			@RequestParam(value = "sesioncodigo") Long sesioncodigo,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol) {

		AsistenciasArchivos asistenciasArchivos = new AsistenciasArchivos();
		Map<String, Object> response = new HashMap<>();
		try {
			
			if (docasistencia!=null && docasistencia.getSize()>0) {
				Archivos archivo = new Archivos();
				archivo = archivoUtilitarioService.cargarArchivo(docasistencia, ConstantesUtil.C_CONSEJERO_DOC_ASIGNACION);
				
				if (archivo.isVerificarCarga() == true && archivo.isVerificarCarga() == true) {
					asistenciasArchivos.setnOmbrearchivo(archivo.getNombre());
					asistenciasArchivos.seteXtensionarchivo(archivo.getExtension());
					asistenciasArchivos.setuBicacionarchivo(archivo.getUbicacion());
					asistenciasArchivos.setsEsionfk(sesioncodigo);
					asistenciasArchivos.setvFlgeliminado("0");
				} else {
					response.put(ConstantesUtil.X_MENSAJE, "ARCHIVO NO ADJUNTO");
					response.put(ConstantesUtil.X_ERROR, "NO SE ENCONTRO EL ARCHIVO ADJUNTO");
					response.put(ConstantesUtil.X_ENTIDAD, archivo);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

				} 
			}
			
 
			asistenciasArchivos = asistenciasArchivoService.cargarArchivo(asistenciasArchivos);


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
			response.put(ConstantesUtil.X_ENTIDAD, asistenciasArchivos);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<AsistenciasArchivos>(asistenciasArchivos, HttpStatus.OK);
 
	}
	
	
	@ApiOperation(value = "Verifica si el archivo existe")
	@GetMapping("/verificarasistencia/{idsesion}")
	public ResponseEntity<?>  verificarArchivoAsistencia(
			@PathVariable Long idsesion,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol  ) {
		
		AsistenciasArchivos asistenciasArchivos = new AsistenciasArchivos();
		Map<String, Object> response = new HashMap<>();
		try {
			asistenciasArchivos = asistenciasArchivoService.VerificarArchivo(idsesion);
			 
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
			response.put(ConstantesUtil.X_ENTIDAD, asistenciasArchivos);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<AsistenciasArchivos>(asistenciasArchivos, HttpStatus.OK);

	}
	
	
	
	@ApiOperation(value = "Descargar archivo de asistencia")
	@GetMapping("/descargar/{idsesion}")
	public void descargarArchivo(
			@PathVariable Long idsesion,
			HttpServletResponse res ) {
		AsistenciasArchivos asistenciasArchivos = new AsistenciasArchivos(); 
		String ruta = "";
		try {
			asistenciasArchivos = asistenciasArchivoService.descargarArchivo(idsesion);
			ruta = rutaRaiz + asistenciasArchivos.obtenerRutaAbsoluta();
			res.setHeader("Content-Disposition", "attachment; filename=" + asistenciasArchivos.getnOmbrearchivo()+"."+asistenciasArchivos.geteXtensionarchivo());
			res.getOutputStream().write(Files.readAllBytes(Paths.get(ruta)));
		} catch (Exception e) {
			log.error("INICIA ***********************");
			SQLException sqle = (SQLException) e.getCause();
			log.error("Codigo Error: " + sqle.getErrorCode());
			log.error("Estado Sql: " + sqle.getSQLState());
			log.error("Mensaje de Error: " +e.getMessage().concat(":").concat(e.getMessage()));
			log.error("Fin *****************************");
		}

	}
	

}
