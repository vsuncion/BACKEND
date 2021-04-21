package pe.gob.mtpe.sivice.externo.core.accesodatos.repository.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoSesionDao;

@Component
public class CorrelativoSesionDaoImpl  extends BaseDao<Long, CorrelativosSesion>  implements CorrelativoSesionDao {

	
	
	@Override
	public List<CorrelativosSesion> listarCorrelativoSesiones(CorrelativosSesion correlativosSesion) {
		List<CorrelativosSesion> lista = new ArrayList<CorrelativosSesion>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<CorrelativosSesion> criteriaQuery = builder.createQuery(CorrelativosSesion.class);
		Root<CorrelativosSesion> root = criteriaQuery.from(CorrelativosSesion.class);
		
		if(correlativosSesion.getRegion()!=null) {
			Predicate valor1 = builder.equal(root.get("region"), correlativosSesion.getRegion().getrEgionidpk());
			criteriaQuery.where(valor1);
		}else {
			criteriaQuery.select(root);
		}
		
		criteriaQuery.orderBy(builder.desc(root.get("correlativoSesion")),builder.desc(root.get("region")));
		Query<CorrelativosSesion> query = (Query<CorrelativosSesion>) manager.createQuery(criteriaQuery);
		 
		lista = query.getResultList();
		manager.close();
		
		return lista;
	}

	@Override
	public List<CorrelativosSesion> buscarCorrelativoSesiones(CorrelativosSesion correlativosSesion) {

		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<CorrelativosSesion> criteriaQuery = builder.createQuery(CorrelativosSesion.class);
		Root<CorrelativosSesion> root = criteriaQuery.from(CorrelativosSesion.class);
		List<CorrelativosSesion> lista = new ArrayList<CorrelativosSesion>();
		Predicate valor1 = null;
		Predicate valor2 = null;
		Predicate valor3 = null;
		Predicate valor4 = null;
		Predicate finalbusqueda = null;
		 
			
			if(correlativosSesion.getRegion().getrEgionidpk()>0) {
				valor1 = builder.equal(root.get("region"), correlativosSesion.getRegion().getrEgionidpk());
				finalbusqueda = builder.and(valor1);
			}
			
			if(correlativosSesion.getTipoSesion().gettIposesionidpk()!=null) {
				valor2 = builder.equal(root.get("TipoSesion"), correlativosSesion.getTipoSesion().gettIposesionidpk());
				finalbusqueda = builder.and(valor1, valor2);
			}
			
			if(correlativosSesion.getTipoSesion().gettIposesionidpk()!=null && correlativosSesion.getCodigocomision().trim().length()>0) {
				valor3 = builder.equal(root.get("comision").get("vCodcomision"), correlativosSesion.getCodigocomision().trim());
				finalbusqueda = builder.and(valor1, valor2, valor3);
			}else if(correlativosSesion.getCodigocomision().trim().length()>0) {
				valor1 = builder.equal(root.get("region"), correlativosSesion.getRegion().getrEgionidpk());
				valor4 =builder.equal(root.get("comision").get("vCodcomision"), correlativosSesion.getCodigocomision().trim());
				finalbusqueda = builder.and(valor1,valor4);
			}
			
			 
			
			criteriaQuery.where(finalbusqueda);
			Query<CorrelativosSesion> query = (Query<CorrelativosSesion>) manager.createQuery(criteriaQuery);
			lista = query.getResultList();
			manager.close();
			
		return lista;
	}

	@Override
	public CorrelativosSesion RegistrarCorrelativoSesion(CorrelativosSesion correlativosSesion) { 
		guardar(correlativosSesion);
		correlativosSesion = buscarId(correlativosSesion.getCorrelativoSesion());
		return correlativosSesion;
	}

	@Override
	public CorrelativosSesion ActualizarCorrelativoSesion(CorrelativosSesion correlativosSesion) {
		actualizar(correlativosSesion);
		correlativosSesion = buscarId(correlativosSesion.getCorrelativoSesion());
		return correlativosSesion;
	}

	@Override
	public CorrelativosSesion buscarCorrelativoSesionId(CorrelativosSesion correlativosSesion) {
		correlativosSesion = buscarId(correlativosSesion.getCorrelativoSesion());
		return correlativosSesion;
	}

}
