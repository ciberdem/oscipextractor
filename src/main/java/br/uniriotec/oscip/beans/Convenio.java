package br.uniriotec.oscip.beans;

public class Convenio {
	
	String id;
	String idProposta;
	long idOscip;
	String cnpjOscip;
	String endereco;
	String cep;
	String cidade;
	String UF;
	String municipio;
	String orgao;
	String orgaoSup;
	String naturezaJuridica;
	String dtProposta;
	String modalidade;
	String cnpj;
	String nomeProponente;
	String situacao;
	String situacaoProposta;
	String dtInicio;
	String dtFim;
	String objeto;
	String valorGlobal;
	String valorRepasse;
	String valorContrapartida;
	String valorEmpenhado;
	String valorDesembolsado;
	EmpenhoConv[] empenhos;
	CronogramaConv[] cronogramas;
	boolean temConvenio = false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdProposta() {
		return idProposta;
	}
	public void setIdProposta(String idProposta) {
		this.idProposta = idProposta;
	}
	public String getCnpjOscip() {
		return cnpjOscip;
	}
	public void setCnpjOscip(String cnpjOscip) {
		this.cnpjOscip = cnpjOscip;
	}
	public String getUF() {
		return UF;
	}
	public void setUF(String uF) {
		UF = uF;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public String getOrgao() {
		return orgao;
	}
	public void setOrgao(String orgao) {
		this.orgao = orgao;
	}
	public String getOrgaoSup() {
		return orgaoSup;
	}
	public void setOrgaoSup(String orgaoSup) {
		this.orgaoSup = orgaoSup;
	}
	public String getNaturezaJuridica() {
		return naturezaJuridica;
	}
	public void setNaturezaJuridica(String naturezaJuridica) {
		this.naturezaJuridica = naturezaJuridica;
	}
	public String getDtProposta() {
		return dtProposta;
	}
	public void setDtProposta(String dtProposta) {
		this.dtProposta = dtProposta;
	}
	public String getModalidade() {
		return modalidade;
	}
	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getNomeProponente() {
		return nomeProponente;
	}
	public void setNomeProponente(String nomeProponente) {
		this.nomeProponente = nomeProponente;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getDtInicio() {
		return dtInicio;
	}
	public void setDtInicio(String dtInicio) {
		this.dtInicio = dtInicio;
	}
	public String getDtFim() {
		return dtFim;
	}
	public void setDtFim(String dtFim) {
		this.dtFim = dtFim;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
	public String getValorGlobal() {
		return valorGlobal;
	}
	public void setValorGlobal(String valorGlobal) {
		this.valorGlobal = valorGlobal;
	}
	public String getValorRepasse() {
		return valorRepasse;
	}
	public void setValorRepasse(String valorRepasse) {
		this.valorRepasse = valorRepasse;
	}
	public String getValorContrapartida() {
		return valorContrapartida;
	}
	public void setValorContrapartida(String valorContrapartida) {
		this.valorContrapartida = valorContrapartida;
	}
	public String getValorEmpenhado() {
		return valorEmpenhado;
	}
	public void setValorEmpenhado(String valorEmpenhado) {
		this.valorEmpenhado = valorEmpenhado;
	}
	public String getValorDesembolsado() {
		return valorDesembolsado;
	}
	public void setValorDesembolsado(String valorDesembolsado) {
		this.valorDesembolsado = valorDesembolsado;
	}
	public EmpenhoConv[] getEmpenhos() {
		return empenhos;
	}
	public void setEmpenhos(EmpenhoConv[] empenhos) {
		this.empenhos = empenhos;
	}
	public CronogramaConv[] getCronogramas() {
		return cronogramas;
	}
	public void setCronogramas(CronogramaConv[] cronogramas) {
		this.cronogramas = cronogramas;
	}
	public String getSituacaoProposta() {
		return situacaoProposta;
	}
	public void setSituacaoProposta(String situacaoProposta) {
		this.situacaoProposta = situacaoProposta;
	}
	public long getIdOscip() {
		return idOscip;
	}
	public void setIdOscip(long idOscip) {
		this.idOscip = idOscip;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public boolean isTemConvenio() {
		return temConvenio;
	}
	public void setTemConvenio(boolean temConvenio) {
		this.temConvenio = temConvenio;
	}
	
}
