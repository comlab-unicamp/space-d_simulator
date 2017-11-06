package opticalnetwork.rwa;



import graph.No;

import java.util.Arrays;


public class PacoteRsvp extends Pacote{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7910335800136824753L;
	private No hopAnterior;
	private No hopAtual;
	private Tipo tipo;
	private double duracao;
	private String chaveOrigemDestino;
	
	private Boolean[] labelSet;

	
	public enum Tipo{
		PATH,
		RESV,
		PATH_ERR_LABEL_SET_VAZIO,
		PATH_TEAR,
		RESV_ERR,
		RESV_TEAR, RESV_ERR_LAMBDA_INDISPONIVEL, PATH_ERR, PATH_ERR_LAMBDA_INDISPONIVEL, FIM
	}
	
	PacoteRsvp() {
		
	}
	public PacoteRsvp(Requisicao requisicao, Tipo tipo, Boolean[] listaRotulos){
		super(requisicao.getOrigem(), requisicao.getDestino());
		super.setId(requisicao.getIdRequisicao());
		setHopAtual(requisicao.getOrigem());
		setHopAnterior(null);
		setLabelSet(listaRotulos);
		this.tipo = tipo;
		this.duracao = requisicao.getDuracao();
		this.chaveOrigemDestino = requisicao.getChaveOrigemDestino();
	}
	

	public String getChaveOrigemDestino(){
		return chaveOrigemDestino;
	}
	
	public void setChaveOrigemDestino(String chave){
		this.chaveOrigemDestino = chave;
	}
	
	public Boolean[] getLabelSet(){
		return this.labelSet;
	}
	
	public double getDuracao(){
		return this.duracao;
	}
	

	public No getHopAtual() {
		return hopAtual;
	}

	public void setHopAtual(No hopAtual) {
		this.hopAtual = hopAtual;
	}				//PacoteRsvp pacoteResvErr = pacote.clone(); 

	/**
	 * @return the hopAnterior
	 */
	public No getHopAnterior() {
		return hopAnterior;
	}

	/**
	 * @param hopAnterior the hopAnterior to set
	 */
	public void setHopAnterior(No hopAnterior) {
		this.hopAnterior = hopAnterior;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}


	public void setLabelSet(Boolean[] labelSet) {
		this.labelSet = labelSet;
	}
	
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("PACOTE Id ");
		builder.append(this.getId());
		builder.append(", Tipo:");
		builder.append(tipo.toString());
		builder.append(", ");
		builder.append(getOrigem());
		builder.append("<->");
		builder.append(getDestino());
		builder.append(" (");
		builder.append(this.hopAtual);
		builder.append(") ");
		
		if(getTipo() == Tipo.RESV | getTipo() == Tipo.PATH_ERR_LAMBDA_INDISPONIVEL | 
				getTipo() == Tipo.RESV_ERR_LAMBDA_INDISPONIVEL | getTipo() == Tipo.PATH_TEAR | getTipo() == Tipo.FIM){
			builder.append(", Label: ");
			builder.append(this.getRotulo());
		} else {
			builder.append(", Label Set: ");
			builder.append(Arrays.toString(labelSet));
		}
		builder.append(" }");
		return builder.toString();
	}
	
	public PacoteRsvp clone(){
		PacoteRsvp pacoteRsvp = new PacoteRsvp();
		pacoteRsvp.setDestino(this.getDestino());
		pacoteRsvp.setHopAnterior(this.getHopAnterior());
		pacoteRsvp.setHopAtual(this.getHopAtual());
		pacoteRsvp.setId(this.getId());
		pacoteRsvp.setLabelSet(this.getLabelSet().clone());
		pacoteRsvp.setRotulo(this.getRotulo());
		pacoteRsvp.setOrigem(this.getOrigem());
		pacoteRsvp.setRro(getRro().clone());
		pacoteRsvp.setTipo(this.getTipo());
		
		return pacoteRsvp;
	}
	
}
