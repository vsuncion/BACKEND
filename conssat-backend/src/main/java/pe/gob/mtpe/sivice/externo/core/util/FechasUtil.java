package pe.gob.mtpe.sivice.externo.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FechasUtil {

	public static String obtenerCorrelativo(Long numCorrelativo,Long tipo, String alias) {
		int anio = 0;
		String vTipo = null;
		String strignAnio = null;
		String StrcorrelativoFinal = null;
		String StrCorrelativo = String.valueOf(numCorrelativo);
		try {
			String adicionalTipo = "";
			
			// OBTENER ALIAS DE LOS TIPOS
			adicionalTipo=obtenertipoAbreviado(tipo,alias);
			/*
			if(tipo>0) {
				if(tipo==1) {
					vTipo ="O";
				}else if(tipo==2) {
					vTipo ="E";
				}
				adicionalTipo = "-" + vTipo;
			}else {
				
			}*/
			
			Calendar cal = Calendar.getInstance();
			anio = cal.get(Calendar.YEAR);
			strignAnio = String.valueOf(anio);
			switch (StrCorrelativo.length()) {
			case 1:
				//StrcorrelativoFinal = alias + adicionalTipo +"-" + strignAnio + '-' + "000" + StrCorrelativo;
				StrcorrelativoFinal =  adicionalTipo +'-' + "000" + StrCorrelativo;
				break;
			case 2:
				//StrcorrelativoFinal = alias + adicionalTipo + "-" + strignAnio + '-' + "00" + StrCorrelativo;
				StrcorrelativoFinal =  adicionalTipo +'-' + "00" + StrCorrelativo;
				break;
			case 3:
				StrcorrelativoFinal = adicionalTipo + '-' + "0" + StrCorrelativo;
				//StrcorrelativoFinal = alias + adicionalTipo + "-" + strignAnio + '-' + "0" + StrCorrelativo;
				break;
			default:
				StrcorrelativoFinal = StrCorrelativo;
			}

		} catch (Exception e) {
		
		}

		return StrcorrelativoFinal;
	}
	
	public static String obtenertipoAbreviado(Long tipo, String alias) {
		
		String vTipo ="";
		
		switch(alias) {
		
		case  ConstantesUtil.COMISION_ALIAS_CORRELATIVO:
			if(tipo==1) {
			  vTipo ="T";
			}else if(tipo==2) {
			  vTipo ="P";
			}else {
			 vTipo="";	
			}
			vTipo=alias+"-"+vTipo;
		break;
		
		
		case  ConstantesUtil.SESION_ALIAS_CORRELATIVO:
		   if(tipo==1) {
			  vTipo ="O";
			}else if(tipo==2) {
			  vTipo ="E";
			}else {
			 vTipo="";	
			}
		   vTipo=alias+"-"+vTipo;
		break;
		
			
		default:
			vTipo=alias;
		
		}
		
		return vTipo;
	}
	
	
	public static Date convertStringToDate(String fecha){
		DateFormat  formatoFecha = new SimpleDateFormat("dd-MM-yyyy");	 
		 Date fechaDate = null ;
		 try {			
			 fechaDate = formatoFecha.parse(fecha);		    
		} catch (Exception e) {			
		}	
		return fechaDate;
	}
	
	
	public static String convertDateToString(Date fecha){
		DateFormat   formatoFecha = new SimpleDateFormat("dd-MM-yyyy");	 
		String fechaDate = null;
		 try {			
			   fechaDate = formatoFecha.format(fecha) ;	    
		} catch (Exception e) {			
		}	
		return fechaDate;
	}
	
	
	
	public static Date fechaActual() {
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Calendar cal = Calendar.getInstance();
		format.setCalendar(cal);
		return cal.getTime();
	}
	
	
	
	 

}
