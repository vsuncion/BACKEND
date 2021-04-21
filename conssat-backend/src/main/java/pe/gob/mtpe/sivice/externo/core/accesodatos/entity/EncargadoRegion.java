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
@Table(name = "TBX_ENCARGADO_REGION")
public class EncargadoRegion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="SEQ_TBX_ENCARGADO_REGION") 
	@SequenceGenerator(name="SEQ_TBX_ENCARGADO_REGION",sequenceName="DB_TRAMITE.SEQ_TBX_ENCARGADO_REGION", allocationSize=1) 
	@Column(name = "ENCARG_REGION_ID_PK")
	private Long eNcargadoregionidpk;

	@ManyToOne
	@JoinColumn(name="REGION_FK",nullable = false, insertable = true, updatable = true)  
	private Regiones region;
	
	
	@ManyToOne
	@JoinColumn(name="ENTIDAD_FK",nullable = false, insertable = true, updatable = true) 
	private Entidades entidades;
	
	@ManyToOne
	@JoinColumn(name="TIPO_DOCUMENTO_FK",nullable = false, insertable = true, updatable = true) 
	private TipoDocumentos tipoDocumentos;
	
	@Column(name = "V_NUMERODOCUMENTO")
	private String vNumdocumento;
	
	@Column(name = "V_NOMBRE")
	private String vNombre;
	
	@Column(name = "V_APELLIDO_PATERNO")
	private String vApellidopaterno;
	
	@Column(name = "V_APELLIDO_MATERNO")
	private String vApellidomaterno;
	
	@Column(name = "V_NUMEROCELULAR")
	private String vNumerocelular;

	@Column(name = "D_FECDOCAPROBACION") 
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date dFechaprobacion;

	@Column(name = " V_NOMBREARCHIVO") 
	private String vNombreArchivo;

	@Column(name = "V_UBIARCHIVO")
	private String vUbicacionarchivo;

	@Column(name = "V_EXTENSION")  
	private String vExtension;

	@Column(name = "C_FLGELIMINADO",length = 1) 
	private String cFlgeliminado;

	@Column(name = "D_FECREG") 
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date dFecregistro;

	@Column(name = "N_USUREG") 
	private Long nUsuarioregistra;

	@Column(name = "D_FECMODIFICA") 
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date dFechamodifica;

	@Column(name = "N_USUMODIFICA") 
	private Long nUsuariomodifica;

	@Column(name = "D_FECELIMINA") 
	@JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Lima")
	private Date dFechaelimina;

	@Column(name = "N_USUELIMINA")  
	private Long nUsuarioelimina;
	
	@Column(name = "V_NUMDOCAPROBACION")  
	private String vNumdocaprobacion;
	
	private transient Long regionpk;
	private transient Long consejeropk;

	public EncargadoRegion() {
		super();
	}

	@PrePersist
	protected void valoresIniciales() {
	  this.dFecregistro =new Date();
	  this.cFlgeliminado="0";
	}

	public Long geteNcargadoregionidpk() {
		return eNcargadoregionidpk;
	}

	public void seteNcargadoregionidpk(Long eNcargadoregionidpk) {
		this.eNcargadoregionidpk = eNcargadoregionidpk;
	}

	public Regiones getRegion() {
		return region;
	}

	public void setRegion(Regiones region) {
		this.region = region;
	}

	public Entidades getEntidades() {
		return entidades;
	}

	public void setEntidades(Entidades entidades) {
		this.entidades = entidades;
	}

	public TipoDocumentos getTipoDocumentos() {
		return tipoDocumentos;
	}

	public void setTipoDocumentos(TipoDocumentos tipoDocumentos) {
		this.tipoDocumentos = tipoDocumentos;
	}

	public String getvNumdocaprobacion() {
		return vNumdocaprobacion;
	}

	public void setvNumdocaprobacion(String vNumdocaprobacion) {
		this.vNumdocaprobacion = vNumdocaprobacion;
	}

	public String getvNombre() {
		return vNombre;
	}

	public void setvNombre(String vNombre) {
		this.vNombre = vNombre;
	}

	public String getvApellidopaterno() {
		return vApellidopaterno;
	}

	public void setvApellidopaterno(String vApellidopaterno) {
		this.vApellidopaterno = vApellidopaterno;
	}

	public String getvApellidomaterno() {
		return vApellidomaterno;
	}

	public void setvApellidomaterno(String vApellidomaterno) {
		this.vApellidomaterno = vApellidomaterno;
	}

	public String getvNumerocelular() {
		return vNumerocelular;
	}

	public void setvNumerocelular(String vNumerocelular) {
		this.vNumerocelular = vNumerocelular;
	}

	public Date getdFechaprobacion() {
		return dFechaprobacion;
	}

	public void setdFechaprobacion(Date dFechaprobacion) {
		this.dFechaprobacion = dFechaprobacion;
	}

	public String getvNombreArchivo() {
		return vNombreArchivo;
	}

	public void setvNombreArchivo(String vNombreArchivo) {
		this.vNombreArchivo = vNombreArchivo;
	}

	public String getvUbicacionarchivo() {
		return vUbicacionarchivo;
	}

	public void setvUbicacionarchivo(String vUbicacionarchivo) {
		this.vUbicacionarchivo = vUbicacionarchivo;
	}

	public String getvExtension() {
		return vExtension;
	}

	public void setvExtension(String vExtension) {
		this.vExtension = vExtension;
	}

	public String getcFlgeliminado() {
		return cFlgeliminado;
	}

	public void setcFlgeliminado(String cFlgeliminado) {
		this.cFlgeliminado = cFlgeliminado;
	}

	public Date getdFecregistro() {
		return dFecregistro;
	}

	public void setdFecregistro(Date dFecregistro) {
		this.dFecregistro = dFecregistro;
	}

	public Long getnUsuarioregistra() {
		return nUsuarioregistra;
	}

	public void setnUsuarioregistra(Long nUsuarioregistra) {
		this.nUsuarioregistra = nUsuarioregistra;
	}

	public Date getdFechamodifica() {
		return dFechamodifica;
	}

	public void setdFechamodifica(Date dFechamodifica) {
		this.dFechamodifica = dFechamodifica;
	}

	public Long getnUsuariomodifica() {
		return nUsuariomodifica;
	}

	public void setnUsuariomodifica(Long nUsuariomodifica) {
		this.nUsuariomodifica = nUsuariomodifica;
	}

	public Date getdFechaelimina() {
		return dFechaelimina;
	}

	public void setdFechaelimina(Date dFechaelimina) {
		this.dFechaelimina = dFechaelimina;
	}

	public Long getnUsuarioelimina() {
		return nUsuarioelimina;
	}

	public void setnUsuarioelimina(Long nUsuarioelimina) {
		this.nUsuarioelimina = nUsuarioelimina;
	}

	public Long getRegionpk() {
		return regionpk;
	}

	public void setRegionpk(Long regionpk) {
		this.regionpk = regionpk;
	}

	public Long getConsejeropk() {
		return consejeropk;
	}

	public void setConsejeropk(Long consejeropk) {
		this.consejeropk = consejeropk;
	}
	
	public String getvNumdocumento() {
		return vNumdocumento;
	}

	public void setvNumdocumento(String vNumdocumento) {
		this.vNumdocumento = vNumdocumento;
	}
	
	public String obtenerRutaAbsolutaArchivo() {
		return this.getvUbicacionarchivo()+this.getvNombreArchivo()+"."+this.getvExtension();
	}
 
}
