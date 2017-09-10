package uk.co.jpm.TradeReport;

import java.util.Date;
import java.util.TreeMap;
import uk.co.jpm.TradingLibrary.TradingData;

/**
 * A class to hold a list of daily settlements and rankings as a Map of
 * Date and SettlementAndRankings objects. Once created individual TradingData
 * objects can be appended to the list. Daily SettlementsAndRankings results
 * can be retrieved from the list with getter objects. The daily SettlementsAndRankings
 * will be ordered by date (ascending).
 * 
 * @author smf
 */
public class DailySettlementsAndRankings {
    
    // using TreeMap for the list of daily settlements means the list will
    // be automaticaly sorted by date
    private TreeMap <Date, SettlementsAndRankings> daily_sar;
    
    public DailySettlementsAndRankings () {
        daily_sar = new TreeMap<> ();
    }
    
    /** Append a transaction to the accumulation of results.
     * @param trading_data The transaction to append.
     */
    public void append (TradingData trading_data) {
        // is there already a SettlementsAndRankins object in the map for this date?
        // if not create one
        Date trans_date = trading_data.getActualSettlementDate();
        SettlementsAndRankings sar = daily_sar.get (trans_date);
        if (sar == null) {
            sar = new SettlementsAndRankings(trans_date);
            daily_sar.put (trans_date, sar);
        }
        
        // append this TradingData to the daily SattlementsAndRankings
        sar.append (trading_data);
    }
    
    /** return the map of daily settlements and rankings
     * @return daily settlements and rankings ordered by date */
    public TreeMap<Date, SettlementsAndRankings> getDailySAR () { return daily_sar; }
}
