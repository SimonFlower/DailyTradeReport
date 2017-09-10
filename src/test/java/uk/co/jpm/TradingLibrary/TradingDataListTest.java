package uk.co.jpm.TradingLibrary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The test data for this test is a CSV file containing a header row and the two
 * rows of data from the example table in the project specification.
 * 
 * This test also tests the constructor of TradingData.
 * 
 * @author smf
 */
public class TradingDataListTest {
    
    private TradingDataList trd_list;
    
    public TradingDataListTest() {
        Reader reader = new InputStreamReader (this.getClass().getResourceAsStream("testTradingData.csv"));
        try {
            trd_list = new TradingDataList (reader);
        } catch (IOException e) {
            fail ("Failed to create TradingDataList");
        }
    }

    @Test
    public void testSomeMethod() {
        assertEquals (2, trd_list.size());
        TradingData trd = trd_list.get(0);
        assertEquals ("foo", trd.getEntityName());
        assertEquals (100.25, trd.getPricePerUnit(), BusinessRulesTest.TEST_USD_TOLERANCE);
        assertEquals (0.50 * 200.00 * 100.25, trd.calcPriceUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);

        trd = trd_list.get(1);
        assertEquals ("bar", trd.getEntityName());
        assertEquals (150.5, trd.getPricePerUnit(), BusinessRulesTest.TEST_USD_TOLERANCE);
        assertEquals (0.22 * 450.00 * 150.50, trd.calcPriceUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
    }
    
}
