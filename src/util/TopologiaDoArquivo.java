package util;

import graph.Caminho;
import graph.Grafo;
import graph.No;
import opticalnetwork.NoOptico;

import java.util.HashMap;
import java.util.Iterator;


public class TopologiaDoArquivo {

	private Grafo grafo;
	private String nomeDoArquivo;

	private HashMap<String,Caminho> tabelaDeRotas;

	public TopologiaDoArquivo(String nomeDoArquivo) throws Exception {
		grafo = new Grafo();
		this.nomeDoArquivo = nomeDoArquivo;
		this.tabelaDeRotas = new HashMap<String,Caminho>();
		this.criaTopologiaNSFNet();
	}

	void adicionarRota(Caminho rota){
		StringBuilder builder = new StringBuilder();
		builder.append(rota.getOrigem().getId());
		builder.append("-");
		builder.append(rota.getDestino().getId());
		this.tabelaDeRotas.put(builder.toString(), rota);
	}

	public HashMap<String,Caminho> getTabelaDeRotas(){
		return this.tabelaDeRotas;
	}


	public void parserRotas(AnalisadorLexico lexico) throws Exception {
		Token token = lexico.proximoToken();
		if(token.getTipo().equals(TipoToken.TK_FINAL_ARQUIVO)){
			return;
		}

		if (!token.getTipo().equals(TipoToken.TK_COLCHETE_ABRIR)) {
			throw new LerArquivoException("[ERRO linha: " + token.getLinha() + " abrir colchetes esperado!" );
		}

		token = lexico.proximoToken();
		Caminho rota = new Caminho();

		No origem = grafo.getNo(token.getValor());
		rota.setOrigem(origem);
		rota.adicionarNo(origem);
		token =  lexico.proximoToken();
		while(token.getTipo().equals(TipoToken.TK_IFEM)){
			token =  lexico.proximoToken();
			rota.adicionarNo(grafo.getNo(token.getValor()));

			token =  lexico.proximoToken();
		}
		if (!token.getTipo().equals(TipoToken.TK_COLCHETE_FECHAR)) {
			throw new LerArquivoException("[ERRO linha: " + token.getLinha() + " Fechar colchetes esperado!" );
		}
		lexico.voltarToken();
		lexico.voltarToken();
		token =  lexico.proximoToken();
		No destino = grafo.getNo(token.getValor());
		rota.setDestino(destino);
		Iterator<No> it = rota.getNos().valores().iterator();
		No no1 = it.next();
		while(it.hasNext()){
			No no2 = it.next();
			rota.adicionarEnlace(grafo.getEnlace(no1, no2));
			no1 = no2;
		}
//		System.out.println(rota.getEnlaces());
		adicionarRota(rota);

		token =  lexico.proximoToken();
		parserRotas(lexico);
	}

	private void criaTopologiaNSFNet() throws Exception{
		//Nos da Topologia da NSFNet

		for (Integer i = 1 ; i < 15 ; i++){
			grafo.adicionarNo(new NoOptico(i.toString(),"Node"+i.toString(),grafo));
		}

		//		inserir enlaces bidirecionais
		/*grafo.adicionarEnlaceBidirecional(grafo.getNo("1"), grafo.getNo("2"));//1
		grafo.adicionarEnlaceBidirecional(grafo.getNo("1"), grafo.getNo("3"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("1"), grafo.getNo("8"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("2"), grafo.getNo("3"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("2"), grafo.getNo("4"));//5
		grafo.adicionarEnlaceBidirecional(grafo.getNo("3"), grafo.getNo("6"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("4"), grafo.getNo("5"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("4"), grafo.getNo("10"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("5"), grafo.getNo("6"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("5"), grafo.getNo("7"));//10
		grafo.adicionarEnlaceBidirecional(grafo.getNo("6"), grafo.getNo("9"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("6"), grafo.getNo("12"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("7"), grafo.getNo("8"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("8"), grafo.getNo("11"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("9"), grafo.getNo("11"));//15
		grafo.adicionarEnlaceBidirecional(grafo.getNo("10"), grafo.getNo("13"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("10"), grafo.getNo("14"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("11"), grafo.getNo("13"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("11"), grafo.getNo("14"));
		grafo.adicionarEnlaceBidirecional(grafo.getNo("12"), grafo.getNo("13"));//20
		grafo.adicionarEnlaceBidirecional(grafo.getNo("12"), grafo.getNo("14"));
*/
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("1"), grafo.getNo("2"));//1
		grafo.adicionarEnlaceBidirecional(1000, grafo.getNo("1"), grafo.getNo("3"));
		grafo.adicionarEnlaceBidirecional(2000, grafo.getNo("1"), grafo.getNo("8"));
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("2"), grafo.getNo("3"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("2"), grafo.getNo("4"));//5
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("3"), grafo.getNo("6"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("4"), grafo.getNo("5"));
		grafo.adicionarEnlaceBidirecional(2000, grafo.getNo("4"), grafo.getNo("10"));
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("5"), grafo.getNo("6"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("5"), grafo.getNo("7"));//10
		grafo.adicionarEnlaceBidirecional(1000, grafo.getNo("6"), grafo.getNo("9"));
		grafo.adicionarEnlaceBidirecional(2000, grafo.getNo("6"), grafo.getNo("12"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("7"), grafo.getNo("8"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("8"), grafo.getNo("11"));
		grafo.adicionarEnlaceBidirecional(800, grafo.getNo("9"), grafo.getNo("11"));//15
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("10"), grafo.getNo("13"));
		grafo.adicionarEnlaceBidirecional(200, grafo.getNo("10"), grafo.getNo("14"));
		grafo.adicionarEnlaceBidirecional(500, grafo.getNo("11"), grafo.getNo("13"));
		grafo.adicionarEnlaceBidirecional(300, grafo.getNo("11"), grafo.getNo("14"));
		grafo.adicionarEnlaceBidirecional(200, grafo.getNo("12"), grafo.getNo("13"));//20
		grafo.adicionarEnlaceBidirecional(800, grafo.getNo("12"), grafo.getNo("14"));

		System.out.println("Lendo arquivos de rotas: " + this.nomeDoArquivo);
		AnalisadorLexico lexico = new AnalisadorLexico(this.nomeDoArquivo);
//		System.out.println("Carregando Rotas Expl�citas ...");
		parserRotas(lexico);


	}

	public Grafo getGrafo() {
		return this.grafo;
	}
}
