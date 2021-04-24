package pe.gob.mtpe.sivice.externo.core.negocio.service.impl;


import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.FijasDao;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ComisionService;
import pe.gob.mtpe.sivice.externo.core.negocio.service.FijasService;  
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;


@Service("profesionesService")
@Transactional(readOnly = true)
public class FijasServiceImpl implements FijasService {
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FijasDao fijasDao;
	
	@Autowired
	private ComisionService comisionService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${correoEnvia}")
	private String xFrom;

	@Override
	public List<Profesiones> listarProfesiones() {
		return fijasDao.listarProfesiones();
	}

	@Override
	public Profesiones buscarPorCodigoProfesion(Profesiones profesion) {
		return fijasDao.buscarPorCodigoProfesion(profesion);
	}

	@Override
	public List<TipoDocumentos> listarTipoDocumentos() {
		return fijasDao.listarTipoDocumentos();
	}

	@Override
	public TipoDocumentos buscarPorCodigoTipoDocumento(TipoDocumentos tipoDocumentos) {
		return fijasDao.buscarPorCodigoTipoDocumento(tipoDocumentos);
	}

	@Override
	public List<Tipoconsejero> listarTipoConsejeros() {
		return fijasDao.listarTipoConsejeros();
	}

	@Override
	public Tipoconsejero buscarPorCodigoTipoConsejero(Tipoconsejero tipoconsejero) {
		return fijasDao.buscarPorCodigoTipoConsejero(tipoconsejero);
	}

	@Override
	public List<Regiones> listarTipoRegiones() {
		return fijasDao.listarTipoRegiones();
	}

	@Override
	public Regiones buscarPorCodigoRegion(Regiones regiones) {
		return fijasDao.buscarPorCodigoRegion(regiones);
	}

	@Override
	public List<Consejos> listarConsejos() {
		return fijasDao.listarConsejos();
	}

	@Override
	public Consejos buscarPorCodigoConsejo(Consejos consejos) {
		return fijasDao.buscarPorCodigoConsejo(consejos);
	}

	@Override
	public List<TipoComisiones> listarTipoComisiones() {
		return fijasDao.listarTipoComisiones();
	}

	@Override
	public TipoComisiones buscarPorCodigoTipoComision(TipoComisiones tipocomisiones) {
		return fijasDao.buscarPorCodigoTipoComision(tipocomisiones);
	}

	@Override
	public List<TipoSesiones> listarTipoSesion() {
		return fijasDao.listarTipoSesion();
	}

	@Override
	public TipoSesiones buscarPorCodigoTipoSesion(TipoSesiones sesion) {
		return fijasDao.buscarPorCodigoTipoSesion(sesion);
	}

	@Override
	public List<TipoTemas> listarTipoTemas() {
		return fijasDao.listarTipoTemas();
	}

	@Override
	public TipoTemas buscarPorCodigoTipoTema(TipoTemas temas) {
		return fijasDao.buscarPorCodigoTipoTema(temas);
	}

	@Override
	public List<Entidades> listarEntidades() {
		
		return fijasDao.listarEntidades();
	}

	@Override
	public Entidades buscarPorEntidad(Entidades entidad) {
		
		return fijasDao.buscarPorEntidad(entidad);
	}

	@Override
	public List<Roles> listaRoles() { 
		return fijasDao.listaRoles();
	}

	@Override
	public Roles buscaRoles(Roles rol) { 
		return fijasDao.buscaRoles(rol);
	}

	@Override
	public Long BuscarConsejoPorNombre(String rolusuario) { 
		return fijasDao.BuscarConsejoPorNombre(rolusuario);
	}

	@Override
	public List<Regiones> listarTipoRegiones(Regiones regiones) { 
		return fijasDao.listarTipoRegiones(regiones);
	}

	@Override
	public List<Roles> listaRolesCorssat() { 
		return fijasDao.listaRolesCorssat();
	}

	@Override
	public String cuerpoCorreo(String xRegion, String xTipoComision, String xCodigoSesion, String xFechaSesion,
			String xHoraInicio, String xHoraFin, List<String> lsIntegrantes) {
		  String xcontenido = null;
		   
		 
		  xcontenido ="<html><body>";
		  xcontenido+="<p style=margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-size: 12px; font-family: Arial, Helvetica, sans-serif;'>Estimados(a):</span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>El siguiente correo tiene como fin, informar que se ha registrado una sesi&oacute;n, en la cual se encuentran asignados, la siguiente informaci&oacute;n contempla la sesi&oacute;n.</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>REGI&Oacute;N: "+xRegion+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>TIPO DE COMISI&Oacute;N: "+xTipoComision+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>C&Oacute;DIGO SESI&Oacute;N: "+xCodigoSesion+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>FECHA SESI&Oacute;N: "+xFechaSesion+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>HORA INICIO: "+xHoraInicio+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>HORA FIN: "+xHoraFin+"</span></span></p>";
		  xcontenido+="<p style='margin-top:0cm;margin-right:0cm;margin-bottom:8.0pt;margin-left:0cm;line-height:107%;font-size:15px;font-family:'Calibri',sans-serif;'><span style='font-family: Arial, Helvetica, sans-serif;'><span style='font-size: 12px;'>INTEGRANTES:</span></span></p>";
		  xcontenido+="<ul style='list-style-type: disc'>";  
		  /*
		  xcontenido = "<html><body>El siguiente correo tiene como fin, informar que se ha registrado una sesi&oacute;n y en la cual se encuentran asignados, la siguiente informaci&oacute;n contempla la sesi&oacute;n.<ul style='list-style-type: disc'>";
		  xcontenido = xcontenido + "REGION:"+xRegion;
		  xcontenido = xcontenido + "TIPO DE COMISION:"+xTipoComision;
		  xcontenido = xcontenido + "CODIGO SESION:"+xCodigoSesion;
		  xcontenido = xcontenido + "FECHA SESION:"+xFechaSesion;
		  xcontenido = xcontenido + "HORA INICIO:"+xHoraInicio;
		  xcontenido = xcontenido + "HORA FIN:"+xHoraFin;
		  xcontenido = xcontenido + "INTEGRANTES:"; */
		  
		  for(int i=0;i<lsIntegrantes.size();i++) {
			  xcontenido+="<li><span style='font-size: 12px; font-family: Arial, Helvetica, sans-serif'>"+lsIntegrantes.get(i)+"</span></li>";
		 } 
		  xcontenido+="</ul></body></html>";
		return xcontenido;
	}

	@Override
	public void enviarCorreo(String lsCorreos, String xCuerpoCorreo) {
		
		 //String xFrom ="vladimir.suncion.c@gmail.com";
 
		 MimeMessage message = javaMailSender.createMimeMessage();
		 try {
			message.setSubject("SESION ASIGNADA");
			MimeMessageHelper helper;
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(xFrom);
			helper.setTo(lsCorreos.split(","));
			helper.setText(xCuerpoCorreo, true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			log.error("CORREO ***********************"); 
			log.error("Mensaje de Error: " +e.getMessage());
			log.error("Fin ***************************************************************");
		}
		
		 
	}

	@Override
	public List<Seleccion> listaConsejo(int idRegion) {
		List<Seleccion> lista = new ArrayList<Seleccion>();
		 try {
			 
			 if(idRegion==27) {
				 lista.add(new Seleccion(1,ConstantesUtil.C_CONSSAT));
				// lista.add(new Seleccion(3,"COMISION CONSSAT"));
			 }else {
				  lista.add(new Seleccion(2,ConstantesUtil.C_CORSAT));
				 // lista.add(new Seleccion(4,"COMISION CORSSAT"));
			 }
			 
			
		} catch (Exception e) {
			log.error("CORREO ***********************"); 
			log.error("Mensaje de Error: " +e.getMessage());
			log.error("Fin ***************************************************************");
		}
		return lista;
	}

	/*
	@Override
	public List<Seleccion> listaModulo(int idComision) {
		List<Seleccion> lista = new ArrayList<Seleccion>();
		try {
			 if(idComision==0) { 
				 lista=null;
			// }else if(idComision==1 || idComision==2){
			//	 lista.add(new Seleccion(1,ConstantesUtil.C_SESION_MODULO));
			 }else {
				 lista.add(new Seleccion(1,ConstantesUtil.C_SESION_MODULO));
				// lista.add(new Seleccion(2,ConstantesUtil.C_COMISION_MODULO)); 
			 }
			
		} catch (Exception e) {
			log.error("CORREO ***********************"); 
			log.error("Mensaje de Error: " +e.getMessage());
			log.error("Fin ***************************************************************");
		}
		return lista;
	}*/

	@Override
	public List<Seleccion> listaTipoModulo() {
		List<Seleccion> lista = new ArrayList<Seleccion>();
		try {
			 lista.add(new Seleccion(1,"ORDINARIAS"));
			 lista.add(new Seleccion(2,"EXTRAORDINARIAS")); 
		} catch (Exception e) {
			log.error("CORREO ***********************"); 
			log.error("Mensaje de Error: " +e.getMessage());
			log.error("Fin ***************************************************************");
		}
		return lista;
	}

	/*
	@Override
	public List<Seleccion> listaCorssat() {
		List<Seleccion> lista = new ArrayList<Seleccion>();
		 try {
			 lista.add(new Seleccion(1,"CORSSAT"));
			 lista.add(new Seleccion(2,"COMISION CORSSAT"));
		} catch (Exception e) {
			log.error("CORREO ***********************"); 
			log.error("Mensaje de Error: " +e.getMessage());
			log.error("Fin ***************************************************************");
		}
		return lista;
	}*/

 

	
}
