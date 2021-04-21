package pe.gob.mtpe.sivice.externo.core.accesodatos.repository;

import java.util.List;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;

public interface CorrelativoSesionDao {

	 List<CorrelativosSesion> listarCorrelativoSesiones(CorrelativosSesion correlativosSesion);
	 List<CorrelativosSesion> buscarCorrelativoSesiones(CorrelativosSesion correlativosSesion);
	 CorrelativosSesion RegistrarCorrelativoSesion(CorrelativosSesion correlativosSesion);
	 CorrelativosSesion ActualizarCorrelativoSesion(CorrelativosSesion correlativosSesion);
	 CorrelativosSesion buscarCorrelativoSesionId(CorrelativosSesion correlativosSesion);
	
}
