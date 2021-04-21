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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Consejeros;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.ConsejeroDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil; 

@Component
public class ConsejeroDaoImpl extends BaseDao<Long, Consejeros> implements ConsejeroDao {
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Override
	public List<Consejeros> listar(Consejeros consejero) {
		EntityManager manager = createEntityManager();
		List<Consejeros> lista = manager
				.createQuery("SELECT c FROM Consejeros c INNER JOIN c.region r WHERE r.rEgionidpk=:idregion AND c.cFlgeliminado=:eliminado ORDER BY c.cOnsejeroidpk DESC") 
				.setParameter("idregion",consejero.getRegion().getrEgionidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();

		if (lista.isEmpty()) {
			List<Consejeros> listavacia  = new ArrayList<Consejeros>();
			lista =listavacia;
		}

		return lista;
	}

	@Override
	public Consejeros buscarPorId(Consejeros consejero) {
		Consejeros consejeros = buscarId(consejero.getcOnsejeroidpk());
		return consejeros;
	}

	 
	@Override
	public List<Consejeros> buscar(Consejeros consejero) {
		
		List<Consejeros> resultado = new ArrayList<Consejeros>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Consejeros> criteriaQuery = builder.createQuery(Consejeros.class);
		Root<Consejeros> root = criteriaQuery.from(Consejeros.class);
		
		//VALIDACIONES
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,valor2 = null,
		valor3 = builder.equal(root.get("region"),consejero.getRegion().getrEgionidpk()),
		valor4 = builder.equal(root.get("consejo"),consejero.getConsejo().getcOnsejoidpk());
		if(consejero.getvNumdocasig().trim().length()>0) { 
			valor1 = builder.equal(root.get("vNumdocasig"), consejero.getvNumdocasig());
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(vcontinuar==1) {
			
			if(consejero.getdFecinicio()!= null && consejero.getdFecfin()!=null) {
				log.info("========= entro fechas");
				valor1 = builder.between(root.get("dFecinicio"), consejero.getdFecinicio(), consejero.getdFecfin());
				finalbusqueda = builder.and(valor1,valor3,valor4);
				vcontinuar = 0;
			}else {
				log.info("========= no entro fechas");
				vcontinuar = 1;
			}	
			
		}
		
		
		if(vcontinuar==1) {
			if(consejero.getvTipdocumento()!=null && consejero.getvNumdocumento().trim().length()>0) {
				valor1 =  builder.equal(root.get("tipodocumento"), consejero.getvTipdocumento());
				valor2 = builder.equal(root.get("vNumdocumento"), consejero.getvNumdocumento());
				finalbusqueda = builder.and(valor1,valor2,valor3,valor4);
				vcontinuar = 0;
			}else {
				vcontinuar = 1;
			}
			
		}
		
		
		if(vcontinuar==1) {
			if(consejero.getvDesnombre().trim().length()>0) {
				valor1 = builder.like(root.get("vDesnombre"), "%"+ consejero.getvDesnombre()+"%") ; 
				vcontinuar = 0;
				
			}else if(consejero.getvDesappaterno().trim().length()>0){
				valor1 = builder.like(root.get("vDesappaterno"), "%"+ consejero.getvDesappaterno()+"%") ;
				vcontinuar = 0;
				
			}else if(consejero.getvDesapmaterno().trim().length()>0) {
				valor1 = builder.like(root.get("vDesapmaterno"), "%"+ consejero.getvDesapmaterno()+"%") ;
				vcontinuar = 0;
				
			}else if(consejero.getvEntidad()!=null) {
				valor1 = builder.equal(root.get("entidad"),consejero.getvEntidad()) ; 
				vcontinuar = 0;
				
			}else {
				vcontinuar = 1;
			}
			
			finalbusqueda = builder.and(valor1,valor3,valor4);
			
		}

		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<Consejeros> query = (Query<Consejeros>) manager.createQuery(criteriaQuery);
			  resultado = query.getResultList();
		}else {
			resultado = new ArrayList<Consejeros>();
		}
		
		 manager.close();
		return resultado;
	}

	@Override
	public Consejeros Registrar(Consejeros consejero) {
		guardar(consejero);
		return consejero;

	}

	@Override
	public Consejeros Actualizar(Consejeros consejero) {
		consejero.setdFecmodifica(new Date());
		actualizar(consejero);
		return consejero;
	}

	@Override
	public Consejeros Eliminar(Consejeros consejero) {
		consejero.setdFecelimina(new Date());
		consejero.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(consejero);
		return consejero;
	}

	@Override
	public Consejeros buscarPorTipoDocNumero(Consejeros consejero) {
		
		return null;
	}

	@Override
	public List<Consejeros> listarConsejerosPorComision(Consejeros consejero) {
		/*
		EntityManager manager = createEntityManager();
		List<Consejeros> lista = manager
				.createQuery("FROM Consejeros c WHERE c.rEgionfk=:region AND c.cOmisionfk=:consejo  AND c.cFlgeliminado=:eliminado ORDER BY c.cOnsejeroidpk DESC")
				.setParameter("region", consejero.getrEgionfk())
				.setParameter("comision", consejero.getcOmisionfk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();

		if (lista.isEmpty()) {
			lista = null;
		}
*/
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Consejeros buscarPorDni(Consejeros consejero) {
		EntityManager manager = createEntityManager();
		 Consejeros  consejeroresultado = new Consejeros();
		List<Consejeros> lista = manager
				.createQuery("FROM Consejeros c WHERE c.vNumdocumento=:numerodoc")
				.setParameter("numerodoc", consejero.getvNumdocumento()).getResultList();
		manager.close();

		if (lista.isEmpty()) {
			consejeroresultado = null;
		}else {
			consejeroresultado = lista.get(0);
		}

		return consejeroresultado;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Consejeros> listarConsejerosPorConsejo(Long idconsejo,Long idregion) {
		EntityManager manager = createEntityManager();
		List<Consejeros> lista = manager
				//.createQuery("SELECT c FROM Consejeros c INNER JOIN c.consejo co INNER JOIN  c.region r WHERE  co.cOnsejoidpk=:idconsejo AND r.rEgionidpk=:idregion  AND c.cFlgeliminado=:eliminado ORDER BY c.cOnsejeroidpk DESC")
				//.setParameter("idconsejo", idconsejo)
				.createQuery("SELECT c FROM Consejeros c INNER JOIN  c.region r WHERE  r.rEgionidpk=:idregion  AND c.cFlgeliminado=:eliminado ORDER BY c.cOnsejeroidpk DESC")
				.setParameter("idregion", idregion)
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
/*
		if (lista.isEmpty()) {
			List<Consejeros> listavacia  = new ArrayList<Consejeros>();
			lista =listavacia;
		}
*/
		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Consejeros> listarConsejerosPorRegion(Long region) {
		EntityManager manager = createEntityManager();
		List<Consejeros> lista = manager
				.createQuery("SELECT c FROM Consejeros c INNER JOIN c.region r WHERE  r.rEgionidpk=:idregion  AND c.cFlgeliminado=:eliminado ORDER BY c.cOnsejeroidpk DESC")
				.setParameter("idregion", region)
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

}
