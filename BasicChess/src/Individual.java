import java.util.Comparator;

public class Individual implements Comparable<Individual>{
	double[] weights =new double[100];
	int generationnum=0;
	int positionssolved=0;// out of 1000 for now
	int []solvedgameindex=new int[300];
	int id;// id of an individual
	// lower and upper bounds for initialization
	@Override
	public int compareTo(Individual ind1) {
		// TODO Auto-generated method stub
        int comparefitness=((Individual)ind1).positionssolved;
        /* For Descending order*/
        return comparefitness-this.positionssolved;
	}
	void printWeights(){
		for(int i =0; i<weights.length;i++){
			if(weights[i]!=0)
				System.out.println("weight "+ i+ " : "+ weights[i]);
		}
	}
	
}
