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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Comisiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.ComisionDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class ComisionDaoImpl extends BaseDao<Long, Comisiones> implements ComisionDao {

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Comisiones> listar(Comisiones comisiones) {
		EntityManager manager = createEntityManager();
		List<Comisiones> lista = manager
				.createQuery("SELECT c FROM Comisiones c INNER JOIN  c.region r WHERE  r.rEgionidpk=:idregion AND c.cFlgeliminado=:eliminado ORDER BY c.cOmisionidpk DESC")
				.setParameter("idregion",comisiones.getRegion().getrEgionidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Comisiones buscarPorId(Comisiones comisiones) {
		return buscarId(comisiones.getcOmisionidpk());
	}
 
	@Override
	public List<Comisiones> buscar(Comisiones comisiones) {
		List<Comisiones> resultado = new ArrayList<Comisiones>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Comisiones> criteriaQuery = builder.createQuery(Comisiones.class);
		Root<Comisiones> root = criteriaQuery.from(Comisiones.class);
		
 
		//VALIDACIONES
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("region"),comisiones.getRegion().getrEgionidpk());
		
		try {
			
			if(!"".equals(comisiones.getvCodcomision().trim())) {
				valor1 = builder.like(root.get("vCodcomision"),"%"+comisiones.getvCodcomision()+"%");
				finalbusqueda = builder.and(valor1,valor3); 
				vcontinuar = 0;
			}
			
			
			if(!"".equals(comisiones.getNombrencargado().trim()) && vcontinuar == 1) {
				valor1 = builder.like(root.get("consejero").get("vDesnombre"),"%"+comisiones.getNombrencargado()+"%");
				finalbusqueda = builder.and(valor1,valor3); 
				vcontinuar = 0;
			}
			
			if(!"".equals(comisiones.getvNumdocapr().trim()) && vcontinuar == 1) {
				valor1 = builder.like(root.get("vNumdocapr"),"%"+comisiones.getvNumdocapr()+"%");
				finalbusqueda = builder.and(valor1,valor3); 
				vcontinuar = 0;
			}
			
			if(!"".equals(comisiones.getvDescripcion().trim()) && vcontinuar == 1) {
				valor1 = builder.like(root.get("vDescripcion"),"%"+comisiones.getvDescripcion()+"%");
				finalbusqueda = builder.and(valor1,valor3); 
				vcontinuar = 0;
			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(vcontinuar == 0) {
			criteriaQuery.where(finalbusqueda);
			Query<Comisiones> query = (Query<Comisiones>) manager.createQuery(criteriaQuery);
			  resultado = query.getResultList();	
		}else {
			resultado = new ArrayList<Comisiones>();
		}
		
		
		return resultado;
	}

	@Override
	public Comisiones Registrar(Comisiones comisiones) {
		comisiones.setvCodcomision(GenerarCorrelativo(comisiones));
		guardar(comisiones); 
		return comisiones;
	}

	@Override
	public Comisiones Actualizar(Comisiones comisiones) {
		comisiones.setdFecmodifica(new Date());
		actualizar(comisiones);
		return comisiones;
	}

	@Override
	public Comisiones Eliminar(Comisiones comisiones) {
		comisiones.setdFecelimina(new Date());
		comisiones.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(comisiones);
		return comisiones;
	}
	
	
	public String GenerarCorrelativo(Comisiones comisiones) {
		Long correlativoInicial= ValorInicial(comisiones);
		
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(c)+1 FROM Comisiones c WHERE c.region.rEgionidpk=:idregion AND c.consejo.cOnsejoidpk=:idconsejo AND c.tipocomision.tIpocomsidpk=:idtpcomision")
				.setParameter("idregion", comisiones.getRegion().getrEgionidpk())
				.setParameter("idconsejo", comisiones.getConsejo().getcOnsejoidpk())
				.setParameter("idtpcomision", comisiones.getTipocomision().gettIpocomsidpk())
				.getSingleResult();
		manager.close();

		if(correlativoInicial>0) {
			correlativo = correlativo + correlativoInicial;
		}
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativo,comisiones.getTipocomision().gettIpocomsidpk(),ConstantesUtil.COMISION_ALIAS_CORRELATIVO);
		return StrcorrelativoFinal;
	}
	
	
	@SuppressWarnings("unchecked")
	public Long ValorInicial(Comisiones comisiones) {
		Long valorInicial = Long.parseLong("0");
		List<CorrelativosComision> lista = new ArrayList<CorrelativosComision>();
		EntityManager manager = createEntityManager();
		
		lista=manager.createQuery("SELECT c FROM CorrelativosComision c WHERE c.region.rEgionidpk=:vregion AND c.tipoComisiones.tIpocomsidpk=:vtipocomision")
				.setParameter("vregion", comisiones.getRegion().getrEgionidpk())  
				.setParameter("vtipocomision", comisiones.getTipocomision().gettIpocomsidpk())
				.getResultList();

		
		manager.close();
		if(!lista.isEmpty()) {
			valorInicial = lista.get(0).getValorInicial();
		}else {
			valorInicial =Long.parseLong("0");
		}
		
		return valorInicial;  
	}

	@Override
	public List<Comisiones> buscarComision(Comisiones comisiones) {
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Comisiones> criteriaQuery = builder.createQuery(Comisiones.class);
		Root<Comisiones> root = criteriaQuery.from(Comisiones.class);
		
		Predicate valor1 = builder.like(root.get("vCodcomision"), "%"+comisiones.getvCodcomision()+"%");
		//Predicate valor1 = builder.equal(root.get("vCodcomision"),comisiones.getvCodcomision());
		Predicate valor2 = builder.equal(root.get("region"), comisiones.getRegion().getrEgionidpk());
		Predicate valor3 = builder.and(valor2);
		criteriaQuery.where(valor1,valor3);
		Query<Comisiones> query = (Query<Comisiones>) manager.createQuery(criteriaQuery);
		List<Comisiones> resultado = query.getResultList();
		manager.close();
		return resultado;
	}

	 
	@Override
	public Comisiones consultaPorId(Comisiones comisiones) {
		
		/*
		Comisiones consultacomision = new Comisiones();
		EntityManager manager = createEntityManager();
		List<Comisiones> lista = manager
				.createQuery("SELECT c FROM Comisiones c WHERE c.cOmisionidpk=:idcomision  AND c.cFlgeliminado=:eliminado")
				.setParameter("idcomision", comisiones.getcOmisionidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		
		if(!lista.isEmpty()) {
			consultacomision = lista.get(0);
		}
		
		return consultacomision;*/
		
		
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Comisiones> criteriaQuery = builder.createQuery(Comisiones.class);
		Root<Comisiones> root = criteriaQuery.from(Comisiones.class);
		
		Predicate valor1 = builder.equal(root.get("cOmisionidpk"),comisiones.getcOmisionidpk());
		criteriaQuery.where(valor1);
		Query<Comisiones> query = (Query<Comisiones>) manager.createQuery(criteriaQuery);
		List<Comisiones> resultado = query.getResultList();
		manager.close();
		
		if(!resultado.isEmpty()) {
			comisiones = resultado.get(0);
		}
		
		return comisiones;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Comisiones buscarComisionPorNombre(String nombre_comision,Long idRegion) {
		Comisiones consultacomision = new Comisiones();
		EntityManager manager = createEntityManager();
		List<Comisiones> lista = manager
				.createQuery("SELECT c FROM Comisiones c INNER JOIN c.region r  WHERE r.rEgionidpk=:idRegion AND c.vCodcomision=:codigocomision  AND c.cFlgeliminado=:eliminado")
				.setParameter("codigocomision", nombre_comision) 
				.setParameter("idRegion", idRegion) 
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		
		if(!lista.isEmpty()) {
			consultacomision = lista.get(0);
		}
		
		return consultacomision;
	}
	
	


}
