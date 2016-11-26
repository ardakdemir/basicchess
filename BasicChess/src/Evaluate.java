//given a board evaluates the static score of the current position
public class Evaluate {
	Board myboard;
	long col1=0b1000000010000000100000001000000010000000100000001000000010000000L;
	long whtA=0b0000000000000000111111111111111111111111000000000000000000000000L;
	long blkA=0b0000000000000000000000001111111111111111111111110000000000000000L;
	long col8=0b0000000100000001000000010000000100000001000000010000000100000001L;
	long wReach=0;
	long bReach=0;
	double weights[]=new double[100];
	 int wNmob;
	 int bNmob;
	public Evaluate(Board b){
		myboard=b;
		int count=0;// weak square
		weights[count++]=-5;//weakcount;
		weights[count++]=-9;//enemy knight on weak;
		weights[count++]=10;//center pawn count;
		count+=7;// king value
		weights[count++]=6;//pawn shield;
		count+=9;//bishop value
		weights[count++]=4;//bishop mob;
		weights[count++]=7;//bishop on large diag;
		weights[count++]=28;//bishop pair;
		count+=7;//knight value
		weights[count++]=6;//knight mob;
		count+=9;// pawn vlaue
		weights[count++]=-8;//iso pawn ;
		weights[count++]=-14;//double pawn ;
		weights[count++]=10;//pass pawn ;
		weights[count++]=30;//rookbhd pawn ;
		weights[count++]=-3;//back pawn ;
		weights[count++]=6;//rank of passed pawn
		count+=4;// rook vlaue
		weights[count++]=27;//rook open file ;
		weights[count++]=11;//rook semi-open file ;
		weights[count++]=30;//rook on seventh;
		weights[count++]=4;//rook mob;
		weights[count++]=6;//rook con;
		count+=5;// queen vlaue
		weights[count++]=2;//queen mob;

	}

	// these are not values of the pieces they are positional values positional parameters
	public double positionalValues(){
		// TODO Auto-generated method stub
		long allp=myboard.whitePieces|myboard.blackPieces;
		int wbishopcount=0;
		int bbishopcount=0;
		double rookValue=0;
		double pawnValue=0;
		double knightValue=0;
		double bishopValue=0;
		double queenValue=0;
		double kingValue=0;
		double weaks=weakSquare(0)-weakSquare(1);
		long a=1;
		for(int i=0;i<64;i++)
		{
			a=1;
			a<<=i;
			if((a&allp)==0){// if the square is empty which is the case most times

			}
			else if((a&myboard.wB)!=0){
				bishopValue+=bishopValue(i, 0);
				wbishopcount++;
			}
			else if((a&myboard.bB)!=0){
				bishopValue-=bishopValue(i,1);
				bbishopcount++;
			}
			else if((a&myboard.wP)!=0){
				pawnValue+=pawnValue(i, 0);
			}
			else if((a&myboard.bP)!=0){
				pawnValue-=pawnValue(i,1);
			}else if((a&myboard.wN)!=0){
				knightValue+=knightValue(i, 0);
			}
			else if((a&myboard.bN)!=0){
				knightValue-=knightValue(i,1);
			}else if((a&myboard.wR)!=0){
				rookValue+=rookValue(i, 0);
			}
			else if((a&myboard.bR)!=0){
				rookValue-=rookValue(i,1);
			}else if((a&myboard.wQ)!=0){
				queenValue+=queenValue(i, 0);
			}
			else if((a&myboard.bQ)!=0){
				queenValue-=queenValue(i,1);
			}else if((a&myboard.wK)!=0){
				kingValue+=kingValue(i, 0);
			}
			else if((a&myboard.bK)!=0){
				kingValue-=kingValue(i,1);
			}
		}
		double bishoppair=0;
		if(wbishopcount>=2)
			bishoppair+=weights[22];
		if(bbishopcount>=2)
			bishoppair-=weights[22];
		/*System.out.println("rooks: "+rookValue);
		System.out.println("queens: "+queenValue);
		System.out.println("knights: "+knightValue);
		System.out.println("bishop: "+bishopValue);
		System.out.println("pawn: "+pawnValue);
		System.out.println("king: "+kingValue);
		System.out.println("weaks: "+weaks);
		System.out.println("bishop pair: "+bishoppair);*/
		return (rookValue+pawnValue+knightValue+bishopValue+queenValue+kingValue+bishoppair+weaks);// þimdilik bu sayýlarý elle girdim
	}
	//kontrol etmedim burada hem rakip atlar weak squarede mi kontrolü
	// hem de weak square sayýsý bulunuyor baþka weak square ile alakalý þeyler de ekleneilir
	double weakSquare(int side){
		long mychain=0;
		long leftcap=0;
		long rightcap=0;
		long cap=0;
		long weak=0;
		long ptr=1;
		int enemyknightonWeak=0;
		int weakcount=0;
		int centerpawncount=0;
		if(side==0){
			mychain=myboard.wP;
			leftcap=(mychain<<9)&~col8;
			rightcap=(mychain<<7)&~col1;
			wReach|=leftcap;
			wReach|=rightcap;
			cap=leftcap|rightcap;
			for(int i=0;i<2;i++){
				cap|=(cap<<8);
				weak=(whtA&~cap);
			}
			for(int a=16;a<32;a++){
				ptr=1;
				ptr<<=a;
				if((ptr&weak)!=0){
					weakcount++;
					if((ptr&myboard.bN)!=0)
						enemyknightonWeak++;
				}
			}
		}
		else{
			mychain=myboard.bP;
			leftcap=(mychain>>9)&~col1;
			rightcap=(mychain>>7)&~col8;
			bReach|=leftcap;
			bReach|=rightcap;
			cap=leftcap|rightcap;
			for(int i=0;i<2;i++){
				cap|=(cap>>8);
				weak=(blkA&~cap);
			}
			for(int a=32;a<48;a++){
				ptr=1;
				ptr<<=a;
				if((ptr&weak)!=0){
					weakcount++;
					if((ptr&myboard.wN)!=0)
						enemyknightonWeak++;
				}
			}
		}
		long temp=mychain;
		if((temp>>=27)%2==1)
			centerpawncount++;
		if((temp>>=1)%2==1)
			centerpawncount++;
		if((temp>>=7)%2==1)
			centerpawncount++;
		if((temp>>=1)%2==1)
			centerpawncount++;
		return weakcount*weights[0]+enemyknightonWeak*weights[1]+centerpawncount*weights[2];
	}
	// bu karýþýk biraz daha
	double kingValue(int squ,int side) {
		// TODO Auto-generated method stub
		int pawnshield=0;
		if(side==0&&myboard.isWhiteCastled==1)
			pawnshield=pawnshield(squ, side);
		if(side==1&&myboard.isBlackCastled==1)
			pawnshield=pawnshield(squ, side);
		return pawnshield*weights[10];
	}
	
	//number of pawns in front of the king after castled
	 int pawnshield(int squ,int side) {
		// TODO Auto-generated method stub
		 int dir=0;
		 int pawncount=0;
		 int temp=squ;
		 long mypwn=0;
		 long a=1;
		 if(side==0){
			 mypwn=myboard.wP;
		 }
		if(side==1){
			dir=-1;
			 mypwn=myboard.bP;

		}
		temp+=dir*8;
		for(int i=-1;i<2;i++){
			a=1;
			a<<=(temp+i);
			if((a&mypwn)!=0)
				pawncount++;
		}
		return pawncount++;
	}
	 
	double bishopValue(int squ,int side) {
		// TODO Auto-generated method stub
		int bmob=0;
		int blargediag=0;
		blargediag=isbislargeDiag(squ,side);
		bmob=bishopMob(squ, side);
		return bmob*weights[20]+blargediag*weights[21];
	}

	int isbislargeDiag(int squ, int side) {
		// TODO Auto-generated method stub
		int col=squ%8;
		int row=squ/8;
		if(row+col==7)
			return 1;
		if(row==col)
			return 1;
		return 0;

	}

	double knightValue(int squ,int side) {
		// TODO Auto-generated method stub
		int kniMob=knightMob(squ, side);
		return kniMob*weights[30];
	}

	double pawnValue(int squ,int side) {
		// TODO Auto-generated method stub
		int isoPawn=0;
		int doubPawn=0;
		int passPawn=0;
		int rookbhdpasspawn=0;
		int backPawn=0;
		int passPawnrank=0;
		int blockedPawnpen=0;
		int blockedpasspawnpen=0;
		isoPawn=isoPawn(squ,side);
		doubPawn=doubledPawn(squ, side);
		passPawn=passedPawn(squ, side);
		blockedPawnpen=blockedPawnpen(squ,side);// by own pieces
		blockedpasspawnpen=blockedPasspawnpen(squ,side);// by enemy piece
		if(isoPawn==1)
			backPawn=0;
		else{
			backPawn=backPawn(squ, side);
		}
		if(passPawn==1){
			rookbhdpasspawn=isrookbehind(squ,side);
			passPawnrank=squ/8;
			if(side==1){
				passPawnrank= 7-passPawnrank;
			}
		}
		//passPawnrank*=passPawnrank; // give a lot importance 
//position startpos moves e2e4 g8f6 b1c3 e7e5 d2d3 f8b4 c1g5 b4c3 b2c3 b8c6 g1f3 d7d6 d3d4 e5d4 f3d4 c8g4 f2f3 c6d4 c3d4 g4d7 f1c4 a7a5 a1b1 b7b6 e4e5 h7h6 e5f6 h6g5 f6g7 d8e7 d1e2
//position startpos moves e2e4 e7e5 f2f4 g8f6 d2d3 b8c6 b1c3 f8c5 g1f3 f6g4 d3d4 e5d4 c3d5 g4f6 f1d3 f6d5 e4d5 c6e7 e1f2 e7d5 h1e1 e8f8 e1e5 d5e3 c1e3 d4e3 f2g3 d7d6 e5e4 d6d5 e4e5 f7f6 e5e3 c5e3 d1e2 e3c5 c2c4 d5c4 d3c4 d8e8 e2e8 f8e8 a1e1 e8d8 c4e6 c8e6 e1e6 h8e8 e6e8 d8e8 g3g4 e8d7 h2h3 a8e8 b2b3 e8e2 g2g3 e2a2 f3e1 a2e2 e1d3 c5d4 b3b4 e2d2 d3c5 d4c5 b4c5 d7c6 g4f5 d2a2 f5e4 c6c5 g3g4 a2h2 e4f5 h2h3 g4g5 f6g5 f5g5 c5d5 g5g4 h3a3 f4f5
		
		/*System.out.println("side: "+side+"  squ: "+ squ);
		System.out.println("iso: "+isoPawn);
		System.out.println("doub "+doubPawn);
		System.out.println("pass "+passPawn);
		System.out.println("pass rank: "+passPawnrank);
		System.out.println("rookbhd "+rookbhdpasspawn);*/
		return isoPawn*weights[40]+doubPawn*weights[41]+passPawn*weights[42]+rookbhdpasspawn*weights[43]+backPawn*weights[44]+passPawnrank*weights[45];
	}
     int blockedPasspawnpen(int squ, int side) {
		// TODO Auto-generated method stub
    	 long enemypieces=0;
    	int pos=0;
    	if(side==1){
    		enemypieces=myboard.whitePieces;
    		pos=squ-8;
    	}
    	else{
    		enemypieces=myboard.blackPieces;
    		pos=squ+8;
    	}
    	long a=1;
    	a<<=pos;
    	if((enemypieces&a)!=0)
    		return 1;
		return 0;
	}

	// returns if the pawn is undeveloped and blocked which prevents development
	 int blockedPawnpen(int squ, int side) {
		// TODO Auto-generated method stub
		 long mypiece=0;
		 if(side==0)
			 mypiece=myboard.whitePieces;
		 else
			 mypiece=myboard.blackPieces;
		int squ1=squ+8;
		long pos=1;
		pos<<=squ1;
		if((mypiece&pos)!=0)
			return 1;
		return 0;
	}

	//1 means the pawn is backward
	//0 otherwise
	int backPawn(int squ, int side) {
		// TODO Auto-generated method stub
		int row=squ/8;
		int col=squ%8;
		long mypawns=0;
		long a=1;
		int dir=1;
		if(side==0){
			mypawns=myboard.wP;
		}
		else if(side==1){
			mypawns=myboard.bP;
			dir=-1;
		}
		if(col==0){
			for(int i=-2*dir+3;i!=row+1;i+=dir){
				a=1;
				if(((a<<(i*8+col+1))&mypawns)!=0)
					return 0;

			}
		}else if(col==7){
			for(int i=-2*dir+3;i!=row+1;i+=dir){
				a=1;
				if(((a<<(i*8+col-1))&mypawns)!=0)
					return 0;

			}
		}else{
			for(int i=-2*dir+3;i!=row+1;i+=dir){
				a=1;
				if(((a<<(i*8+col+1))&mypawns)!=0||((a<<(i*8+col-1))&mypawns)!=0)
					return 0;

			}

		}
		return 1;
	}

	// this just checks if a rook is behind the given square
	// now working only to check rook behind passed pawns
	int isrookbehind(int squ, int side) {
		// TODO Auto-generated method stub
		long pos=1;
		int col=squ%8;
		int row=squ/8;
		long my=0;
		if(side==0){
			my=myboard.wR;
			for(int i =0;i<row;i++){
				pos=(1L<<(8*i+col));
				if((pos&my)!=0)
					return 1;
			}
		}
//position startpos moves e2e4 e7e5 f2f4 g8f6 d2d3 b8c6 b1c3 f8c5 g1f3 f6g4 d3d4 e5d4 c3d5 g4f6 f1d3 f6d5 e4d5 c6e7 e1f2 e7d5 h1e1 e8f8 e1e5 d5e3 c1e3 d4e3 f2g3 d7d6 e5e4 d6d5 e4e5 f7f6 e5e3 c5e3 d1e2 e3c5 c2c4 d5c4 d3c4 d8e8 e2e8 f8e8 a1e1 e8d8 c4e6 c8e6 e1e6 h8e8 e6e8 d8e8 g3g4 e8d7 h2h3 a8e8 b2b3 e8e2 g2g3 e2a2 f3e1 a2e2 e1d3 c5d4 b3b4 e2d2 d3c5 d4c5 b4c5 d7c6 g4f5 d2a2 f5e4 c6c5 g3g4 a2h2 e4f5 h2h3 g4g5 f6g5 f5g5 c5d5 g5g4 h3a3 f4f5 a3a2 g4f3 a2b2 f3g3 b2a2 g3f3 a2b2 f3g3

		else{
			my=myboard.bR;
			for(int i = row;i<8;i++){
				pos=(1L<<(8*i+col));
				if((pos&my)!=0)
					return 1;
			}
		}
		
		return 0;
	}

	double rookValue(int squ,int side) {
		// TODO Auto-generated method stub
		int ropenfile=isrookOpenFile(squ,side);
		int rookseventh=isrookSeventh(squ,side);
		int rookmob=rookMob(squ,side);
		int rookcon=isrookconnected(squ, side);
		double ropenfileweight=0;
		if(ropenfile==2)
			ropenfileweight=weights[50];
		if(ropenfile==1)
			ropenfileweight=weights[51];
		//System.out.println("square"+squ+"rook open file ?"+ ropenfile);
		return ropenfileweight+rookseventh*weights[52]+rookmob*weights[53]+rookcon*weights[54];
	}

	int isrookSeventh(int squ, int side) {
		// TODO Auto-generated method stub
		if(squ/8==(6-side*5))
			return 1;
		return 0;
	}

	// returns 0 if closed 1 if half-open 2 if open
	int isrookOpenFile(int squ, int side) {
		// TODO Auto-generated method stub
		int col=squ%8;
		long mypawn=0;
		long enempawn=0;
		long pos=1;
		int me=0;
		int enemy=0;
		if(side==0){
			mypawn=myboard.wP;
			enempawn=myboard.bP;
		}
		if(side==1){
			mypawn=myboard.bP;
			enempawn=myboard.wP;
		}
		for(int i=1;i<7;i++){
			pos=1;
			pos<<=(i*8)+col;
			if((pos&mypawn)!=0){
				me=1;
				return 0;
			}
			else if((pos&enempawn)!=0)
			{
				enemy=1;
			}
		}
		if(enemy==0)
			return 2;

		return 1;
	}

	int isrookconnected(int squ,int side){
		long my=1;
		my<<=squ;
		int row=squ/8;
		long all=0;
		long myrooks=1;
		if(side==0)
		{
			myrooks=myboard.wR;
			all=myboard.whitePieces|myboard.blackPieces;
		}
		else
		{
			myrooks=myboard.bR;
			all=myboard.whitePieces|myboard.blackPieces;
		}
		long rookrow=(all&~my);
		for(int i=0;i<8;i++){
			my=1;
			my<<=(8*row)+i;
			if((my&rookrow)!=0){
				if((myrooks&my)!=0)
					return 1;
				else
					return 0;
			}
		}
		return 0;
	}

	// burada bana bu tarafýn bu noktada queeni varmýþ bilgisi geldi 
	// bu bilgiyle ben queen parametrelerini çaðýrýyorum
	double queenValue(int squ,int side) {
		int qmob=queenMob(squ,side);
		return qmob*weights[60];
	}
	//returns if a pawn is isolated
	int isoPawn(int squ, int side) {
		int count=0;
		// TODO Auto-generated method stub
		long my=0;
		int limit=6,i=0;
		if(side==0){
			my=myboard.wP;
		}
		if(side==1){
			my=myboard.bP;
		}
		int col=squ%8;
		if(col==7){
			while(i++!=limit){
			long right=1;
			right<<=(i*8+col-1);
			if((right&my)!=0)
				count++;
			}
		}
		else if(col==0){
			while(i++!=limit){
			long left=1;
			left<<=(i*8+col+1);
			if((left&my)!=0){
				count++;
			}
			}
		}
		else{
			while(i++!=limit){
			long right=1;
			right<<=(i*8+col-1);
			long left=1;
			left<<=(i*8+col+1);
			if((right&my)!=0||(left&my)!=0)
				count++;
			}
		}
		if(count==0)
			return 1;
	
		return 0;
	}

	//returns 1 if the pawn is doubled
	int doubledPawn(int squ, int side) {
		int count=0;
		long squy=1;
		// TODO Auto-generated method stub
		long my=0;
		if(side==0){
			my=myboard.wP;
		}
		if(side==1){
			my=myboard.bP;
		}
		int pos=squ;
		int gir=0;
		while(pos<55){
			pos+=8;
			squy=1;
			squy<<=pos;
			if((my&squy)!=0)//double detected
			{
				count++;
				gir=1;
				break;
			}
		}
		pos=squ;
		while(pos>0&&gir==0){
			pos-=8;
			squy=1;
			squy<<=pos;
			if((my&squy)!=0)//double detected
			{
				count++;
				break;
			}
		}
		return count;
	}

	int passedPawn(int squ, int side){
		int count=0;
		long enemypawn=0;
		int dir=1;
		int initrow=1;
		if(side==0){
			enemypawn=myboard.bP;
			dir=-1;
			initrow=6;
		}
		if(side==1){
			enemypawn=myboard.wP;

		}
		int col=squ%8;
		int row=squ/8;
		long left=1;
		long right=1;
		long center=1;
		if(col==0){
			while(initrow!=row){
				left=1;
				center=1;
				center<<=initrow*8+col;
				left<<=initrow*8+col+1;
				if((enemypawn&left)!=0||(enemypawn&center)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		else if(col==7)
		{
			while(initrow!=row){
				right=1;
				center=1;
				center<<=initrow*8+col;
				right<<=initrow*8+col-1;
				if((enemypawn&right)!=0||(enemypawn&center)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		else{
			while(initrow!=row){
				left=1;
				center=1;
				right=1;
				center<<=initrow*8+col;
				right<<=initrow*8+col-1;
				left<<=initrow*8+col+1;
				if((enemypawn&right)!=0||(enemypawn&center)!=0||(enemypawn&left)!=0){
					count=1;
					return 0;
				}
				initrow+=dir;
			}
		}
		return 1;
	}

	int rookMob(int squ,int side){
		int count=0;// # of available moves
		count +=horizontalMob(squ, side);
		count+=verticalMob(squ, side);
		return count;
	}
	int knightMob(int squ,int side){
		int count=0;// # of available moves
		long mypiece=0;
		long reach=0;
		if(side==0){
			mypiece=myboard.whitePieces;
		}
		else {
			mypiece=myboard.blackPieces;
		}
		int row=squ/8;
		int col=squ%8;
		if(row+1<8&&col+2<8&&(mypiece&(1L<<8*(row+1)+col+2))==0){
			reach|=(1L<<8*(row+1)+col+2);
			count++;
	}
		if(row+2<8&&col+1<8&&(mypiece&(1L<<8*(row+2)+col+1))==0){reach|=(1L<<8*(row+2)+col+1);
	}	count++;

		if(row+1<8&&col-2>=0&&(mypiece&(1L<<8*(row+1)+col-2))==0){reach|=(1L<<8*(row+1)+col-2);
		count++;
	}
		if(row+2<8&&col-1>=0&&(mypiece&(1L<<8*(row+2)+col-1))==0){reach|=(1L<<8*(row+2)+col-1);
		count++;
	}
		if(row-1>=0&&col+2<8&&(mypiece&(1L<<8*(row-1)+col+2))==0){reach|=(1L<<8*(row-1)+col+2);
		count++;
	}
		if(row-2>=0&&col+1<8&&(mypiece&(1L<<8*(row-2)+col+1))==0){reach|=(1L<<8*(row-2)+col+1);
		count++;
	}
		if(row-1>=0&&col-2>=0&&(mypiece&(1L<<8*(row-1)+col-2))==0){reach|=(1L<<8*(row-1)+col-2);
		count++;
	}
		if(row-2>=0&&col-1>=0&&(mypiece&(1L<<8*(row-2)+col-1))==0){reach|=(1L<<8*(row-2)+col-1);
		count++;
	}
		if(side==0){
			wReach|=reach;
		}
		if(side==1){
			bReach|=reach;
		}
		//count=bitCounter(reach);
		return count;
	}
	// counts the number of 1's 
	 int bitCounter(long reach) {
		// TODO Auto-generated method stub
		 
		return 0;
	}

	//Calculate mobility of queens amount of squares queens can go to
	int queenMob(int squ, int side) {
		// TODO Auto-generated method stub
		int count=0;// # of available moves
		count +=horizontalMob(squ, side);
		count+=verticalMob(squ, side);
		count+=diagMob(squ, side);
		count+=antiDMob(squ, side);
		return  count;
	}
	int bishopMob(int squ, int side) {
		// TODO Auto-generated method stub
		int count=0;// # of available moves
		count+=diagMob(squ, side);
		count+=antiDMob(squ, side);
		return count;
	}
	int diagMob(int squ,int side){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>8)&&(squt%8!=0)){
			squt-=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		squt=squ;
		while((squt<55)&&(squt%8!=7)){
			squt+=9;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		return count;
	}
	int antiDMob(int squ,int side){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>8)&&(squt%8!=7)){
			squt-=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				break;
			}
			else{
				count++;
				reach|=squbit;

			}
		}
		squt=squ;
		while((squt<55)&&(squt%8!=0)){
			squt+=7;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;

				break;
			}
			else{
				count++;
				reach|=squbit;

			}
		}
		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		return count;
	}
	int horizontalMob(int squ,int side){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt%8!=0)){
			squt--;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				reach|=squbit;
				count++;
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		squt=squ;
		while((squt%8!=7)){
			squt++;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}
		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		return count;
	}
	int verticalMob(int squ,int side){
		long my=0;
		long enemy=0;
		long reach=0;
		if(side==0){
			my=myboard.whitePieces;
			enemy=myboard.blackPieces;
		}if(side==1){
			enemy=myboard.whitePieces;
			my=myboard.blackPieces;
		}
		long squbit=1;
		int count=0;
		int squt=squ;
		while((squt>=8)){
			squt-=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squt;
				break;
			}
			else{
				reach|=squt;
				count++;
			}
		}
		squt=squ;
		while((squt<=55)){
			squt+=8;
			squbit=1;
			squbit<<=squt;
			if((my&squbit)!=0){
				break;
			}
			else if((enemy&squbit)!=0){
				count++;
				reach|=squbit;
				break;
			}
			else{
				reach|=squbit;
				count++;
			}
		}

		if(side==0)
			wReach|=reach;
		else
			bReach|=reach;
		return count;
	}

	//returns material count 
	// weights are initialized manually
	public int materialcount(){
		int score=0;
		long pos=0b1;
		for(int i=0;i<64;i++){
			pos=1;
			pos<<=i;
			if((myboard.wP&pos)!=0){
				score+=100;
			}
			else if((myboard.bP&pos)!=0){
				score-=100;

			}
			else if((myboard.wN&pos)!=0){
				score+=300;

			}
			else if((myboard.bN&pos)!=0){
				score-=300;
			}
			else if((myboard.wB&pos)!=0){
				score+=300;
			}
			else if((myboard.bB&pos)!=0){	
				score-=300;
			}
			else if((myboard.wR&pos)!=0){
				score+=500;
			}
			else if((myboard.bR&pos)!=0){
				score-=500;
			}
			else if((myboard.wQ&pos)!=0){
				score+=900;
			}	
			else if((myboard.bQ&pos)!=0){
				score-=900;
			}	
			else if((myboard.wQ&pos)!=0){
				score+=9000;
			}	
			else if((myboard.bQ&pos)!=0){
				score-=9000;
			}	
		}

		return score;
	}
}
