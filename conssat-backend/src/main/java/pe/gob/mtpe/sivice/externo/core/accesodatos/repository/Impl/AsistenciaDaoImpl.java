package pe.gob.mtpe.sivice.externo.core.accesodatos.repository.Impl;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;
import pe.gob.mtpe.sivice.externo.core.accesodatos.base.BaseDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Asistencias;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.AsistenciaDao;
import pe.gob.mtpe.sivice.externo.core.util.ConstantesUtil; 

@Component
public class AsistenciaDaoImpl extends BaseDao<Long, Asistencias> implements AsistenciaDao {
 

	@SuppressWarnings("unchecked")
	@Override
	public List<Asistencias> listar() {
		EntityManager manager = createEntityManager();
		List<Asistencias> lista = manager
				.createQuery("FROM Asistencias b WHERE b.cFlgeliminado=:eliminado ORDER BY b.aSistenciaidpk DESC")
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Asistencias buscarPorId(Asistencias asistencia) {
		Asistencias infoAsistencias = buscarId(asistencia.getaSistenciaidpk());
		return infoAsistencias;
	}

	@Override
	public List<Asistencias> buscar(Asistencias asistencia) {
		return null;
	}

	@Override
	public Asistencias Registrar(Asistencias asistencia) {
		guardar(asistencia);
		return asistencia;
	}

	@Override
	public Asistencias Actualizar(Asistencias asistencia) {
		asistencia.setdFecmodifica(new Date());
		actualizar(asistencia);
		return asistencia;
	}

	@Override
	public Asistencias Eliminar(Asistencias asistencia) {
		asistencia.setdFecelimina(new Date());
		asistencia.setcFlgeliminado(ConstantesUtil.C_INDC_ACTIVO);
		actualizar(asistencia);
		return asistencia;
	}

	 
	@SuppressWarnings("unchecked")
	@Override
	public List<Asistencias> listarConsejerosAsistencia(Long idsesion) {
		EntityManager manager = createEntityManager();
		List<Asistencias> lista = manager
				.createQuery("FROM Asistencias a WHERE  a.sEsionfk=:sesion AND a.cFlgeliminado=:eliminado ORDER BY a.aSistenciaidpk DESC")
				.setParameter("sesion",idsesion)
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Asistencias> listarConsejerosAsistenciaConfirmados(Long idsesion) {
		EntityManager manager = createEntityManager();
		List<Asistencias> lista = manager
				.createQuery("FROM Asistencias a WHERE  a.sEsionfk=:sesion AND a.cFlgasistio='1' AND a.cFlgeliminado=:eliminado ORDER BY a.aSistenciaidpk DESC")
				.setParameter("sesion",idsesion)
				.setParameter("eliminado", ConstantesUtil.C_INDC_INACTIVO).getResultList();
		manager.close();
		return lista;
	}

	@Override
	public Long cantidadAsistentesPorSesion(Long idsesion) {
		EntityManager manager = createEntityManager();
		Long correlativo = (Long) manager.createQuery("SELECT COUNT(a) FROM Asistencias a WHERE a.sEsionfk="+idsesion+" AND a.cFlgeliminado='0'").getSingleResult(); 
		manager.close();
		return correlativo;
	}

}
