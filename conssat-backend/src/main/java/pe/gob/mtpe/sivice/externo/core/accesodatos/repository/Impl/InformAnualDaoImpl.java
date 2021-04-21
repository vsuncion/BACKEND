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
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.InfAnuales; 
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.InformAnualDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;
import pe.gob.mtpe.sivice.externo.core.util.FechasUtil;

@Component
public class InformAnualDaoImpl extends BaseDao<Long, InfAnuales> implements InformAnualDao {
	

	@SuppressWarnings("unchecked")
	@Override
	public List<InfAnuales> listar(InfAnuales infAnuales) {
		EntityManager manager = createEntityManager();
		List<InfAnuales> lista = manager
				.createQuery("SELECT b FROM InfAnuales b INNER JOIN b.region r WHERE r.rEgionidpk=:idregion AND b.consejo.cOnsejoidpk=:idconsejo AND b.cFlgeliminado=:eliminado ORDER BY b.iNformeidpk DESC")
				.setParameter("idregion", infAnuales.getRegion().getrEgionidpk())
				.setParameter("idconsejo", infAnuales.getConsejo().getcOnsejoidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InfAnuales buscarPorId(InfAnuales infAnuales) { 
		InfAnuales informe = new InfAnuales();
		EntityManager manager = createEntityManager();
		List<InfAnuales> lista = manager
				.createQuery("FROM InfAnuales i WHERE i.iNformeidpk=:codigoinforme AND i.cFlgeliminado=:eliminado ")
				.setParameter("codigoinforme", infAnuales.getiNformeidpk())
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		if(lista.isEmpty()) {
			informe = null;
		}else {
			informe = lista.get(0);
		}
		
		return informe;
	}

	@Override
	public List<InfAnuales> buscar(InfAnuales infAnuales) { 
		
		List<InfAnuales> resultado = new ArrayList<InfAnuales>();
		EntityManager manager = createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<InfAnuales> criteriaQuery = builder.createQuery(InfAnuales.class);
		Root<InfAnuales> root = criteriaQuery.from(InfAnuales.class);
		
		int vcontinuar =1 ;
		Predicate finalbusqueda = null; 
		Predicate valor1 = null,
		valor3 = builder.equal(root.get("region"),infAnuales.getRegion().getrEgionidpk()),
		valor4 = builder.equal(root.get("consejo"),infAnuales.getConsejo().getcOnsejoidpk());

		
		if(infAnuales.getvCodinforme().trim().length()>0) {
			valor1 = builder.like(root.get("vCodinforme"),"%"+ infAnuales.getvCodinforme()+"%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		
		if(infAnuales.getvSesion().trim().length()>0 && vcontinuar==1)  {
			valor1 = builder.like(root.get("vSesion"),"%"+ infAnuales.getvSesion()+"%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(infAnuales.getvNumdocap().trim().length()>0 && vcontinuar==1) {
			valor1 = builder.like(root.get("vNumdocap"),"%"+ infAnuales.getvNumdocap()+"%");
			finalbusqueda = builder.and(valor1,valor3,valor4); 
			vcontinuar = 0;
		}
		
		if(infAnuales.getdFecdesde()!= null && infAnuales.getdFhasta()!=null && vcontinuar==1) {
			valor1 = builder.between(root.get("dFecdesde"), infAnuales.getdFecdesde(), infAnuales.getdFhasta());
			finalbusqueda = builder.and(valor1,valor3,valor4);
			vcontinuar = 0;
		}
		
		if(vcontinuar==0) {
			criteriaQuery.where(finalbusqueda);
			Query<InfAnuales> query = (Query<InfAnuales>) manager.createQuery(criteriaQuery);
			resultado = query.getResultList();
		}else {
			resultado = new ArrayList<InfAnuales>();
		}
		
		
		manager.close(); 
		
		return resultado;
	}

	@Override
	public InfAnuales Registrar(InfAnuales infAnuales) {
		infAnuales.setvCodinforme(GenerarCorrelativo(infAnuales));
		infAnuales.setdFecreg(new Date());
		guardar(infAnuales);
		return infAnuales;
	}

	@Override
	public InfAnuales Actualizar(InfAnuales infAnuales) {
		infAnuales.setdFecmodifica(new Date());
		actualizar(infAnuales);
		return infAnuales;
	}

	@Override
	public InfAnuales Eliminar(InfAnuales infAnuales) {
		infAnuales.setdFecelimina(new Date());
		infAnuales.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(infAnuales);
		return infAnuales;
	}
	
	public String GenerarCorrelativo(InfAnuales infAnuales) {
		Long tipoinforme = Long.parseLong("0");
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(f)+1 FROM InfAnuales f WHERE f.region.rEgionidpk=:idregion AND f.consejo.cOnsejoidpk=:idconsejo")
				.setParameter("idregion", infAnuales.getRegion().getrEgionidpk())
				.setParameter("idconsejo", infAnuales.getConsejo().getcOnsejoidpk())
				.getSingleResult();
		String StrcorrelativoFinal = FechasUtil.obtenerCorrelativo(correlativo,tipoinforme,ConstantesUtil.INFORME_ALIAS_CORRELATIVO);
		manager.close();
		return StrcorrelativoFinal;
	}

}
