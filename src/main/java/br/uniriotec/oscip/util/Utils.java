package br.uniriotec.oscip.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Fonte;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.beans.Pagina;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.db.DBConnector;

public class Utils {
	
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
	
	public static String trataParametroBusca (String term){
		
		//remove espaços
		term = term.trim();
		
		//remove caracteres especiais
		term = Utils.removeAcentos(term);
		
		//adiciona + entre nos espaços
		term = term.replaceAll(" ", "+");
		
		//aplicar sinonimos
		
		return term;
		
	}
	
	public static String decodeURL(final String str) {
		
		String straux = str;
		try {
			straux =  URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return straux;
	}
	
	public static String removeAcentoseEspeciais(final String str) {
		String straux = Normalizer.normalize(str, Normalizer.Form.NFD);
		straux = straux.replaceAll("[^\\p{ASCII}]", "");
		straux = straux.replaceAll("[^a-zZ-Z1-9 ]", "");
		return straux;
	}
	
	public static String removeAcentos(String string) {
		
		string = string.replaceAll("[ÂÀÁÄÃ]","A"); 
		string = string.replaceAll("[âãàáä]","a"); 
		string = string.replaceAll("[ÊÈÉË]","E"); 
		string = string.replaceAll("[êèéë]","e"); 
		string = string.replaceAll("ÎÍÌÏ","I"); 
		string = string.replaceAll("îíìï","i"); 
		string = string.replaceAll("[ÔÕÒÓÖ]","O"); 
		string = string.replaceAll("[ôõòóö]","o"); 
		string = string.replaceAll("[ÛÙÚÜ]","U"); 
		string = string.replaceAll("[ûúùü]","u"); 
		string = string.replaceAll("Ç","C"); 
		string = string.replaceAll("ç","c");  
		string = string.replaceAll("[ýÿ]","y"); 
		string = string.replaceAll("Ý","Y"); 
		string = string.replaceAll("ñ","n"); 
		string = string.replaceAll("Ñ","N"); 

	    return string;
	}

	public static void geraArquivoCache(String destino, String nome, String header, String texto) {
		
		List<String> lines = Arrays.asList(header, texto);
		Path file = Paths.get(destino+nome);
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.error("Erro em geraArquivoCache() :" +e.getMessage());
			e.printStackTrace();
		}
		 
	}
	
	public static String getCachePesquisa(){
		
		String linha = "";

		try{
			BufferedReader br = new BufferedReader(new FileReader(Config.PATH_CACHE_DOCS + Config.CACHE_PESQUISA_ATUAL));
			if(br.ready()){
				linha = br.readLine();
			}
			br.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return linha;
	}
	
	public static String[] getNomeArquivosPorOscip (String caminho, String idOscip, String CNPJ) {
		
		ArrayList<String> arrArq = new ArrayList<String>();
		Path diretorio = Paths.get(caminho);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(diretorio, "cache_" + idOscip + "_" + CNPJ +  "*.txt")){
			  for(Path path : stream){
				 LOGGER.debug("Arquivo cache gerado :" + path.getFileName());
			     arrArq.add(path.getFileName().toString());
			  }	 
		} catch (IOException e) {
			LOGGER.error("Erro em getNomeArquivosPorOscip() :" +e.getMessage());
			e.printStackTrace();
		}
		
		return arrArq.toArray(new String[]{});
		
	}
	
	public static String[] getNomeArquivosPorContextoOscip (String caminho, String idOscip, String CNPJ, String idContexto) {
		
		ArrayList<String> arrArq = new ArrayList<String>();
		Path diretorio = Paths.get(caminho);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(diretorio, "cache_" + idOscip + "_" + CNPJ +  "*_"+idContexto+".txt")){
			  for(Path path : stream){
				 LOGGER.debug("Arquivo cache gerado :" + path.getFileName());
			     arrArq.add(path.getFileName().toString());
			  }	 
		} catch (IOException e) {
			LOGGER.error("Erro em getNomeArquivosPorContextoOscip() :" +e.getMessage());
			e.printStackTrace();
		}
		
		return arrArq.toArray(new String[]{});
		
	}
	
	public static String[] getNomeArquivosPorOscipeContexto (String caminho, String idOscip, String CNPJ, String contexto) {
		
		ArrayList<String> arrArq = new ArrayList<String>();
		Path diretorio = Paths.get(caminho);
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(diretorio, "cache_" + idOscip + "_" + CNPJ + "_" + contexto + ".*")){
			  for(Path path : stream){
				 LOGGER.debug("Arquivo cache gerado :" + path.getFileName());
			     arrArq.add(path.getFileName().toString());
			  }	 
		} catch (IOException e) {
			LOGGER.error("Erro em getNomeArquivosPorOscipeContexto() :" +e.getMessage());
			e.printStackTrace();
		}
			
		return arrArq.toArray(new String[]{});
		
	}
	
	public static Pagina[] getPaginasFromCache(String caminho, String[] nomeArquivos){
		
		ArrayList<Pagina> arrPg = new ArrayList<Pagina>();
		
		for(String nomeArquivo : nomeArquivos){
			
			//formato nome do arquivo
			String[] arrAux = nomeArquivo.split("_");
			Pagina pagina = new Pagina();
			
			if(arrAux.length >=6){
				pagina.setIdOscip(arrAux[1]);
				pagina.setTipo(arrAux[4]);
				String[] arrContextoAux = arrAux[5].split("\\.");
				pagina.setIdContexto(arrContextoAux[0]);
				if(nomeArquivo.contains("_T_")){ //se pagina coletada é texto simples
					pagina.setConteudoTxt(Utils.getTextoFromCache(caminho, nomeArquivo));
					pagina.setUrl(Utils.getUrlFromTexto(pagina.getConteudoTxt()));
				}else{ //se for html
					pagina.setConteudoHtml(Utils.getTextoFromCache(caminho, nomeArquivo));
					pagina.setUrl(Utils.getUrlFromTexto(pagina.getConteudoHtml()));
				}
				pagina.setPath(nomeArquivo);
				
				arrPg.add(pagina);
			}
		}
		
		return arrPg.toArray(new Pagina[]{});
		
	}
	
	public static String getUrlFromTexto (String texto){
		
		int posIni = texto.indexOf("<!--URLINICIO:") + 14;
		int posFim = texto.indexOf("URLFIM-->");
		String result = "";
		if (posIni > -1 && posFim >=-1){
			result = texto.substring(posIni, posFim);
		}
		
		return result;
	}
	
	public static String getTextoFromCache (String caminho, String nome){
		
		byte[] encoded;
		String texto = "";
		
		try {
			encoded = Files.readAllBytes(Paths.get(caminho+nome));
			texto = new String(encoded, Charset.defaultCharset());
					
		} catch (IOException e) {
			LOGGER.error("Erro em getTextoFromCache() :" +e.getMessage());
			e.printStackTrace();
		}
		 
		return texto;

	}
	
	public static void deletePesquisaCache(){
		
		File file = new File( Config.PATH_CACHE_DOCS + Config.CACHE_PESQUISA_ATUAL );
		file.delete();
	 
	}
	
	public static boolean isArrayContemString(List<String> list, String str){
		
		for (String l : list){
			if(l.contains(str)){
				return true;
			}
		}	
		return false;
			
	}
	
	public static boolean isConteudotxtPaginaContemString(Pagina[] pags, String str){
		
		for (Pagina pag: pags){
			if(pag.getConteudoTxt().contains(str)){
				return true;
			}
		}	
		return false;
			
	}
	
	public static boolean isFontesContemUrl(Fonte[] fontes, String url){
		
		for (Fonte fonte : fontes){
			
			if(url.contains(Utils.getDomainName(fonte.getUrl()))){
				return true;
			}
		}	
		return false;
			
	}
	
	public static String getDominioUrl(String url){
		
		String[] str = url.split("/");
		
		if(str.length <= 3){
			return url;
		}else{
			return str[0]+"//"+str[1]+str[2];
		}
		
	}
	
	public static String getDomainName(String url){
	    if(!url.startsWith("http") && !url.startsWith("https")){
	         url = "http://" + url;
	    }        
	    URL netUrl = null;
		try {
			netUrl = new URL(url);
		} catch (MalformedURLException e) {
			LOGGER.error("Erro em getDomainName() :" +e.getMessage());
			e.printStackTrace();
		}
	    String host = netUrl.getHost();
	    if(host.startsWith("www")){
	        host = host.substring("www".length()+1);
	    }
	    return host;
	}
	
	public static String arrayToString (String[] arrstr){
		
		String str = "";
		for (String straux : arrstr){
			str += straux+"\n";
		}
		
		return str;
	}
	
	public static String arrayToStringSemQuebra (String[] arrstr){
		
		String str = "";
		for (String straux : arrstr){
			str += straux+" ";
		}
		
		return str;
	}
	
	public static String arrayToString (String[] arrstr, String delimit){
		
		String str = "";
		for (String straux : arrstr){
			str += straux + delimit;
		}
		
		return str;
	}
	
	public static String getContentType(String adress){
		
		URL url = null;
		URLConnection u = null;
		String type = "";
		
		if(adress.contains(".doc") || adress.contains(".odt")){
			return ("application/doc");
		}else if (adress.contains(".pdf")){
			return ("application/pdf");
		}else{
		
			try {
				url = new URL(adress);
				u = url.openConnection();
			} catch (MalformedURLException e1) {
				LOGGER.error("Erro em getContentType() :" +e1.getMessage());
				e1.printStackTrace();
			} catch (IOException e) {
				LOGGER.error("Erro em getContentType() :" +e.getMessage());
				e.printStackTrace();
			}
			type = u.getHeaderField("Content-Type");
			return type;
		}
	}
	
	public static boolean gravaArquivoDeURL(String stringUrl, String pathLocal) {
		try {
			//Encapsula a URL num objeto java.net.URL
			URL url = new URL(stringUrl);
			
			//Queremos o arquivo local com o mesmo nome descrito na URL
			//Lembrando que o URL.getPath() ira retornar a estrutura 
			//completa de diretorios e voce deve tratar esta String
			//caso nao deseje preservar esta estrutura no seu disco local.
			String nomeArquivoLocal = url.getPath();
			
			//Cria streams de leitura (este metodo ja faz a conexao)...
			InputStream is = url.openStream();
			
			//... e de escrita.
			FileOutputStream fos = new FileOutputStream(pathLocal+nomeArquivoLocal);
			
			//Le e grava byte a byte. Voce pode (e deve) usar buffers para
			//melhor performance (BufferedReader).
			int umByte = 0;
			while ((umByte = is.read()) != -1){
				fos.write(umByte);
			}
			//Nao se esqueca de sempre fechar as streams apos seu uso!
			is.close();
			fos.close();
			
			//apos criar o arquivo fisico, retorna referencia para o mesmo
			//return new File(pathLocal+nomeArquivoLocal);
			return true;
			
		} catch (Exception e) {
			LOGGER.error("Erro em gravaArquivoDeURL() :" +e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean existeArquivoCacheUrl(String cnpj, int contexto){
		
		String pathArq = Config.PATH_CACHE_URLS + "urls_"+cnpj+"_ctx"+contexto+".txt";
		
		File f = new File(pathArq);

		if(f.exists()){
			return true;
		}else{
			return false;
		}
		
	}
	
	public static void loadArquivoCacheUrlBinarios(String cnpj, int numContexto){
		
		String linha = "";
		ArrayList<String> arr = new ArrayList<String>();
		
		if(Utils.existeArquivoCacheUrl(cnpj, numContexto)){
			
			try{
				BufferedReader br = new BufferedReader(new FileReader(Config.PATH_CACHE_URLS + "urls_"+cnpj+"_ctx"+numContexto+".txt"));
				if(br.ready()){
					
					linha = br.readLine();
					
					while(linha !=null){
						
						String[] linhaAux = linha.split("\\|\\|");
						
						//baixo arquivos tipo pdf/odt/doc/docx
						if(linhaAux.length > 1 && !linhaAux[0].contains("html")){
							
							String url = linhaAux[1];
							
							String extensao = url.substring(url.lastIndexOf(".") + 1);
							String nomeArq = Config.PATH_CACHE_URLS + "bin_"+ cnpj + "_" + numContexto + extensao;
							System.out.println("Arquivo "+nomeArq+" a gravar do cache");
							
							FileUtils.copyURLToFile(new URL(url), new File(nomeArq), 10000, 10000);
							
						}	
						
						linha = br.readLine();
					}	
				}
				br.close();
				
			}catch(IOException ioe){
				LOGGER.error("Erro em loadArquivoCacheUrlBinarios() :" +ioe.getMessage());
				ioe.printStackTrace();
			}
		
		}else{ //end if 1
			
			System.out.println("Arquivo <<"+Config.PATH_CACHE_URLS + "urls_"+cnpj+"_ctx"+numContexto+".txt>> não existe e não carregado");
		}	
	}
	
	public static void loadArquivosToBin(Connection conn, Oscip oscip, Contexto[] contextos){
		
		String linha = "";
		ArrayList<String> arr = new ArrayList<String>();
		
		try{
			
			for(Contexto contexto : contextos){
				
				String numContexto = contexto.getIdContexto()+"";
				
				BufferedReader br = new BufferedReader(new FileReader(Config.PATH_CACHE_URLS + "urls_"+oscip.getCNPJ()+"_ctx"+numContexto+".txt"));
				if(br.ready()){
					
					linha = br.readLine();
					int cont = 1;
					
					while(linha !=null){
						
						//se nao for o header ou linha vazia
						if(!linha.equals("") && !linha.contains("CNPJ:")){
						
							String[] arrLinha = linha.split("\\|\\|");
							
							//baixo arquivos tipo pdf/odt/doc/docx
							if(arrLinha.length > 1 && !arrLinha[0].toLowerCase().contains("html")){
								
								String url = arrLinha[1];
								String extensao = url.substring(url.lastIndexOf(".")).toLowerCase();
								
								//gera apenas documentos com extensoes reconhecidas
								if (extensao.contains("pdf") || extensao.contains("xls") || extensao.contains("doc")){
									
									String hash = "";
									String nomeArq = "";
										
									hash = UUID.randomUUID()+"";
									nomeArq = "cache_"+ oscip.getId() + "_" + oscip.getCNPJ() + "_"+ hash + "_" + numContexto + "_" + cont + extensao;

									FileUtils.copyURLToFile(new URL(url), new File(Config.PATH_CACHE_BIN + nomeArq), 10000, 10000);

									LOGGER.debug("Arquivo binario ["+nomeArq+"] gravado no cache");

									//instancia e insere pagina binaria no banco
									Pagina pag = new Pagina();
									pag.setIdContexto(numContexto);
									pag.setIdOscip(oscip.getId()+"");
									pag.setPath(Config.PATH_CACHE_BIN + nomeArq);
									pag.setUrl(url);
									pag.setTipo("B");
									pag.setConteudoHtml(null);
									pag.setConteudoTxt(null);

									//insere pagina binária no banco
									DBConnector.inserePaginaBinaria(conn, pag);

									cont++;
									
								}	
								
							}
						} //end if header
						
						linha = br.readLine();
					}	//end while
						
					br.close();
				
				}else{ //end if 1
					LOGGER.debug("Arquivo  binario ["+Config.PATH_CACHE_URLS + "] urls_"+oscip.getCNPJ()+"_ctx"+numContexto+".txt>> não existe e não carregado");
				}//end if
			
			}//end for
			
		}catch(IOException ioe){
			LOGGER.error("Erro ao gerar arquivo binario cache (loadArquivosToBin) :" +ioe.getMessage());
		}
		
	}
	
	public static String[] loadUrlsCandidatasPorContexto(String cnpj, int numContexto, String tipoDoc){
		
		String linha = "";
		ArrayList<String> arr = new ArrayList<String>();
		
		if(Utils.existeArquivoCacheUrl(cnpj, numContexto)){
		
			try{
				BufferedReader br = new BufferedReader(new FileReader(Config.PATH_CACHE_URLS + "urls_"+cnpj+"_ctx"+numContexto+".txt"));
				if(br.ready()){
					
					linha = br.readLine();
					
					while(linha !=null){
						
						String[] linhaAux = linha.split("\\|\\|");
						
						if(linhaAux.length > 1 && linhaAux[0].contains(tipoDoc)){
							if(linha.contains("http://")){
								arr.add(linhaAux[1]);
							}
						}	
						
						linha = br.readLine();
					}	
				}
				br.close();
			}catch(IOException ioe){
				LOGGER.error("Erro ao carregar URls candidatas (loadUrlsCandidatasPorContexto) :" +ioe.getMessage());
				ioe.printStackTrace();
			}
		
		}else{
			LOGGER.debug("Arquivo <<"+Config.PATH_CACHE_URLS + "urls_"+cnpj+"_ctx"+numContexto+".txt>> não existe e não carregado");
		}
		
		return (String[])arr.toArray(new String[]{});
	}
	
	public static void removerArquivosCache(String caminho){
		
		Arrays.stream(new File(caminho).listFiles()).forEach(File::delete);
		LOGGER.debug("Arquivos cache do caminho ["+ caminho + "] removidos");
			
	}
	
	public static boolean isDominioConfiavel (String url, String oscipUrl, Fonte[] fontes){
		
		//se contém dominio governamental ou institucional (.gov.br / .org)
		if (url !=null && (url.contains(".gov.br") || url.contains(".org"))){
			return true;
		}
		
		//se dominio faz parte do site oficial da OSCIP
		if(oscipUrl != null && oscipUrl.contains(Utils.getDomainName(url))){
			return true;
		}
		
		//se dominio está registrado em base de fontes confiáveis
		if (url !=null && Utils.isFontesContemUrl(fontes, url)){
			return true;
		}
		
		return false;
	}
	
	public static boolean isTextoContemOSCIP (Oscip oscip, String texto){
		
		String cnpj = oscip.getCNPJ();
		String cnpjform = cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + 
				          cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
				          
		//para ocorrencias exatas de nome, cnpj, sigla
		if(texto.contains(oscip.getNome().toLowerCase())){
			return true;
		}else if(texto.contains(oscip.getNome2().toLowerCase())){
			return true;	
		}else if(texto.contains(oscip.getCNPJ())){
			return true;
		}else if (texto.contains(oscip.getSigla().toLowerCase())){
			return true;
		}else if (texto.contains("oscip")){
			return true;
		}else if (texto.contains("ongs")){
			return true;
		//para cnpj formatado
		}else if (texto.contains(cnpjform)){
			return true;
		//para ocorrencia com o nome sem acento e sem espaco	
		}else if (texto.contains(oscip.getNome().toLowerCase().replaceAll(" ", ""))){
			return true;
	    }else if (texto.contains(oscip.getNome().toLowerCase().replaceAll(" ", "-"))){
	    	return true;
		}//para ocorrencia com o nome com acento e sem espaco	
		else if (texto.contains(oscip.getNome2().toLowerCase().replaceAll(" ", ""))){
			return true;
		}else if (texto.contains(oscip.getNome2().toLowerCase().replaceAll(" ", "-"))){
			return true;
		}	

		return false;
	}
	
	public static boolean contemLixo (String sentence){
		
		String[] trash = {"<span", "<script", "function", "()", "<", ">", "&&", "||", ");",
						"{", "}", "];", "javascript", "document.", "boolean", ".js", ".css", 
						".find(", "if(", "while(", "if (", "while (", "for(", "for (", "//", "[cdata", "window.",
						"else(", "else (",};
		
		for (String t : trash){
			if(sentence.contains(t)){
				return true;
			}
		}
			
		return false;
		
	}
	
	public static String[] JsoupGetLinks (String dochtml){
		
		ArrayList<String> arr = new ArrayList<String>();
		
		try {
			
			File input = new File(dochtml);
	        Document doc = Jsoup.parse(input, "UTF-8");			
			
	        Elements links = doc.select("a[href]");
	        
	        for (Element link : links) {
	        	arr.add(link.attr("href")+"||"+link.text());
	            System.out.println(link.attr("href")+ " -  " + link.text());
	        }
	        
		}catch (IOException e) {
			LOGGER.error("Erro em JsoupGetLinks() :" +e.getMessage());
			e.printStackTrace();
		}
		
        return (String[])arr.toArray(new String[]{});
		
	}
	
	public static String[] JsoupGetElementsFromTable (String dochtml){
		
		ArrayList<String> arr = new ArrayList<String>();
		
		try {	
			File input = new File(dochtml);
	        Document doc = Jsoup.parse(input, "UTF-8");	
	        
		     //System.out.println(doc);
		     Elements tableElements = doc.select("table");
		     Elements tableHeaderEles = tableElements.select("thead tr th");
		     
		     //buscando headers
		     System.out.println("headers");
		     for (int i = 0; i < tableHeaderEles.size(); i++) {
		        System.out.println(tableHeaderEles.get(i).text());
		     }
		     //System.out.println();
	
		     Elements tableRowElements = tableElements.select(":not(thead) tr");
	
		     for (int i = 0; i < tableRowElements.size(); i++) {
		    	 
		        Element row = tableRowElements.get(i);
		        
		        //buscando conteudo das linhas
		        System.out.println("----begin row "+i+ "---");
		        
		        Elements rowItems = row.select("td");
		        
		        for (int j = 0; j < rowItems.size(); j++) {
		           System.out.println(rowItems.get(j).text());
		        }
		        
		        System.out.println("----end row "+i+ "---");
		     }
		
		}catch (IOException e) {
			LOGGER.error("Erro em JsoupGetElementsFromTable() :" +e.getMessage());
			e.printStackTrace();
		}
		
		return (String[])arr.toArray(new String[]{});
		
	}

	
	public static void JsoupGetET2 (String dochtml){
		
		try {   
		
				String url = "http://www.htmlcodetutorial.com/tables/_THEAD.html";
		        String fileName = "table.csv";
		        FileWriter writer = new FileWriter(fileName);
		        
		        //Document doc = Jsoup.connect(url).get();
		        File input = new File(dochtml);
		        Document doc = Jsoup.parse(input, "UTF-8");	
		        //Document doc = Jsoup.parse(html)
		        
		        Element tableElement = doc.select("table").first();

		        Elements tableHeaderEles = tableElement.select("thead tr th");
		        System.out.println("headers");
		        for (int i = 0; i < tableHeaderEles.size(); i++) {
		            System.out.println(tableHeaderEles.get(i).text());
		            writer.append(tableHeaderEles.get(i).text());

		            if(i != tableHeaderEles.size() -1){             
		                writer.append(',');
		            }
		        }
		        writer.append('\n');

		        Elements tableRowElements = tableElement.select(":not(thead) tr");

		        for (int i = 0; i < tableRowElements.size(); i++) {
		            Element row = tableRowElements.get(i);
		            System.out.println("row");
		            Elements rowItems = row.select("td");
		            for (int j = 0; j < rowItems.size(); j++) {
		                System.out.println(rowItems.get(j).text());
		                writer.append(rowItems.get(j).text());

		                if(j != rowItems.size() -1){
		                    writer.append(',');
		                }
		            }
		            writer.append('\n');
		        }

		        writer.close();
	          
			}catch (IOException e) {
				LOGGER.error("Erro em JsoupGetET2() :" +e.getMessage());
				e.printStackTrace();
			}
		
	}
	
	public static java.util.Date getDataAtual(){
		
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = sdf.format(dt);
		
		java.util.Date myDate = null;
		
		try {
			
			myDate = sdf.parse(dateString);

		} catch (ParseException e) {
			LOGGER.error("Erro em getDataAtual() :" +e.getMessage());
			e.printStackTrace();
		}
		
		return myDate;
		
	}
	
	public static String getSiglaTipoContexto (Pagina pg){
		
		String tipo = "";
		int idContexto = Integer.parseInt(pg.getIdContexto());
		
		/*
		 * Contexto 1  = OBJ = Objetivos
		 * Contexto 2  = SRV = Serviços
		 * Contexto 3  = EML = Email
		 * Contexto 4  = TLF = Telefone
		 * Contexto 5  = END = Endereço
		 * Contexto 6  = MBO = Membro
		 * Contexto 7  = PRJ = Projetos
		 * Contexto 8  = STF = Nivel de Satisfação
		 * Contexto 9  = PRF = Perfil
		 * Contexto 10 = PUA = Publico Alvo
		 * Contexto 11 = INF = Infra
		 * Contexto 12 = EDT = Edital
		 * Contexto 13 = DST = Distribuição
		 * Contexto 14 = PUB = Notícias, Boletins, Blogs e Publicações
		 * Contexto 15 = OBR = Obras e Exposições 
		 * Contexto 16 = AJU = Como ajudar (Patrocinadores/Doações e Voluntariado)
		 * Contexto 17 = SIT = Sites sobre cultura
		 * 
         */
		
		switch (idContexto) {
			case 1:
				tipo = "OBJ"; //Objetivo da OSCIP
				break;
			case 2:
				tipo = "SRV"; //Servicos Prestados
				break;
			case 3:
				tipo = "EML"; //Email
				break;
			case 4:
				tipo = "TLF"; //Telefone
				break;
			case 5:
				tipo = "END"; //Endereço
				break;
			case 6:
				tipo = "MBO"; // Membros
				break;
			case 7:
				tipo = "PRJ"; //Projetos
				break;
			case 8:
				tipo = "STF"; //Nivel de satisfação
				break;
			case 9:
				tipo = "PRF"; //Perfil
				break;
			case 10:
				tipo = "PUA"; //Publico Alvo
				break;
			case 11:
				tipo = "INF"; //Infra Estrutura (Sub: Texto = Titulo)
				break;
			case 12:
				tipo = "EDT"; //Editais
				break;
			case 13:
				tipo = "DST"; // % de distribuição de renda
				break;
			case 14:
				tipo = "PUB"; //Publicações (Sub: Noticia/Eventos/Boletim/Blob e Publicacoes)
				break;
			case 15:
				tipo = "OBR"; //Obras e Exposicoes
				break;
			case 16:
				tipo = "AJU"; //Como ajudar (Sub: Patrocinadores/Doações e Voluntariado)
				break;
			case 17:
				tipo = "SIT"; //Sites de Cultura
				break;
			default:
				tipo = "Não Definido";
		}
		
		return tipo;
		
	}
}	