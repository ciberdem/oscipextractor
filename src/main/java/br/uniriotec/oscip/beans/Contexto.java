package br.uniriotec.oscip.beans;

public class Contexto {

	//classe responsavel para definir contexto de pesquisa, ou seja, os termos de buscas na web e regras de parser nas paginas
	
	private int idContexto; //id do contexto
	private String cnpj;
	private String nome;
	private String sigla;
	private String tpContexto; //tipo de contexto de pesquisa (objetivo, metas, servicos, convenios, etc)
	public String sinonimos; // lista de sinonimos por tipo (Ex: para objetivo = objetivo, finalidade, etc)
	
	public int getIdContexto() {
		return idContexto;
	}
	public void setIdContexto(int idContexto) {
		this.idContexto = idContexto;
	}
	public String getTpContexto() {
		return tpContexto;
	}
	public void setTpContexto(String tpContexto) {
		this.tpContexto = tpContexto;
	}
	public String getSinonimos() {
		return sinonimos;
	}
	public void setSinonimos(String sinonimos) {
		this.sinonimos = sinonimos;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
}
