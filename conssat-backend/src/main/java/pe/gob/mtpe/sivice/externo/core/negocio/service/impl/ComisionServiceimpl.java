package pe.gob.mtpe.sivice.externo.core.negocio.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.ComiConsej;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Comisiones;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.ComisionConsejeroDao;
import pe.gob.mtpe.sivice.externo.core.accesodatos.repository.ComisionDao;
import pe.gob.mtpe.sivice.externo.core.negocio.service.ComisionService;

@Service("ComisionService")
@Transactional(readOnly = true)
public class ComisionServiceimpl implements ComisionService {

	@Autowired
	private ComisionDao ComisionDao;
	
	@Autowired
	private ComisionConsejeroDao comisionConsejeroDao;
	
	@Override
	public List<Comisiones> listar(Comisiones comisiones) {
		return ComisionDao.listar(comisiones);
	}

	@Override
	public Comisiones buscarPorId(Comisiones comisiones) {
		
		return ComisionDao.buscarPorId(comisiones);
	}

	@Override
	public List<Comisiones> buscar(Comisiones comisiones) {
		return ComisionDao.buscar(comisiones);
	}

	@Override
	public Comisiones Registrar(Comisiones comisiones) {
		return ComisionDao.Registrar(comisiones);
	}

	@Override
	public Comisiones Actualizar(Comisiones comisiones) {
		return ComisionDao.Actualizar(comisiones);
	}

	@Override
	public Comisiones Eliminar(Comisiones comisiones) {
		return ComisionDao.Eliminar(comisiones);
	}

	@Override
	public List<Comisiones> buscarComision(Comisiones comisiones) { 
		return ComisionDao.buscarComision(comisiones);
	}

	@Override
	public Comisiones buscarComisionPorNombre(String nombre_comision,Long idRegion) { 
		return ComisionDao.buscarComisionPorNombre(nombre_comision,idRegion);
	}

	@Override
	public List<Comisiones> buscarComisionSesion(Comisiones comisiones) { 
		List<Comisiones> listaComisionConsejeros = new ArrayList<Comisiones>();
		List<ComiConsej> lista = new ArrayList<ComiConsej>();
		lista= comisionConsejeroDao.buscarPorRegion(comisiones.getRegion().getrEgionidpk());
		if(lista.size()>0) {
			int contador =0;
			int bandera =0;
			Long codigoComision=0L;
			Comisiones agregarcomision = new Comisiones();
			
			for(ComiConsej comiConsejero: lista) {
				
				 if(contador==0) {
					 codigoComision = comiConsejero.getComision().getcOmisionidpk();
					 bandera=0;
					 contador=contador+1;
				 }else {
					 if(comiConsejero.getComision().getcOmisionidpk()!=codigoComision && codigoComision>0) {
						 bandera=0;
					 }else {
						 bandera=1; 
					 }
					 codigoComision = comiConsejero.getComision().getcOmisionidpk();
				 }
				 
				 if(bandera==0) {
					 agregarcomision = comiConsejero.getComision();
					listaComisionConsejeros.add(agregarcomision);
				 }
				 
				
			}
			
		}else {
			listaComisionConsejeros = new ArrayList<Comisiones>();
		}
		return listaComisionConsejeros;
	}

}
