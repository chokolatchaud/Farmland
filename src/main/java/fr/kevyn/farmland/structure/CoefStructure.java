package fr.kevyn.farmland.structure;

public class CoefStructure {
	static float CoefCréativité = 35;
	static float CoefArchitecture = 23;
	static float CoefDensité = 11;
	static float CoefÉquilibre = 16;
	static float CoefFinition = 15;
	
	
	
	
	
	
	
	
	
	public static float ScoreToCoefCréativité(float score) {
		float coef1 = scoretocaculcoef(score) * CoefCréativité;
		return coef1;
		
	}
	
	public static float ScoreToCoefArchitecture(float score) {
		float coef2 = scoretocaculcoef(score) * CoefArchitecture;
		return coef2;
		
	}
	
	public static float ScoreToCoefDensité(float score) {
		float coef3 = scoretocaculcoef(score) * CoefDensité;
		return coef3;
		
		
	}
	



	public static float ScoreToCoefÉquilibre(float score) {
		float coef4 = scoretocaculcoef(score) * CoefÉquilibre;
		return coef4;
		
	}
	
	public static float ScoreToCoefFinition(float score) {
		float coef5 = scoretocaculcoef(score) * CoefFinition;
		return coef5;
		
	}
	
	
	public static float scoretocaculcoef(float score) {
		return score / 100f;
	}
	
	public static float getCoefCréativité() {
		return CoefCréativité;
	}


	public static float getCoefArchitecture() {
		return CoefArchitecture;
	}



	public static float getCoefDensité() {
		return CoefDensité;
	}



	public static float getCoefÉquilibre() {
		return CoefÉquilibre;
	}



	public static float getCoefFinition() {
		return CoefFinition;
	}
	
	


}
