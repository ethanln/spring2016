package dc_test;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import mnb.DC;
import mnb.MNBClassification;

public class DCTest {

	@Test
	public void test() {
		//try{
			//DC documentCollection = new DC(800, 200);
			//documentCollection.init("20NG");
		//}
		//catch(IOException e){
			
		//}
		
		MNBClassification mnb = new MNBClassification(800, 200);
		mnb.init("20NG");
		mnb.doClassification();
		//mnb.featureSelect(6200);
	}

}
