package br.uniriotec.oscip.mining;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Descoberta;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.util.Utils;

public class PreProc {
	
	final static Logger LOGGER = Logger.getLogger(PreProc.class);
	
	public static String[] discoverTextoTxtBetSentences (Oscip oscip, String texto, String[] sinonimos, String[] estMorfs){
		
		ArrayList<String> arrDisc = new ArrayList<String>();
		
		//texto = Utils.removeAcentos(texto);
		
		//para cada sinonimo
		for(String sinonimo : sinonimos){
			
			if (sinonimo.equals("e-mail") && texto.contains("e-mail")){
				sinonimo = "email";
				texto = texto.replace("e-mail", "email");
			}
			
			//ANALISA SENTENCAS DE UM SINONIMO
			if(texto.contains(sinonimo) && !texto.contains("<"+sinonimo) &&  //retirar tags ex: <meta
					//SE PÁGINA POSSUIR ALGUMA REFERENCIA DA OSCIP (NOME, SIGLA, URL OU CNPJ)
					Utils.isTextoContemOSCIP(oscip, texto)){ 

				LOGGER.info("----------(MM) (F1 - HTML/TXT) - ["+oscip.getNome()+"] - ENCONTRADO SINONIMO ["+ sinonimo +"] NO TEXTO  ---");

				String []sents = OpenPLN.getSentences(texto);

				//PARA CADA SENTENCA, ANALISA OS TOKENS
				for(String sent : sents){

					String[] posTags = OpenPLN.POS_PartofSpeech(sent);
					
					//para cada Estrutura Morfológica (EM)
					for(String estMorf : estMorfs){
						
						LOGGER.info("INICIO POS---------- \n");
						
						//analisando Estrutura Morfologica das sentenças para descoberta
						if(PreProc.PTHasEM(sinonimo, estMorf.split(";"), posTags)){
							LOGGER.info("<<<<DESCOBERTA!!!>>> ["+oscip.getNome()+"]--- Sinonimo ["+sinonimo+"]  Sentença ["+sent+"] / POS_Tag ["+Utils.arrayToString(posTags, " ")+"] / Estmorf ["+Utils.arrayToString(estMorfs, ";") + "]");
							arrDisc.add(sinonimo + "||" + sent);
						}else{
							//LOGGER.info("Sentença desprezada ["+oscip.getNome()+"] --- Sinonimo ["+sinonimo+"] Sentença ["+sent+"] / POS_Tag["+ Utils.arrayToString(posTags, " ") +"] / Estmorf ["+Utils.arrayToString(estMorfs, ";") + "]");
							LOGGER.info("Sentença desprezada ["+oscip.getNome()+"] --- Sinonimo ["+sinonimo+"] Sentença ["+sent+"]");
						}
						
						LOGGER.info("FIM POS---------- \n");
					}

				}//end for

			}else{
				
				//TODO: aqui seria para rastrear textos recusados
			}
			//end if
					
		}//end for
		
		return (String[])arrDisc.toArray(new String[]{});
		
	}//end function	
	
	
	private static boolean PTHasEM (String chave, String[] arrEM, String[] postTags){
		
		boolean encontrouChave = false;
		boolean interrompeBusca = false;
		boolean finalizouBusca = false;
		
		System.out.println("PTHasEM - arrEM corrente "+Utils.arrayToString(arrEM, ";"));
		String disc = "";
		int jAnt = 0;
		
		for(int i=0; i < arrEM.length ; i++){
			
			if (interrompeBusca){
				break;
			}
			
			boolean endLoopInterno = false;
			
			for(int j=jAnt; j < postTags.length ; j++){
				
				String[] arrpt = postTags[j].split("_");
				String palavra = "";
				String eme = "";
				
				if(arrpt.length>=2){
					
					palavra = arrpt[0];
					eme = arrpt[1];
				}
				
				//ENCONTROU A CHAVE E ESTRUTURA MORFOLOGICA
				if(!encontrouChave && eme.equals(arrEM[i]) && chave.equals(palavra)){
					encontrouChave = true;
					disc += palavra + " ";

					jAnt = j+1;
					break; //pula para proximo indice do EM
					
				//JÁ TINHA ENCONTRADO A CHAVE, CONTINUA BUSCA	
				}else if(encontrouChave){	
					
					//* = WILDCARD 
					if(arrEM[i].equals("*")){
						
						i++; //pula para proximo valor do indice
						String EM = "";
						
						//Se * nao estiver no fim
						if(i < arrEM.length){
							EM = arrEM[i];
						}
						
						String discAux = "";
						//percorre o texto até encontrar proximo padrão de EM do array
						for(int k=j; k < postTags.length ; k++, j++ ){
							
							String emLoop = postTags[k].split("_")[1];
							String palavraLoop = postTags[k].split("_")[0];
							
							if(!EM.equals(emLoop)){
								discAux += palavraLoop + " ";
							//encontrou padrão morfologico
							}else{
								//System.out.println("ENCONTROU EM=emLopp "+EM);
								discAux += palavraLoop + " ";
								endLoopInterno = true;
								jAnt = j+1;
								if(i == arrEM.length -1){
									finalizouBusca = true;
								}
								break; //sai do for e continua busca
							}
							
							jAnt = j;
							
						} //end for (K = para cada pt)
						
						disc += discAux;
						
						if(endLoopInterno){
							break;
						}
					
					//a proxima ocorrencia tem o mesmo valor morfolofico e ainda nao finalizou
					}else if(j == jAnt && eme.equals(arrEM[i]) && i < arrEM.length -1 ){
						
						System.out.println("PROXIMA OCORRENCIA MESMO em = arrEM[i] = "+eme);
						disc += palavra + " ";
						jAnt = j+1;
						
					//a proxima ocorrencia tem o mesmo valor morfológico e é ultimo indice da chave
					}else if(j == jAnt && eme.equals(arrEM[i]) && i == arrEM.length - 1){
						
						System.out.println("FINALIZACAO DA BUSCA COM SUCESSO (return true) "+disc);
						return true;
						
					//a proxima ocorrencia nao tem o mesmo valor morfologico
					}else{
						interrompeBusca = true;
						break;
					}	
					
				//nao encontrou chave, continua busca
				}else{
					continue;
				}
			}	
			
		}
		
		if(finalizouBusca){
			System.out.println("FINALIZACAO DA BUSCA COM SUCESSO (return true) "+disc);
			return true;
		}else{
			return false;
		}	

	}
	
	
	public static void insereDescobertaTxtSentenca (Connection conn, long idPagina, int idContexto, String texto, String[] strDescobertas){
		
		for(String strDesc  : strDescobertas){
			
			String sinonimo = "";
			String desc = "@#$%ˆ&";
			String[] arrDesc = strDesc.split("\\|\\|");
			if(arrDesc.length >=2){
				
				sinonimo = arrDesc[0];
				desc = arrDesc[1];
			}
			
			int iniPos = texto.indexOf(desc);
			int fimPos = iniPos + desc.length();
			
			//instanciando a descoberta
			Descoberta dsc = new Descoberta();
			dsc.setIdPagina(idPagina);
			dsc.setIdContexto(idContexto);
			dsc.setPosIniKey(iniPos);
			dsc.setPosFimKey(fimPos);
			dsc.setSinonimo(sinonimo);
			dsc.setTexto(texto);
			dsc.setTpDescoberta("S");
			
			DBConnector.insereDescoberta(conn, dsc);
			
		}
		
	}
		
}
