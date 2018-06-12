package br.uniriotec.oscip.mining;


import java.sql.Connection;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.beans.Pagina;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.db.MySQLConn;

public class MininigMonitor {
	
	/* 
	 * Etapa responsavel pela análise sintática dos termos
	 * Utilização da API OPENNPL (https://opennlp.apache.org/)
	 */
	
	final static Logger LOGGER = Logger.getLogger(MininigMonitor.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LOGGER.info("----------MININING MONITOR (MM) - START --------------");
		
		LOGGER.info("(MM) - Conectando ao banco de dados para iniciar transação");
		
		//inicia transacao
		Connection conn = MySQLConn.getConnection();
		
		if(Config.LIMPAR_DESCOBERTA){
			DBConnector.truncateDescoberta(conn);
		}
		
		LOGGER.info("----------(MM) FASE 1 - PRÉ-PROCESSAMENTO (PP) --------------");
		
		//carrega contextos de busca
		Contexto[] contextos = DBConnector.loadContextos(conn); 
		
		LOGGER.info("----------(MM) (F1 - PP) - COLETANDO E ATUALIZANDO PAGINAS TEXTO/HTML CANDIDADAS --------------");
		
		Oscip[] oscips = DBConnector.loadOscipsSemDescoberta(conn);
		//Oscip[] oscips = DBConnector.loadAllOscips(conn); 
		
		for(Oscip oscip : oscips){
			
			//insere os convenios da OSCIP no banco
			LOGGER.info("INSERE CONVENIOS NO BANCO....");
			
			//TODO: Melhorar processo para coleta de Convenios para coletar via API 
			if(Config.CONVENIOS_COLETAR_NOVOS_DADOS){
				ConvenioExtractor.insereConveniosOscip(conn, oscip);
			}
		
			DBConnector.updateDebugOscip(conn, oscip.getId(), "dtinimng");
			
			int totalDesc = 0;
			//paginas texto
			for(Contexto contexto : contextos){

				//ATUALIZA PAGINAS TEXTO PARA CANDIDATAS POR OSCIP, DE ACORDO COM OS SINONIMOS ENCONTRADOS NO TEXTO
				DBConnector.updatePaginasTextoCandidata(conn, oscip, contexto);
				
				//CARREGA AS INSTANCIAS DAS PAGINAS WEB CANDIDATAS
				Pagina[] paginas = DBConnector.loadPaginasTextoCandidatas(conn, oscip, contexto); 
						
				//CARREGA ESTRUTURAS MORFOLÓGICAS DAQUELE CONTEXTO
				String[] estMorf = DBConnector.loadEstruturasMorfologicas(conn, contexto.getIdContexto());
				
				//CARREGA AS INSTANCIAS DAS PAGINAS BINARIAS CANDIDATAS
				
				LOGGER.info("----------(MM) (F1 - PP HTML/TXT) - CONTEXTO ["+contexto.getIdContexto()+"] - MINERAÇÃO --------------");
				
				for (Pagina pagina : paginas){
					
					String[] sinonimos = contexto.getSinonimos().split(";");
					
					LOGGER.info("----------(MM) (F1 - HTML/TXT) - CONTEXTO ["+contexto.getIdContexto()+"] - PREPROCESSAMENTO (discoverTextoTxt) --------------");
					
					//<<<<MÉTODO 1 - DESCOBERTAS DENTRO DE UMA SENTENÇA>>>>>
					String[] strDescobertas = PreProc.discoverTextoTxtBetSentences(oscip, pagina.getConteudoTxt(), sinonimos, estMorf);			
					PreProc.insereDescobertaTxtSentenca(conn, pagina.getIdPagina(), contexto.getIdContexto(), pagina.getConteudoTxt(), strDescobertas);
					
				} //end for pagina
				
				//coleta total de descobertas para OSCIP e contexto e....
				totalDesc = DBConnector.getTotDescobertasOSCIPContexto(conn, oscip.getId(), contexto.getIdContexto());
				//...insere relatorio de quantidade de descobertas da OSCIP e CONTEXTO	
				DBConnector.updateRelatorioDescobertas(conn, oscip.getId(), contexto.getIdContexto(), totalDesc);
				totalDesc = 0;
				
			} // end for contexto
			
			DBConnector.updateDebugOscip(conn, oscip.getId(), "dtfimmng");
			
		}//end for oscip	
		
		LOGGER.info("----------MINING MONITOR (MM) - END --------------");

	}

}
