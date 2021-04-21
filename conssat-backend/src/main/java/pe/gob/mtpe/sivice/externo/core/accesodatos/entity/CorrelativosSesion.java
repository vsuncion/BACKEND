package pe.gob.mtpe.sivice.externo.core.accesodatos.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "TBX_CORRELATIVO_SESION")
public class CorrelativosSesion  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_TBS_CORRELATIVO_SESION") 
	@SequenceGenerator(name="SEQ_TBS_CORRELATIVO_SESION",sequenceName="DB_TRAMITE.SEQ_CORRELATIVO_SESION", allocationSize=1) 
	@Column(name = "CORRELATIVO_SESION_ID_PK")
	private Long correlativoSesion;
 
	 
	@ManyToOne
	@JoinColumn(name="REGION_FK",nullable = false, insertable = true, updatable = true)
	private Regiones region;
	  
	@ManyToOne
	@JoinColumn(name="COMISION_FK",nullable = false, insertable = true, updatable = true)
	private Comisiones comision;
	 
	@ManyToOne
	@JoinColumn(name="TIPO_SESION_FK",nullable = false, insertable = true, updatable = true)
	private TipoSesiones tipoSesion;
	
	@Column(name = "N_VALOR_INICIAL")  
	private Long valorInicial;
	
	
	@Column(name = "D_FECREG")  
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date fechaRegistro;
 
    private transient String codigocomision;
    
    @Column(name = "N_USUMODIFICA") 
	private Long nUsuariomodifica;
	
	@Column(name = "D_FECMODIFICA")  
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date fechaModifica;
    
	@PrePersist
	protected void valoresIniciales() {
	  this.fechaRegistro =new Date(); 
	}
    
	public CorrelativosSesion() {
		super();
	}


	public Long getCorrelativoSesion() {
		return correlativoSesion;
	}


	public void setCorrelativoSesion(Long correlativoSesion) {
		this.correlativoSesion = correlativoSesion;
	}


	public Regiones getRegion() {
		return region;
	}


	public void setRegion(Regiones region) {
		this.region = region;
	}


	public Comisiones getComision() {
		return comision;
	}


	public void setComision(Comisiones comision) {
		this.comision = comision;
	}


	public TipoSesiones getTipoSesion() {
		return tipoSesion;
	}

	public void setTipoSesion(TipoSesiones tipoSesion) {
		this.tipoSesion = tipoSesion;
	}

	public Long getValorInicial() {
		return valorInicial;
	}


	public void setValorInicial(Long valorInicial) {
		this.valorInicial = valorInicial;
	}


	public Date getFechaRegistro() {
		return fechaRegistro;
	}


	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}


	public String getCodigocomision() {
		return codigocomision;
	}


	public void setCodigocomision(String codigocomision) {
		this.codigocomision = codigocomision;
	}

	public Long getnUsuariomodifica() {
		return nUsuariomodifica;
	}

	public void setnUsuariomodifica(Long nUsuariomodifica) {
		this.nUsuariomodifica = nUsuariomodifica;
	}

	public Date getFechaModifica() {
		return fechaModifica;
	}

	public void setFechaModifica(Date fechaModifica) {
		this.fechaModifica = fechaModifica;
	}


	 
	 

}
