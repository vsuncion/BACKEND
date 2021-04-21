package pe.gob.mtpe.sivice.externo.core.negocio.service;

import java.util.List;

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

public interface FijasService {

	// PROFESIONES
	public List<Profesiones> listarProfesiones();
	public Profesiones buscarPorCodigoProfesion(Profesiones profesion);

	// TIPOS DE DOCUMENTOS
	public List<TipoDocumentos> listarTipoDocumentos();
	public TipoDocumentos buscarPorCodigoTipoDocumento(TipoDocumentos tipoDocumentos);

	// TIPOS DE CONSEJEROS
	public List<Tipoconsejero> listarTipoConsejeros();
	public Tipoconsejero buscarPorCodigoTipoConsejero(Tipoconsejero tipoconsejero);

	// TIPOS DE REGIONES
	public List<Regiones> listarTipoRegiones();
	public Regiones buscarPorCodigoRegion(Regiones regiones);
	public List<Regiones> listarTipoRegiones(Regiones regiones);
	
	// TIPOS DE CONSEJO
	public List<Consejos> listarConsejos();
	public Consejos buscarPorCodigoConsejo(Consejos consejos);
	public Long BuscarConsejoPorNombre(String rolusuario); 

	// TIPOS DE COMISIONES
	public List<TipoComisiones> listarTipoComisiones();
	public TipoComisiones buscarPorCodigoTipoComision(TipoComisiones tipocomisiones);

	// TIPOS DE SESION
	public List<TipoSesiones> listarTipoSesion();
	public TipoSesiones buscarPorCodigoTipoSesion(TipoSesiones sesion);

	// TIPOS DE TEMAS
	public List<TipoTemas> listarTipoTemas();
	public TipoTemas buscarPorCodigoTipoTema(TipoTemas temas);

	// ENTIDADES
	public List<Entidades> listarEntidades();
	public Entidades buscarPorEntidad(Entidades entidad);
	
	//ROLES
	public List<Roles> listaRoles();
	public Roles buscaRoles(Roles rol);
	public List<Roles> listaRolesCorssat();
	
	//CORREOS
	public String cuerpoCorreo(String xRegion,String xTipoComision,String xCodigoSesion,String xFechaSesion,String xHoraInicio,String xHoraFin,List<String> lsIntegrantes); 
    public void enviarCorreo(String lsCorreos,String xCuerpoCorreo);
    
    public List<Seleccion> listaConsejo(int idRegion);
    //public List<Seleccion> listaModulo(int idComision);
    public List<Seleccion> listaTipoModulo();
	 

}
