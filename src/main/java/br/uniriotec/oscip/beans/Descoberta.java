package br.uniriotec.oscip.beans;

public class Descoberta{
	
	//chave estrangeira da pagina
	private long idPagina; 
	
	//id contexto
	private int idContexto; 
	
	//tipo da descoberta
	private String tpDescoberta;
	
	 //sinonimo utilizado na descoberta
	String sinonimo = "";
	
	//texto original
	String texto = ""; 
	
	//string da descoberta
	String descoberta = "";
	
	//posicao da descoberta no texto original
	int posIniKey; 
	
	//posicao da descoberta no texto original
	int posFimKey;

	public long getIdPagina() {
		return idPagina;
	}

	public void setIdPagina(long idPagina) {
		this.idPagina = idPagina;
	}

	public int getIdContexto() {
		return idContexto;
	}

	public void setIdContexto(int idContexto) {
		this.idContexto = idContexto;
	}

	public String getTpDescoberta() {
		return tpDescoberta;
	}

	public void setTpDescoberta(String tpDescoberta) {
		this.tpDescoberta = tpDescoberta;
	}

	public String getSinonimo() {
		return sinonimo;
	}

	public void setSinonimo(String sinonimo) {
		this.sinonimo = sinonimo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getDescoberta() {
		return descoberta;
	}

	public void setDescoberta(String descoberta) {
		this.descoberta = descoberta;
	}

	public int getPosIniKey() {
		return posIniKey;
	}

	public void setPosIniKey(int posIniKey) {
		this.posIniKey = posIniKey;
	}

	public int getPosFimKey() {
		return posFimKey;
	}

	public void setPosFimKey(int posFimKey) {
		this.posFimKey = posFimKey;
	}
	
}
