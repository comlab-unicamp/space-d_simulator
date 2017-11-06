package util;

public class Token {
	
	private String valor = null;
	
	private TipoToken tipo;
	private int linha;
	
	public Token() {
	}
	
	public TipoToken getTipo() {
		return tipo;
	}
	
	public String getValor() {
		return valor;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public void setTipo(TipoToken tipo) {
		this.tipo = tipo;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
