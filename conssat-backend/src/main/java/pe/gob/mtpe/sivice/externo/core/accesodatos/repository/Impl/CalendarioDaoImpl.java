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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Calendarios;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CalendarioDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil; 

@Component
public class CalendarioDaoImpl extends BaseDao<Long, Calendarios> implements CalendarioDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Calendarios> listar(Calendarios calendario) {
		EntityManager manager = createEntityManager();
		List<Calendarios> lista = manager
				.createQuery("SELECT b FROM Calendarios b INNER JOIN b.region r WHERE r.rEgionidpk=:idregion AND b.consejo.cOnsejoidpk=:idconsejo AND b.cFlgeliminado=:eliminado ORDER BY b.cAlendarioidpk DESC")
				.setParameter("idregion",calendario.getRegion().getrEgionidpk())
				.setParameter("idconsejo", calendario.getConsejo().getcOnsejoidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Calendarios buscarPorId(Calendarios calendarios) {
		calendarios = buscarId(calendarios.getcAlendarioidpk());
		return calendarios;
	}

	@Override
	public List<Calendarios> buscar(Calendarios calendarios) {
		
		List<Calendarios> resultado = new ArrayList<Calendarios>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Calendarios> criteriaQuery = builder.createQuery(Calendarios.class);
		Root<Calendarios> root = criteriaQuery.from(Calendarios.class);
 		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("region"),calendarios.getRegion().getrEgionidpk()),
		valor4 = builder.equal(root.get("consejo"),calendarios.getConsejo().getcOnsejoidpk());
		
		if(calendarios.getvDesactividad()!=null) {
			valor1 = builder.like(root.get("vDesactividad"), "%"+calendarios.getvDesactividad()+"%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(calendarios.getvFechaInicioActividad()!= null && calendarios.getvFechaFinActividad()!=null && vcontinuar==1) {
			calendarios.setdFechaInicioActividad(FechasUtil.convertStringToDate(calendarios.getvFechaInicioActividad()));
			calendarios.setdFechaFinActividad(FechasUtil.convertStringToDate(calendarios.getvFechaFinActividad()));
			valor1 = builder.between(root.get("dFecactividad"), calendarios.getdFechaInicioActividad(), calendarios.getdFechaFinActividad());
			finalbusqueda = builder.and(valor1,valor3,valor4);
			vcontinuar = 0;
		}
		
		if(calendarios.getcFlgejecuto()!=null && vcontinuar==1) {
			valor1 = builder.equal(root.get("cFlgejecuto"), "1");
			finalbusqueda = builder.and(valor1,valor3,valor4);
			vcontinuar = 0;
		}
		
		
		if(calendarios.getvFechaActividad()!=null && vcontinuar==1) {
			calendarios.setdFechaActividad(FechasUtil.convertStringToDate(calendarios.getvFechaActividad()));
			valor1 = builder.equal(root.get("dFecejecuto"), calendarios.getdFechaActividad());
			finalbusqueda = builder.and(valor1,valor3,valor4);
			vcontinuar = 0;
		}
		
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<Calendarios> query = (Query<Calendarios>) manager.createQuery(criteriaQuery);
		    resultado = query.getResultList();
		}else {
			resultado = new ArrayList<Calendarios>();
		}
		
		manager.close();
		
		return resultado;
	}

	@Override
	public Calendarios Registrar(Calendarios calendarios) {
		guardar(calendarios);
		return calendarios;
	}

	@Override
	public Calendarios Actualizar(Calendarios calendarios) {
		calendarios.setdFecmodifica(new Date());
		actualizar(calendarios);
		return calendarios;
	}

	@Override
	public Calendarios Eliminar(Calendarios calendarios) {
		calendarios.setdFecelimina(new Date());
		calendarios.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(calendarios);
		return calendarios;
	}

}
