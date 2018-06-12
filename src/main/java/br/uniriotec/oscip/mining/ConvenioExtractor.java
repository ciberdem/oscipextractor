package br.uniriotec.oscip.mining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Convenio;
import br.uniriotec.oscip.beans.CronogramaConv;
import br.uniriotec.oscip.beans.EmpenhoConv;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.db.DBConnector;

public class ConvenioExtractor {
	
	/*
	 * Esta etapa coleta dados dos arquivos .xls disponibilizados pelo SICONV 
	 * E insere no banco de dados
	 * Referência: http://portal.convenios.gov.br/download-de-dados
	 * 
	 */

	private static final Logger LOGGER = Logger.getLogger(ConvenioExtractor.class.getName());
	
	public static void insereConveniosOscip(Connection conn, Oscip oscip) {
		// TODO Auto-generated method stub
		
		//limpar tabelas de convênio
		DBConnector.truncateTabelasConvenios(conn);
		
		//coleta proposta dos convenios
		Convenio[] propConvenios = getPropostaConvenioFromOscip(Config.PATH_SICONV_PROPOSTA_CONVENIOS, oscip);
		
		for(Convenio propConv : propConvenios){
			
			//se tiver convenio incorpora mais dados, senao devolve o objeto 
			Convenio convenio = getConvenioFromProposta(Config.PATH_SICONV_CONVENIOS, propConv);
			
			//coleta cronograma dos convenios
			CronogramaConv[] cronos = getCronogramasConveniosFromOscip(Config.PATH_SICONV_CRONOGRAMAS, convenio);
			
			//coleta empenhos dos convenios
			EmpenhoConv[] emps = getEmpenhosConveniosFromOscip(Config.PATH_SICONV_EMPENHOS, convenio);
			
			//inserir dados do convenio no banco
			DBConnector.insereConvenio(conn, convenio);
			
			//inserir dados dos cronogramas do convenio no banco
			DBConnector.insereCronogramasConvenio(conn, cronos, convenio.getId());
			
			//inserir dados dos empenhos do convenio no banco
			DBConnector.insereEmpenhosConvenio(conn, emps, convenio.getId());
			
		}
		
	}
	
	public static Convenio[] getPropostaConvenioFromOscip(String pathArq, Oscip oscip){
			
		ArrayList<Convenio> arrConv = new ArrayList<Convenio>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(pathArq));
			if(br.ready()){
				String linha = br.readLine();
				
				if(linha.contains("ID_PROPOSTA")){

					while((linha = br.readLine()) != null){
						String[] arrLinha = linha.split(";");
						if(arrLinha.length >=30){
										
							//achou CNPJ da OSCIP, adicionando dados
							if(arrLinha[15].equals(oscip.getCNPJ())){
								
								Convenio conv = new Convenio();
								
								conv.setIdProposta(arrLinha[0]);
								conv.setIdOscip(oscip.getId());
								conv.setObjeto(arrLinha[26]);
								conv.setNomeProponente(arrLinha[16]);
								conv.setOrgao(arrLinha[13]);
								conv.setModalidade(arrLinha[14]);
								conv.setOrgaoSup(arrLinha[5]);
								conv.setMunicipio(arrLinha[2]);
								conv.setSituacaoProposta(arrLinha[23]);
								conv.setDtInicio(arrLinha[24]);
								conv.setDtFim(arrLinha[25]);
								conv.setValorGlobal(arrLinha[27]);
								conv.setValorRepasse(arrLinha[28]);
								conv.setValorContrapartida(arrLinha[29]);
																	
								arrConv.add(conv);
								
								LOGGER.info("------OSCIP["+oscip.getId()+"] - PROPOSTA DE CONVENIOS ["+conv.getIdProposta()+"]-------");
								
							}
						}
						
					}
				}
			}
			br.close();
		}catch(IOException ioe){
			LOGGER.error("Erro getPropostaConvenioFromOscip "+ ioe.getMessage());
			ioe.printStackTrace();
		}
		
		return (Convenio[])arrConv.toArray(new Convenio[]{});
	}
	
	public static Convenio getConvenioFromProposta(String pathArq, Convenio conv){
		
		System.out.println("### CONVENIOS ### ");
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(pathArq));
			
			if(br.ready()){
				String linha = br.readLine();
				
				if(linha.contains("NR_CONVENIO")){

					while((linha = br.readLine()) != null){
						
						String[] arrLinha = linha.split(";");
						
						if(arrLinha.length >=24){
							
							if(arrLinha[1].equals(conv.getIdProposta())){
								
								System.out.println("oscip ["+ conv.getIdOscip() +" ] ");
								System.out.println("convenio ["+ arrLinha[0] +" ] proposta =  "+conv.getIdProposta());

								conv.setId(arrLinha[0]);
								conv.setSituacao(arrLinha[6]);
								conv.setDtInicio(arrLinha[14]);
								conv.setDtFim(arrLinha[15]);
								conv.setValorGlobal(arrLinha[21]);
								conv.setValorRepasse(arrLinha[22]);
								conv.setValorContrapartida(arrLinha[23]);
								conv.setValorEmpenhado(arrLinha[24]);
								conv.setTemConvenio(true);
								//conv.setValorDesembolsado(arrLinha[25]);

								LOGGER.info("------OSCIP["+conv.getIdOscip()+"]  / -------");
								LOGGER.info("------CONVENIO ["+conv.getId()+"] - PROPOSTA ["+conv.getIdProposta()+"] / -------");

							}
						}	//end if length
					} //end whie
						
					} //end if contanis
				}//end
			br.close();
		}catch(IOException ioe){
			LOGGER.error("Erro getConvenioFromProposta "+ ioe.getMessage());
			ioe.printStackTrace();
		}
		
		return conv;
	}

	public static CronogramaConv[] getCronogramasConveniosFromOscip(String pathArq, Convenio conv){
		
		ArrayList<CronogramaConv> arrCrono = new ArrayList<CronogramaConv>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(pathArq));
			if(br.ready()){
				String linha = br.readLine();
				
				if(linha.contains("ID_META")){

					while((linha = br.readLine()) != null){
						
						String[] arrLinha = linha.split(";");
						
						if(arrLinha.length >=13){
									
							if(arrLinha[1].equals(conv.getId())){

								CronogramaConv crono = new CronogramaConv();
								crono.setId(arrLinha[0]);
								crono.setIdConvenio(conv.getId());
								crono.setDescricao(arrLinha[6]);
								crono.setDtInicio(arrLinha[7]);
								crono.setDtFim(arrLinha[8]);
								crono.setUnidFornec(arrLinha[14]);
								crono.setValor(arrLinha[15]);

								arrCrono.add(crono);
								
								LOGGER.info("------OSCIP["+conv.getIdOscip()+"] / CONVENIO ["+crono.getIdConvenio()+"] \n"
										+ "CRONOGRAMA DE CONVENIOS ["+crono.getIdConvenio()+"]-------");
							}	
						}
						
					}
				}
			}
			br.close();
		}catch(IOException ioe){
			LOGGER.error("Erro getCronogramasConveniosFromOscip "+ ioe.getMessage());
			ioe.printStackTrace();
		}
		
		return (CronogramaConv[])arrCrono.toArray(new CronogramaConv[]{});
	}
	
	
	public static EmpenhoConv[] getEmpenhosConveniosFromOscip(String pathArq, Convenio conv){
		
		ArrayList<EmpenhoConv> arrEmp = new ArrayList<EmpenhoConv>();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(pathArq));
			if(br.ready()){
				String linha = br.readLine();
				
				if(linha.contains("NR_EMPENHO")){

					while((linha = br.readLine()) != null){
						
						String[] arrLinha = linha.split(";");
						
						if(arrLinha.length >=8){
									
							if(arrLinha[0].equals(conv.getId())){

								EmpenhoConv emp = new EmpenhoConv();
								emp.setId(arrLinha[1]);
								emp.setIdConvenio(conv.getId());
								emp.setTipoNota(arrLinha[2]);
								emp.setDescTipoNota(arrLinha[3]);
								emp.setDtEmissao(arrLinha[4]);
								emp.setCodSituacao(arrLinha[5]);
								emp.setDescSituacao(arrLinha[6]);
								emp.setValor(arrLinha[7]);

								arrEmp.add(emp);
								
								LOGGER.info("------OSCIP["+conv.getIdOscip()+"] / CONVENIO ["+emp.getIdConvenio()+"] \n"
										+ "EMPENHO DE CONVENIOS ["+emp.getIdConvenio()+"]-------");
								

							}	
						}
						
					}
				}
			}
			br.close();
		}catch(IOException ioe){
			LOGGER.error("Erro getEmpenhosConveniosFromOscip "+ ioe.getMessage());
			ioe.printStackTrace();
		}
		
		return (EmpenhoConv[])arrEmp.toArray(new EmpenhoConv[]{});
	}
}
