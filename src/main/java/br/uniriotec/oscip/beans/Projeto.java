package br.uniriotec.oscip.beans;

public class Projeto {

	private String CNPJOscip;
	private String nome;
	private String dtFundacao;
	private String objetivo;
	private String numConvenio;
	private double valConvenio;
	private String contratante;
	private String status;
	private String justificativa;
	private String meta;
	private String origemRecurso;

	public String getCNPJOscip() {
		return CNPJOscip;
	}

	public void setCNPJOscip(String cNPJOscip) {
		CNPJOscip = cNPJOscip;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDtFundacao() {
		return dtFundacao;
	}

	public void setDtFundacao(String dtFundacao) {
		this.dtFundacao = dtFundacao;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public String getNumConvenio() {
		return numConvenio;
	}

	public void setNumConvenio(String numConvenio) {
		this.numConvenio = numConvenio;
	}

	public double getValConvenio() {
		return valConvenio;
	}

	public void setValConvenio(double valConvenio) {
		this.valConvenio = valConvenio;
	}

	public String getContratante() {
		return contratante;
	}

	public void setContratante(String contratante) {
		this.contratante = contratante;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public String getOrigemRecurso() {
		return origemRecurso;
	}

	public void setOrigemRecurso(String origemRecurso) {
		this.origemRecurso = origemRecurso;
	}

}
