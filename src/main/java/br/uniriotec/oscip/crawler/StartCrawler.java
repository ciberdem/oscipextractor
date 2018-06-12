package br.uniriotec.oscip.crawler;

import java.sql.Connection;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.db.MySQLConn;
import br.uniriotec.oscip.util.Utils;

public class StartCrawler {
	
	private static final Logger LOGGER = Logger.getLogger(StartCrawler.class.getName());
	
	public static void main(String[] args) throws Exception {
	
		/*
		 * Inicio do processo de Coleta
		 * PASSO 1 - Acionar API JSOUP para coletar URls e gravar em arquivos cache (GoogleSearch.buscaUrlsPorOSCIP)
		 * PASSO 2 - Acionar Crawler4J para coletar conteúdo em texto livre das URLs coletadas
		 * 
		 */
		
		LOGGER.info("----------FASE 1 - CRAWLER INICIADO-----------");
		
		//inicia conexão com o banco
		Connection conn = MySQLConn.getConnection();
		
		//trucar tabelas do banco de dados (descoberta, pagina, convenios e relatorios de processamento)
		if(Config.LIMPAR_BANCO_DE_DADOS){
			LOGGER.info("Removendo do banco dados de pesquisas anteriores (descoberta/pagina/convemp/convcrono/convproj/relatorio)...");
			DBConnector.truncateTabelas(conn);	
		}
		
		//remover arquivos cache BIN, se necessário
		if(Config.LIMPAR_CACHE_URLS){
			Utils.removerArquivosCache(Config.PATH_CACHE_URLS);
		}
		
		//remover arquivos cache BIN, se necessário
		if(Config.LIMPAR_CACHE_BIN){
			Utils.removerArquivosCache(Config.PATH_CACHE_BIN);
		}
		
		//remover arquivos cache texto, se necessário
		if(Config.LIMPAR_CACHE_TEXTO){
			Utils.removerArquivosCache(Config.PATH_CACHE_DOCS);
		}
		
		//carrega contextos de busca
		Contexto[] contextos = DBConnector.loadContextos(conn);
		
		LOGGER.info("Carrega lista de OSCIPs...");
		//carrega lista de oscips
		Oscip[] arrOscipsAux = DBConnector.loadNewOscips(conn); 
		
		//TODO: Criar processo para buscar fontes confiáveis
		// (comentado propositalmente)
		//Fonte[] fontes = DBConnector.loadFontesConfiaveis(conn); 
		
		//TODO: Criar processo para buscar do site do ministerio da justiça
		//Oscip[] arrOscipsAux = GoogleSearch.importOSCIPsFromArquivo();
				
		LOGGER.info(">>>> tamanho da lista de OSCIPs a varrer ..." + arrOscipsAux.length);
		
		Oscip[] arrOscips = null;
		
		boolean loadCache = true; //essa opcao é caso queira carregar do cache, se houver
		
		//<<<FASE 1 - COLETA>>> PASSO A - Armazena em arquivo cache URLs candidatas por OSCIP
		LOGGER.info("INICIANDO GOOGLESEARCH - COLETA DE URLS CANDIDATAS....");
		
		//Aciona biblioteca Jsoup para coletar URLs e Documentos candidatos
		arrOscips = GoogleSearch.buscaUrlsPorOSCIP(arrOscipsAux, contextos, loadCache, conn);
			
		//<<<FASE 1 - COLETA>>> PASSO B - Rastreia na web cada URL candidata
		LOGGER.info("INICIANDO FASE 1 / PASSO B - Rastreando  URLS CANDIDATAS....");
		
		//armazenando as URls validas por OSCIP no crawler para mineração posterior
		for(Oscip oscip : arrOscips){	
			
			DBConnector.updateDebugOscip(conn, oscip.getId(), "dtinicrl");
			
			//ativa crawler URLS por contextos
			for(Contexto contexto : contextos){
				
				String[] arqsCacheOscip = Utils.getNomeArquivosPorContextoOscip(Config.PATH_CACHE_DOCS, oscip.getId()+"", oscip.getCNPJ(),
						contexto.getIdContexto()+""); 
				
				if(arqsCacheOscip.length >0){
					LOGGER.info("OSCIP {"+oscip.getCNPJ()+"} / contexto "+contexto.getIdContexto()+", carregando arquivos cache...");
				}else{	
					LOGGER.info("OSCIP {"+oscip.getCNPJ()+"} / contexto "+contexto.getIdContexto()+", iniciando crawler...");
					//chamada ao crawler controller para rastrear URLs de cada OSCIP
					BasicCrawlController.start(oscip, contexto.getIdContexto());
				}
			 }	
				 
			 //Grava paginas do cache no Banco
			 DBConnector.inserePaginasTxtWeb(conn, oscip);
			 
			 //gera relatorio de paginas carregadas por oscip/contexto
			 for(Contexto contexto : contextos){
				 
				 int totalPags = DBConnector.getTotPaginasPorOSCIPContexto(conn, oscip.getId(), contexto.getIdContexto());
				 DBConnector.updateRelatorioPaginas(conn, oscip.getId(), contexto.getIdContexto(), totalPags);
			 }
		 
			 //TODO: Criar processo para buscar em arquivos em PDF (caso necessário)
			 //Utils.loadArquivosToBin(conn, oscip, contextos);
				 
			DBConnector.updateDebugOscip(conn, oscip.getId(), "dtfimcrl");
			
		} //end for oscips
		
		LOGGER.info("FIM DO CRAWLER - Iniciando relatorios...");
		LOGGER.info("-------------------INICIO RELATORIO----------------------");
		DBConnector.relatorioTotalOSCIPs(conn);
		LOGGER.info("-------------------FIM RELATORIO----------------------");
		LOGGER.info("----------FASE 1 - CRAWLER FINALIZADO-----------");
		
		//funalizando conexão no banco
		conn.close();
	}
}	
