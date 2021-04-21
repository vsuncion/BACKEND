package pe.gob.mtpe.sivice.externo.core.accesodatos.repository.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery; 
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Sesiones; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.SesionDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class SesionDaoImpl extends BaseDao<Long, Sesiones> implements SesionDao {
	

	@SuppressWarnings("unchecked")
	@Override
	public List<Sesiones> listar(Sesiones sesion) {
		EntityManager manager = createEntityManager();
		List<Sesiones> lista = manager
				.createQuery("SELECT s FROM Sesiones s INNER JOIN s.region r WHERE r.rEgionidpk=:idregion AND s.consejofk.cOnsejoidpk=:idconsejo  AND s.cFlgeliminado=:eliminado ORDER BY s.sEsionidpk DESC")
				.setParameter("idregion",sesion.getRegion().getrEgionidpk())
				.setParameter("idconsejo", sesion.getConsejofk().getcOnsejoidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Sesiones buscarPorId(Sesiones sesion) {
		Sesiones infosesion = buscarId(sesion.getsEsionidpk());
		return infosesion;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Sesiones buscarPorIdAsistencia(Sesiones sesion) {
		EntityManager manager = createEntityManager();
		List<Sesiones> lista = manager
				.createQuery("SELECT c FROM Sesiones c WHERE c.sEsionidpk=:idsesion AND c.cFlgeliminado=:eliminado ORDER BY c.sEsionidpk DESC")
				.setParameter("idsesion", sesion.getsEsionidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		if(!lista.isEmpty()) {
			sesion = lista.get(0);
		}else {
			sesion =null;
		}
		return sesion;
	}

	@Override
	public List<Sesiones> buscar(Sesiones sesion) {
		
		List<Sesiones> resultado = new ArrayList<Sesiones>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Sesiones> criteriaQuery = builder.createQuery(Sesiones.class);
		Root<Sesiones> root = criteriaQuery.from(Sesiones.class);
		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null, 
		valor3 = builder.equal(root.get("region"),sesion.getRegion().getrEgionidpk()),
		valor4 = builder.equal(root.get("consejofk"),sesion.getConsejofk().getcOnsejoidpk());
 
		
		if(sesion.getvCodsesion().trim().length()>0) {
			valor1 = builder.like(root.get("vCodsesion"),"%"+sesion.getvCodsesion()+"%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}else {
			vcontinuar = 1;
		}
		
		
		if(sesion.getdFechaInicio()!=null && sesion.getdFechaFin()!=null && vcontinuar==1 ) {
			valor1 = builder.between(root.get("dFecreacion"), sesion.getdFechaInicio(), sesion.getdFechaFin());
			finalbusqueda = builder.and(valor1,valor3,valor4);
			vcontinuar = 0;		
		} 

		if(sesion.getTipoSesiones().gettIposesionidpk()!=null && vcontinuar==1) {
			valor1 = builder.equal(root.get("tipoSesiones"),sesion.getTipoSesiones().gettIposesionidpk());
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		} 
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			criteriaQuery.orderBy( builder.desc(root.get("comisionfk")), builder.desc(root.get("sEsionidpk")) );
			Query<Sesiones> query = (Query<Sesiones>) manager.createQuery(criteriaQuery);
			resultado = query.getResultList();
		}else {
			resultado = new ArrayList<Sesiones>();
		}
		
		
		manager.close();
		return resultado;
	}

	@Override
	public Sesiones Registrar(Sesiones sesion) {
		sesion.setvCodsesion(GenerarCorrelativo(sesion));
		guardar(sesion);
		return sesion;
	}

	@Override
	public Sesiones Actualizar(Sesiones sesion) {
		sesion.setdFecmodifica(new Date());
		actualizar(sesion);
		return sesion;
	}

	@Override
	public Sesiones Eliminar(Sesiones sesion) {
		sesion.setdFecelimina(new Date());
		sesion.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(sesion);
		return sesion;
	}
	
	public String GenerarCorrelativo(Sesiones sesion) {
		
		Long correlativoInicial= ValorInicial(sesion);
		
		/*
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(s)+1 FROM Sesiones s WHERE s.region.rEgionidpk=:idregion AND s.consejofk.cOnsejoidpk=:idconsejo AND s.tipoSesiones.tIposesionidpk=:idtiposesion")
				.setParameter("idregion", sesion.getRegion().getrEgionidpk())
				.setParameter("idconsejo", sesion.getConsejofk().getcOnsejoidpk())
				.setParameter("idtiposesion", sesion.getTipoSesiones().gettIposesionidpk())
				.getSingleResult();
		manager.close();
		if(correlativoInicial>0) {
			correlativo = correlativo + correlativoInicial;
		}
		*/
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativoInicial,sesion.getTipoSesiones().gettIposesionidpk(),ConstantesUtil.SESION_ALIAS_CORRELATIVO);
		return StrcorrelativoFinal;
	}
	
	
	public Long ValorInicial(Sesiones sesion) {
		Long valorInicial = Long.parseLong("0");
		 switch(sesion.getConsejofk().getvDesnombre()) {
		  case ConstantesUtil.C_CONSSAT :
			  valorInicial=correlativoConssatCorsat(sesion);
			break;
		
		  case ConstantesUtil.C_CORSAT :
			  valorInicial=correlativoConssatCorsat(sesion);
			break;
			
		  case ConstantesUtil.C_COMICONSSAT :
			  valorInicial=correlativoComisionConssatCorsat(sesion);
			break;
			
			
		  case ConstantesUtil.C_COMICORSAT :
			  valorInicial=correlativoComisionConssatCorsat(sesion);
			break;
			
		  default :	 
			   
		   }
		return valorInicial;
	}

	@Override
	public List<Sesiones> buscarSesion(Sesiones sesion) {
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Sesiones> criteriaQuery = builder.createQuery(Sesiones.class);
		Root<Sesiones> root = criteriaQuery.from(Sesiones.class);
		
		Predicate valor1 = builder.like(root.get("vCodsesion"), "%"+sesion.getvCodsesion()+"%");
		Predicate valor2 = builder.equal(root.get("consejofk"), sesion.getConsejofk().getcOnsejoidpk());
		Predicate valor3 = builder.equal(root.get("region"), sesion.getRegion().getrEgionidpk());
		Predicate valor4 = builder.and(valor2,valor3);
		criteriaQuery.where(valor1,valor4);
		Query<Sesiones> query = (Query<Sesiones>) manager.createQuery(criteriaQuery);
		List<Sesiones> resultado = query.getResultList();
		manager.close();
		return resultado;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long correlativoConssatCorsat(Sesiones sesion) {
		
		//OBTENER CORRELATIVO 
		EntityManager manager = createEntityManager();
		List<Correlativos> lista1 = new ArrayList<Correlativos>(); 
		Long valorInicial = Long.parseLong("0");
	 
		lista1 = manager.createQuery("SELECT c FROM Correlativos c WHERE vRegion=:nomregion AND vModulo=:nommodulo AND vConsejo=:nomconsejo AND vTipo=:nomtiposesion")
				.setParameter("nomregion", sesion.getRegion().getvDesnombre())
				.setParameter("nommodulo", ConstantesUtil.C_SESION_MODULO)
				.setParameter("nomconsejo", sesion.getConsejofk().getvDesnombre())
				.setParameter("nomtiposesion", sesion.getTipoSesiones().getvDesnombre())
				.getResultList(); 
		manager.close();
		
		if(!lista1.isEmpty()) {
			valorInicial = lista1.get(0).getnValorInicial();
		} 
		
		
		//OBTENER CANTIDAD DE SESIONES 
		EntityManager manager2 = createEntityManager();
		Long correlativo = (Long) manager2.createQuery("SELECT COUNT(s)+1 FROM Sesiones s WHERE s.region.rEgionidpk=:idregion AND s.consejofk.cOnsejoidpk=:idconsejo AND s.tipoSesiones.tIposesionidpk=:idtiposesion")
				.setParameter("idregion", sesion.getRegion().getrEgionidpk())
				.setParameter("idconsejo", sesion.getConsejofk().getcOnsejoidpk())
				.setParameter("idtiposesion", sesion.getTipoSesiones().gettIposesionidpk())
				.getSingleResult();
		manager2.close();
		
		valorInicial = valorInicial+correlativo;
		 
		
		return valorInicial; 
		 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long correlativoComisionConssatCorsat(Sesiones sesion) {
		EntityManager manager = createEntityManager();
		List<CorrelativosSesion> lista1 = new ArrayList<CorrelativosSesion>(); 
		Long valorInicial = Long.parseLong("0");
		Long correlativo = Long.parseLong("0");
		lista1 = manager.createQuery("SELECT cr FROM CorrelativosSesion cr  WHERE cr.region.rEgionidpk=:region_pk AND cr.comision.cOmisionidpk=:comision_pk AND cr.tipoSesion.tIposesionidpk=:tiposesion_pk")
		//lista1 = manager.createQuery("SELECT COUNT(1) FROM CorrelativosSesion cr INNER JOIN cr.region re   WHERE re.vDesnombre=:nomregion")
		//lista1 = manager.createQuery("SELECT COUNT(1) FROM CorrelativosSesion cr INNER JOIN cr.region re INNER JOIN cr.comision cm  WHERE re.rEgionidpk=:region_pk AND cm.cOmisionidpk=:comision_pk")
				.setParameter("region_pk", sesion.getRegion().getrEgionidpk())
				.setParameter("comision_pk", sesion.getComisionfk().getcOmisionidpk()) 
				.setParameter("tiposesion_pk", sesion.getTipoSesiones().gettIposesionidpk())
				.getResultList(); 
		manager.close();
		
		if(!lista1.isEmpty()) {
			valorInicial = lista1.get(0).getValorInicial();
		} 
		
		//OBTENER CANTIDAD DE SESIONES 
		EntityManager manager2 = createEntityManager();
		correlativo = (Long) manager2.createQuery("SELECT COUNT(s)+1 FROM Sesiones s WHERE s.region.rEgionidpk=:idregion AND s.consejofk.cOnsejoidpk=:idconsejo AND s.tipoSesiones.tIposesionidpk=:idtiposesion AND s.comisionfk.cOmisionidpk=:comision_pk")
				.setParameter("idregion", sesion.getRegion().getrEgionidpk())
				.setParameter("idconsejo", sesion.getConsejofk().getcOnsejoidpk())
				.setParameter("idtiposesion", sesion.getTipoSesiones().gettIposesionidpk())
				.setParameter("comision_pk", sesion.getComisionfk().getcOmisionidpk()) 
				.getSingleResult();
		manager2.close();
		
		valorInicial = valorInicial+correlativo;
		
		return valorInicial; 
	}

	 

}
