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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Actas;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Acuerdos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Sesiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.ActasDao;
import pe.gob.mtpe.sivice.externo.core.negocio.service.impl.ActaServiceImpl;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class ActasDaoImpl extends BaseDao<Long, Actas> implements ActasDao {

	private static final Logger logger = LoggerFactory.getLogger(ActaServiceImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<Actas> listar() {
		EntityManager manager = createEntityManager();
		List<Actas> lista = manager
				.createQuery("FROM Actas b WHERE b.cFlagelimina=:eliminado ORDER BY b.aCtaidpk DESC")
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Actas buscarPorId(Actas actas) {
		actas = buscarId(actas.getaCtaidpk());
		return actas;
	}

	@Override
	public List<Actas> buscar(Actas actas) {
		    return null;
	}

	@Override
	public Actas Registrar(Actas actas) {
		actas.setvCodacta(GenerarCorrelativo(actas));
		guardar(actas);
		return actas;
	}

	@Override
	public Actas Actualizar(Actas actas) {
		actas.setdFecmodifica(new Date());
		actualizar(actas);
		return actas;
	}

	@Override
	public Actas Eliminar(Actas actas) {
		actas.setdFecelimina(new Date());
		actas.setcFlagelimina(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(actas);
		return actas;
	}

	public String GenerarCorrelativo(Actas actas) {
		Long tipoacta = Long.parseLong("0");
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(b)+1 FROM Actas b INNER JOIN b.sesionfk s WHERE s.region.rEgionidpk=:idregion AND s.consejofk.cOnsejoidpk=:idconsejo AND s.tipoSesiones.tIposesionidpk=:idtiposesion")
				.setParameter("idregion", actas.getSesionfk().getRegion().getrEgionidpk())
				.setParameter("idconsejo", actas.getSesionfk().getConsejofk().getcOnsejoidpk())
				.setParameter("idtiposesion", actas.getSesionfk().getTipoSesiones().gettIposesionidpk())
				.getSingleResult();
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativo,tipoacta,ConstantesUtil.ACTA_ALIAS_CORRELATIVO);
		manager.close();
		return StrcorrelativoFinal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Actas buscarActaPorIdSesion(Long idSesion) {
		Actas acta = new Actas();
		EntityManager manager = createEntityManager();
		List<Actas> lista = manager
				.createQuery("SELECT a FROM Actas a INNER JOIN a.sesionfk s WHERE s.sEsionidpk=:sesion AND a.cFlagelimina=:eliminado ")
				.setParameter("sesion", idSesion)
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		 if(lista.isEmpty()) {
			 acta =null;
		 }else {
			 acta = lista.get(0);
		 }
		return acta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Acuerdos> listaAcuerdosPorActa(Actas actas) {
		EntityManager manager = createEntityManager();
		List<Acuerdos> lista = manager
				.createQuery("SELECT a FROM Acuerdos a INNER  JOIN a.acta ac WHERE ac.aCtaidpk=:actafk AND a.cFlgeliminado=:eliminado ORDER BY a.aCuerdoidpk DESC")
				.setParameter("actafk", actas.getaCtaidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	 
	@Override
	public List<Actas> buscarActasPorSesion(Actas actas) {
		Sesiones session = new Sesiones();
		List<Actas> resultado = new ArrayList<Actas>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Actas> criteriaQuery = builder.createQuery(Actas.class);
		Root<Actas> root = criteriaQuery.from(Actas.class);
		
		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("sesionfk").get("region"),actas.getNregion()),
		valor4 = builder.equal(root.get("sesionfk").get("tipoSesiones"),actas.getnTipoSesion());
		
		
		if(actas.getvCodigoSesion().trim().length()>0) {
			valor1 = builder.like(root.get("sesionfk").get("vCodsesion"), "%"+actas.getvCodigoSesion()+"%");
			finalbusqueda = builder.and(valor1,valor3); 
			vcontinuar = 0;
		}else {
			vcontinuar = 1;
		}
		
		if(actas.getnTipoSesion()!=null && vcontinuar==1) {
			valor1 = builder.equal(root.get("sesionfk").get("tipoSesiones"),actas.getnTipoSesion());
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(actas.getVfechaInicio()!=null && actas.getVfechafin()!=null && vcontinuar==1) {
			logger.info("================"+actas.getVfechaInicio()+"**********"+FechasUtil.convertStringToDate(actas.getVfechaInicio()));
			session.setdFechaInicio(FechasUtil.convertStringToDate(actas.getVfechaInicio()));
			session.setdFechaFin(FechasUtil.convertStringToDate(actas.getVfechafin()));
			 
			valor1 = builder.between(root.get("sesionfk").get("dFecreacion"),session.getdFechaInicio(),session.getdFechaFin());
			finalbusqueda = builder.and(valor1,valor3);
			vcontinuar = 0;
		}
		
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<Actas> query = (Query<Actas>) manager.createQuery(criteriaQuery);
		    resultado = query.getResultList();	
		}else {
			resultado = new ArrayList<Actas>();
		}
		
		manager.close();
		return resultado;  
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Actas> listarActasPorSesion(Actas actas) {
		EntityManager manager = createEntityManager();
		List<Actas> lista = manager
				.createQuery("SELECT a  FROM Actas  a INNER JOIN  a.sesionfk s INNER JOIN s.region r  WHERE r.rEgionidpk=:codigoregion AND s.consejofk.cOnsejoidpk=:idconsejo AND a.cFlagelimina=:eliminado AND s.cFlgeliminado=:eliminado ORDER BY s.sEsionidpk DESC")
				.setParameter("codigoregion", actas.getNregion())
				.setParameter("idconsejo", actas.getnTipoConsejo())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}
 
	
 
}
