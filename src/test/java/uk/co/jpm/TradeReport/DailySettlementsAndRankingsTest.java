package uk.co.jpm.TradeReport;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.co.jpm.TradingLibrary.BusinessRulesTest;
import uk.co.jpm.TradingLibrary.TradingData;

/**
 *
 * @author smf
 */
public class DailySettlementsAndRankingsTest {
    
    // all tests will use one set of dates
    private final Date trans_dates [];
    
    // a test data set
    private final TradingData test_data [];
    
    // the DailySettlementsAndRankings objecct to test
    private final DailySettlementsAndRankings daily_sar;
    
    // daily totals for accumulated settlements
    private final double daily_outgoing_totals [];

    public DailySettlementsAndRankingsTest() {
        // dates for the test data - a Monday (2017-1-1 is a Sunday)
        trans_dates = new Date [3];
        GregorianCalendar cal = new GregorianCalendar (2017, 0, 2, 0, 0, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        trans_dates [0] = cal.getTime();
        cal.add (GregorianCalendar.DAY_OF_MONTH, 2);
        trans_dates [1] = cal.getTime();
        cal.add (GregorianCalendar.DAY_OF_MONTH, 7);
        trans_dates [2] = cal.getTime();
        
        // create some data - we want to test:
        //   data is accumulated into the correct daily SettlementsAndRankings objects
        test_data = new TradingData [] {
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [0], trans_dates [0], 100, 100.25),
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [0], trans_dates [0], 200, 100.25),
            
            new TradingData ("bar", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [1], trans_dates [1], 100, 100.25),
            new TradingData ("bar", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [1], trans_dates [1], 200, 100.25),
            
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [2], trans_dates [2], 100, 100.25),
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [2], trans_dates [2], 200, 100.25),
            new TradingData ("foo", TradingData.TradeType.BUY,  0.50, "SGP", trans_dates [2], trans_dates [2], 200, 100.25),
        };
        
        // build the SettlementsAndRankings object and fill it with the test data
        daily_sar = new DailySettlementsAndRankings ();
        for (TradingData td_val : test_data)
            daily_sar.append(td_val);
        
        // manually calculate daily totals from test data
        daily_outgoing_totals = new double [3];
        daily_outgoing_totals[0] = test_data[0].calcPriceUSD() + test_data[1].calcPriceUSD();
        daily_outgoing_totals[1] = test_data[2].calcPriceUSD() + test_data[3].calcPriceUSD();
        daily_outgoing_totals[2] = test_data[4].calcPriceUSD() + test_data[5].calcPriceUSD() + test_data[6].calcPriceUSD();
    }

    /**
     * Test of getDailySAR method, of class DailySettlementsAndRankings.
     * 
     * This method test the logic in DailySettlementsAndRankings.append
     */
    @Test
    public void testGetDailySAR() {
        System.out.println("getDailySAR");

        // get the map of daily results and convert to a list we can index numrically
        TreeMap<Date, SettlementsAndRankings> sar_map = daily_sar.getDailySAR();
        assertEquals (trans_dates.length, sar_map.size());
        Set sar_set = sar_map.entrySet();
        List<Map.Entry<Date, SettlementsAndRankings>> sar_list = new ArrayList (sar_set);

        // the first element from the list should hold data for the first transaction date (and so on)
        Map.Entry<Date, SettlementsAndRankings> entry = sar_list.get (0);
        SettlementsAndRankings sar = entry.getValue();
        assertEquals (trans_dates[0].getTime(), sar.getAllowedDate().getTime());
        assertEquals (daily_outgoing_totals[0], sar.getOutgoingTotalUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
        
        entry = sar_list.get (1);
        sar = entry.getValue();
        assertEquals (trans_dates[1].getTime(), sar.getAllowedDate().getTime());
        assertEquals (daily_outgoing_totals[1], sar.getOutgoingTotalUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
        
        entry = sar_list.get (2);
        sar = entry.getValue();
        assertEquals (trans_dates[2].getTime(), sar.getAllowedDate().getTime());
        assertEquals (daily_outgoing_totals[2], sar.getOutgoingTotalUSD(), BusinessRulesTest.TEST_USD_TOLERANCE);
    }
    
}
