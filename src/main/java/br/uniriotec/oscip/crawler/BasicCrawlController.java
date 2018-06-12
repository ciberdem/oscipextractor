package br.uniriotec.oscip.crawler;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.util.Utils;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController {
	
	private static final Logger LOGGER = Logger.getLogger(BasicCrawlController.class.getName());

	public static void start(Oscip oscip, int numContexto) throws Exception {
		
		//carrega urls candidatas daquele contexto (apenas documentos web/html)
		oscip.setUrlsCandidatas(Utils.loadUrlsCandidatasPorContexto(oscip.getCNPJ(), numContexto, "html"));
		
		//se o cache foi carregado com sucesso para aquela OSCIP
		if(oscip.getUrlsCandidatas().length > 0){
			
			LOGGER.info("CRAWLER - OSCIP {"+oscip.getCNPJ()+"} - PASSO 1 - Mapeando configurações (CrawlerConfig)...");
			
			/* crawlStorageFolder é uma pasta onde os dados de
			 rastreamento intermediários são armazenados. */
			String crawlStorageFolder = Config.PATH_CACHE_URLS;

			// numberOfCrawlers mostra o número de threads simultâneos
			int numberOfCrawlers = Config.CRAWLER_QTD_MAX_THREADS;
			
			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(crawlStorageFolder);
			/*
			 * Be polite: Make sure that we don't send more than 1 request per
			 * second (1000 milliseconds between requests).
			 */
			config.setPolitenessDelay(1000); 
			/*
			 * You can set the maximum crawl depth here. The default value is -1 for
			 * unlimited depth
			 */
			config.setMaxDepthOfCrawling(Config.CRAWLER_QTD_DEEP_PAGES); //profundidade de buscas nas URLs
			
			/*
			 * You can set the maximum number of pages to crawl. The default value
			 * is -1 for unlimited number of pages
			 */
			
			config.setMaxPagesToFetch(Config.CRAWLER_QTD_MAX_PAGES_TO_TRACK); //quantidade de paginas pra rastrear
			
			/*
			 * Do you want crawler4j to crawl also binary data ? example: the
			 * contents of pdf, or the metadata of images etc
			 */
			
			config.setIncludeBinaryContentInCrawling(Config.CRAWLER_MUST_INSERT_BIN_PAGES);

			/*
			 * Do you need to set a proxy? If so, you can use:
			 * config.setProxyHost("proxyserver.example.com");
			 * config.setProxyPort(8080);
			 *
			 * If your proxy also needs authentication:
			 * config.setProxyUsername(username); config.getProxyPassword(password);
			 */

			/*
			 * This config parameter can be used to set your crawl to be resumable
			 * (meaning that you can resume the crawl from a previously
			 * interrupted/crashed crawl). Note: if you enable resuming feature and
			 * want to start a fresh crawl, you need to delete the contents of
			 * rootFolder manually.
			 */
			config.setResumableCrawling(false);

			/*
			 * Instantiate the controller for this crawl.
			 * (Iniciar crawler - coleta o conteúdo das urls para armazenar em arquivos-cache
			 */

			LOGGER.info("CRAWLER - OSCIP {"+oscip.getCNPJ()+"} - PASSO 2 - Configurando dados do Robot...");
			
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
			
			List<String> arrUrls = new ArrayList<String>(); 	
			
			//carrega documentos
			
			for (String urlValida : oscip.getUrlsCandidatas()){
				
				//verifica se no array já contem a mesma URL (aceita apenas se nao tiver)
				if(!Utils.isArrayContemString(arrUrls, Utils.getDomainName(urlValida))){
					
					controller.addSeed(urlValida);
					arrUrls.add(urlValida);	
					LOGGER.info(">>>> adicionando URL válida {"+urlValida+"}");
				}
				
			}
			
			String linhaPesquisa = oscip.getId()  + "||" + oscip.getCNPJ() + "||" + numContexto;
			
			//gerando cache da pesquisa atual (arquivo temporário)
			Utils.geraArquivoCache(Config.PATH_CACHE_DOCS, Config.CACHE_PESQUISA_ATUAL, linhaPesquisa, "");
			
			/*
			 * Start the crawl. This is a blocking operation, meaning that your code
			 * will reach the line after this only when crawling is finished.
			 */
			LOGGER.info("CRAWLER - OSCIP {"+oscip.getCNPJ()+"} - Passo 3 - Iniciando crawler com as informações...");
			
			controller.start(Crawler4jWebCrawler.class, numberOfCrawlers);
			
			//remove arquivo cache CNPJ atual
			Utils.deletePesquisaCache();			
			
			LOGGER.info(">>> numero de URLs visitadas..."+Crawler4jWebCrawler.URLS_VISITADAS.size());
		
		}else{
			LOGGER.info("Não encontrado arquivo de URLs para OSCIP " + oscip.getCNPJ());
		}
	}
}