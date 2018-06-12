package br.uniriotec.oscip.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.util.Utils;

import java.util.UUID;

/* LINKS DE REFERENCIA para o CRAWLER4J
 * https://ellisonalves.wordpress.com/2015/11/29/escrevendo-crawlers-em-java-parte-1-crawler4j/ 
 * https://javaes.wordpress.com/2013/04/20/crawler4j-web-robot-em-java/
 * https://github.com/yasserg/crawler4j
 * 
 */

public class Crawler4jWebCrawler extends WebCrawler {

	private static final Logger LOGGER = Logger.getLogger(Crawler4jWebCrawler.class.getName());
    
	// A variável FILTERS serve para filtrar extensões que não serão consideradas no processo de coleta
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpg" + "|png|tiff|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	public static final Set<String> URLS_VISITADAS = new HashSet<String>();
	
	private String cnpj = "";
	
	public String getCnpj() {
		return cnpj;
	}
    
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	// verifica se uma URL é valida baseada nos filtros (FILTERS)
	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		String href = url.getURL().toLowerCase();
		
		boolean urlValida = !FILTERS.matcher(href).matches();
		boolean deveVisitar = urlValida && !URLS_VISITADAS.contains(href);
		LOGGER.info("shouldVisit {"+href+"} ? " + deveVisitar);
		if (deveVisitar) {
			URLS_VISITADAS.add(href);
		}
		return deveVisitar;
	}

	@Override
	public void visit(Page page) {
		LOGGER.info("Visiting: {"+page.getWebURL().getURL()+"}");
		
		//coleta dados da página visidada
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			String url = page.getWebURL().getURL();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			
			LOGGER.info("url: " + url+ " --- CONTENT TYPE = "+ page.getContentType());

			String[] arrPesq = Utils.getCachePesquisa().split("\\|\\|");
			String idOscip = "";
			String cnpj = "";
			String numContexto = "";
			if(arrPesq.length >= 3){
				idOscip = arrPesq[0];
				cnpj = arrPesq[1];
				numContexto = arrPesq[2];
			}
			
			LOGGER.info("cnpj atual: " +cnpj);
			LOGGER.info("Text length: " + text.length());
			LOGGER.info("Html length: " + html.length());
			LOGGER.info("Number of outgoing links: " + links.size());
			
			//GERACAO DOS ARQUIVOS CACHE DE RESULTADO PARA POSTERIOR MINERACAO
			String hash = UUID.randomUUID()+"";
			String nomeTxt = "cache_"+ idOscip + "_" + cnpj + "_"+ hash + "_T_" + numContexto+".txt";
			
			String header = "<!--URLINICIO:"+url+"URLFIM-->";
			
			LOGGER.info("Gerando arquivo cache txt: " + nomeTxt);
			
			//armazena arquivo de cache texto (futura mineracao)
			Utils.geraArquivoCache(Config.PATH_CACHE_DOCS, nomeTxt, header, text);
			
			//TODO: Armazenar arquivo de cache HTML (caso seja necessário)
			//(comentado propositalmente)
			//String nomeHtml = "cache_"+ idOscip + "_" + cnpj + "_"+ hash + "_H_" + numContexto+".txt";
			//Utils.geraArquivoCache(Config.PATH_CACHE_DOCS, nomeHtml, header, html);
			//LOGGER.info("Gerando arquivo cache html: " + nomeHtml);
			
		}

	}

}