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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Boletines; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.BoletinDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class BoletinDaoImpl extends BaseDao<Long, Boletines> implements BoletinDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Boletines> listar(Boletines boletines) {
		EntityManager manager = createEntityManager();
		List<Boletines> lista = manager
				.createQuery("SELECT b FROM Boletines b INNER JOIN b.region r WHERE r.rEgionidpk=:idregion AND b.consejo.cOnsejoidpk=:idconsejo AND b.cFlgeliminado=:eliminado ORDER BY b.bOletinidpk DESC")
				.setParameter("idregion",boletines.getRegion().getrEgionidpk())
				.setParameter("idconsejo", boletines.getConsejo().getcOnsejoidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Boletines buscarPorId(Boletines boletines) {
		Boletines boletin = buscarId(boletines.getbOletinidpk());
		return boletin;
	}

	@Override
	public Boletines Registrar(Boletines boletines) {
		boletines.setvNumbol(GenerarCorrelativo(boletines));
		guardar(boletines);
		return boletines;
	}

	@Override
	public Boletines Actualizar(Boletines boletines) {
		boletines.setdFecmodifica(new Date());
		actualizar(boletines);
		return boletines;
	}

	@Override
	public Boletines Eliminar(Boletines boletines) {
		boletines.setdFecelimina(new Date());
		boletines.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(boletines);
		return boletines;
	}

	public String GenerarCorrelativo(Boletines boletines) {
		Long tipoboletin = Long.parseLong("0");
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(b)+1 FROM Boletines b WHERE b.region.rEgionidpk=:idregion AND b.consejo.cOnsejoidpk=:idconsejo")
				.setParameter("idregion", boletines.getRegion().getrEgionidpk())
				.setParameter("idconsejo", boletines.getConsejo().getcOnsejoidpk())
				.getSingleResult();
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativo,tipoboletin,ConstantesUtil.BOLETIN_ALIAS_CORRELATIVO);
		manager.close();
		return StrcorrelativoFinal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Boletines> buscarRangoFechas(Boletines boletines) {
		EntityManager manager = createEntityManager();
		List<Boletines> boletin = manager.createQuery(
				"FROM Boletines b WHERE b.dFecboletin  BETWEEN  :fechaInicio AND :fechaFin  AND b.cFlgeliminado=:eliminado")
				.setParameter("fechaInicio", boletines.getdFechaInicio())
				.setParameter("fechaFin", boletines.getdFechaFin())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return boletin;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Boletines> BuscarPorCodigo(Boletines boletines) {
		EntityManager manager = createEntityManager();
		List<Boletines> boletin = manager
				.createQuery("FROM Boletines b WHERE b.vNumbol=:numeroboletin AND b.cFlgeliminado=:eliminado")
				.setParameter("numeroboletin", boletines.getvNumbol())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return boletin;

	}

	@Override
	public List<Boletines> buscarBoletines(Boletines boletines) {
		
		List<Boletines> resultado = new ArrayList<Boletines>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();

		CriteriaQuery<Boletines> criteriaQuery = builder.createQuery(Boletines.class);
		Root<Boletines> root = criteriaQuery.from(Boletines.class);
		
		/*
		boletines.setdFechaInicio((boletines.getdFechaInicio()!= null) ?   boletines.getdFechaInicio() :FechasUtil.convertStringToDate("01-01-1880"));
		boletines.setdFechaFin( (boletines.getdFechaFin()!=null)?            boletines.getdFechaFin()  :FechasUtil.convertStringToDate("01-01-1880"));

		Predicate valor1 = builder.equal(root.get("vNumbol"), boletines.getvNumbol());
		Predicate valor2 = builder.between(root.get("dFecboletin"), boletines.getdFechaInicio(), boletines.getdFechaFin());
		
		Predicate finalbusqueda = builder.or(valor1, valor2);
		 */
		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("region"),boletines.getRegion().getrEgionidpk());
		
		if(boletines.getvNumbol().trim().length()>0) {
			valor1 = builder.like(root.get("vNumbol"), "%"+ boletines.getvNumbol()+"%");
			finalbusqueda = builder.and(valor1,valor3); 
			vcontinuar = 0;
		}
		
		if(boletines.getdFechaInicio()!= null && boletines.getdFechaFin()!=null && vcontinuar==1) {
			valor1 = builder.between(root.get("dFecboletin"), boletines.getdFechaInicio(), boletines.getdFechaFin());
			finalbusqueda = builder.and(valor1,valor3);
			vcontinuar = 0;	
		}
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<Boletines> query = (Query<Boletines>) manager.createQuery(criteriaQuery);
			resultado = query.getResultList();	
		}else {
			resultado = new ArrayList<Boletines>();
		}
		
		manager.close();
		return resultado;
	}

}
