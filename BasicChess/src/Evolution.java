import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

// this class initializes the first generation and starts the evolution
public class Evolution {
	// lower and upper bounds for evaluation parameters
	int weakcountlow=-5;//weakcount;
	int enemyknightonweaklow=-9;//enemy knight on weak;
	int centerpawncountlow=10;//center pawn count;
	int kingpawnshieldlow=6;//pawn shield;
	int kingadjacentattackedlow=-4;//king adjacent attacked;
	int kingcastledlow=40;
	int bishopmoblow=4;//bishop mob;
	int bishoponlargelow=7;//bishop on large diag;
	int pairlow=28;//bishop pair;
	int knightmoblow=6;//knight mob;
	int isopawnlow=-8;//iso pawn ;
	int doublepawnlow=-7;//double pawn ;
	int passpawnlow=10;//pass pawn ;
	int rookbhdpawnlow=40;//rookbhd pawn ;
	int backpawnlow=-3;//back pawn ;
	int rankpasspawnlow=6;//rank of passed pawn
	int blockedpawnpenlow=-6;//blocked pawn pen
	int blockedpasspawnpenlow=-6;//blocked pass pawn pen
	int rookopenfilelow=27;//rook open file ;
	int rooksemiopenfilelow=11;//rook semi-open file ;
	int rookonseventhlow=30;//rook on seventh;
	int rookmoblow=4;//rook mob;
	int rookconlow=6;//rook con;
	int queenmoblow=2;//queen mob;
	int pawnvaluelow=100;// static
	int knightvallow=200;
	int knightvalup=400;
	int bishvallow=200;
	int bishvalup=400;
	int rookvallow=400;
	int rookvalup=600;
	int queenvallow=800;
	int queenvalup=1000;
	int boundaries[][]=new int [100][2];//low up
	// starts the evolution calls necessary methods
	Individual startEvol(int gennum, int popcount,int positionnum,double crossoverrate,
			double mutarate,int upperbound,String gamefile,String outputfile) throws IOException{
		setBoundaries();
		int bestindweights[][]=new int [gennum][100];
		double mutrat=mutarate;
		PrintStream out = new PrintStream(new FileOutputStream(outputfile));
		System.setOut(out);
		Individual bestind = new Individual();
		bestind.positionssolved=0;
		ArrayList<Individual> indlist = new ArrayList<>();
		indlist=initializeGeneration(popcount,upperbound);
		Board startB=new Board(1);
		UCI uci =new UCI(startB);
		uci.games=new String[positionnum];
		uci.Reader(positionnum, gamefile);
		Search src = new Search();
		int totalcount = 0;
		int totalpop= 0;
		int improveind = 0;
		int flag = 0;
		for (int a = 0; a < gennum; a++){
			int b=0;
			System.out.println("generation: "+ a);
			for(Individual ind : indlist){
				totalpop+=1;
				b++;
				int count=0;
				ind.positionssolved=0;
				for(int i = 0; i<positionnum;i++){
					String move = uci.Solver(i);
					//	System.out.print(i+"  grandmaster move: "+ move);
					Move bestmove= src.minimax3(startB, startB.sideToMove, 1, -99999, 99999, null, 0, ind);// simdilik nullmove veriyorum enpas
					//Move bestmove = src.randomSearch(startB, startB.sideToMove);
					String bstmv=uci.moveToAlgebra(bestmove);
					//	System.out.println("  my bestmove: "+ bstmv);
					if(move.substring(0,4).equals(bstmv)){
						ind.solvedgameindex[count]=i;
						count++;
					}
				}
				totalcount+=count;
				System.out.println("individual:  "+b+" position solved: "+ count);
				ind.positionssolved=count;
				if(count>bestind.positionssolved){
					//System.out.println("count: "+ count + "  best: "+ bestind.positionssolved);
					bestind.weights=ind.weights;
					bestind.positionssolved=ind.positionssolved;
					bestind.solvedgameindex=ind.solvedgameindex;
					bestind.generationnum=a;
					flag++;
				}

			}
			indlist.sort(null);
			if(flag == 0 ){
				improveind++;
			}
			else{
				improveind=0;
				mutrat=mutarate;
			}
			//dynamic mutation rate
			if(improveind>5){// no improvement in the last generations on the best individual
				if(mutrat<0.20)
					mutrat+=0.01;
			}
			flag = 0;
			/*	for(Individual ind: indlist) {
				System.out.println("fitness: "+ ind.positionssolved);
			} */
			for(int i = 0;i<100;i++)
			{
				bestindweights[a][i]=(int) bestind.weights[i];
			}
			System.out.println("best individual weights: ");
				bestind.printWeights();
			indlist=nextGen1(indlist, crossoverrate, mutrat,a);
			System.out.println("ortalama : "+totalcount/totalpop);
			System.out.println("mutation rate: "+mutrat);
		}
		// end of simulation
		
		
		System.out.println("Best individual");
		System.out.println("number of positions solved: "+ bestind.positionssolved + " generation: "+ bestind.generationnum);
		bestind.printWeights();
		//draw weights i every generation
		ArrayList<Double>weight = new ArrayList<>();
		for(int i = 0;i<gennum;i++)
			weight.add((double)bestindweights[i][22]);
		String title="weight "+ 22;
		DrawGraph.createAndShowGui(weight,title);
		System.out.println("solved positions:");
		for(int i = 0;i<bestind.positionssolved;i++)
			System.out.println(bestind.solvedgameindex[i]);
		out.close();
		return bestind;
	}
	// mutates the given list with given parameters
	Individual mutate(Individual ind, double mutarate){
		//ArrayList<Individual >mutated= new ArrayList<>();
		Individual ind1=new Individual();
		int count = 0;
		while(count==0)
			for(int i = 0; i<100;i++){
				int weight = (int) ind.weights[i];
				if(weight!=0&& i!=94){
					double rand= Math.random();
					if(rand < mutarate)// mutate
					{
						count++;
						ind1.weights[i]=randomMutate(weight, i);
						if(i>94&&i<98&&ind1.weights[i]>ind.weights[i+1])
						{
							ind1.weights[i]=ind.weights[i+1];
						}
						else if(i>94&&i<98&&ind1.weights[i-1]>ind.weights[i])
						{
							ind1.weights[i]=ind.weights[i-1];
						}
					}
					else
						ind1.weights[i]=ind.weights[i];
				}
				if(i==94)
					ind1.weights[i]=100;
			}
		return ind1;
	}
	
	// if i is even makes crossover only if i is odd mutation only
	ArrayList<Individual>nextGen1(ArrayList<Individual>indlist, double crossoverrate, double mutarate,int i){
		ArrayList<Individual>nextGeneration=new ArrayList<>();
		if(i%2==0){//only crossover
			int num = (int) (crossoverrate*indlist.size());//individuals to keep 
			int rest = indlist.size()- num ;
			for(int a=0;a<num;a++)
				nextGeneration.add(indlist.get(a));
			int x=0;

			while(x*x+x<rest){
				x++;
			}
			x--;
			for(int b= 0 ;b<x;b++){
				for(int j=b+1;j<=2*x-b;j++){
					Individual newind= crossOver(indlist.get(b), indlist.get(j));
					nextGeneration.add(newind);
				}
			}
			int kalan = rest - (x*x+x);
			for(int a = 0; a< kalan ; a ++){
				Individual newind = crossOver(indlist.get(1+a),indlist.get(x*x+a+1));
				nextGeneration.add(newind);
			}
		}
		else{
			int copy= indlist.size()/5;
			for(int a = 0;a<copy;a++){
				nextGeneration.add(indlist.get(a));
			}
			for(int y=copy;y<indlist.size();y++){
				Individual ind1= mutate(indlist.get(y), mutarate);
				nextGeneration.add(ind1);
			}
		}
		return nextGeneration;
	}
	// creates the next generation according to rates
	// indlist is sorted so first individual is the strongest
	ArrayList<Individual>nextGen(ArrayList<Individual>indlist, double crossoverrate, double mutarate){
		ArrayList<Individual>nextGeneration=new ArrayList<>();
		int num=(int) (crossoverrate* indlist.size());
		//System.out.println("num: "+ num);
		int crossnum=indlist.size()-2*num;
		for(int i=0;i<num;i++){
			nextGeneration.add(indlist.get(i));
			//	System.out.println("bildi sayisi: "+ indlist.get(i).positionssolved);
		}
			for(int i=0;i<num;i++){
			Individual a = mutate(indlist.get(i), mutarate);
			nextGeneration.add(a);
		} 

		int x=0;

		while(x*x+x<crossnum){
			x++;
		}
		x--;
		for(int i= 0 ;i<x;i++){
			for(int j=i+1;j<=2*x-i;j++){
				Individual newind= crossOver(indlist.get(i), indlist.get(j));
				newind= mutate(newind, mutarate);
				nextGeneration.add(newind);
			}
		}
		int kalan = crossnum - (x*x+x);
		for(int a = 0; a< kalan ; a ++){
			Individual newind = crossOver(indlist.get(1+a),indlist.get(x*x+a+1));
			newind= mutate(newind, mutarate);
			nextGeneration.add(newind);

		}
		return nextGeneration;
	}

	// crossover is updated so that it is a random process
	Individual crossOver(Individual ind1, Individual ind2){
		Individual ind=new Individual();
		double rand =1;
		for(int i = 0 ; i<100;i++){
			rand = Math.random();
			if(rand>0.45)
				ind.weights[i]=ind1.weights[i];
			else
				ind.weights[i]=ind2.weights[i];
		}
		return ind ;
	}
	// initializes the first generation
	ArrayList<Individual> initializeGeneration(int popcount,int ub) {
		ArrayList<Individual> indlist = new ArrayList<>();
		for(int i=0;i<popcount;i++){
			Individual ind = new Individual();
			initializeWeightsbyBoundary(ind);
			ind.generationnum=0;
			indlist.add(ind);
		}
		return indlist;
	}
	void initializeIndividualWeightsArray(Individual ind,int ub,double []weightsarr){
		for(int i = 0; i<100;i++)
			ind.weights[i]=randomGenerator(weightsarr[i], weightsarr[i], ub);
	}
	void setBoundaries(){
		int count=0;
		boundaries[count][0]=0;
		boundaries[count++][1]=30;//weaks
		boundaries[count][0]=-100;
		boundaries[count++][1]=0;//knightonweak
		boundaries[count][0]=-30;
		boundaries[count++][1]=70;//centerpawn
		count+=7;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//kingpawnshield
		boundaries[count][0]=-100;
		boundaries[count++][1]=0;//kingattacked
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//kingcastled
		count+=7;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//bishopmob
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//bishoponlarge
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//bishoppair
		count+=7;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//knightmob
		count+=9;
		boundaries[count][0]=-70;
		boundaries[count++][1]=30;//isopawn
		boundaries[count][0]=-70;
		boundaries[count++][1]=30;//doublepawn
		boundaries[count][0]=-20;
		boundaries[count++][1]=100;//passpawn
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookbhdpawn
		boundaries[count][0]=-70;
		boundaries[count++][1]=30;//backpawn
		boundaries[count][0]=-30;
		boundaries[count++][1]=60;//rankpasspawn
		boundaries[count][0]=-100;
		boundaries[count++][1]=0;//blockedpawnpen
		boundaries[count][0]=-70;
		boundaries[count++][1]=30;//blockedpasspawnpen
		count+=2;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookopenfile
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rooksemiopenfile
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookonseventh
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookmob
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookcon
		count+=5;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//queenmob	
		count=95;
		boundaries[count][0]=200;
		boundaries[count++][1]=400;//knight
		boundaries[count][0]=220;
		boundaries[count++][1]=420;//bishop
		boundaries[count][0]=400;
		boundaries[count++][1]=600;//rook
		boundaries[count][0]=800;
		boundaries[count++][1]=1000;//queen
	}
	void initializeWeightsbyBoundary(Individual ind){
		for(int i =0;i<100;i++){
			ind.weights[i]=randomGenerator2(boundaries[i][0], boundaries[i][1]);
		}
	}
	private double randomGenerator2(int i, int j) {
		// TODO Auto-generated method stub
		double rand = Math.random();
		int diff = j-i;
		int mut = (int) (i + diff *rand);
		return mut;
	}
	void initializeIndividualWeights(Individual ind,int ub){
		int count=0;// weak square
		ind.weights[count++]=randomGenerator(weakcountlow, weakcountlow, ub);//weakcount;
		ind.weights[count++]=randomGenerator(enemyknightonweaklow, enemyknightonweaklow,ub);//enemy knight on weak;
		ind.weights[count++]=randomGenerator(centerpawncountlow, centerpawncountlow,ub);//center pawn count;
		count+=7;// king value
		ind.weights[count++]=randomGenerator(kingpawnshieldlow, kingpawnshieldlow,ub);//pawn shield;
		ind.weights[count++]=randomGenerator(kingadjacentattackedlow, kingadjacentattackedlow,ub);//king attacked;
		ind.weights[count++]=randomGenerator(kingcastledlow, kingcastledlow,ub);//king attacked;		
		count+=7;//bishop value
		ind.weights[count++]=randomGenerator(bishopmoblow, bishopmoblow,ub);//bishop mob;
		ind.weights[count++]=randomGenerator(bishoponlargelow, bishoponlargelow,ub);//bishop on large diag;
		ind.weights[count++]=randomGenerator(pairlow, pairlow,ub);//bishop pair;
		count+=7;//knight value
		ind.weights[count++]=randomGenerator(knightmoblow, knightmoblow,ub);//knight mob;
		count+=9;// pawn vlaue
		ind.weights[count++]=randomGenerator(isopawnlow, isopawnlow,ub);//iso pawn ;
		ind.weights[count++]=randomGenerator(doublepawnlow, doublepawnlow,ub);//double pawn ;
		ind.weights[count++]=randomGenerator(passpawnlow, passpawnlow,ub);//pass pawn ;
		ind.weights[count++]=randomGenerator(rookbhdpawnlow, rookbhdpawnlow,ub);//rookbhd pawn ;
		ind.weights[count++]=randomGenerator(backpawnlow, backpawnlow,ub);//back pawn ;
		ind.weights[count++]=randomGenerator(rankpasspawnlow, rankpasspawnlow,ub);//rank of passed pawn
		ind.weights[count++]=randomGenerator(blockedpawnpenlow, blockedpawnpenlow,ub);//blocked pawn pen
		ind.weights[count++]=randomGenerator(blockedpasspawnpenlow, blockedpasspawnpenlow,ub);//blocked pass pawn pen
		count+=2;// rook vlaue
		ind.weights[count++]=randomGenerator(rookopenfilelow, rookopenfilelow,ub);//rook open file ;
		ind.weights[count++]=randomGenerator(rooksemiopenfilelow, rooksemiopenfilelow,ub);//rook semi-open file ;
		ind.weights[count++]=randomGenerator(rookonseventhlow, rookonseventhlow,ub);//rook on seventh;
		ind.weights[count++]=randomGenerator(rookmoblow, rookmoblow,ub);//rook mob;
		ind.weights[count++]=randomGenerator(rookconlow, rookconlow,ub);//rook con;
		count+=5;// queen vlaue
		ind.weights[count++]=randomGenerator(queenmoblow, queenmoblow,ub);//queen mob;

		// material values
		int piece=94;
		ind.weights[piece++]=100;// pawn value is fixed
		while(ind.weights[95]>=ind.weights[96]||ind.weights[96]>=ind.weights[97]||ind.weights[97]>=ind.weights[98]){
			piece=95;
			ind.weights[piece++]=randomGenerator(300, 300,ub);
			ind.weights[piece++]=randomGenerator(330, 330,ub);
			ind.weights[piece++]=randomGenerator(500, 500, ub);
			ind.weights[piece++]=randomGenerator(900, 900, ub);
		}
	}
	
	int randomGenerator(double weightsarr, double weightsarr2,int ub){
		int num=0;
		int low1 =(int) ( weightsarr /ub );
		int up1 = (int) (weightsarr2);
		num = (int) (Math.random()*(up1)+low1);
		return num;
	}
	int randomMutate(int weight , int index){
		int low= boundaries[index][0];
		int up= boundaries[index][1];
		double rand = Math.random();
		int mut=0;
		if(rand<0.50){
			int diff = weight - low;
			mut=weight-(int) (Math.random()*diff);
			if(mut ==0)
				mut=-1;
		}
		else{
			int diff = up-weight;
			mut=(int) (Math.random()*diff)+weight;
			if(mut==0)
				mut=1;
		}
		return mut;
	}

}
