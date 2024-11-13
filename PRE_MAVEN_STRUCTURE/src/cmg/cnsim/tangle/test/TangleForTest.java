package cmg.cnsim.tangle.test;

import cmg.cnsim.tangle.Tangle;
import cmg.cnsim.tangle.TangleCutSetFinder;

public class TangleForTest extends Tangle {

	
	public void printTangleLiteral() {
		System.out.print("{");
		for(int i=0;i<= super.maxTransaction;i++) {
			System.out.println("{" + getTangle()[i][0] + "," + getTangle()[i][1] + "," + getTangle()[i][2] + "},");
		}
		System.out.println("}");
	}
	
	public int[][] getTangle() {
		return(super.getTangle());
	}
	
	
	public TangleCutSetFinder getCutSetFinder() {
		return this.cutSetFinder;
	}
}
