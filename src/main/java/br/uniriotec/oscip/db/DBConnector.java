package br.uniriotec.oscip.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.uniriotec.oscip.beans.Contexto;
import br.uniriotec.oscip.beans.Convenio;
import br.uniriotec.oscip.beans.CronogramaConv;
import br.uniriotec.oscip.beans.Descoberta;
import br.uniriotec.oscip.beans.EmpenhoConv;
import br.uniriotec.oscip.beans.Fonte;
import br.uniriotec.oscip.beans.Oscip;
import br.uniriotec.oscip.beans.Pagina;
import br.uniriotec.oscip.beans.Tripla;
import br.uniriotec.oscip.config.Config;
import br.uniriotec.oscip.triples.TripleProc;
import br.uniriotec.oscip.util.Utils;

public class DBConnector {
	
	private static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
	
	
	public static void truncateTabelas (Connection conn){
		
		try{
			
			//descoberta
			String sql = "truncate table descoberta";
			Statement stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//pagina
			sql = "truncate table pagina";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//empenhos de convenios
			sql = "truncate table convemp";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//cronograma de convenios
			sql = "truncate table convcrono";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//convenios/projetos
			sql = "truncate table convproj";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//relatorio
			sql = "truncate table relatorio";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//debug
			sql = "truncate table debug";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			stmt.close();	
			
			LOGGER.info("Tabelas truncadas (descoberta/pagina/convemp/convcrono/convproj/relatorio/debug)");
			
		}catch(Exception e ){
			LOGGER.error("Erro ao truncar pagina/descoberta "+ e.getMessage());
		}
	}
	
	public static void truncateTabelasConvenios (Connection conn){
		
		try{
				
			String sql = "";
			Statement stmt=conn.createStatement();  
			
			//empenhos de convenios
			sql = "truncate table convemp";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//cronograma de convenios
			sql = "truncate table convcrono";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			//convenios/projetos
			sql = "truncate table convproj";
			stmt=conn.createStatement();  
			stmt.executeUpdate(sql);
			
			stmt.close();	
			
			LOGGER.info("Tabelas de convênio truncadas (convemp/convcrono/convproj)");
			
		}catch(Exception e ){	
			LOGGER.error("Erro ao truncar Tabelas de convênio"+ e.getMessage());
		}
	}
	
   public static void truncateDescoberta (Connection conn){
		
		try{
			
			String sql = "truncate table descoberta";
			Statement stmt=conn.createStatement();  
			
			stmt.executeUpdate(sql);
			stmt.close();	
			
			LOGGER.info("Tabela de descoberta truncada");
			
		}catch(Exception e ){
			
			LOGGER.error("Erro ao truncar pagina/descoberta "+ e.getMessage());

		}
	}
	
	public static void inserePaginasTxtWeb (Connection conn, Oscip oscip){
		
		try{
			
			String[] nomeArquivos = Utils.getNomeArquivosPorOscip(Config.PATH_CACHE_DOCS, oscip.getId()+"", oscip.getCNPJ());
			Pagina[] paginas = Utils.getPaginasFromCache(Config.PATH_CACHE_DOCS, nomeArquivos);
			
			int cont = 0;
			String htmlAux = "";
			String txtAux = "";
			
			List<String> pagIns = new ArrayList<String>();
			
			for(Pagina pagina : paginas){ 

				if(pagina.getPath().contains("_T_")){
					txtAux = pagina.getConteudoTxt().toLowerCase();
				}else{
					htmlAux = pagina.getConteudoHtml().toLowerCase();
				}
	
				String strPgContexto = pagina.getIdContexto() + "||" + pagina.getUrl();	
				if(!Utils.isArrayContemString(pagIns, strPgContexto)){ 
	
					pagIns.add(strPgContexto);
	
					LOGGER.info("Inserindo arquivo texto/web no banco ---- "+ pagina.getPath());
	
					//Statement stmt=conn.createStatement();  
					String sql = "insert into pagina (fkcontexto, fkoscip, tipo, status, url, "
							+ "texto, termos, path) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?)";
	
					PreparedStatement pstmt = conn.prepareStatement(sql);
	
					pstmt.setInt(1, new Integer(pagina.getIdContexto()));
					pstmt.setInt(2, new Integer(pagina.getIdOscip()));
					pstmt.setString(3, pagina.getTipo());
					pstmt.setString(4, "E"); //E - Extraida, C - Candidata, A - Aceita
					pstmt.setString(5, pagina.getUrl());
					//pstmt.setBlob(6, new SerialBlob(htmlAux.getBytes()));
					pstmt.setBlob(6, new SerialBlob(txtAux.getBytes()));
					pstmt.setString(7 , null);
					pstmt.setString(8 , pagina.getPath());
	
					pstmt.executeUpdate();	
	
					pstmt.close();	
				}else{
					LOGGER.info("Já inseriu contexto/url no banco, não inserir ---- "+ strPgContexto);
				}
	
				htmlAux = "";
				txtAux ="";
				cont=0;
			}	
	
		}catch(Exception e ){
			LOGGER.error("Erro inserePaginasTxtWeb "+e.getMessage());
		}
	}
	
	public static void inserePaginaBinaria (Connection conn, Pagina pagina){
		
		try{
					
			LOGGER.info("Inserindo pagina binaria no banco -- "+ pagina.getPath());

			String sql = "insert into pagina (fkcontexto, fkoscip, tipo, status, url, "
					+ "html, texto, termos, path) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, new Integer(pagina.getIdContexto()));
			pstmt.setInt(2, new Integer(pagina.getIdOscip()));
			pstmt.setString(3, pagina.getTipo());
			pstmt.setString(4, "E"); //E - Extraida, C - Candidata, A - Aceita
			pstmt.setString(5, pagina.getUrl());
			pstmt.setString(6, null);
			pstmt.setString(7, null);
			pstmt.setString(8 , null);
			pstmt.setString(9 , pagina.getPath());

			pstmt.executeUpdate();	
			pstmt.close();					
			
		}catch(Exception e ){
			LOGGER.error("Erro em inserePaginaBinaria "+e.getMessage());
		}
	}
	
	public static Oscip[] loadAllOscips (Connection conn){
		
		ArrayList<Oscip> arrOscips = new ArrayList<Oscip>(){};
		
		String sql = "select * from oscip " +
				     "order by idoscip ";
		
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				Oscip op = new Oscip();
				
				op.setId(rs.getInt("idoscip"));
				op.setCNPJ(rs.getString("cnpj"));
				op.setNome(rs.getString("nome"));
				op.setNome2(rs.getString("nome2"));
				op.setSigla(rs.getString("sigla"));
				op.setWebsite(rs.getString("url"));
				op.setEndereco(rs.getString("endereco"));
				op.setCep(rs.getString("cep"));
				op.setCidade(rs.getString("cidade"));
				op.setUf(rs.getString("uf"));
				op.setTelefone(rs.getString("telefone"));
				op.setDtQualificacao(rs.getString("dataQualif"));
				
				arrOscips.add(op);

			}
			
			stmt.close();
			rs.close();
			
		} catch (SQLException e) {
			LOGGER.error("Erro em loadAllOscips() "+e.getMessage());
			e.printStackTrace();
		}  
		
		return (Oscip[]) arrOscips.toArray(new Oscip[]{});	
	}
	
	public static Oscip[] loadOscipsSemDescoberta (Connection conn){
		
		ArrayList<Oscip> arrOscips = new ArrayList<Oscip>(){};
		
		String sql = "select * from oscip where idoscip not in "+ 
					 "(select distinct p.fkoscip "+
					 "from pagina p, descoberta d "+
					 "where p.idpagina = d.fkpagina)";
		
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				Oscip op = new Oscip();
				
				op.setId(rs.getInt("idoscip"));
				op.setCNPJ(rs.getString("cnpj"));
				op.setNome(rs.getString("nome"));
				op.setNome2(rs.getString("nome2"));
				op.setSigla(rs.getString("sigla"));
				op.setWebsite(rs.getString("url"));
				op.setEndereco(rs.getString("endereco"));
				op.setCep(rs.getString("cep"));
				op.setCidade(rs.getString("cidade"));
				op.setUf(rs.getString("uf"));
				op.setTelefone(rs.getString("telefone"));
				op.setDtQualificacao(rs.getString("dataQualif"));
				
				arrOscips.add(op);
			}
			
			stmt.close();
			rs.close();
			
		} catch (SQLException e) {
			LOGGER.error("Erro em loadOscipsSemDescoberta() "+e.getMessage());
			e.printStackTrace();
		}  
		
		return (Oscip[]) arrOscips.toArray(new Oscip[]{});	
	}
	
	public static Oscip[] loadNewOscips (Connection conn){
		
		ArrayList<Oscip> arrOscips = new ArrayList<Oscip>(){};
		
		String sql = "select * from oscip o " +
				     "where not exists (select idpagina from pagina p where p.fkoscip = o.idoscip) "+
				     "order by o.idoscip LIMIT "+Config.QTD_MAX_OSCIP;
		
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				Oscip op = new Oscip();
				
				op.setId(rs.getInt("idoscip"));
				op.setCNPJ(rs.getString("cnpj"));
				op.setNome(rs.getString("nome"));
				op.setNome2(rs.getString("nome2"));
				op.setSigla(rs.getString("sigla"));
				op.setWebsite(rs.getString("url"));
				op.setEndereco(rs.getString("endereco"));
				op.setCep(rs.getString("cep"));
				op.setCidade(rs.getString("cidade"));
				op.setUf(rs.getString("uf"));
				op.setTelefone(rs.getString("telefone"));
				op.setDtQualificacao(rs.getString("dataQualif"));
				
				arrOscips.add(op);
			}
			
			stmt.close();
			rs.close();
			
		} catch (SQLException e) {
			LOGGER.error("Erro em loadNewOscips() "+e.getMessage());
			e.printStackTrace();
		}  
		
		return (Oscip[]) arrOscips.toArray(new Oscip[]{});	
	}
	
	public static Contexto[] loadContextos (Connection conn){
		
		ArrayList<Contexto> arrContextos = new ArrayList<Contexto>(){};
		
		String sql = "select * from contexto order by idcontexto";
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				Contexto ctx = new Contexto();
				
				ctx.setIdContexto(rs.getInt("idcontexto"));
				ctx.setSinonimos(rs.getString("sinonimos"));
				ctx.setTpContexto(rs.getString("tipo"));
				
				arrContextos.add(ctx);
			}
			
			stmt.close();
			rs.close();
			
		} catch (SQLException e) {
			LOGGER.error("Erro em loadContextos() "+e.getMessage());
			e.printStackTrace();
		}  
		
		return (Contexto[]) arrContextos.toArray(new Contexto[]{});
		
	}
	
	public static void relatorioTotalOSCIPs (Connection conn){
		
		int totalOscips = 0;
		int totalPaginas = 0;
		String oscipAnt ="";
		
		try{
			
			String sql = "SELECT p.fkoscip, o.nome, p.tipo, count(p.idpagina) as total "+
						 "from pagina p, oscip o "+
						 "where p.fkoscip = o.idoscip "+
						 "group by fkoscip, o.nome, p.tipo";
			
			Statement stmt=conn.createStatement();   
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				String tipo = rs.getString("tipo").equals("B")?"BINARIO":"TEXTO";
				String oscipAtual = rs.getString("fkoscip");
				
				LOGGER.info("OSCIP: "+ rs.getString("nome") +" / TIPO DA PAGINA: "+ tipo + " / TOTAL PAGINAS: "+ rs.getString("total"));
				
				if(!oscipAnt.equals(oscipAtual)){
					totalOscips++;
				}
				
				oscipAnt = oscipAtual;
				totalPaginas += rs.getInt("total");
			}
			
			LOGGER.info("TOTAL DE OSCIPs PESQUISADAS: " +totalOscips);  
			LOGGER.info("TOTAL DE PAGINAS COLETADAS: " +totalPaginas); 

			stmt.close();	
			
		}catch(Exception e ){
			LOGGER.error("Erro relatorioTotalOSCIPs "+ e.getMessage());
		}
		
	}
	
	public static int getTotPaginasPorOSCIPContexto (Connection conn, long idOscip, int idContexto){
		
		int total = 0;
		
		try{
				
			String sql =   "SELECT COUNT(*) as total FROM oscip.pagina WHERE fkcontexto = "+idContexto+" "
					     + "AND fkoscip = "+idOscip;
			
			Statement stmt=conn.createStatement();  
			ResultSet rs = stmt.executeQuery(sql);

			if(rs.next()){	
				total = rs.getInt("total");
			}
			
			stmt.close();
			rs.close();
		
		}catch(Exception e ){
			LOGGER.error("Erro getTotPaginasPorOSCIPContexto "+ e.getMessage());
		}
		
		return total;
		
	}
	
	public static void relatorioTotPaginasPorOSCIP (Connection conn, int idOscip){
		
		try{
			
			for(int i=1; i <= 7 ; i++){
				
				String sql = "SELECT COUNT(*) as total FROM oscip.pagina WHERE FKCONTEXTO = "+i+" AND FKOSCIP = "+idOscip;
				
				Statement stmt=conn.createStatement();  
				ResultSet rs = stmt.executeQuery(sql);
				
				while(rs.next()){
					
					LOGGER.info("OSCIP: "+ idOscip +" / TOTAL CONTEXTO: "+ i + " / TOTAL COLETAS : "+ rs.getString("total"));
				}
				
				stmt.close();
				rs.close();
			}	
			
		}catch(Exception e ){
			LOGGER.error("Erro relatorioTotPaginasPorOSCIP "+ e.getMessage());
		}
		
	}
	
	public static int getTotDescobertasOSCIPContexto (Connection conn, long idOscip, int contexto ){
		
		int total = 0;
		
		try{
			
			String sql = "select count(*) as total from " +
					     "(SELECT distinct p.url, d.sinonimo, d.texto, c.tipo "+
						 "from descoberta d, pagina p, oscip o, contexto c "+
					     "where d.fkpagina = p.idpagina "+ 
					     "and p.fkoscip = o.idoscip "+ 
						 "and p.fkcontexto= c.idcontexto "+ 
						 "and p.fkoscip = "+ idOscip +
						 " and p.fkcontexto = " +contexto+") as tabtotal";
			
			Statement stmt=conn.createStatement();  
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				total = rs.getInt("total");
			}
			
			stmt.close();
			rs.close();
			
		}catch(Exception e ){			
			LOGGER.error("Erro relatorioTotDescobertasOSCIPContexto "+ e.getMessage());
		}
		
		return total;
		
	}
	
	public static void relatorioTotDescobertasPorOSCIP (Connection conn, int idOscip){
		
		try{
			
			for(int i=1; i <= 7 ; i++){
				
				String sql = "SELECT distinct p.url, d.sinonimo, d.texto, c.tipo "+
							 "from descoberta d, pagina p, oscip o, contexto c "+
						     "where d.fkpagina = p.idpagina "+ 
						     "and p.fkoscip = o.idoscip "+ 
							 "and p.fkcontexto= c.idcontexto "+ 
							 "and p.fkoscip = "+ idOscip +
							 " and p.fkcontexto = " +i;
				
				Statement stmt=conn.createStatement();  
				ResultSet rs = stmt.executeQuery(sql);
				int cont = 0;
				
				rs.last();
				cont = rs.getRow();
					
				LOGGER.info("OSCIP: "+ idOscip +" / TOTAL CONTEXTO: "+ i + " / TOTAL DESCOBERTA : "+ cont);
				
				stmt.close();
				rs.close();
				
			}	
			
		}catch(Exception e ){			
			LOGGER.error("Erro relatorioTotPaginasPorOSCIP "+ e.getMessage());
		}
		
	}
	
	public static void updatePaginasTextoCandidata (Connection conn, Oscip oscip, Contexto contexto){
		
		try{
			
			String[] sinonimos = contexto.getSinonimos().split(";");
			int idContexto = contexto.getIdContexto();
			
			String sql = "update pagina set status ='C' "
					+    "where tipo = 'T' "
					+ 	 "and fkcontexto = "+idContexto+" "
					+ 	 "and fkoscip = "+oscip.getId()+" "
					+    "and status = 'E' and ( ";
			
			for (int i = 0;i < sinonimos.length;i++){
				
				if(i == sinonimos.length -1){
					sql += "lower(texto) like '%"+sinonimos[i]+"%') ";
				}else{
					sql += "lower(texto) like '%"+sinonimos[i]+"%' or ";
				}
			}
			
			Statement st= conn.createStatement();
			int rows = st.executeUpdate(sql);
			
			LOGGER.info("updatePaginaTextoCandidata sql =  "+sql);
			
			st.close();	
			
			LOGGER.info("updatePaginaTextoCandidata realizado ["+rows+"] paginas candidatas para contexto[ "+contexto.getTpContexto()+"]");
			
		}catch(Exception e ){			
			LOGGER.error("Erro updatePaginaTextoCandidata "+ e.getMessage());
		}
	}
	
	public static Pagina[] loadPaginasTextoCandidatas (Connection conn, Oscip oscip, Contexto contexto){
			
		ArrayList<Pagina> arrPag = new ArrayList<Pagina>();
		
		try{
			
			//coletando paginas candidatas 
			String sql = "select * from pagina "
					+    "where tipo = 'T' "
					+ 	 "and fkcontexto = "+contexto.getIdContexto()+" "
					+ 	 "and fkoscip = "+oscip.getId()+" "
					+    "and status = 'C' order by idpagina ";
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			int i= 0;
			while (rs.next()){
				
				Pagina pg = new Pagina();
				pg.setIdPagina(rs.getLong("idpagina"));
				pg.setIdOscip(rs.getString("fkoscip"));
				pg.setIdContexto(rs.getString("fkcontexto"));
				pg.setTipo(rs.getString("tipo"));
				pg.setStatus(rs.getString("status"));
				pg.setUrl(rs.getString("url"));
				pg.setConteudoHtml(rs.getString("html"));
				pg.setConteudoTxt(rs.getString("texto"));
				pg.setPath(rs.getString("path"));
				
				arrPag.add(pg);
				
				i++;
			}
			
			rs.close();
			st.close();	
			
		}catch(Exception e ){
			LOGGER.error("Erro loadPaginasTextoCandidatas "+ e.getMessage());
		}
		
		 return (Pagina[])arrPag.toArray(new Pagina[]{});
	}
	
	public static Pagina loadPagina (Connection conn, int idPagina){
		
		Pagina pg = new Pagina();
		
		try{
			
			String sql = "select * from pagina "
					+    "where idpagina = "+idPagina;
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			if (rs.next()){
				
				pg.setIdPagina(rs.getLong("idpagina"));
				pg.setIdOscip(rs.getString("fkoscip"));
				pg.setIdContexto(rs.getString("fkcontexto"));
				pg.setTipo(rs.getString("tipo"));
				pg.setStatus(rs.getString("status"));
				pg.setUrl(rs.getString("url"));
				pg.setConteudoHtml(rs.getString("html"));
				pg.setConteudoTxt(rs.getString("texto"));
				pg.setPath(rs.getString("path"));
				
			}
			
			rs.close();
			st.close();	
			
		}catch(Exception e ){
			LOGGER.error("Erro loadPagina "+ e.getMessage());
		}
		
		 return pg;
	}
	
	public static String[] loadEstruturasMorfologicas (Connection conn, int numContexto){
		
		ArrayList<String> estMorf = new ArrayList<String>();
		
		try{
			
			String sql = "select estrutura from estmorf "
					+ 	 "where fkcontexto = "+numContexto+" ";
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()){				
				estMorf.add(rs.getString("estrutura"));
			}
			
			rs.close();
			st.close();	
			
			LOGGER.info("loadEstruturasMorfologicas ara o idcontexto "+numContexto);
			
		}catch(Exception e ){	
			LOGGER.error("Erro loadEstruturasMorfologicas "+ e.getMessage());
		}
		
		 return (String[])estMorf.toArray(new String[]{});
	}
	
	public static void insereDescoberta (Connection conn, Descoberta dsc){
		
		try{
			
			String sql = "insert into descoberta (fkpagina, tipo, "
					+    "sinonimo, texto, descoberta, posini, posfim, dtproc) "
					+ 	 "values (?, ?, ?, ?, ?, ?, ?, now())";
			
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dsc.getIdPagina());
			pstmt.setString(2, dsc.getTpDescoberta());
			pstmt.setString(3, dsc.getSinonimo());
			pstmt.setBlob(4, new SerialBlob(dsc.getTexto().getBytes()));
			pstmt.setString(5, dsc.getDescoberta());
			pstmt.setInt(6, dsc.getPosIniKey());
			pstmt.setInt(7, dsc.getPosFimKey());
			
			pstmt.executeUpdate();
			
			System.out.println("sql =  "+ sql);			
			
			pstmt.close();	
			
			LOGGER.info("insereDescoberta realizado");
			
		}catch(Exception e ){
			LOGGER.error("Erro insereDescoberta "+ e.getMessage());
		}
	}
	
	public static String JsoupGetLinks (String dochtml){
		
        Document doc = Jsoup.parse(dochtml);
        Elements links = doc.select("a[href]");
        
        for (Element link : links) {
            System.out.println(""+  link.attr("abs:href") + "(" + link.text() + ")");
        } 
        
		return "";		
	}
	
	public static Fonte[] loadFontesConfiaveis (Connection conn){
		
		ArrayList<Fonte> fontes = new ArrayList<Fonte>();
		
		try{
			
			//coletando paginas candidatas 
			String sql = "SELECT * FROM fonte ";
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			while(rs.next())	{
				
				Fonte fonte = new Fonte();
				fonte.setDescricao(rs.getString("desc"));
				fonte.setUrl(rs.getString("url"));
				fonte.setDominio(rs.getString("dominio"));
				
				fontes.add(fonte);
			}
			
			rs.close();
			st.close();	
			
			
		}catch(Exception e ){
			
			LOGGER.error("Erro loadFontesConfiaveis "+ e.getMessage());
		}
		
		return (Fonte[])fontes.toArray(new Fonte[]{});
	}
	
	public static Tripla[] loadTriplasPorOscipContexto (Connection conn, int idContexto){
		
		ArrayList<Tripla> triplas = new ArrayList<Tripla>();
		
		try{
			
			//coletando paginas candidatas 
			String sql = "SELECT * FROM tripla "
					   + "where idcontexto =  "+idContexto;
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			while(rs.next())	{
				
				Tripla tripla = new Tripla();
				tripla.setIdContexto(idContexto);
				tripla.setEnt1(rs.getString("ent1"));
				tripla.setRel(rs.getString("rel"));
				tripla.setEnt2(rs.getString("ent2"));
				
				triplas.add(tripla);
			}
			
			rs.close();
			st.close();	
			
			
		}catch(Exception e ){			
			LOGGER.error("Erro loadTriplasPorContexto "+ e.getMessage());
		}
		
		return (Tripla[])triplas.toArray(new Tripla[]{});
		
	}
	
	public static void insereConvenio (Connection conn, Convenio conv){
		
		try{
			
			String sql = "insert into convproj (idconv, idoscip, "
					+    "tipo, status, codproposta, descricao, orgao, modalidade, "
					+    "endereco, cep, cidade, uf, valorglob, valorep, valorcont, dtini, dtfim) "
					+ 	 "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?)";
			
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, conv.getId());
			pstmt.setLong(2, conv.getIdOscip());
			pstmt.setString(3, "C");
			if(conv.getSituacao() != null){
				pstmt.setString(4, conv.getSituacao());
			}else{
				pstmt.setString(4, conv.getSituacaoProposta());
			}			
			pstmt.setString(5, conv.getIdProposta());
			pstmt.setBlob(6, new SerialBlob(conv.getObjeto().getBytes()));
			pstmt.setString(7, conv.getOrgao());
			pstmt.setString(8, conv.getModalidade());
			pstmt.setString(9, conv.getEndereco());
			pstmt.setString(10, conv.getCep());
			pstmt.setString(11, conv.getCidade());
			pstmt.setString(12, conv.getUF());
			pstmt.setString(13, conv.getValorGlobal());
			pstmt.setString(14, conv.getValorRepasse());
			pstmt.setString(15, conv.getValorDesembolsado());
			pstmt.setString(16, conv.getDtInicio());
			pstmt.setString(17, conv.getDtFim());	

			pstmt.executeUpdate();
			pstmt.close();	
			
			LOGGER.info("insereConvenio realizado");
			
		}catch(Exception e ){			
			LOGGER.error("Erro insereConvenio "+ e.getMessage());
		}
	}
	
	public static void insereCronogramasConvenio (Connection conn, CronogramaConv[] cronos, String idConv){
		
		try{
			
			for(CronogramaConv crono: cronos){

				String sql = "insert into convcrono (idconv, descricao, "
						+    "dtinicio, dtfim, unidfornec, valor) "
						+ 	 "values (?, ?, ?, ?, ?, ?)";
								
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, idConv);
				pstmt.setBlob(2, new SerialBlob(crono.getDescricao().getBytes()));
				pstmt.setString(3, crono.getDtInicio());
				pstmt.setString(4, crono.getDtFim());
				pstmt.setString(5, crono.getUnidFornec());
				pstmt.setString(6, crono.getValor());	
				
				pstmt.executeUpdate();					
				pstmt.close();	
		
			}	
			
			LOGGER.info("insereCronogramasConvenio realizado");
			
		}catch(Exception e ){			
			LOGGER.error("Erro insereCronogramaConvenio "+ e.getMessage());
		}
	}
	
	public static void insereEmpenhosConvenio (Connection conn, EmpenhoConv[] emps, String idConv){
		
		try{
			
			for(EmpenhoConv emp: emps){
				
				String sql = "insert into convemp (idconv, tiponota, tiponotadesc, "
						+    "dtemissao, codsit, descsit, valor) "
						+ 	 "values (?, ?, ?, ?, ?, ?, ?)";
				
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, idConv);
				pstmt.setString(2, emp.getTipoNota());
				pstmt.setString(3, emp.getDescTipoNota());
				pstmt.setString(4, emp.getDtEmissao());
				pstmt.setString(5, emp.getCodSituacao());
				pstmt.setString(6, emp.getDescSituacao());	
				pstmt.setString(7, emp.getValor());	
				
				pstmt.executeUpdate();					
				pstmt.close();	
		
			}	
			
			LOGGER.info("insereEmpenhosConvenio realizado");
			
		}catch(Exception e ){
			LOGGER.error("Erro insereEmpenhosConvenio "+ e.getMessage());
		}
	}
	
	public static void insereRelatorioLinks (Connection conn, long idoscip, int idcontexto, int qtdlinks){
		
		try{
					
			LOGGER.info("Inserindo dados de link no relatorio OSCIP ["+idoscip+"] / CONTEXTO ["+idcontexto+"] ");

			String sql = "insert into relatorio (idoscip, idcontexto, qtdlink, dtiniproc) "
					+ "values (?, ?, ?, now())";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, idoscip);
			pstmt.setInt(2, idcontexto);
			pstmt.setInt(3, qtdlinks);
			
			pstmt.executeUpdate();	
			pstmt.close();	
							
		}catch(Exception e ){
			LOGGER.error("Erro insereRelatorioLinks "+e.getMessage());
		}
	}
	
	public static void insereNewDebugOscip (Connection conn, long idoscip){
		
		try{
					
			LOGGER.info("insereNewReportOscip ["+idoscip+"] ");

			String sql = "insert into debug (idoscip, dtinilink) "
						+ "values (?, now())";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, idoscip);
			pstmt.executeUpdate();	
			pstmt.close();	
							
		}catch(Exception e ){
			LOGGER.error("Erro insereNewReportOscip "+e.getMessage());
		}
	}
	
	public static void updateDebugOscip (Connection conn, long idoscip, String nmCol){
		
		try{
					
			LOGGER.info("updateDebugOscip ["+idoscip+"] ");

			String sql = "update debug set "+nmCol+" = now() "
						+ "where idoscip = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, idoscip);	
			pstmt.executeUpdate();	
			pstmt.close();	
			
		}catch(Exception e ){
			LOGGER.error("Erro updateDebugOscip "+e.getMessage());
		}
	}
	
	public static void updateRelatorioPaginas (Connection conn, long idoscip, int idcontexto, int qtdpags){
		
		try{
					
			LOGGER.info("updateRelatorioPaginas -  OSCIP ["+idoscip+"] / CONTEXTO ["+idcontexto+"] ");

			String sql = "update relatorio set qtdpag = ? "+
					     "where idoscip = ? and idcontexto = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, qtdpags);	
			pstmt.setLong(2, idoscip);
			pstmt.setInt(3, idcontexto);
			
			pstmt.executeUpdate();	
			pstmt.close();	
								
		}catch(Exception e ){
			LOGGER.error("Erro updateRelatorioPaginas "+e.getMessage());
		}
	}
	
	public static void updateRelatorioDescobertas (Connection conn, long idoscip, int idcontexto, int qtddescs){
		
		try{
					
			LOGGER.info("updateRelatorioDescobertas - OSCIP ["+idoscip+"] / CONTEXTO ["+idcontexto+"] ");

			String sql = "update relatorio set dtfimproc = now(), "+
					     "qtddesc = ? where idoscip = ? and idcontexto = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, qtddescs);	
			pstmt.setLong(2, idoscip);
			pstmt.setInt(3, idcontexto);
			
			pstmt.executeUpdate();	
			pstmt.close();	
									
		}catch(Exception e ){
			LOGGER.error("Erro updateRelatorioDescobertas "+e.getMessage());
		}
	}
	
	public static Pagina[] loadDescobertaOscipContexto (Connection conn, Oscip oscip, Contexto contexto){
		
		ArrayList<Pagina> arrPag = new ArrayList<Pagina>();
		
		try{
			
			//coletando paginas candidatas 
			String sql = "select * from descoberta d where exists ( "+
						 "select * from pagina p where "+ 
						 "d.fkpagina = p.idpagina "+
						 "and p.fkcontexto = "+contexto.getIdContexto()+" "+
						 "and fkoscip = "+oscip.getId()+")";
			              
			             //and status = "Nao processado"
			//TODO: Coletar os dados apenas de descobertas que ainda não foram comparados na tripla
			
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()){
				
				Pagina pg = new Pagina();
				pg.setIdPagina(rs.getLong("fkpagina"));
				pg.setIdOscip(oscip.getId()+"");
				pg.setIdContexto(contexto.getIdContexto()+"");
				pg.setTipo(rs.getString("tipo"));
				pg.setConteudoTxt(rs.getString("texto"));
				
				String texto = rs.getString("texto");
				
				//coletando url do texto
				int pos = texto.indexOf("-->");
				if (pos != -1){
					pg.setUrl(texto.substring(14, pos));
				}
				
				arrPag.add(pg);
			}
			
			rs.close();
			st.close();	
			
		}catch(Exception e ){
			LOGGER.error("Erro loadDescobertaOscipContexto "+ e.getMessage());
		}
		
		 return (Pagina[])arrPag.toArray(new Pagina[]{});
	}
	
	public static void insereDescobertaMetadados (Connection conn, Tripla[] triplasRef, Pagina[] descs, Contexto contexto){

		for(Pagina desc : descs){
			
			/*
			 * SE TEXTO DA DESCOBERTA NAO EXISTE EM METADADOS E 
			 * SE ENQUADRA NO PADRAO DA TRIPLA DE REFERÊNCIA, INSERE NO BANCO DE METADADOS			
			 */
			if(!DBConnector.existeDescobertaMetadado(conn, desc)){
				if(TripleProc.isDescobertaCompativelComTriplaRef(triplasRef, desc, contexto)){
					
					/*
					 *  Está sendo inserido as descobertas em uma tabela temporaria (Stagging area) para depois
					 *  inserir via script SQL as marcadas como válidas					
					 */
					
					//TODO: Aplicar a inserção dos dados da stagarea para a tabela de metadados
					DBConnector.insertDescobertaMetadadosDB(conn, desc);
				}	
			}	
		}
			
	}
	
	public static boolean existeDescobertaMetadado(Connection conn, Pagina pag){
		
		try{
			
			String texto = "";
			
			if(pag.getConteudoTxt().length() > 200){
				texto = pag.getConteudoTxt().substring(100, 199);
			}else{
				texto = pag.getConteudoTxt();
			}
			
			String sql = "select * from oscmeta "
					   + "where fkoscip = "+pag.getIdOscip() +" "
					   + "and texto like '%"+texto+"%' ";
					   
			Statement st= conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			if(rs.next()){
				return true;
			}else{
				return false;
			}
									
		}catch(Exception e ){
			LOGGER.error("Erro existeDescobertaMetadado "+e.getMessage());
		}
		
		return false;
	}
	
	public static void insertDescobertaMetadadosDB (Connection conn, Pagina desc){
		
		try{
			 
			String tipo = Utils.getSiglaTipoContexto(desc);
			String titulo = ""; //TODO: tratar titulo de acordo com o contexto
						
			String sql = "insert into stagarea (fkoscip, tipo, titulo, texto, fonte, dtinclusao) "+
					     "values (?, ?, ?, ?, ?, now())";
			
			
			//TODO: Inserir status de descoberta 
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, desc.getIdOscip());	
			pstmt.setString(2, tipo);
			pstmt.setString(3, titulo);
			pstmt.setString(4, desc.getConteudoTxt());
			pstmt.setString(5, desc.getUrl());
			
			pstmt.executeUpdate();	
			pstmt.close();	
									
		}catch(Exception e ){
			LOGGER.error("Erro insertDescobertaMetadadosDB "+e.getMessage());
		}
	}	
	
}
