package uk.co.jpm.TradeReport;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.co.jpm.TradingLibrary.BusinessRules;
import uk.co.jpm.TradingLibrary.BusinessRulesTest;
import uk.co.jpm.TradingLibrary.TradingData;

/**
 *
 * @author smf
 */
public class SettlementsAndRankingsTest {

    // all tests will use one set of dates
    private final Date trans_date;
    
    // a test data set
    private final TradingData test_data [];

    // the SettlementsAndRankings objecct to test
    private final SettlementsAndRankings sar;
    
    public SettlementsAndRankingsTest() {

        // dates for the test data - a Monday (2017-1-1 is a Sunday)
        GregorianCalendar cal = new GregorianCalendar (2017, 0, 2, 0, 0, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        trans_date = cal.getTime();
        
        // create some data - we want to test:
        //   separate accumulastion of BUY and SELL transactions on a single day
        //   entity rankings are properly ordered
        test_data = new TradingData [] {
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 100, 100.25),
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 200, 100.25),
            new TradingData ("bar", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 300, 100.25),
            new TradingData ("bar", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 400, 100.25),
            new TradingData ("did", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 500, 100.25),
            new TradingData ("did", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 600, 100.25),
            new TradingData ("dod", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 700, 100.25),
            new TradingData ("dod", TradingData.TradeType.BUY,  0.50, "SGP", trans_date, trans_date, 800, 100.25),
            
            new TradingData ("dod", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 100, 100.25),
            new TradingData ("dod", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 200, 100.25),
            new TradingData ("did", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 300, 100.25),
            new TradingData ("did", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 400, 100.25),
            new TradingData ("bar", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 500, 100.25),
            new TradingData ("bar", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 600, 100.25),
            new TradingData ("foo", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 700, 100.25),
            new TradingData ("foo", TradingData.TradeType.SELL, 0.60, "SGP", trans_date, trans_date, 800, 100.25)
        };
        
        // build the SettlementsAndRankings object and fill it with the test data
        sar = new SettlementsAndRankings (trans_date);
        for (TradingData td_val : test_data)
            sar.append(td_val);
    }

    /**
     * Test of append method, of class SettlementsAndRankings.
     */
    @Test
    public void testAppend() {
        System.out.println("append");
        
        // append a data value outside the data range to check we get an exception
        // add a day to the allowed date for this SettelemntsAndRankings test object
        Date bad_date = new Date (trans_date.getTime() + 86400000l);    
        TradingData td = new TradingData ("aaa", TradingData.TradeType.BUY, 0.50, "SGP", bad_date, bad_date, 100, 100.25);
        boolean exception_found = false;
        try {
            sar.append (td);
        } catch (RuntimeException e) {
            exception_found = true;
        }
        assertEquals(true, exception_found);
    }

    /**
     * Test of getIncomingTotalUSD method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetIncomingTotalUSD() {
        System.out.println("getIncomingTotalUSD");
        
        // calculate USD total from test data
        double usd_total = 0.0;
        for (TradingData td_val : test_data) {
            double usd = BusinessRules.calcPriceUSD(td_val.getPricePerUnit(), td_val.getNumberOfUnits(), td_val.getAgreedFX());
            if (td_val.getTradeType() == TradingData.TradeType.SELL)
                usd_total += usd;
        }
        assertEquals (usd_total, sar.getIncomingTotalUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
    }

    /**
     * Test of getOutgoingTotalUSD method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetOutgoingTotalUSD() {
        System.out.println("getOutgoingTotalUSD");

        // calculate USD total from test data
        double usd_total = 0.0;
        for (TradingData td_val : test_data) {
            double usd = BusinessRules.calcPriceUSD(td_val.getPricePerUnit(), td_val.getNumberOfUnits(), td_val.getAgreedFX());
            if (td_val.getTradeType() == TradingData.TradeType.BUY)
                usd_total += usd;
        }
        assertEquals (usd_total, sar.getOutgoingTotalUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
    }

    /**
     * Test of createIncomingRankings method, of class SettlementsAndRankings.
     */
    @Test
    public void testCreateIncomingRankings() {
        System.out.println("getIncomingRankings");
        
        // the Map returned from getIncomingRankings() is ordered by the total USD transaction
        // value for each entity, so iterating gives the entity's rankings
        List <Map.Entry<String, Double>> ranks = sar.createIncomingRankings();
        int count = 0;
        assertEquals (4, ranks.size());
        for (Map.Entry<String, Double> val : ranks) {
            switch (count ++) {
                case 0: assertEquals ("foo", val.getKey()); break;
                case 1: assertEquals ("bar", val.getKey()); break;
                case 2: assertEquals ("did", val.getKey()); break;
                case 3: assertEquals ("dod", val.getKey()); break;
                default: fail ("Too many entities in incoming rankings"); break;
            }
        }
    }

    /**
     * Test of createOutgoingRankings method, of class SettlementsAndRankings.
     */
    @Test
    public void testCreateOutgoingRankings() {
        System.out.println("getOutgoingRankings");

        // the Map returned from getOutgoingRankings() is ordered by the total USD transaction
        // value for each entity, so iterating gives the entity's rankings
        List <Map.Entry<String, Double>> ranks = sar.createOutgoingRankings();
        int count = 0;
        assertEquals (4, ranks.size());
        for (Map.Entry<String, Double> val : ranks) {
            switch (count ++) {
                case 0: assertEquals ("dod", val.getKey()); break;
                case 1: assertEquals ("did", val.getKey()); break;
                case 2: assertEquals ("bar", val.getKey()); break;
                case 3: assertEquals ("foo", val.getKey()); break;
                default: fail ("Too many entities in outgoing rankings"); break;
            }
        }
    }

    /**
     * Test of getAllowedDate method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetAllowedDate() {
        System.out.println("getAllowedDate");
        
        assertEquals (trans_date.getTime(), sar.getAllowedDate().getTime());
    }
    
    /**
     * Test of getEarlistActualSettlementDate method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetEarlistActualSettlementDate() {
        System.out.println("getEarlistActualSettlementDate");
        
        assertEquals (trans_date.getTime(), sar.getEarlistActualSettlementDate().getTime());
    }

    /**
     * Test of getLatestActualSettlementDate method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetLatestActualSettlementDate() {
        System.out.println("getLatestActualSettlementDate");

        assertEquals (trans_date.getTime(), sar.getLatestActualSettlementDate().getTime());
    }

    /**
     * Test of getNSettlements method, of class SettlementsAndRankings.
     */
    @Test
    public void testGetNSettlements() {
        System.out.println("getNSettlements");
        
        assertEquals (test_data.length, sar.getNSettlements());
    }

}
