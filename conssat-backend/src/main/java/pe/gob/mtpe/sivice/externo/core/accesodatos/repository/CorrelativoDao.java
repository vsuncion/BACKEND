package pe.gob.mtpe.sivice.externo.core.accesodatos.repository;

import java.util.List;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones; 

public interface CorrelativoDao {
	
	List<Correlativos> listar(Regiones region);

	Correlativos buscarPorId(Correlativos correlativo);

	Correlativos Registrar(Correlativos correlativo);

	Correlativos Actualizar(Correlativos correlativo);
	
	String Duplicado(Correlativos correlativo);
	
	
}
