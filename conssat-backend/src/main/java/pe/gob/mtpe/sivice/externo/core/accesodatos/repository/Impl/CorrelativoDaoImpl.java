package pe.gob.mtpe.sivice.externo.core.accesodatos.repository.Impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;
import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil;

@Component
public class CorrelativoDaoImpl extends BaseDao<Long, Correlativos> implements CorrelativoDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Correlativos> listar(Regiones region) {
		EntityManager manager = createEntityManager();
		List<Correlativos> lista = new ArrayList<>();
		
		if(region.getrEgionidpk()==0L) {
			lista= manager.createQuery("SELECT c FROM Correlativos c ORDER BY c.vRegion,c.vConsejo,c.vModulo DESC")
					.getResultList();
		}else {
			lista= manager.createQuery("SELECT c FROM Correlativos c WHERE c.vRegion=:pRegion  ORDER BY c.vRegion,c.vConsejo,c.vModulo DESC")
					.setParameter("pRegion", region.getvDesnombre())
					.getResultList();
		}

		
		manager.close();

		if (lista.isEmpty()) {
			List<Correlativos> listavacia = new ArrayList<Correlativos>();
			lista = listavacia;
		}

		return lista;
	}

	@Override
	public Correlativos buscarPorId(Correlativos correlativo) {
		correlativo = buscarId(correlativo.getcOrrelativoidpk());
		return correlativo;
	}

	@Override
	public Correlativos Registrar(Correlativos correlativo) {
		guardar(correlativo);
		return correlativo;
	}

	@Override
	public Correlativos Actualizar(Correlativos correlativo) {
	    actualizar(correlativo);
		return correlativo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String Duplicado(Correlativos correlativo) {
		EntityManager manager = createEntityManager();
		String Respuesta = "NO";
		List<Correlativos> lista = manager.createQuery("SELECT c FROM Correlativos c WHERE c.vRegion=:pRegion AND c.vModulo=:pModulo AND c.vConsejo=:pConsejo AND c.vTipo=:pTipo")
				.setParameter("pRegion", correlativo.getvRegion())
				.setParameter("pModulo", correlativo.getvModulo())
				.setParameter("pConsejo", correlativo.getvConsejo())
				.setParameter("pTipo", correlativo.getvTipo())
				.getResultList();
		manager.close();

		if (lista.isEmpty()) {
			Respuesta = ConstantesUtil.C_RESPUESTA_INACTIVO;
		} else {
			Respuesta = ConstantesUtil.C_RESPUESTA_ACTIVO;
		}

		return Respuesta;
	}

	
}
