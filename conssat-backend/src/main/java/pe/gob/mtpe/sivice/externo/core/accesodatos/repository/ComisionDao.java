package pe.gob.mtpe.sivice.externo.core.accesodatos.repository;

import java.util.List;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Comisiones;

public interface ComisionDao {
	
	List<Comisiones> listar(Comisiones comisiones);

	Comisiones buscarPorId(Comisiones comisiones);

	List<Comisiones> buscar(Comisiones comisiones);

	public Comisiones Registrar(Comisiones comisiones);

	public Comisiones Actualizar(Comisiones comisiones);

	public Comisiones Eliminar(Comisiones comisiones);
	
	List<Comisiones> buscarComision(Comisiones comisiones);
	
	Comisiones consultaPorId(Comisiones comisiones);
	
	Comisiones buscarComisionPorNombre(String nombre_comision,Long idRegion);
	 
}
