package br.uniriotec.oscip.config;

public class Config {	
	
	//configuracao do banco
	public static final String DB_HOST = "localhost";
	public static final String DB_PORT = "3306";
	public static final String DB_NAME = "oscip";
	public static final String DB_USER = "root";
	public static final String DB_PASS = "unirio";
	
	//CAMINHO DE PASTAS 
	public static final String ROOT_PATH = "./tmpfiles";   //ARQUIVOS TEMPORÁRIOS
	public static final String PATH_LIB= "./lib/";      //CAMINHO DE ARQUIVOS DE BIBLIOTECAS DO OPENNPL
	public static final String PATH_CACHE_DOCS = ROOT_PATH + "/cache/docs/";
	public static final String PATH_CACHE_URLS = ROOT_PATH + "/cache/urls/";
	public static final String PATH_CACHE_BIN = ROOT_PATH + "/cache/bin/";
	public static final String PATH_OPENNLP_BIN_SENT = "./bin/pt-sent.bin";
	public static final String PATH_OPENNLP_BIN_TOKEN = "./bin/pt-token.bin";
	public static final String PATH_OPENNLP_BIN_POSTTAG = "./bin/pt-pos-maxent_2.bin";
	public static final String PATH_OPENNLP_BIN_POSTTAG2 = "./bin/pt-pos-perceptron.bin";
	public static final String PATH_OPENNLP_BIN_NER_PERSON = "./bin/en-ner-person.bin";
	public static final String PATH_OPENNLP_BIN_PARSER_CHUNKER = "./bin/en-parser-chunking.bin";
	public static final String PATH_OPENNLP_BIN_CHUNKER = "./bin/en-chunker.bin";
	
	//NOME DE ARQUIVO TEMPORARIO DO CACHE
	public static final String CACHE_PESQUISA_ATUAL = "cache_pesquisa_atual.txt";
	
	//CAMINHO E NOME DOS ARQUIVOS DE CONVÊNIOS (COLETADOS DO SICONV)
	public static final boolean CONVENIOS_COLETAR_NOVOS_DADOS = false; // se for necessário coletar novos dados xls, colocar como true
	public static final String PATH_OSCIP_CSV = ROOT_PATH + "/oscips_jus.csv";
	public static final String PATH_SICONV_PROPOSTA_CONVENIOS = ROOT_PATH + "/siconv_proposta.csv";
	public static final String PATH_SICONV_CONVENIOS = ROOT_PATH + "/siconv_convenio.csv";
	public static final String PATH_SICONV_EMPENHOS = ROOT_PATH + "/siconv_empenho.csv";
	public static final String PATH_SICONV_CRONOGRAMAS = ROOT_PATH + "/siconv_meta_crono_fisico.csv";
	
	//PADRAO DE URL DE BUSCA DO GOOGLE
	public static final String GOOGLE_SEARCH_URL = "https://www.google.com.br/search?lr=&hl=pt-PT&as_qdr=all&q=";
	
	//CONFIGURACOES DO EXTRATOR
	public static final int SLEEP_TIME = 6000; //timeout de intervalo das buscas
	public static final int QTD_MAX_OSCIP = 2; //quantidade maxima de OSCIPs para processar (para processar tudo coloque um valor alto)
	public static final boolean LIMPAR_BANCO_DE_DADOS = true; 
	public static final boolean LIMPAR_DESCOBERTA = true;  //LIMPA DADOS DE DESCOBERTA
	public static final boolean LIMPAR_CACHE_URLS = true;   //LIMPA CACHE DE URLS ENCONTRADAS
	public static final boolean LIMPAR_CACHE_TEXTO = true;  //LIMPA CACHE DE DOCUMENTOS ENCONTRADOS
	public static final boolean LIMPAR_CACHE_BIN = true;    //LIMPA CACHE DE ARQUIVOS BINÁRIOS ENCONTRADOS
	public static final int CRAWLER_QTD_MAX_THREADS = 3;    //QUANTIDADE MÁXIMA DE THREADS FUNCIONANDO EM PARALELO
	public static final int CRAWLER_QTD_MAX_PAGES_TO_TRACK = 200;  //QUANTIDADE MÁXIMA DE PAGINAS PARA VARRER
	public static final int CRAWLER_QTD_DEEP_PAGES = 0;      //PROFUNDIDADE DE BUSCA DE LINKS (ZERO significa não buscar os documentos apontados por links)
	//
	public static final boolean CRAWLER_MUST_INSERT_BIN_PAGES = false;  //CONSIDERA BUSCA EM ARQUIVOS BINÁRIOS

}
