package uk.co.jpm.TradingLibrary;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.csv.CSVRecord;

/**
 * A class to hold a single trade. AN important feature of Date objects used in
 * this class is that all Date values for the same day have the same time portion
 * as each other. Time information is not part of the description of a 
 * transaction, so the time is not needed for reporting, but other parts of the 
 * application need to be able to compare dates by date only (and not time).
 * 
 * @author smf
 */
public class TradingData {

    public enum TradeType { BUY, SELL }
    
    // data read from the input file
    private final String entity_name;
    private final TradeType trade_type;
    private final double agreed_fx;
    private final String currency_name;
    private final Date instruction_date;
    private final Date nominal_settlement_date;
    private final int number_of_units;
    private final double price_per_unit;
        
    // calculated data
    private final Date actual_settlement_date;
    
    // All dates are read/written through this formatter. Creating it is
    // expensive, so we have a single instance.
    private final static SimpleDateFormat date_format;
    
    static
    {
        // the call to TimeZone.getTimeZone with parameter "GMT" is safe because the method's
        // contract states that it will return GMT if it can't decode the timezone string
        date_format = new SimpleDateFormat ("dd-MMM-yyyy");
        date_format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    /** Create a TradingData record from a row in a CSV file. The order of cells in
     * the row should be the same as that in the project specification example data table.
     * Dates should be formatted for the date_format object in this class.
     * 
     * @param csv_record the CVS row
     * @throws IOException if there is a problem with the CSV data
     */
    public TradingData (CSVRecord csv_record) throws IOException {
        entity_name = csv_record.get(0);
        if (entity_name == null) throw new IOException ("Missing entity name");
        String trade_type_string = csv_record.get(1);
        if ("B".equalsIgnoreCase(trade_type_string))
            trade_type = TradeType.BUY;
        else if ("S".equalsIgnoreCase(trade_type_string))
            trade_type = TradeType.SELL;
        else
            throw new IOException ("Bad buy/sell code");
        try { agreed_fx = Double.parseDouble(csv_record.get (2)); }
        catch (NumberFormatException | NullPointerException e) { throw new IOException ("Bad or missing Agreed FX value"); }
        currency_name = csv_record.get (3);
        if (currency_name == null) throw new IOException ("Missing currency");
        try { instruction_date = date_format.parse(csv_record.get (4)); }
        catch (ParseException | NullPointerException e) { throw new IOException ("Bad or mising Instruction Date: " + csv_record.get(4)); }
        try { nominal_settlement_date = date_format.parse(csv_record.get (5)); }
        catch (ParseException | NullPointerException e) { throw new IOException ("Bad or mising Settlement Date: " + csv_record.get(5)); }
        try { number_of_units = Integer.parseInt(csv_record.get (6)); }
        catch (NumberFormatException | NullPointerException e) { throw new IOException ("Bad or missing Units"); }
        try { price_per_unit = Double.parseDouble(csv_record.get (7)); }
        catch (NumberFormatException | NullPointerException e) { throw new IOException ("Bad or missing Agreed FX value"); }
        
        this.actual_settlement_date = BusinessRules.findActualSettlementDate(nominal_settlement_date, currency_name);
    }

    /** manually initialise the TradingData record, for testing purposes
     * 
     * @param entity_name name of the entity responsbile for the trade
     * @param trade_type TradeType.BUY or TradeType.SELL
     * @param agreed_fx the exchange rate
     * @param currency_name the name of the currency
     * @param instruction_date date that the transaction was instructed
     * @param nominal_settlement_date date the that transaction was scheduled to be settled
     * @param number_of_units number of units to buy or sell
     * @param price_per_unit price of a unit in the given currency
     */
    public TradingData (String entity_name, TradeType trade_type, double agreed_fx,
                        String currency_name, Date instruction_date,
                        Date nominal_settlement_date,
                        int number_of_units, double price_per_unit)
    {
        this.entity_name = entity_name;
        this.trade_type = trade_type;
        this.agreed_fx = agreed_fx;
        this.currency_name = currency_name;
        this.instruction_date = instruction_date;
        this.nominal_settlement_date = nominal_settlement_date;
        this.number_of_units = number_of_units;
        this.price_per_unit = price_per_unit;
        
        this.actual_settlement_date = BusinessRules.findActualSettlementDate(nominal_settlement_date, currency_name);
    }
    
    public String getEntityName () { return entity_name; }
    public TradeType getTradeType () { return trade_type; }
    public double getAgreedFX () { return agreed_fx; }
    public String getCurrecnyName () { return currency_name; }
    public Date getInstructionDate () { return instruction_date; }
    public Date getNominalSettlemenetDate () { return nominal_settlement_date; }
    public int getNumberOfUnits () { return number_of_units; }
    public double getPricePerUnit () {return price_per_unit; }
    public Date getActualSettlementDate () { return actual_settlement_date; }

    public double calcPriceUSD () { return BusinessRules.calcPriceUSD(price_per_unit, number_of_units, agreed_fx); }
    
}
