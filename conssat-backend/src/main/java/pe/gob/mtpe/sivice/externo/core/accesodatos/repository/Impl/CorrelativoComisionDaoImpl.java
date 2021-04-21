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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoComisionDao;


@Component
public class CorrelativoComisionDaoImpl  extends BaseDao<Long, CorrelativosComision>  implements CorrelativoComisionDao {

	@Override
	public List<CorrelativosComision> listarCorrelativoComisiones(CorrelativosComision CorrelativosComision) {
		List<CorrelativosComision> lista = new ArrayList<CorrelativosComision>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<CorrelativosComision> criteriaQuery = builder.createQuery(CorrelativosComision.class);
		Root<CorrelativosComision> root = criteriaQuery.from(CorrelativosComision.class);
		
		if(CorrelativosComision.getRegion()!=null) {
			Predicate valor1 = builder.equal(root.get("region"), CorrelativosComision.getRegion().getrEgionidpk());
			criteriaQuery.where(valor1);
		}else {
			criteriaQuery.select(root);
		}
		
		criteriaQuery.orderBy(builder.asc(root.get("region")));
		Query<CorrelativosComision> query = (Query<CorrelativosComision>) manager.createQuery(criteriaQuery);
		 
		lista = query.getResultList();
		manager.close();
		
		return lista;
	}

	@Override
	public CorrelativosComision RegistrarCorrelativoComision(CorrelativosComision correlativosComision) {
		 guardar(correlativosComision);
		 correlativosComision = buscarId(correlativosComision.getCorrelativoComision()); 
		return correlativosComision;
	}

	@Override
	public CorrelativosComision buscarCorrelativoComisionId(CorrelativosComision correlativosComision) { 
		return  buscarId(correlativosComision.getCorrelativoComision());
	}

	@Override
	public CorrelativosComision ActualizarCorrelativoComision(CorrelativosComision correlativosComision) {
		actualizar(correlativosComision);
		correlativosComision = buscarId(correlativosComision.getCorrelativoComision());
		return correlativosComision;
	}

}
