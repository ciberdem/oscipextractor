package br.uniriotec.oscip.beans;

public class Tripla {
	
	String ent1 = null;
	String rel = null;
	String ent2 =  null;
	int idContexto =0;
	long idOscip = 0;
	
	public int getIdContexto() {
		return idContexto;
	}
	public void setIdContexto(int idContexto) {
		this.idContexto = idContexto;
	}
	public long getIdOscip() {
		return idOscip;
	}
	public void setIdOscip(long idOscip) {
		this.idOscip = idOscip;
	}
	public String getEnt1() {
		return ent1;
	}
	public void setEnt1(String ent1) {
		this.ent1 = ent1;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getEnt2() {
		return ent2;
	}
	public void setEnt2(String ent2) {
		this.ent2 = ent2;
	}
	
	
}
