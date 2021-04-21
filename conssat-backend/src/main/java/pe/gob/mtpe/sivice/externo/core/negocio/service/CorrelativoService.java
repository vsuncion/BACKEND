package pe.gob.mtpe.sivice.externo.core.negocio.service;

import java.util.List;

import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Correlativos;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosComision;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.CorrelativosSesion;
import pe.gob.mtpe.sivice.externo.core.accesodatos.entity.Regiones;

public interface CorrelativoService {
  
  List<Correlativos> listar(Regiones region);
  Correlativos buscarPorId(Correlativos correlativo);
  Correlativos Registrar(Correlativos correlativo);
  Correlativos Actualizar(Correlativos correlativo);
  String Duplicado(Correlativos correlativo);
  
  List<CorrelativosSesion> listarCorrelativoSesiones(CorrelativosSesion correlativosSesion);
  List<CorrelativosSesion> buscarCorrelativoSesiones(CorrelativosSesion correlativosSesion);
  CorrelativosSesion RegistrarCorrelativoSesion(CorrelativosSesion correlativosSesion);
  CorrelativosSesion ActualizarCorrelativoSesion(CorrelativosSesion correlativosSesion);
  CorrelativosSesion buscarCorrelativoSesionId(CorrelativosSesion correlativosSesion);
  
  List<CorrelativosComision> listarCorrelativoComisiones(CorrelativosComision CorrelativosComision);
  CorrelativosComision RegistrarCorrelativoComision(CorrelativosComision correlativosComision);
  CorrelativosComision buscarCorrelativoComisionId(CorrelativosComision correlativosComision);
  CorrelativosComision ActualizarCorrelativoComision(CorrelativosComision correlativosComision);
}
