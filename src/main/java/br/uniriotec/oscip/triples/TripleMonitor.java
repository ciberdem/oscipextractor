package br.uniriotec.oscip.triples;

import java.sql.Connection;
import java.util.ArrayList;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.beans.Pagina;
import br.uniriotec.oscip.beans.Tripla;
import br.uniriotec.oscip.db.DBConnector;
import br.uniriotec.oscip.db.MySQLConn;

public class TripleMonitor {
	
	/*
	 *  ETAPA para comparar as triplas de referência carregadas no banco com os termos candidatos 
	 *  na tabela de descoberta para inserir na tabela de metadados do portal
	 *  TODO: O processo de carregamento de triplas foi realizado pela ferramenta
	 *        Linguakit (https://github.com/citiususc/Linguakit) através de script em pearl
	 *        de verbetes coletado do CETENFOLHA (https://www.linguateca.pt/CETENFolha/)
	 *        Será necessário no futuro criar uma integração automática das triplas geradas pelo
	 *        script automaticamente no banco (tabela oscmeta e tabela de projetos)
	 *  
	 */
	
	public static void main( String[] args )
    {
       
    	try {
    		
    		//TODO: CRIAR PASSO PARA GERAR TRIPLAS AUTOMATICAMENTE PRO BANCO VIA LINGUAKIT
    		
    		//inicia conexão com o banco
    		Connection conn = MySQLConn.getConnection();
    		
    		//coletando as oscips
    		Oscip[] oscips = DBConnector.loadAllOscips(conn); 
    		
    		//para cada OSCIP
    		for(Oscip oscip : oscips){
    			
    			Contexto[] ctxs = DBConnector.loadContextos(conn);
    			
    			//para cada contexto de OSCIP
    			for(Contexto ctx : ctxs){
    				
		    		//PASSO 1 - COLETA AS TRIPLAS DE REFERÊNCIA (TR) DO BANCO POR CONTEXTO DE OSCIP
		    		Tripla[] triplasRef = DBConnector.loadTriplasPorOscipContexto(conn, ctx.getIdContexto());
		    		
		    		//PASSO 2 - COLETA OS DADOS DAS TABELAS CANDIDATAS (TABELA DESCOBERTA)
		    		//TODO: Coletar apenas os dados de descoberta que ainda não foram comparados com a tripla
		    		Pagina[] descs = DBConnector.loadDescobertaOscipContexto(conn, oscip, ctx);
		    		
		    		//PASSO 3 - COMPARA TRs com TEXTO das DESCOBERTAS E INSERE NA TABELA DE METADADOS DO PORTAL
		    		//Obs:só vai inserir se pedaço do texto não tiver sido encontrada antes
		    		DBConnector.insereDescobertaMetadados(conn, triplasRef, descs, ctx);	
    			}	
    		} 
    		
    	}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
}
