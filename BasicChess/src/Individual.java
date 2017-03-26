import java.util.Comparator;

public class Individual implements Comparable<Individual>{
	double[] weights =new double[100];
	int generationnum=0;
	int positionssolved=0;// out of 1000 for now
	int onfirsttrial=0;
	double fitness=0;
	int []solvedgameindex=new int[1000];
	int id;// id of an individual
	// lower and upper bounds for initialization
	@Override
	public int compareTo(Individual ind1) {
		// TODO Auto-generated method stub
        int  comparefitness=(int) ((Individual)ind1).fitness;
        /* For Descending order*/
        return (int) (comparefitness-this.fitness);
	}
	void printWeights(){
		for(int i =0; i<weights.length;i++){
			if(weights[i]!=0)
				System.out.println("weight "+ i+ " : "+ weights[i]);
		}
	}
	
}
