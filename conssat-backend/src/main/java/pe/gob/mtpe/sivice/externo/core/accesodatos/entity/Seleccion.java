package pe.gob.mtpe.sivice.externo.core.accesodatos.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
 

@Entity
public class Seleccion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	private String etiqueta;
	
	public Seleccion(Integer id, String etiqueta) { 
		this.id = id;
		this.etiqueta = etiqueta;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
 
	 
	
}
