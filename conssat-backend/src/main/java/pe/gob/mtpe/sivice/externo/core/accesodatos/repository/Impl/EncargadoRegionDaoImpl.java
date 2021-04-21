package pe.gob.mtpe.sivice.externo.core.accesodatos.repository.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder; 
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.query.Query; 
import org.springframework.stereotype.Component;
import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.EncargadoRegion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.EncargadoRegionDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;


@Component
public class EncargadoRegionDaoImpl extends BaseDao<Long, EncargadoRegion>  implements EncargadoRegionDao {

	@Override
	public EncargadoRegion Registrar(EncargadoRegion encargadoRegion) {
		 guardar(encargadoRegion);
		return encargadoRegion;
	}

	@Override
	public EncargadoRegion Actualizar(EncargadoRegion encargadoRegion) {
		encargadoRegion.setdFechamodifica(new Date());
		actualizar(encargadoRegion);
		return encargadoRegion;
	}

	@Override
	public EncargadoRegion Eliminar(EncargadoRegion encargadoRegion) {
		encargadoRegion.setdFechaelimina(new Date());
		encargadoRegion.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(encargadoRegion);
		return encargadoRegion;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EncargadoRegion> listar() {
		EntityManager manager = createEntityManager();
		List<EncargadoRegion> lista = manager
				.createQuery("FROM EncargadoRegion e WHERE e.cFlgeliminado=:eliminado ORDER BY e.eNcargadoregionidpk DESC")
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public EncargadoRegion buscarPorId(EncargadoRegion encargadoRegion) { 
		return buscarId(encargadoRegion.geteNcargadoregionidpk());
	}

	 
	@Override
	public List<EncargadoRegion> buscar(EncargadoRegion encargadoRegion) {
		
		List<EncargadoRegion> resultado =new ArrayList<EncargadoRegion>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<EncargadoRegion> criteriaQuery = builder.createQuery(EncargadoRegion.class);
		Root<EncargadoRegion> root = criteriaQuery.from(EncargadoRegion.class);
		
		int vcontinuar =1 ;
		Predicate busqueda=null,finalbusqueda = null;
		
		try {
			
			if(encargadoRegion.getRegionpk()!=null) {
				busqueda = builder.equal(root.get("region"),encargadoRegion.getRegionpk());
				vcontinuar=0;
			}
			
			if(!"".equals(encargadoRegion.getvNombre()) && vcontinuar==1) {
				busqueda = builder.like(root.get("vNombre"),"%"+encargadoRegion.getvNombre()+"%");
				vcontinuar=0;
			}
			
			if(!"".equals(encargadoRegion.getvNumdocaprobacion()) && vcontinuar==1) {
				busqueda = builder.equal(root.get("vNumdocumento"),encargadoRegion.getvNumdocaprobacion());
				vcontinuar=0;
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		if(vcontinuar==0) {
			finalbusqueda = builder.and(busqueda);
			criteriaQuery.where(finalbusqueda);
			Query<EncargadoRegion> query = (Query<EncargadoRegion>) manager.createQuery(criteriaQuery);
			resultado = query.getResultList();
		}else {
			resultado =new ArrayList<EncargadoRegion>();
		}
		
		manager.close();
		
		return resultado;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EncargadoRegion> listarFiltrado(Regiones region) {
		EntityManager manager = createEntityManager();
		List<EncargadoRegion> lista = manager
				.createQuery("SELECT e FROM EncargadoRegion e INNER JOIN e.region r WHERE r.rEgionidpk=:idregion AND e.cFlgeliminado=:eliminado ORDER BY e.eNcargadoregionidpk DESC")
				.setParameter("idregion", region.getrEgionidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

 
}
