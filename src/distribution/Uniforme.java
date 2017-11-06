package distribution;


import random.MersenneTwister;



public class Uniforme{
	private MersenneTwister random;
	
	public Uniforme() {
		random = new MersenneTwister();
	}
	
	public Uniforme(int seed) {
		random = new MersenneTwister(seed);
	}
	
		
	
	public int sorteiaNo(int numeroDeNos){
		double aleatorio = random.nextDouble();
		double delta = 1.0/numeroDeNos;
		double soma = delta;
		int no = 0;
		
		while(aleatorio > soma){
			 soma = soma + delta;
			no++;
		}
		return no+1;
	}
	
	public int sorteiaNo(int numeroDeNos, int origem){
		origem = origem -1;
		double aleatorio = random.nextDouble();
		double delta = 1.0/(numeroDeNos-1);
		double soma;
		int no = 0;
		if (origem == no)
			soma = 0;
		else
			soma = delta;
		
		while(aleatorio > soma){
			no++;		
			if(no!=origem){
				soma = soma + delta;
			} 
		}
		
		return no+1;
	}
	
	public static void main(String [] args){
		int numNos = 14; 
		Uniforme dist = new Uniforme();
		
		
		for (int i = 0 ; i < 1000 ; i++){
			int n1 = dist.sorteiaNo(numNos);
			int n2 = dist.sorteiaNo(numNos, n1);
			if(n1==n2)
			System.out.println(n1 + " --> " + n2);
		}
	}
}