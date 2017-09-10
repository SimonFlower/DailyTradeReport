package uk.co.jpm.TradeReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.co.jpm.TradingLibrary.TradingData;

/**
 * A class to accumulate trading statistics.
 * 
 * @author smf
 */
public class SettlementsAndRankings {

    // date bound for settlements - null means no bound
    private Date allowed_date;
    
    // dates found in settlements
    private Date earliest_found_date;
    private Date latest_found_date;
    
    // total number of settlements
    private int n_settlements = 0;
    
    // totals for incoming and outgoing settlements
    private double total_incoming_settlements;
    private double total_outgoing_settlements;
    
    // incoming and outgoing entity rankings
    private Map <String, Double> entity_incoming_rankings;
    private Map <String, Double> entity_outgoing_rankings;
    
    /** Create a new SettlementsAndRnakings object specifying the valid date for settlements.
     * 
     * @param allowed_date Actual settlement dates must be on this date, null indicates no bound.
     */
    public SettlementsAndRankings (Date allowed_date) {
        this.allowed_date = allowed_date;
        earliest_found_date = null;
        latest_found_date = null;
        n_settlements = 0;
        total_incoming_settlements = 0.0;
        total_outgoing_settlements = 0.0;
        entity_incoming_rankings = new HashMap<> ();
        entity_outgoing_rankings = new HashMap<> ();
    }

    /** Append a transaction to the accumulation of results.
     * 
     * @param trading_data The transaction to append.
     */
    public void append (TradingData trading_data) {
        // check date bound
        if (allowed_date != null)
        {
            if (trading_data.getActualSettlementDate().getTime() != allowed_date.getTime())
                throw new RuntimeException ("SettlementsAndRankings.append passed invalid date: " + trading_data.getActualSettlementDate().toString());
        }
        
        // general information
        if (earliest_found_date == null)
            earliest_found_date = trading_data.getActualSettlementDate();
        else if (trading_data.getActualSettlementDate().getTime() < earliest_found_date.getTime())
            earliest_found_date = trading_data.getActualSettlementDate();
        if (latest_found_date == null)
            latest_found_date = trading_data.getActualSettlementDate();
        else if (trading_data.getActualSettlementDate().getTime() > latest_found_date.getTime())
            latest_found_date = trading_data.getActualSettlementDate();
        n_settlements ++;
        
        // accumulate settlement totals and trading entity rankings
        double trade_value = trading_data.calcPriceUSD();
        switch (trading_data.getTradeType()) {
            case SELL: 
                total_incoming_settlements += trade_value; 
                accumulateRankingValue (entity_incoming_rankings, trading_data.getEntityName(), trade_value);
                break;
            case BUY:  
                total_outgoing_settlements += trade_value; 
                accumulateRankingValue (entity_outgoing_rankings, trading_data.getEntityName(), trade_value);
                break;
            default: 
                throw new RuntimeException ("Bad trading type: " + trading_data.getTradeType().toString());
        }
    }
    
    /** Get the total of incoming trades.
     * @return The total in USD. */
    public double getIncomingTotalUSD () { return total_incoming_settlements; }
    
    /** Get the total of outgoing trades.
     * @return The total in USD. */
    public double getOutgoingTotalUSD () { return total_outgoing_settlements; }
    
    /** Create a List of entities with the total incoming trades each has transacted, in order of highest total.
     * @return The list, sorted into ascending order. */
    public List<Map.Entry<String, Double>> createIncomingRankings () { return createSortedListFromMap (entity_incoming_rankings); }

    /** Create a List of entities with the total outgoing trades each has transacted, in order of highest total.
     * @return The list, sorted into ascending order. */
    public List<Map.Entry<String, Double>> createOutgoingRankings () { return createSortedListFromMap (entity_outgoing_rankings); }

    /** get the allowed date for this set of SettlementsAndRankings
     * @return the allowed date which will be null if there is no date bound */
    public Date getAllowedDate () { return allowed_date; }
    
    /** Get the earliest settlement date in the statistics or null.
     * @return May return null if no trades have been appended. */
    public Date getEarlistActualSettlementDate () { return earliest_found_date; }

    /** Get the latest settlement date in the statistics or null.
     * @return May return null if no trades have been appended. */
    public Date getLatestActualSettlementDate () { return latest_found_date; }
    
    /** Get the number of settlements transacted.
     * @return The number of settlements. */
    public int getNSettlements () { return n_settlements; }

    /** Helper function to accumulate rankings for entities into a Map
     * @param entity_rankings the map
     * @param entity_name the entity name to add or accumulate in the map
     * @param trade_value the value of the transaction to accumulate
     */
    private static void accumulateRankingValue (Map<String, Double> entity_rankings, String entity_name, double trade_value) {
        Double total_value = entity_rankings.get (entity_name);
        if (total_value == null) total_value = new Double (0.0);
        total_value = new Double (total_value.doubleValue() + trade_value);
        entity_rankings.put (entity_name, total_value);
    }
    
    /** Helper function to convert an unsorted Map to a sorted List. Sorting is done
     * using the value of the Double map values (could be implemented using generics for
     * a more general solution. Sort order is reversed (greatest first).
     * @param unsorted_map the map to sort
     * @return the sorted List */
    private static List<Map.Entry<String, Double>> createSortedListFromMap (Map<String, Double> unsorted_map) {
        Set<Map.Entry<String, Double>> set = unsorted_map.entrySet();
        List<Map.Entry<String, Double>> list = new ArrayList<> (set);
        Collections.sort (list, (Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) -> {
            if (e1.getValue() < e2.getValue()) return 1;
            if (e1.getValue() > e2.getValue()) return -1;
            return 0;
        });
        return list;
    }
    
}
