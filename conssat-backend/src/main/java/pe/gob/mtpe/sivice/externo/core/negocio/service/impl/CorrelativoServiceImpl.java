package pe.gob.mtpe.sivice.externo.core.negocio.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoComisionDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.CorrelativoSesionDao;
import pe.gob.mtpe.sivice.externo.core.negocio.service.CorrelativoService;

@Service("CorrelativoService")
@Transactional(readOnly = true)
public class CorrelativoServiceImpl implements CorrelativoService {
	
	@Autowired
	private CorrelativoDao correlativoDao;
	
	@Autowired
	private CorrelativoSesionDao correlativoSesionDao;
	
	@Autowired
	private CorrelativoComisionDao correlativoComisionDao;

	@Override
	public List<Correlativos> listar(Regiones region) { 
		return correlativoDao.listar(region);
	}

	@Override
	public Correlativos buscarPorId(Correlativos correlativo) { 
		return correlativoDao.buscarPorId(correlativo);
	}

	@Override
	public Correlativos Registrar(Correlativos correlativo) { 
		return correlativoDao.Registrar(correlativo);
	}

	@Override
	public Correlativos Actualizar(Correlativos correlativo) { 
		return correlativoDao.Actualizar(correlativo);
	}

	@Override
	public String Duplicado(Correlativos correlativo) { 
		return correlativoDao.Duplicado(correlativo);
	}

	@Override
	public List<CorrelativosSesion> listarCorrelativoSesiones(CorrelativosSesion correlativosSesion) { 
		return correlativoSesionDao.listarCorrelativoSesiones(correlativosSesion);
	}

	@Override
	public List<CorrelativosSesion> buscarCorrelativoSesiones(CorrelativosSesion correlativosSesion) {
		 return correlativoSesionDao.buscarCorrelativoSesiones(correlativosSesion);
	}

	@Override
	public CorrelativosSesion RegistrarCorrelativoSesion(CorrelativosSesion correlativosSesion) { 
		return correlativoSesionDao.RegistrarCorrelativoSesion(correlativosSesion);
	}

	@Override
	public CorrelativosSesion ActualizarCorrelativoSesion(CorrelativosSesion correlativosSesion) { 
		return correlativoSesionDao.ActualizarCorrelativoSesion(correlativosSesion);
	}

	@Override
	public CorrelativosSesion buscarCorrelativoSesionId(CorrelativosSesion correlativosSesion) { 
		return correlativoSesionDao.buscarCorrelativoSesionId(correlativosSesion);
	}

	@Override
	public List<CorrelativosComision> listarCorrelativoComisiones(CorrelativosComision CorrelativosComision) { 
		return correlativoComisionDao.listarCorrelativoComisiones(CorrelativosComision);
	}

	@Override
	public CorrelativosComision RegistrarCorrelativoComision(CorrelativosComision correlativosComision) { 
		return correlativoComisionDao.RegistrarCorrelativoComision(correlativosComision);
	}

	@Override
	public CorrelativosComision buscarCorrelativoComisionId(CorrelativosComision correlativosComision) { 
		return correlativoComisionDao.buscarCorrelativoComisionId(correlativosComision);
	}

	@Override
	public CorrelativosComision ActualizarCorrelativoComision(CorrelativosComision correlativosComision) { 
		return correlativoComisionDao.ActualizarCorrelativoComision(correlativosComision);
	}

}
