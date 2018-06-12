package br.uniriotec.oscip.beans;

public class Pagina {
	
	//classe que armazena as informacoes de uma pagina WEB a examinar
	
	private long idPagina;
	private String idContexto = "";
	private String idOscip = "";
	private String tipo = "";
	private String url = ""; // URL da pagina
	private String status = ""; // COLETADA (CO), CANDIDATA (CD), ACEITA (AC)
	private String conteudoHtml = ""; //conteudo html da pagina
	private String conteudoTxt = ""; //conteudo txt da pagina
	private String path = "";
	private String[] termos; //termos candidatos para analise
	
	public long getIdPagina() {
		return idPagina;
	}
	public void setIdPagina(long idPagina) {
		this.idPagina = idPagina;
	}
	public String getConteudoHtml() {
		return conteudoHtml;
	}
	public void setConteudoHtml(String conteudoHtml) {
		this.conteudoHtml = conteudoHtml;
	}
	public String getConteudoTxt() {
		return conteudoTxt;
	}
	public void setConteudoTxt(String conteudoTxt) {
		this.conteudoTxt = conteudoTxt;
	}
	public String[] getTermos() {
		return termos;
	}
	public void setTermos(String[] termos) {
		this.termos = termos;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIdContexto() {
		return idContexto;
	}
	public void setIdContexto(String idContexto) {
		this.idContexto = idContexto;
	}
	public String getIdOscip() {
		return idOscip;
	}
	public void setIdOscip(String idOscip) {
		this.idOscip = idOscip;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
