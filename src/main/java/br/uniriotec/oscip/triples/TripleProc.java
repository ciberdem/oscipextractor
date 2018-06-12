package br.uniriotec.oscip.triples;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Pagina;
import br.uniriotec.oscip.beans.Tripla;
import br.uniriotec.oscip.mining.OpenPLN;
import br.uniriotec.oscip.util.Utils;

public class TripleProc {
	
	final static Logger LOGGER = Logger.getLogger(TripleProc.class);
	

	public static boolean isDescobertaCompativelComTriplaRef (Tripla[] triplasRef, Pagina desc, Contexto contexto){
		
		//TODO: carregar objetos de contextos do banco
		String[] sinonimos = null;
		
		if(contexto.getSinonimos() != null){
			sinonimos = contexto.getSinonimos().split(";");
		}

		//para cada descoberta coletada compara com cada tripla de referencia daquele contexto 
		for(Tripla tripla : triplasRef){
			if(TripleProc.isPadraoStrCompativelRef(tripla, desc, sinonimos)){
				return true;
			}
				
		}
	
		return false;
	}
	
	public static boolean isPadraoStrCompativelRef(Tripla tripla, Pagina desc, String[] sinonimos){
		
		//TODO: Comparar o padrão do objeto da tripla com o texto da pagina
		String txtDesc = desc.getConteudoTxt().toLowerCase();
		
		String ent1 = tripla.getEnt1()==null?null:tripla.getEnt1().toLowerCase();
		String rel = tripla.getRel()==null?null:tripla.getRel().toLowerCase();
		String ent2 = tripla.getEnt2()==null?null:tripla.getEnt2().toLowerCase();
		
		//verifica se são nulas
		boolean isEnt1Null = (ent1 == null)?true:false;
		boolean isRelNull = (rel == null)?true:false;
		boolean isEnt2Null = (ent2 == null)?true:false;
		
		String strConcatComSeparador = "";
		String strConcatSemSeparador = "";
		
		if(!isEnt1Null){
			strConcatComSeparador += ent1; 
			strConcatSemSeparador += ent1;
		}
		
		if(!isRelNull){
			
			if(isEnt1Null){
				strConcatComSeparador += rel;
				strConcatSemSeparador += rel;
			}else{
				strConcatComSeparador += ";"+rel; 
				strConcatSemSeparador += " "+rel;
			}
		}
		
		if(!isEnt2Null){
			
			if(isEnt1Null && isRelNull){
				strConcatComSeparador += ent2; 
				strConcatSemSeparador += ent2;
			}else if(!isEnt1Null && isRelNull){
				strConcatComSeparador += ent1+";"+ent2; 
				strConcatSemSeparador += ent1+" "+ent2;
			}else if(isEnt1Null && !isRelNull){
				strConcatComSeparador += rel+";"+ent2; 
				strConcatSemSeparador += rel+" "+ent2;
			}
		}
		
		String strDescAnt = ""; //ultima descoberta ("", SYN, WC, EM) 
		String sentAnt = ""; //sentença anterior
		String palavraAnt = ""; //palavra anterior
		String EMAnt = ""; //Estrutura morfolófica anterior
		int posAnt = -1; //posicao anterior
		String estBusca  = "";
		boolean ativarWC = false;
		
		String[] triplaStr = strConcatComSeparador.split(";");
		
		LOGGER.info("#####isPadraoStrCompativelRef - Nova Tripla de Referência = ["+strConcatComSeparador+"]########");
		
		for (int i=0; i<triplaStr.length;i++){
			
			String str = triplaStr[i];
			
			///***** BUSCA POR SINONIMO (SYN)********
			if(str.equals("(syn)")){
				
				LOGGER.info("isPadraoStrCompativelRef - busca por synonimo (syn)");
				
				if(strDescAnt.equals("WC")){
					ativarWC = true;
					strDescAnt = "SYN";
				}else{
					ativarWC = false;
				}
				
				estBusca = TripleProc.buscaEmTripla("BUSCA_SINONIMO", sinonimos, sentAnt, posAnt, palavraAnt, EMAnt, strDescAnt, txtDesc, ativarWC);
				
				//encontrou sinônimo
				if(!estBusca.equals("")){
					
					LOGGER.info("isPadraoStrCompativelRef - encontrou (SYN) - estBusca ["+estBusca+"]");
					
					//carrega variaveis auxiliares para proximo loop
					String[] arrEB = estBusca.split(";");
					if (arrEB.length > 1){
												
						sentAnt = arrEB[0];
						palavraAnt = arrEB[1];
						posAnt = Integer.parseInt(arrEB[2]);
						EMAnt = arrEB[3];						
					}
					strDescAnt = "SYN";	
				}else{
					strDescAnt = "SYN";
					return false;
				}
				
				
			///***** BUSCA POR WILDCARD (WC)********
			}else if(str.equals("*")){
				
				LOGGER.info("isPadraoStrCompativelRef - busca por (WC)");
				
				strDescAnt = "WC";
				continue;
				
			///***** BUSCA POR ESTRUTURA MORFOLÓGICA (EM)********
			}else if(str.contains("<")){
				
				LOGGER.info("isPadraoStrCompativelRef - busca por (EM)");
				
				EMAnt = str;
				if(strDescAnt.equals("WC")){
					ativarWC = true;
					strDescAnt = "EM";
				}else{
					ativarWC = false;
				}
				
				estBusca = TripleProc.buscaEmTripla("BUSCA_EM", sinonimos, sentAnt, posAnt, palavraAnt, EMAnt, strDescAnt, txtDesc, ativarWC);
				
				//encontrou sinônimo
				if(!estBusca.equals("")){
					
					LOGGER.info("isPadraoStrCompativelRef - encontrou (EM) - estBusca ["+estBusca+"]");
					//carrega variaveis auxiliares para proximo loop
					String[] arrEB = estBusca.split(";");
					if (arrEB.length > 1){
						
						sentAnt = arrEB[0];
						palavraAnt = arrEB[1];
						posAnt = Integer.parseInt(arrEB[2]);
						EMAnt = arrEB[3];						
					}
					strDescAnt = "EM";
				}else{
					strDescAnt = "EM";
					return false;
				}
			
				///***** BUSCA POR PALAVRA SIMPLES (PS)********
			}else if(TripleProc.isPalavra(str)){	
				
				LOGGER.info("isPadraoStrCompativelRef - busca por (PS)");
				
				palavraAnt = str;
				if(strDescAnt.equals("WC")){
					ativarWC = true;
					strDescAnt = "PS";
				}else{
					ativarWC = false;
				}
				
				estBusca = TripleProc.buscaEmTripla("BUSCA_PALAVRA", sinonimos, sentAnt, posAnt, palavraAnt, EMAnt, strDescAnt, txtDesc, ativarWC);
				
				//encontrou sinônimo
				if(!estBusca.equals("")){
					
					LOGGER.info("isPadraoStrCompativelRef - encontrou (PS) - estBusca ["+estBusca+"]");
					
					//carrega variaveis auxiliares para proximo loop
					String[] arrEB = estBusca.split(";");
					if (arrEB.length > 1){
												
						sentAnt = arrEB[0];
						palavraAnt = arrEB[1];
						posAnt = Integer.parseInt(arrEB[2]);
						EMAnt = arrEB[3];						
					}
					strDescAnt = "PS";
				}else{
					strDescAnt = "PS";
					return false;
				}
				
			}else{
				
				LOGGER.info("isPadraoStrCompativelRef - Padrão não encontrado na tripla ["+strConcatSemSeparador+"], abortando... ");
				System.exit(0);
			}
			
		}
		
		LOGGER.info("isPadraoStrCompativelRef - final da função, retornando true , ultima estbusca = "+estBusca);
		return true;
	}

	
	public static String buscaEmTripla(String funcaoAtual, String[] sinonimos, String sentAnt, 
			int posAnt, String palavraAnt, String EMAnt, String strDescAnt, String txtDesc, boolean ativarWC){
		
		String estruturaBusca = "";  //vai armazenar a sentAnt;posAnt;palavraAnt;EMAnt;
		String novoTxtPos = sentAnt.equals("")?txtDesc:sentAnt;
		/*
		//se vem de uma posicao anterior, marca posicao inicial para busca
		if(posAnt >-1){
			novoTxtPos = novoTxtPos.substring(posAnt+(palavraAnt.length()+1));
			sentAnt = novoTxtPos;
		}
		*/
		LOGGER.info("buscaEmTripla - buscando em sentenca ["+novoTxtPos+"]"); 
		boolean buscaProximaPosImediata = false;
		
		String []sents = OpenPLN.getSentences(novoTxtPos);
		
		//*********************************************BUSCA POR SINONIMO**********************************************
		if(funcaoAtual.equals("BUSCA_SINONIMO")){
			
			//*****BUSCA SINONIMO - SE É O PRIMEIRO PARAMETRO DA TRIPLA ******
			if(strDescAnt.equals("")){
				
				LOGGER.info("buscaEmTripla - BUSCA SINONIMO - PRIMEIRA BUSCA DA TRIPLA "); 
				
				buscaProximaPosImediata = false; //como é a primeira vez tem que buscar até achar
				
				//para cada sinonimo
				for(String sinonimo : sinonimos){
					
					//para cada sentença
					for(String sent: sents){
					
						estruturaBusca = TripleProc.montaEstruturaBusca(sent, sinonimo, "SYN", buscaProximaPosImediata);
						if(!estruturaBusca.equals("")){
							return estruturaBusca;
						}	
					}	

				}	
				
				LOGGER.info("buscaEmTripla (SYN) - Não ncontrou estruturaBusca, retornando vazio...");
				return "";
			
			//*****	BUSCA SINONIMO - SE PALAVRA ANTERIOR FOR um WC, EM ou PALAVRA SIMPLES *****
			}else if(strDescAnt.equals("EM") || strDescAnt.equals("PS") 
					|| strDescAnt.equals("WC") || strDescAnt.equals("SYN")){
				
				if(strDescAnt.equals("WC")){
					buscaProximaPosImediata = false; //busca sinonimo ate o final
				}else{
					if(ativarWC){
						buscaProximaPosImediata = false;
					}else{
						buscaProximaPosImediata = true; //proxima posicao deve ser um sinonimo
					}	
				}
				
				//para cada sinonimo
				for(String sinonimo : sinonimos){
						
					//buscando em cima da sentença anterior
					estruturaBusca = TripleProc.montaEstruturaBusca(sentAnt, sinonimo, "SYN", buscaProximaPosImediata);
					if(!estruturaBusca.equals("")){
						LOGGER.info("buscaEmTripla SINONIMO - Encontrou estruturaBusca ["+estruturaBusca+"], retornando...");
						return estruturaBusca;
					}

				}	
				
				LOGGER.info("buscaEmTripla (SYN) - Não ncontrou estruturaBusca, retornando vazio...");
				return estruturaBusca;
				
			}
		
		//*********************************************BUSCA POR EM**********************************************
		}else if(funcaoAtual.equals("BUSCA_EM")){
	
			
			//*****BUSCA EM - SE É O PRIMEIRO PARAMETRO DA TRIPLA ******
			if(strDescAnt.equals("")){
				
				LOGGER.info("buscaEmTripla - BUSCA EM - PRIMEIRA BUSCA DA TRIPLA ");
				buscaProximaPosImediata = false; //como é a primeira vez tem que buscar até achar
				
				//para cada sentença
				for(String sent: sents){
				
					estruturaBusca = TripleProc.montaEstruturaBusca(sent, EMAnt, "EM", buscaProximaPosImediata);
					if(!estruturaBusca.equals("")){
						LOGGER.info("buscaEmTripla - BUSCA EM - Encontrou estruturaBusca ["+estruturaBusca+"], retornando...");
						return estruturaBusca;
					}	
				}
				
			//*****	BUSCA EM - SE PALAVRA ANTERIOR FOR um WC, EM ou PALAVRA SIMPLES *****
			}else if(strDescAnt.equals("EM") || strDescAnt.equals("PS") 
					|| strDescAnt.equals("WC") || strDescAnt.equals("SYN")){
			
				if(strDescAnt.equals("WC")){
					buscaProximaPosImediata = false; //busca sinonimo ate o final
				}else{
					if(ativarWC){
						buscaProximaPosImediata = false;
					}else{
						buscaProximaPosImediata = true; //proxima posicao deve ser um sinonimo
					}	
				}
				
				estruturaBusca = TripleProc.montaEstruturaBusca(sentAnt, EMAnt, "EM", buscaProximaPosImediata);
				if(!estruturaBusca.equals("")){
					
					LOGGER.info("buscaEmTripla - BUSCA EM - Encontrou estruturaBusca ["+estruturaBusca+"], retornando...");
					return estruturaBusca;
				}
			}
			
			LOGGER.info("buscaEmTripla - BUSCA EM - Não encontrou estruturaBusca, retornando vazio...");
			return "";

		//*********************************************BUSCA POR PALAVRA**********************************************
		}else if(funcaoAtual.equals("BUSCA_PALAVRA")){
			
			
			//*****BUSCA PALAVRA - SE É O PRIMEIRO PARAMETRO DA TRIPLA ******
			if(strDescAnt.equals("")){
				
				LOGGER.info("buscaEmTripla - BUSCA PALAVRA - PRIMEIRA BUSCA DA TRIPLA ");
				buscaProximaPosImediata = false; //como é a primeira vez tem que buscar até achar
				
				//para cada sentença
				for(String sent: sents){
					
					estruturaBusca = TripleProc.montaEstruturaBusca(sent, palavraAnt, "PS", buscaProximaPosImediata);
					if(!estruturaBusca.equals("")){
						
						LOGGER.info("buscaEmTripla - BUSCA PALAVRA - Encontrou estruturaBusca ["+estruturaBusca+"], retornando...");
						return estruturaBusca;
					}	
				}	
				
			//*****	BUSCA EM - SE PALAVRA ANTERIOR FOR um WC, EM ou PALAVRA SIMPLES *****
			}else if(strDescAnt.equals("EM") || strDescAnt.equals("PS") 
					|| strDescAnt.equals("WC") || strDescAnt.equals("SYN")){
			
				if(strDescAnt.equals("WC")){
					buscaProximaPosImediata = false; //busca sinonimo ate o final
				}else{
					if(ativarWC){
						buscaProximaPosImediata = false;
					}else{
						buscaProximaPosImediata = true; //proxima posicao deve ser um sinonimo
					}	
				}
				
				estruturaBusca = TripleProc.montaEstruturaBusca(sentAnt, palavraAnt, "PS", buscaProximaPosImediata);
				if(!estruturaBusca.equals("")){
					
					LOGGER.info("buscaEmTripla - BUSCA PALAVRA - Encontrou estruturaBusca ["+estruturaBusca+"], retornando...");
					return estruturaBusca;
				}
			}
			
		} //end if buscas
		
		LOGGER.info("buscaEmTripla - Não encontrou padrão de busca, retornando vazio...");
		return "";
	}
	
	public static String montaEstruturaBusca (String sent, String chave, String ultimaBusca, boolean buscaProximaPosImediata){
		
		String estruturaBusca = "";
		int i=0;
		String strRefBusca = "";
		
		//se buscar for WC, PS ou SYN, buscar palavra na sentença
		if(ultimaBusca.equals("WC") || ultimaBusca.equals("PS") || ultimaBusca.equals("SYN") ){
			
			LOGGER.info("montaEstruturaBusca ["+ultimaBusca+"] - chave ["+chave+"] na sentença ["+sent+"]");
			
			int pos = sent.indexOf(chave);

			char proxChar = sent.charAt(pos+chave.length());
			
			//verifica se o proximo caracter é espaço ou consta dentro de uma palvra
			if(!Character.toString(proxChar).equals(" ")){
				pos = -1;
			}
			
			//não encontrou
			if(pos == -1){
				LOGGER.info("montaEstruturaBusca ["+ultimaBusca+"] - Não encontrou strRefBusca imediata - retornando vazio");	
				return "";
			//encontrou chave 
			}else if((pos == 0 && buscaProximaPosImediata) || (pos >= 0 && !buscaProximaPosImediata)){	
				
				//retira a chave encontrada da sentença para repassar
				String strAux = sent.substring(pos);	
				sent = strAux.substring(chave.length()+1);
				
				estruturaBusca = sent+";"+chave+";"+pos+";;"+ultimaBusca;
				LOGGER.info("montaEstruturaBusca ["+ultimaBusca+"] - Encontrou strRefBusca,  estruturaBusca ["+estruturaBusca+"]");	
				return estruturaBusca;
			}else{	
				LOGGER.info("montaEstruturaBusca ["+ultimaBusca+"]- Não encontrou strRefBusca imediata - retornando vazio");	
				return "";
			}
		
		//busca por EM, percorrer cada estrutura do texto	
		}else if (ultimaBusca.equals("EM")) {
			
			//quebra o texto em tokes adicionando o POS (Ex: viver_v_inf)
			String[] posTags = OpenPLN.POS_PartofSpeech(sent);
			
			LOGGER.info("montaEstruturaBusca - posTags "+Utils.arrayToStringSemQuebra(posTags)); 
			
			//retirando os caracteres < e > da EM
			chave = chave.substring(1, chave.length()-1);
			
			//para cada token_EM
			for(String pt : posTags){
				
				String[] arrpt = pt.split("_");
				String palavra = "";
				String eme = "";
				
				if(arrpt.length>=2){
					
					palavra= arrpt[0];
					eme = arrpt[1];
					
				}else{
					LOGGER.info("Erro na geração do POST_TAG, abortando...");
					System.exit(0);
				}
				
				LOGGER.info("montaEstruturaBusca [eme] - "+strRefBusca+" VS "+chave);

				//achou a chave
				if(eme.equals(chave)){
					
					 //vai armazenar a ESTRUTURA = sentAnt;palavraAnt;posAnt;EMAnt;strDescAnt;
					int pos = sent.indexOf(palavra);
					
					//retira a chave encontrada da sentença para repassar
					if(pos>-1){
						String strAux = sent.substring(pos);	
						sent = strAux.substring(palavra.length()+1);
					}
					estruturaBusca = sent+";;"+pos+";"+eme+";"+ultimaBusca;
					
					LOGGER.info("montaEstruturaBusca - Encontrou strRefBusca,  estruturaBusca ["+strRefBusca+"]");	
					return estruturaBusca;
					
				}else{ 
					
					if(buscaProximaPosImediata){
						
						LOGGER.info("montaEstruturaBusca - Não encontrou strRefBusca imediata - retornando vazio");	
						//se a busca era na posicao imediatamente posterior e não achou, retorna vazio
						return "";
					}	
				}	
		
			}
			
		}
		
		
		
		LOGGER.info("montaEstruturaBusca - Não encontrou strRefBusca, retornando vazio ");	
		return estruturaBusca;
	
	}
	

	public static boolean isPalavra(String palavra){
		
		if(palavra.contains("(") || palavra.contains("*") || palavra.contains("<")){
				return false;
		}else{
				return true;
		}
		
	}
	
	public static boolean contemPalavraEntreSeparador(String strConcatComSeparador){
		
		String[] palavras = strConcatComSeparador.split(";");
		
		for(String palavra : palavras){
			
			if(palavra.contains("(") || palavra.contains("*") || palavra.contains("<")){
				continue;
			}else{
				return true;
			}
		}
		
		return false;
	}

}
