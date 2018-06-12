package br.uniriotec.oscip.mining;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import br.uniriotec.oscip.config.Config;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenPLN {
	
	final static Logger LOGGER = Logger.getLogger(OpenPLN.class);
		
	public static String[] getSentences (String texto){
		
		ArrayList<String> arr = new ArrayList<String>();
		String[] sentences = new String[]{};
		
		try {
			InputStream is = new FileInputStream(Config.PATH_OPENNLP_BIN_SENT);
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
	
		    sentences = sdetector.sentDetect(texto.toLowerCase());
			
			for (String sentence: sentences){
				
				//TODO: Melhorar funcao para remover lixo do texto, como pedaços de tag HTML (funcao atual não funciona bem)
				//if(!Utils.contemLixo(sentence)){ //COMENTADO PROPOSITALMENTE	
					arr.add(sentence);
				//}	
				
				sentence = sentence.replace("\n", "");
				sentence = sentence.replace("	", "");

			}
			
		}catch (IOException e) {
			LOGGER.error("Erro em getSentences() "+ e.getMessage());
			e.printStackTrace();
		}
		
		return (String [])arr.toArray(new String[]{});
	}
		
	//TOKENIZER
	public static String [] tokenizer (String texto){
		
		ArrayList<String> arr = new ArrayList<String>();
		
		TokenizerModel tm;
		InputStream is;
		try {
		    is = new FileInputStream(Config.PATH_OPENNLP_BIN_TOKEN);
			tm = new TokenizerModel(is);
			Tokenizer tokenizer = new TokenizerME(tm);
			
			String tokens[] = tokenizer.tokenize(texto);
			
			for(String token : tokens){
				
				System.out.println(token);
				arr.add(token);
			}
			
			is.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Erro em tokenizer() "+ e.getMessage());
			e.printStackTrace();
		}

		return (String [])arr.toArray(new String[]{});
	
	}		
	
	public static String[] SimpleTokenizerSpans(String sent) {  
		
		ArrayList<String> arr = new ArrayList<String>();

		//Instantiating SimpleTokenizer class 
		SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;  

		//Retrieving the boundaries of the tokens 
		Span[] tokens = simpleTokenizer.tokenizePos(sent) ;  

		//Printing the spans of tokens 
		for( Span token : tokens){
			System.out.println(token +" "+sent.substring(token.getStart() , token.getEnd() ));
			arr.add(token +" "+sent.substring(token.getStart() , token.getEnd()));
		}
		    
		return (String [])arr.toArray(new String[]{});      
	}
	
	public static String[] POSTag(String[] sent) { 
		
		ArrayList<String> arr = new ArrayList<String>();
		
		try {
			
			InputStream modelIn = new FileInputStream(Config.PATH_OPENNLP_BIN_POSTTAG);
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);
			
			String tags[] = tagger.tag(sent);
			int i = 0;
			
			for(String tag : tags){
				
				System.out.println(sent[i]+"_<"+tag+">");
				arr.add(sent[i]+"_<"+tag+">");
				i++;
			}
				
		}catch (IOException e) {
			LOGGER.error("Erro em POSTag() "+ e.getMessage());
			e.printStackTrace();
		}
		
		return (String [])arr.toArray(new String[]{});  
	} 
	
	public static String[] POS_PartofSpeech(String sentence)  {
		
		ArrayList<String> arr = new ArrayList<String>();
		
		try {
	      //Loading Parts of speech-maxent model       
	      InputStream inputStream = new FileInputStream(Config.PATH_OPENNLP_BIN_POSTTAG); 
	      POSModel model = new POSModel(inputStream); 
	       
	      //Instantiating POSTaggerME class 
	      POSTaggerME tagger = new POSTaggerME(model) ;
	       
	      //Tokenizing the sentence using simple class  
	      SimpleTokenizer st= SimpleTokenizer.INSTANCE; 
	      String[] tokens = st.tokenize(sentence) ; 
	       
	      //Generating tags 
	      String[] tags = tagger.tag(tokens) ;
	      
	      //Instantiating the POSSample class 
	      POSSample sample = new POSSample(tokens, tags) ; 
	      
	      String[] twts = sample.toString().split(" ");
	      
	      for(String twt: twts){
	    	  arr.add(twt);
	      }
		
		}catch (IOException e) {
			LOGGER.error("Erro em POS_PartofSpeech() "+ e.getMessage());
			e.printStackTrace();
		}
		
		return (String [])arr.toArray(new String[]{}); 
		
	}
	
	public static void POS_PartofSpeech2(String sentence) throws IOException {
		
		try {
	      //Loading Parts of speech-maxent model       
	      InputStream inputStream = new FileInputStream(Config.PATH_OPENNLP_BIN_POSTTAG); 
	      POSModel model = new POSModel(inputStream); 
	       
	      //Instantiating POSTaggerME class 
	      POSTaggerME tagger = new POSTaggerME(model) ; 
	       
	      //Tokenizing the sentence using WhitespaceTokenizer class  
	      SimpleTokenizer st = SimpleTokenizer.INSTANCE; 
	      String[] tokens = st.tokenize(sentence) ; 
	       
	      //Generating tags 
	      String[] tags = tagger.tag(tokens) ;
	      
	      for(String tag : tags){
	    	  System.out.println(tag);
	      }
		
		}catch (IOException e) {
			LOGGER.error("Erro em POS_PartofSpeech2() "+ e.getMessage());
			e.printStackTrace();
		}
		
	}
}
