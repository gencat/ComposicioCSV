package cat.gencat.ctti.std.composicio;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/canigo-core.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComposicioPDFTest {
	
	@Autowired
	private ComposicioPDF composicioPDF;

	@Test
    public void contextLoaded(){
		Assert.assertNotNull(composicioPDF);
	}
	
}
