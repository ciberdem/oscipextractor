package br.uniriotec.oscip.beans;

public class Oscip {
	
	private long id;
	private String CNPJ = "";
	private String sigla = "";
	private String nome = "";
	private String nome2 = "";
	private String endereco = "";
	private String cep = "";
	private String telefone = "";
	private String fax = "";
	private String cidade = "";
	private String uf = "";
	private String website = "";
	private String historia = "";
	private String metaObj = "";
	private String metas = "";
	private String servicos = "";
	private String remuneracao = "";
	private String dtQualificacao = "";
	private Projeto[] projetos = new Projeto[]{};
	private Projeto[] convenios = new Projeto[]{};
	private String[] urlsCandidatas = new String[]{};	

	public String getSigla() {
		return sigla;
	}

	public String getCNPJ() {
		return CNPJ;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getHistoria() {
		return historia;
	}

	public void setHistoria(String historia) {
		this.historia = historia;
	}

	public String getMetaObj() {
		return metaObj;
	}

	public void setMetaObj(String metaObj) {
		this.metaObj = metaObj;
	}

	public String getMetas() {
		return metas;
	}

	public void setMetas(String metas) {
		this.metas = metas;
	}

	public String getServicos() {
		return servicos;
	}

	public void setServicos(String servicos) {
		this.servicos = servicos;
	}

	public String getRemuneracao() {
		return remuneracao;
	}

	public void setRemuneracao(String remuneracao) {
		this.remuneracao = remuneracao;
	}

	public String getDtQualificacao() {
		return dtQualificacao;
	}

	public void setDtQualificacao(String dtQualificacao) {
		this.dtQualificacao = dtQualificacao;
	}

	public Projeto[] getProjetos() {
		return projetos;
	}

	public String[] getUrlsCandidatas() {
		return urlsCandidatas;
	}

	public void setProjetos(Projeto[] projetos) {
		this.projetos = projetos;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public void setUrlsCandidatas(String[] urlsCandidatas) {
		this.urlsCandidatas = urlsCandidatas;
	}
	
	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Projeto[] getConvenios() {
		return convenios;
	}

	public void setConvenios(Projeto[] convenios) {
		this.convenios = convenios;
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

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome2() {
		return nome2;
	}

	public void setNome2(String nome2) {
		this.nome2 = nome2;
	}
	
	

}
