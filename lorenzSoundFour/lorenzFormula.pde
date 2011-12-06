class LorenzFormula {

	lorenzSoundFour		that;
	
	boolean 			paused=true;

	int 				animate=5,
						direction=1,
						iteration=1000;
	
	float 				animationStep=0.001,
						x,y,z;
	
	float[] 			formulaPosition = {465, 85},
						variable= {-1.11, 1.12, 4.49, 0.13, 1.4, 0.4, 0.13, 1.47, 0.13};
/*	
						defaults = {
							{min, max, start}
						};		
*/
	float[][] 			defaults ={
							{-2.5, -1.07, -1.11},
							{0.25, 1.16, 1.12},
							{3, 4.6, 4.49},
							{0, 1.94, 0.13},
							{-0.8, 1.65, 1.4},
							{-4, 2, 0.4},
							{-0.001, 0.18, 0.13},
							{-0.89, 1.5, 1.47},
							{0.01, 0.13, 0.13}
						};
						
	float[][]			targetAreas = {
							{75,-5},
							{390,-5},
							{515,-5},
							{655,-5},
							{190,27},
							{395,27},
							{535,27},
							{110,59},
							{400,59}
						};
						
///////////////////////////////////////////////////////////
	LorenzFormula(lorenzSoundFour that_) {
		that=that_;
	}
///////////////////////////////////////////////////////////
	void animation(){
		for( int i = 0; i < defaults.length; i++) {
		
			if(animate == i) {
				if(variable[i] <= defaults[i][0]) direction=1;
				if(variable[i] >= defaults[i][1]) direction=-1;
			
				if(direction > 0) variable[i]+=animationStep;
				else variable[i]-=animationStep;
			}
			else {
				if(variable[i] > defaults[i][2]) variable[i]-=animationStep;
				else if(variable[i] < defaults[i][2]) variable[i]+=animationStep;
			}
		}
		
	}
///////////////////////////////////////////////////////////
	void generatePoints(){
		x=1;
		y=1;
		z=1;
		
		for(int i=0; i<50; i++) iterate();
		for(int i=0; i<that.points.length; i++) {
			that.points[i][0]=x;
			that.points[i][1]=y;
			that.points[i][2]=z;			
			iterate();	
		}
	}
///////////////////////////////////////////////////////////
	void iterate(){
		float	dx = (variable[0] * x - y * y - z * z + variable[1] * variable[2]) * variable[3],
				dy = (-y + x * y - variable[4] * x * z + variable[5]) * variable[6],
				dz = (-z + variable[7] * x * y + x * z) * variable[8];
		x+=dx;
		y+=dy;
		z+=dz;
	}
}