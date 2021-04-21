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
@Table(name = "TBX_CORRELATIVO_COMISION")
public class CorrelativosComision implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_CORRELATIVO_COMISION") 
	@SequenceGenerator(name="SEQ_CORRELATIVO_COMISION",sequenceName="DB_TRAMITE.SEQ_CORRELATIVO_COMISION", allocationSize=1) 
	@Column(name = "CORRELATIVO_COMISION_ID_PK")
	private Long correlativoComision;
	
	@ManyToOne
	@JoinColumn(name="REGION_FK",nullable = false, insertable = true, updatable = true)
	private Regiones region;
	
	@ManyToOne
	@JoinColumn(name="TIPO_COMISION_FK",nullable = false, insertable = true, updatable = true)
	private TipoComisiones  tipoComisiones;
	
	@Column(name = "N_VALOR_INICIAL")  
	private Long valorInicial;
	
	
	@Column(name = "D_FECREG")  
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date fechaRegistro;
	
	
	@Column(name = "N_USUMODIFICA") 
	private Long nUsuariomodifica;
	
	@Column(name = "D_FECMODIFICA")  
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date fechaModifica;
	
	@PrePersist
	protected void valoresIniciales() {
	  this.fechaRegistro =new Date(); 
	}

	public Long getCorrelativoComision() {
		return correlativoComision;
	}

	public void setCorrelativoComision(Long correlativoComision) {
		this.correlativoComision = correlativoComision;
	}

	public Regiones getRegion() {
		return region;
	}

	public void setRegion(Regiones region) {
		this.region = region;
	}

	public TipoComisiones getTipoComisiones() {
		return tipoComisiones;
	}

	public void setTipoComisiones(TipoComisiones tipoComisiones) {
		this.tipoComisiones = tipoComisiones;
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
