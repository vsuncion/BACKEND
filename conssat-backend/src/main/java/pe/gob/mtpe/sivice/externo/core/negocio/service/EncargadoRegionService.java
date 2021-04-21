package pe.gob.mtpe.sivice.externo.core.negocio.service;

import java.util.List;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.EncargadoRegion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;

public interface EncargadoRegionService {
  
	EncargadoRegion buscarPorId (EncargadoRegion encargadoRegion);
	List<EncargadoRegion> buscar (EncargadoRegion encargadoRegion);
	EncargadoRegion registrar (EncargadoRegion encargadoRegion);
	EncargadoRegion actualizar (EncargadoRegion encargadoRegion);
	EncargadoRegion Eliminar (EncargadoRegion encargadoRegion);
	List<EncargadoRegion> listar ();
	List<EncargadoRegion> listarFiltrado (Regiones region);
}
