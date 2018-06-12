package br.uniriotec.oscip.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.crawler.StartCrawler;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.db.MySQLConn;
import br.uniriotec.oscip.mining.MininigMonitor;
import br.uniriotec.oscip.triples.TripleMonitor;

/**
 * Hello world!
 *
 */
public class App 

{
	final static Logger LOGGER = Logger.getLogger(App.class);
	
	public static void main( String[] args )
    {
       
    	try {
    		
    		 //chamando APP de Crawler (Coletar URLs e documentos)
			//StartCrawler.main(new String[]{});
			
			//chamando APP de mining (Analise sintatica dos termos e paginas candidatas)
			//MininigMonitor.main(new String[]{});
    		
			//chamando APP para gerando de triplas semânticas (Analise semantica e descoberta)
			TripleMonitor.main(new String[]{});			
			
			//TODO: Criar integração com API DOU (br.gov.in.ws) quando estiver operacional
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }
}
