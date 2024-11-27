package cat.gencat.ctti.std;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/canigo-core.xml"})
public class ApplicationTest {
	
	@Test(expected = Test.None.class)
    public void contextLoaded(){
		
	}

}
