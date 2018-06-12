package br.uniriotec.oscip.crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.util.Utils;

public class GoogleSearch {
	
	private static final Logger LOGGER = Logger.getLogger(GoogleSearch.class.getName());

	// REFERENCIAS PARA MANIPULAR O JSOUP
	/* http://www.journaldev.com/7144/jsoup-example-tutorial-java-html-parser
	 * http://www.journaldev.com/7144/jsoup-example-tutorial-java-html-parser
	 * http://www.devmedia.com.br/desenvolvendo-um-crawler-com-crawler4j/32893
	 * http://www.cos.ufrj.br/~tiagoss/crawler/tutorialbasico.html
	 * https://java-source.net/open-source/crawlers
	 * http://www.netinstructions.com/how-to-make-a-simple-web-crawler-in-java/
	 * http://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
	 * http://www.devmedia.com.br/forum/como-fazer-um-web-crawler/569765
	 */
	
	public static Oscip[] buscaUrlsPorOSCIP(Oscip[] oscips, Contexto[] contextos, boolean loadCache, Connection conn) throws IOException {


		LOGGER.info("total de OSCIPS para varrer:{" + oscips.length + "}");
		LOGGER.info("total de contextos por OSCIP para varrer:{" +contextos.length+ "}");
		LOGGER.info("total de urls a armazenar :{" + Config.CRAWLER_QTD_MAX_PAGES_TO_TRACK + "}");
		
		@SuppressWarnings("serial")
		ArrayList<String> arrUrl = new ArrayList<String>(){};
		int cont = 1;
		
		//para cada OSCIP, fazer:
		for(Oscip oscip : oscips){  //inicio for 1
			
			DBConnector.insereNewDebugOscip(conn, oscip.getId());
			
			LOGGER.info("<<<<<<<<<<<varrendo OSCIP "+ oscip.getNome() + " >>>>>>>>>>>>>");
			
			//gerar URLs por contexto
			for(Contexto contexto : contextos){
				
				//verifica se existe arquivo cache para esse arquivo
				if(loadCache && Utils.existeArquivoCacheUrl(oscip.getCNPJ(), contexto.getIdContexto())){
					
					LOGGER.info("<<<<<<<<<<<CARREGANDO CONTEXTO --{"+ contexto.getTpContexto() + "}-- VIA CACHE  >>>>>>>>>>>>>");
					
				}else{	
					
					LOGGER.info("<<<<<<<<<<<CARREGANDO CONTEXTO --{"+ contexto.getTpContexto() +"}-- VIA JSOUP  >>>>>>>>>>>>>");
					LOGGER.info("<<<<<<<<<<<varrendo contexto TIPO --{"+ contexto.getTpContexto() +"}--  >>>>>>>>>>>>>");
					
					//gerar string de busca avançada do google
					String searchURL = montaStrBuscaGoogle(oscip, contexto.getSinonimos());
					
					LOGGER.info("searchURL2 >>>>>>>>>>>>>>>>>>{"+ searchURL+"}");
					
					//inicia a conexão Jsoup com o Google
					Document doc = null;
					String info = null;
					try{
						
						doc = Jsoup.connect(searchURL).userAgent("Mozilla/17.0").timeout(500000).get();
						info = doc.select("div#resultStats").text();
						
					}catch (org.jsoup.HttpStatusException ex){
						
						/* Esse erro (503) ocorre ocasionalmente como uma proteção do Google contra DOS (Denial of Service),
						 * ou seja, evitar muitas requisições de um mesmo servidor
						 * Quando ocorre esse erro, o sistema aborta pois não é possivel coletar novas URls
						 * (deve aguardar 24 horas para nova solicitação)
						 * 
						 */						
						//TODO: Verificar forma de evitar erros ocasionais do Google contra DOS
						
						LOGGER.error("Erro 503 do Google (anti-DOS) " + ex.getMessage());
						ex.printStackTrace();
						System.exit(0);
					}
					
					LOGGER.info("searchURL2 {"+ info+"}");
					
					//carrega os documentos em objetivos Elements
					Elements results = doc.select("h3.r > a");
					
					int i = 0;
					int contLinks = 0;
					
					for (Element result : results) {
						LOGGER.info("Entrou for elements <<{"+ i + "}>>");
						String linkHref = result.attr("href");
						
						linkHref = linkHref.substring(7, linkHref.indexOf("&"));
						
						String url = Utils.decodeURL(Utils.decodeURL(linkHref));
								
						//TODO: Criar facilidade para filtrar apenas documentos de links confiáveis
						if(linkHref.contains("http://") || linkHref.contains("https://")){
							i++;
							String contentType = Utils.getContentType(url);
							arrUrl.add(contentType + "||" + url);
							LOGGER.info("Link adicionado <<{"+ url +"}>>");
							contLinks++;
							
						}else{
							LOGGER.info("Link recusado <<{"+ url+"}>>");
						}
						
					}
					
					/*  
					 * Nesta etapa o sistema força a inserir URLs das OSCIPs
					 * (ocasinalmente o Google não encontra os sites das OSCIPs)
					 */

					if(oscip.getWebsite() != null && !oscip.getWebsite().equals("")){
						if(!oscip.getWebsite().contains(";")){
							//verifica se array de URls possui o site oficial das OSCIPs
							if(!Utils.isArrayContemString(arrUrl, Utils.getDomainName(oscip.getWebsite()))){						
								arrUrl.add("text/html||"+oscip.getWebsite());
								LOGGER.info("Adicionando site oficial da OSCIP <<{"+ oscip.getWebsite()+"}>>");
								contLinks++;
							}
						}else{
							
							String [] urls = oscip.getWebsite().split(";");
							for (String url : urls){
								//verifica se array de URls possui o site oficial das OSCIPs
								if(!Utils.isArrayContemString(arrUrl, Utils.getDomainName(url))){						
									arrUrl.add("text/html||"+url);
									LOGGER.info("Adicionando site oficial da OSCIP <<{"+ url +"}>>");
									contLinks++;
								}	
							}
						}
					}
					
					//criando relatório da OSCIP e contexto com qtd links
					DBConnector.insereRelatorioLinks(conn, oscip.getId(), contexto.getIdContexto(), contLinks);
					
					//armazena dados das URLs validas em um arquivo de cache
					String arquivo = "urls_"+oscip.getCNPJ()+"_ctx"+contexto.getIdContexto()+".txt";
					String header = "CNPJ: "+oscip.getCNPJ()+"/ CONTEXTO:"+contexto.getTpContexto()+"%n";
					String texto = Utils.arrayToString(arrUrl.toArray(new String[]{}));
					
					LOGGER.info("Criando arquivo cache  <<{"+ arquivo+"}>>");
							
					Utils.geraArquivoCache(Config.PATH_CACHE_URLS, arquivo, header, texto);
					arrUrl.clear();
					
					//sleep para evitar erro 503 do Google (anti DOS)
		            try {
						Thread.sleep(Config.SLEEP_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} //end else (se nao existe arquivo cache)
				
				
			}//end for2	 (for each contexto)
			
			cont++;
			
			DBConnector.updateDebugOscip(conn, oscip.getId(), "dtfimlink");
			
			//Se alcançar quantidade máxima de OSCIPs, sair do processo
			if(cont > Config.QTD_MAX_OSCIP){
				
				LOGGER.info("QTD_MAX_OSCIP alcançada ["+Config.QTD_MAX_OSCIP+"], saindo do processo...");
				break;
			}
			
		}//end for 1  (for each oscip)
		
		return oscips;

	}

	public static Oscip[] importOSCIPsFromArquivo() {

		List<Oscip> arrOscips = new ArrayList<Oscip>();

		try {
			FileReader arq = new FileReader(Config.PATH_OSCIP_CSV);
			BufferedReader lerArq = new BufferedReader(arq);

			String linha = lerArq.readLine();

			while (linha != null) {
				
				//armazenando no bean de OSCIPs dados do arquivo
				String[] arrLinha = linha.split(";");
				Oscip oscip = new Oscip();
				if (arrLinha.length >= 8) {
					oscip.setCNPJ(arrLinha[0]);
					oscip.setSigla(arrLinha[1]);
					oscip.setNome(Utils.removeAcentos(arrLinha[2]));
					oscip.setEndereco(arrLinha[3]);
					oscip.setCep(arrLinha[4]);
					oscip.setCidade(arrLinha[5]);
					oscip.setUf(arrLinha[6]);
					oscip.setTelefone(arrLinha[7]);
					if(arrLinha.length==9){
						oscip.setFax(arrLinha[8]);
					}	
				}
				arrOscips.add(oscip);

				linha = lerArq.readLine();

			}
			lerArq.close();
			arq.close();

		} catch (IOException e) {
			LOGGER.error("Erro na abertura do arquivo: %s.\n"+ e.getMessage());
		}
		
		return arrOscips.toArray(new Oscip[]{});

	}
	
	//busca URls do cache
	public static Oscip[] importUrlsOscipsFromDB() {

		// desenvolver funcao para coletar URLs das OSCIPs do cache
		return new Oscip[] {};
	}
	
	// importa OSCIP do arquivo

	public static Oscip[] importOSCIPsFromWebJUS(String pathArquivo) {

		// retornar OSCIPs do Ministerio da Justiça
		return new Oscip[] {};
	}
	
    private static String montaStrBuscaGoogle (Oscip oscip, String sinonimos){
		
    	/*
    	 * Ao montar a string de busca, algumas consideracoes
    	 * %28 significa o caracter "(" ou abre parenteses
    	 * %29 significa o caracter ")" ou fecha parenteses
    	 * %22 significa o caracter " ou aspas 
    	 * ao realizar a busca se faz a seguinte montagem
    	 * 
    	 * -------------BLOCO1---------////----BLOCO2-------------------
    	 * (<nome oscip> OR <cnpj oscip>) <sinonimo1 OR sinonimo2 (...)>
    	 * 
    	 */

		//gerar string do bloco 1 (termos em parenteses)
    	String strBloco1 = "%28%22"+oscip.getNome().replace(" ", "+")+"%22+OR+"+oscip.getCNPJ()+"%29+"; 			
		
    	//gerar string do bloco 2 (termos em parenteses)
    	String strBloco2 = sinonimos.replace(";", "+OR+");

		LOGGER.info("string de busca: <<{Config.GOOGLE_SEARCH_URL+strBloco1+strBloco2}>>  >>>>>>>>>>>>>");
		
		return Config.GOOGLE_SEARCH_URL+strBloco1+strBloco2;
	}

	public static void main(String[] args) throws IOException {
		
		
		
	}

}
