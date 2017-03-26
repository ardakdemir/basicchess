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
		int fingen=0;
		int improveind = 0;
		int flag = 0;
		int mutimp=0;//improvements made by mutation
		int crossimp=0;// improvements made by crossover
		for (int a = 0; a < gennum; a++){
			int b=0;
			System.out.println("generation: "+ a);
			long starttime = System.currentTimeMillis();
			int known=0;
			while(known<indlist.size()&&indlist.get(known).positionssolved!=0)
				known++;
			for(int i = 0; i<positionnum;i++){
			//	System.out.println(i);
				String move = uci.Solver(i);
				//startB.printBoard();
				//	System.out.print(i+"  grandmaster move: "+ move);
				for(int y = known;y<indlist.size();y++){
					Individual ind = indlist.get(y);
					//Move bestmovelist[]=src.minimax3(startB, startB.sideToMove,1, -99999, 99999, null, 0, ind);
					//Move bestmovelist2[]=src.minimax3(startB, startB.sideToMove,2, -99999, 99999, null, 0, ind);
					//Move bestmov=src.minimax4(startB, startB.sideToMove,2, -99999, 99999, null, 0, ind);
					//Move bestmove= src.minimax4(startB, startB.sideToMove,2, -99999, 99999, null, 0, ind);// simdilik nullmove veriyorum enpas
					//Move bestmove = src.randomSearch(startB, startB.sideToMove);
					ArrayList<Move> sortedmoves=src.minimax5(startB, startB.sideToMove,2, -99999, 99999, null, 0, ind);
					String bstmv=uci.moveToAlgebra(sortedmoves.get(0));
					
					//startB.printBoard();
					/*for(int u = 0;u<sortedmoves.size();u++){
						System.out.println("move: "+sortedmoves.get(u).move+" score: "+sortedmoves.get(u).moveScore);
					}*/
					//	System.out.println("  my bestmove: "+ bstmv);
			//		System.out.println(i+"my: "+bstmv+"  his: "+move);
			//		System.out.println("king black :"+ startB.rights[5]);
					/*System.out.println("pozisyon "+i+" side"+startB.sideToMove);
					startB.printBoard();
					for(int l = 0;l<3;l++){
						if(bestmovelist[l]!=null){
							System.out.println("1move: "+bestmovelist[l].move+"moveScore: "+bestmovelist[l].moveScore);
							System.out.println("2move: "+bestmovelist2[l].move+"moveScore: "+bestmovelist2[l].moveScore);
						}
						
					}*/
					if(move.substring(0,4).equals(bstmv)){
					//	System.out.println(move.substring(0,4));
						//System.out.println("Position: "+i+" Ind:"+y+" First move suggested is true");
						ind.solvedgameindex[ind.positionssolved]=i;
						ind.positionssolved++;
						ind.onfirsttrial++;
						ind.fitness++;
					}
					/*else{
						for(int x=1;x<sortedmoves.size()&&x<3;x++){
							String movestr=uci.moveToAlgebra(sortedmoves.get(x));
							if(move.substring(0,4).equals(movestr)){
								//	System.out.println(move.substring(0,4));
									//System.out.println("Position: "+i+" Ind:"+y+" First move suggested is true");
									ind.solvedgameindex[ind.positionssolved]=i;
									ind.positionssolved++;
									ind.fitness+=(1-(x*0.2));
								}
						}
					}*/
				}
			}
			long diff= System.currentTimeMillis()-starttime;
			System.out.println("one generation: "+ diff);
			for(Individual ind : indlist){
				b++;
				int count=0;
				System.out.println("individual:  "+b+" position solved: "+ ind.positionssolved);
				if(ind.fitness>bestind.fitness){
					//System.out.println("count: "+ count + "  best: "+ bestind.positionssolved);
					bestind.weights=ind.weights;
					bestind.fitness=ind.fitness;
					bestind.positionssolved=ind.positionssolved;
					bestind.solvedgameindex=ind.solvedgameindex;
					bestind.generationnum=a;
					if(a%2==0&&a!=0){
						mutimp++;
					}
					if(a%2==1){
						crossimp++;
					}
					flag++;
				}
			}
			//indlist.sort(null);
			indlist=sortInds(indlist);
			if(flag == 0 ){
				improveind++;
			}
			else{
				improveind=0;
				mutrat=mutarate;
			}
			//dynamic mutation rate
			if(improveind>5){// no improvement in the last generations on the best individual
				if(mutrat<0.14)
					mutrat+=0.011;
			}
			flag = 0;
				for(Individual ind: indlist) {
				System.out.println("fitness: "+ ind.fitness+"  first: "+ind.onfirsttrial);
			} 
			for(int i = 0;i<100;i++)
			{
				bestindweights[a][i]=(int) bestind.weights[i];
			}
			System.out.println("best individual weights: ");
			bestind.printWeights();
			indlist=nextGen1(indlist, crossoverrate, mutrat,a);
			System.out.println("mutation rate: "+mutrat);
			if(improveind>40){
				fingen=a;
				a=gennum;
			}
		}
		// end of simulation

		System.out.println("improvements by crossover:"+ crossimp);
		System.out.println("improvements by mutation:"+ mutimp);		
		System.out.println("Best individual");
		System.out.println("number of positions solved: "+ bestind.positionssolved + " generation: "+ bestind.generationnum);
		bestind.printWeights();
		//draw weights i every generation
//		ArrayList<Double>weight = new ArrayList<>();
//		for(int i = 0;i<fingen;i++)
//			weight.add((double)bestindweights[i][22]);
//		String title="weight "+ 22;
//		DrawGraph.createAndShowGui(weight,title);
		System.out.println("solved positions:");
		for(int i = 0;i<bestind.positionssolved;i++)
			System.out.println(bestind.solvedgameindex[i]);
		out.close();
		return bestind;
	}
	private ArrayList<Individual> sortInds(ArrayList<Individual> indlist) {
		// TODO Auto-generated method stub
		Individual[]inds=new Individual[indlist.size()];
		int count=0;
		for(Individual ind:indlist)
			inds[count++]=ind;
		for(int i = 1;i<indlist.size();i++){
			for(int j= 0;j<i;j++){
				if(inds[i].fitness>inds[j].fitness){
					Individual temp= inds[j];
					inds[j]=inds[i];
					inds[i]=temp;
				}
			}
		}
		ArrayList<Individual> indler=new ArrayList<>();
		for(int a=0;a<indlist.size();a++){
			indler.add(inds[a]);
		}
		return indler;
	}
	// mutates the given list with given parameters
	Individual mutate(Individual ind, double mutarate){
		//ArrayList<Individual >mutated= new ArrayList<>();
		Individual ind1=new Individual();
		int count = 0;
		int again=0;
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
						else if(i>94&&i<=98&&ind1.weights[i-1]>ind.weights[i])
						{
							ind1.weights[i]=ind.weights[i-1];
						}
					}
					else
						ind1.weights[i]=ind.weights[i];
				}
				if(i==94)
					ind1.weights[i]=100;
				again=1;
			}
		return ind1;
	}

	// only eliminate the least successful individuals
	// put the newly created individuals
	// keep the rest of the individuals
	ArrayList<Individual>crossOverRoutine(ArrayList<Individual>indlist,double crossoverrate){
		ArrayList<Individual>newinds= new ArrayList<>();
		int num = (int) (indlist.size() * crossoverrate);
		int rest = indlist.size()-num;
		for(int i = 0;i<rest;i++){
			newinds.add(indlist.get(i));
		}
		for(int i = 0; i<num; i++){
			Individual a =  new Individual();
			int in2= (int) (rest*Math.random());
			a= crossOver(newinds.get(i), newinds.get(in2));
			System.out.println("crossladým: "+i +" + "+in2);
			newinds.add(a);
		}
		return newinds;
	}
	// if i is even makes crossover only if i is odd mutation only
	ArrayList<Individual>nextGen1(ArrayList<Individual>indlist, double crossoverrate, double mutarate,int i){
		ArrayList<Individual>nextGeneration=new ArrayList<>();
		if(i%2==0){//only crossover
			return crossOverRoutine(indlist, crossoverrate);
		}
		else{//only mutation
			int copy= indlist.size()/2;
			for(int a = 0;a<copy;a++){
				nextGeneration.add(indlist.get(a));
			}
			for(int y=0;y<indlist.size()-copy;y++){
				Individual ind1=new Individual();
					ind1=mutate(indlist.get(y), mutarate);
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
			if(rand<0.33)
				ind.weights[i]=ind1.weights[i];
			else if(rand<66)
				ind.weights[i]=ind2.weights[i];
			else
				ind.weights[i]=(ind1.weights[i]+ind2.weights[i])/2;
		}
		for(int i = 94;i<98;i++){
			if(ind.weights[i]>ind.weights[i+1])
				ind.weights[i+1]=ind.weights[i];
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
		boundaries[count][0]=-50;
		boundaries[count++][1]=0;//weaks
		boundaries[count][0]=-100;
		boundaries[count++][1]=0;//enemyknightonweak
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
		boundaries[count++][1]=15;//bishopmob
		boundaries[count][0]=0;
		boundaries[count++][1]=50;//bishoponlarge
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//bishoppair
		count+=7;
		boundaries[count][0]=0;
		boundaries[count++][1]=15;//knightmob
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//knighsupport
		boundaries[count][0]=-100;
		boundaries[count++][1]=0;//knightper0
		boundaries[count][0]=-60;
		boundaries[count++][1]=40;//knightper1
		boundaries[count][0]=-40;
		boundaries[count++][1]=60;//knightper2
		boundaries[count][0]=-30;
		boundaries[count++][1]=70;//knightper3
		count+=4;
		boundaries[count][0]=-50;
		boundaries[count++][1]=0;//isopawn
		boundaries[count][0]=-50;
		boundaries[count++][1]=0;//doublepawn
		boundaries[count][0]=-20;
		boundaries[count++][1]=100;//passpawn
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookbhdpawn
		boundaries[count][0]=-70;
		boundaries[count++][1]=30;//backpawn
		boundaries[count][0]=-40;
		boundaries[count++][1]=40;//rankpasspawn
		boundaries[count][0]=0;
		boundaries[count++][1]=0;//blockedpawnpen
		boundaries[count][0]=-0;
		boundaries[count++][1]=0;//blockedpasspawnpen
		count+=2;
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookopenfile
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rooksemiopenfile
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookonseventh
		boundaries[count][0]=0;
		boundaries[count++][1]=10;//rookmob
		boundaries[count][0]=0;
		boundaries[count++][1]=100;//rookcon
		boundaries[count][0]=-60;
		boundaries[count++][1]=30;//rookclosedfile		
		count+=4;
		boundaries[count][0]=0;
		boundaries[count++][1]=10;//queenmob	
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
		ind.weights[94]=100;
	}
	private double randomGenerator2(int i, int j) {
		// TODO Auto-generated method stub
		double rand = Math.random();
		int diff = j-i;
		int mut = (int) (i + diff *rand);
		if(mut==0&&i!=0&&j!=0){
			if(rand<0.50)
				return -1;
			else
				return 1;
		}
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
