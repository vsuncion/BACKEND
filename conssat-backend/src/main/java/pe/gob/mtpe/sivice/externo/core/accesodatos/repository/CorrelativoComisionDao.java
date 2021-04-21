package pe.gob.mtpe.sivice.externo.core.accesodatos.repository;

import java.util.List;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision; 

public interface CorrelativoComisionDao {
	
	List<CorrelativosComision> listarCorrelativoComisiones(CorrelativosComision CorrelativosComision);
	CorrelativosComision RegistrarCorrelativoComision(CorrelativosComision correlativosComision);
	CorrelativosComision buscarCorrelativoComisionId(CorrelativosComision correlativosComision);
	CorrelativosComision ActualizarCorrelativoComision(CorrelativosComision correlativosComision);
}
