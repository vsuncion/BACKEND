package pe.gob.mtpe.sivice.externo.integracion.api;

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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Comisiones; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoComisiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.TipoSesiones;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ComisionService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.CorrelativoService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;


@CrossOrigin(origins = { "http://localhost:4200", "*"})
@RequestMapping({ "api/correlativos"})
@RestController
public class ControladorCorrelativos {
	
	@Autowired
	private CorrelativoService  correlativoService;
	
	@Autowired
	private FijasService fijasService;
	
	@Autowired
	private ComisionService comisionService;
	
	Logger log = LoggerFactory.getLogger(this.getClass()); 
	
	
	
	
	@ApiOperation(value = "Lista todos los correlativos CONSSAT y CORSAT")
	@GetMapping("/listar")
	public List<Correlativos> listar (
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
		
		List<Correlativos> lista = new ArrayList<Correlativos>();
		Regiones region = new Regiones();
		
		if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
			region.setrEgionidpk(0L); 
		}else {
			region.setrEgionidpk(idRegion);
			region = fijasService.buscarPorCodigoRegion(region);
		}
		
		lista = correlativoService.listar(region);
		return lista;
	}
	
	
	@ApiOperation(value = "Informacion correlativo CONSSAT y CORSAT")
	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorId(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
		
		Correlativos generico = new Correlativos();
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			generico.setcOrrelativoidpk(id);
			generico = correlativoService.buscarPorId(generico);
			
			if (generico == null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CORRELATIVO_MSG_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CORRELATIVO_ERROR_BUSCAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
		} catch (DataAccessException e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Correlativos>(generico,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Registrar un Correlativo CONSSAT y CORSAT")
	@PostMapping("/registrar")
	public  ResponseEntity<?> Registrar(
			@RequestParam(value="vRegion", required = true)   String vRegion, 
			@RequestParam(value="vConsejo", required = true)  String vConsejo,
			@RequestParam(value="vTipo", required = true)     String vTipo,
			@RequestParam(value="nValorInicial", required = true)     Long  nValorInicial,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
		
		Correlativos generico = new Correlativos();
		Map<String, Object> response = new HashMap<>();
		
        //REGION
		Regiones region = new Regiones();
		region.setrEgionidpk(Long.valueOf(vRegion));
		
		 region = fijasService.buscarPorCodigoRegion(region);
		 vRegion = region.getvDesnombre();
		
		// CONSEJO
		switch (vConsejo) {
		case "1":
			vConsejo = ConstantesUtil.C_CONSSAT; //"CONSSAT";
			break;
			
		case "2":
			vConsejo = ConstantesUtil.C_CORSAT; //"CORSAT";
			break;
			
		case "3":
			vConsejo = ConstantesUtil.C_COMICONSSAT; //"COMICONSSAT";
			break;
			
		case "4":
			vConsejo = ConstantesUtil.C_COMICORSAT; //"COMICORSAT";
			break;

		default:
			vConsejo =null;
			break;
		}
		
		
 
		
		
		// TIPO MODULO
		switch (vTipo) {
		
		case "1":
			vTipo = "ORDINARIAS";
			break;
			
		case "2":
			vTipo = "EXTRAORDINARIAS";
			break;

		default:
			vTipo =null;
			break;
		}

		
		try {
			
			generico.setvRegion(vRegion);
			generico.setvModulo(ConstantesUtil.C_SESION_MODULO);
			generico.setvConsejo(vConsejo);
			generico.setvTipo(vTipo);
			generico.setnValorInicial(nValorInicial);
			
			//BUSCAMOS QUE EL CORRELATIVO NO EXISTA
			String respuesta = correlativoService.Duplicado(generico);
			
			//REGISTRAMOS EL CORRELATIVO
			
			if(ConstantesUtil.C_RESPUESTA_INACTIVO.equals(respuesta)) {
				generico = correlativoService.Registrar(generico); 
			}else {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CORRELATIVO_MSG_ERROR_DUPLICADO);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CORRELATIVO_ERROR_DUPLICADO);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
 	
		} catch (DataAccessException e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
 
		return new ResponseEntity<Correlativos>(generico,HttpStatus.CREATED); 
 
	}
	
	
	@ApiOperation(value = "Actualizar valor del Correlativo CONSSAT y CORSAT")
	@PostMapping("/actualizar")
	public  ResponseEntity<?> Actualizar(
			@RequestParam(value="cOrrelativoidpk", required = true)   Long cOrrelativoidpk,
			@RequestParam(value="nValorInicial", required = true)   Long nValorInicial ,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
		Correlativos generico = new Correlativos();
		Map<String, Object> response = new HashMap<>();
		
		try {
			generico.setcOrrelativoidpk(cOrrelativoidpk);
			generico = correlativoService.buscarPorId(generico);
			generico.setnValorInicial(nValorInicial);
			
			if(nValorInicial!=null) {
				generico=correlativoService.Actualizar(generico);
			}else {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.CORRELATIVO_MSG_ERROR_ACTUALIZAR);
				response.put(ConstantesUtil.X_ERROR, ConstantesUtil.CORRELATIVO_ERROR_ACTUALIZAR);
				response.put(ConstantesUtil.X_ENTIDAD, generico);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			
			
		} catch (DataAccessException e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			response.put(ConstantesUtil.X_ENTIDAD, generico);
			e.printStackTrace();
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Correlativos>(generico,HttpStatus.CREATED); 
		
	}
	
	// CORRELATIVOS SESIONES
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion")
	@GetMapping("/listarsesiones")
	public List<CorrelativosSesion> listarCorrelativoSesion ( 
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario, 
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){ 
		
		CorrelativosSesion  correlativosSesion = new CorrelativosSesion();
		Regiones region = new Regiones(); 
		
		if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
			region = null;
		}else {
			region.setrEgionidpk(idRegion);  
			correlativosSesion.setRegion(region);
		}
		 
	 return correlativoService.listarCorrelativoSesiones(correlativosSesion);	
	}
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@PostMapping("/buscarsesiones")
	public List<CorrelativosSesion> buscarCorrelativoSesion ( 
			@RequestParam(value = "region") Long idregion,
			@RequestParam(value = "codigocomision") String codigoComision,
			@RequestParam(value = "tipoSesion") Long idtipoSesion,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,  
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){ 
		
		CorrelativosSesion  correlativosSesion = new CorrelativosSesion();
		Regiones region = new Regiones(); 
		TipoSesiones tipoSesion = new TipoSesiones();
		
		region.setrEgionidpk(idregion); 
		tipoSesion.settIposesionidpk(idtipoSesion);
		
		correlativosSesion.setRegion(region); 
		correlativosSesion.setTipoSesion(tipoSesion);
		correlativosSesion.setCodigocomision(codigoComision);
		
	 return correlativoService.buscarCorrelativoSesiones(correlativosSesion);	
	}
	
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@PostMapping("/registrarsesiones")
	public ResponseEntity<?> registraCorrelativoSesion(
			@RequestParam(value = "vregion") Long vregion,
			@RequestParam(value = "vcomision") String vcomision,
			@RequestParam(value = "vtiposesion") Long vtiposesion,
			@RequestParam(value = "valorinicial") Long valorinicial,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario 
			){
		
		CorrelativosSesion  correlativosSesion = new CorrelativosSesion();
		
		Regiones region = new Regiones(); 
		Comisiones  comision  = new Comisiones();
		TipoSesiones tipoSesion = new TipoSesiones();
		Map<String, Object> response = new HashMap<>();
		Long codigoComision = 0L;
		Comisiones enComision = new Comisiones();
		try {
 
			
			//BUSCAMOS LA COMISION DE LA REGION
			
			enComision=comisionService.buscarComisionPorNombre(vcomision.trim().toUpperCase(),vregion);
			
			if(enComision.getcOmisionidpk()==null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
				response.put(ConstantesUtil.X_ERROR,"NO SE ENCONTRO LA COMISION "+vcomision.trim().toUpperCase());
				response.put(ConstantesUtil.X_ENTIDAD, enComision);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			codigoComision=enComision.getcOmisionidpk();
			
			region.setrEgionidpk(vregion);
			comision.setcOmisionidpk(codigoComision);
			tipoSesion.settIposesionidpk(vtiposesion);
			
			correlativosSesion.setRegion(region);
			correlativosSesion.setComision(comision);
			correlativosSesion.setTipoSesion(tipoSesion);
			correlativosSesion.setValorInicial(valorinicial);
			
			correlativosSesion = correlativoService.RegistrarCorrelativoSesion(correlativosSesion);
			
		} catch (Exception e) {
		 
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosSesion);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CorrelativosSesion>(correlativosSesion,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@PostMapping("/actualizarsesiones")
	public ResponseEntity<?> actualizarCorrelativoSesion(
			@RequestParam(value = "vsesioncorrelativo") Long vsesioncorrelativo,
			@RequestParam(value = "valorinicial") Long valorinicial,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario 
			){
		
		CorrelativosSesion  correlativosSesion = new CorrelativosSesion();
		Map<String, Object> response = new HashMap<>();
		
		try {
			correlativosSesion.setCorrelativoSesion(vsesioncorrelativo);
			correlativosSesion = correlativoService.buscarCorrelativoSesionId(correlativosSesion);
			correlativosSesion.setValorInicial(valorinicial);
			correlativosSesion = correlativoService.ActualizarCorrelativoSesion(correlativosSesion);
			
		} catch (Exception e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosSesion);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CorrelativosSesion>(correlativosSesion,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@GetMapping("/buscarsesionid/{id}")
	public ResponseEntity<?> buscarCorrelativoSesionId(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		CorrelativosSesion  correlativosSesion = new CorrelativosSesion();
		Map<String, Object> response = new HashMap<>();
		try {
			correlativosSesion.setCorrelativoSesion(id);
			correlativosSesion = correlativoService.buscarCorrelativoSesionId(correlativosSesion);
			
			if(!ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
				if(correlativosSesion.getRegion().getrEgionidpk()!=idRegion) {
					response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
					response.put(ConstantesUtil.X_ERROR,"LA REGION ES DIFERENTE "+idRegion);
					response.put(ConstantesUtil.X_ENTIDAD, correlativosSesion);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			
		} catch (Exception e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosSesion);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<CorrelativosSesion>(correlativosSesion,HttpStatus.OK);
	}
	
	
	 // CORRELATIVOS COMISIONES
	@ApiOperation(value = "Lista todos los correlativos de comision")
	@GetMapping("/listarcomisiones")
	public List<CorrelativosComision> listarCorrelativoComisiones ( 
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario, 
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){ 
		
		CorrelativosComision  correlativosComision = new CorrelativosComision();
		Regiones region = new Regiones(); 
		
		if(ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
			region = null;
		}else {
			region.setrEgionidpk(idRegion);  
			correlativosComision.setRegion(region);
		}
		 
	 return correlativoService.listarCorrelativoComisiones(correlativosComision);
	 
	}
	
	
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@PostMapping("/registrarcomisiones")
	public ResponseEntity<?> registraCorrelativoComision(
			@RequestParam(value = "region") Long vregion, 
			@RequestParam(value = "tipoComisiones") Long vtipocomision,
			@RequestParam(value = "valorInicial") Long valorinicial,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol
			){
	 
		
		Regiones region = new Regiones();  
		TipoComisiones tipoComisiones = new TipoComisiones();
		CorrelativosComision  correlativosComision = new CorrelativosComision();
		Map<String, Object> response = new HashMap<>(); 
		
		try {
 
			region.setrEgionidpk(vregion);
			tipoComisiones.settIpocomsidpk(vtipocomision);
			correlativosComision.setRegion(region);
			correlativosComision.setTipoComisiones(tipoComisiones);
			correlativosComision.setValorInicial(valorinicial);
			correlativosComision = correlativoService.RegistrarCorrelativoComision(correlativosComision);
			
		} catch (Exception e) {
		 
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosComision);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CorrelativosComision>(correlativosComision,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@GetMapping("/buscarcomisionid/{id}")
	public ResponseEntity<?> buscarCorrelativoCimisionId(
			@PathVariable Long id,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario,
			@RequestHeader(name = "info_regioncodigo", required = true) Long idRegion,
			@RequestHeader(name = "info_rol", required = true) String nombreRol){
		CorrelativosComision  correlativosComision = new CorrelativosComision();
		Map<String, Object> response = new HashMap<>();
		try {
			correlativosComision.setCorrelativoComision(id);
			correlativosComision = correlativoService.buscarCorrelativoComisionId(correlativosComision);
			
			if(!ConstantesUtil.C_ROLE_ADMCONSSAT.equals(nombreRol)) {
				if(correlativosComision.getRegion().getrEgionidpk()!=idRegion) {
					response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
					response.put(ConstantesUtil.X_ERROR,"LA REGION ES DIFERENTE "+idRegion);
					response.put(ConstantesUtil.X_ENTIDAD, correlativosComision);
					return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			
			if(correlativosComision==null) {
				response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
				response.put(ConstantesUtil.X_ERROR,"No se encontraron datos");
				response.put(ConstantesUtil.X_ENTIDAD, correlativosComision);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			
		} catch (Exception e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosComision);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<CorrelativosComision>(correlativosComision,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Lista todos los correlativos de la sesion al Administrador")
	@PostMapping("/actualizarcomisiones")
	public ResponseEntity<?> actualizarCorrelativoComision(
			@RequestParam(value = "vcodigocorrelativocomision") Long vcodigocorrelativocomision,
			@RequestParam(value = "valorinicial") Long valorinicial,
			@RequestHeader(name = "id_usuario", required = true) Long idUsuario 
			){
		
		CorrelativosComision  correlativosComision = new CorrelativosComision();
		Map<String, Object> response = new HashMap<>();
		
		try {
			correlativosComision.setCorrelativoComision(vcodigocorrelativocomision);
			correlativosComision = correlativoService.buscarCorrelativoComisionId(correlativosComision);
			correlativosComision.setValorInicial(valorinicial);
			correlativosComision = correlativoService.ActualizarCorrelativoComision(correlativosComision);
			
		} catch (Exception e) {
			response.put(ConstantesUtil.X_MENSAJE, ConstantesUtil.GENERAL_MSG_ERROR_BASE);
			response.put(ConstantesUtil.X_ERROR,e.getMessage().concat(":")+e.getCause());
			response.put(ConstantesUtil.X_ENTIDAD, correlativosComision);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<CorrelativosComision>(correlativosComision,HttpStatus.OK);
	}
	
	

}
