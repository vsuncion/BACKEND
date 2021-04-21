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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.PlanTrabajo;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.PlanTrabajoDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class PlanTrabajoDaoImpl extends BaseDao<Long, PlanTrabajo> implements PlanTrabajoDao {

	//private static final Logger logger = LoggerFactory.getLogger(PlanTrabajoDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<PlanTrabajo> listar(PlanTrabajo planTrabajo) {
		EntityManager manager = createEntityManager();
		List<PlanTrabajo> lista = manager
				.createQuery("SELECT c FROM PlanTrabajo c INNER JOIN c.region r WHERE r.rEgionidpk=:idregion AND c.consejo.cOnsejoidpk=:idconsejo  AND c.cFlgeliminado=:eliminado ORDER BY c.pLantrabidpk DESC")
				.setParameter("idregion", planTrabajo.getRegion().getrEgionidpk())
				.setParameter("idconsejo", planTrabajo.getConsejo().getcOnsejoidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public PlanTrabajo buscarPorId(PlanTrabajo planTrabajo) {
		PlanTrabajo plan = buscarId(planTrabajo.getpLantrabidpk());
		return plan;
	}

	@Override
	public List<PlanTrabajo> buscar(PlanTrabajo planTrabajo) {
		 
		List<PlanTrabajo> resultado = new ArrayList<PlanTrabajo>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<PlanTrabajo> criteriaQuery = builder.createQuery(PlanTrabajo.class);
		Root<PlanTrabajo> root = criteriaQuery.from(PlanTrabajo.class);
		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("region"),planTrabajo.getRegion().getrEgionidpk()),
		valor4 = builder.equal(root.get("consejo"),planTrabajo.getConsejo().getcOnsejoidpk());
		
		if(planTrabajo.getvCodigoplantrab().trim().length()>0) {
			valor1 = builder.like(root.get("vCodigoplantrab"), "%"+ planTrabajo.getvCodigoplantrab() + "%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(vcontinuar==1) {
			if(planTrabajo.getdFecaprobacion()!= null && planTrabajo.getdFecaprobacionfin()!=null) {
				valor1 = builder.between(root.get("dFecaprobacion"), planTrabajo.getdFecaprobacion(), planTrabajo.getdFecaprobacionfin());
				finalbusqueda = builder.and(valor1,valor3,valor4);
				vcontinuar = 0;
			} 
		}
		
		
		if(vcontinuar==1) {
			if(planTrabajo.getdFecinicio()!= null && planTrabajo.getdFecfin()!=null) {
				valor1 = builder.between(root.get("dFecfin"), planTrabajo.getdFecinicio(), planTrabajo.getdFecfin());
				finalbusqueda = builder.and(valor1,valor3,valor4);
				vcontinuar = 0;
			} 
		}
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<PlanTrabajo> query = (Query<PlanTrabajo>) manager.createQuery(criteriaQuery);
			  resultado = query.getResultList();
			
		}else {
			resultado = new ArrayList<PlanTrabajo>();
		}
		
		manager.close(); 
		return resultado;
	}

	@Override
	public PlanTrabajo Registrar(PlanTrabajo planTrabajo) {
		planTrabajo.setvCodigoplantrab(GenerarCorrelativo(planTrabajo));
		planTrabajo.setdFecreg(new Date());
		guardar(planTrabajo);
		return planTrabajo;
	}

	@Override
	public PlanTrabajo Actualizar(PlanTrabajo planTrabajo) {
		planTrabajo.setdFecmodifica(FechasUtil.fechaActual());
		planTrabajo.setdFecmodifica(new Date());
		actualizar(planTrabajo);
		return planTrabajo;
	}

	@Override
	public PlanTrabajo Eliminar(PlanTrabajo planTrabajo) {
		planTrabajo.setdFecelimina(FechasUtil.fechaActual());
		planTrabajo.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		planTrabajo.setdFecelimina(new Date());
		actualizar(planTrabajo);
		return planTrabajo;
	}
	
	
	public String GenerarCorrelativo(PlanTrabajo planTrabajo) {
		Long tipoplan = Long.parseLong("0"); 
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(p)+1 FROM PlanTrabajo p WHERE p.region.rEgionidpk=:idregion AND p.consejo.cOnsejoidpk=:idconsejo")
				.setParameter("idregion", planTrabajo.getRegion().getrEgionidpk())
				.setParameter("idconsejo", planTrabajo.getConsejo().getcOnsejoidpk())
				.getSingleResult();
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativo,tipoplan,ConstantesUtil.PLANTRABAJO_ALIAS_CORRELATIVO);
		manager.close();
		return StrcorrelativoFinal;
	}

}
