package uk.co.jpm.TradingLibrary;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A single place for the business rules.
 * 
 * @author smf
 */
public class BusinessRules {
    
    /** Encapsulates the business rules for finding the working days of the week in various countries
     * (currency being a proxy for country).
     * @param nominal_date Nominal settlement date (time information ignored).
     * @param currency Name of the current for the transaction.
     * @return Actual settlement date (time information ignored).
     */
    public static Date findActualSettlementDate (Date nominal_date, String currency) {
        // find the day of the week for the nominal date
        // the call to TimeZone.getTimeZone with parameter "GMT" is safe because the method's
        // contract states that it will return GMT if it can't decode the timezone string
        GregorianCalendar cal = new GregorianCalendar (TimeZone.getTimeZone("GMT"));
        cal.setTime(nominal_date);
        
        // add an offset to the nominal date to find the actual date of the settlement
        cal.add (GregorianCalendar.DAY_OF_MONTH, 
                 findActualSettlementOffset (cal.get (GregorianCalendar.DAY_OF_WEEK), currency));
        
        // return the actual date
        return new Date (cal.getTime().getTime());
    }
    
    /** given a day of the week and a currency (proxy for a country) get
     * the offset (in days) to the next possible trading day
     * @param day_of_week one of the day of the week constants from Gregorian Calendar
     * @param currency name of the currency
     * @return the offset in days
     */
    public static int findActualSettlementOffset (int day_of_week, String currency) {
        int offset = 0;
        if (currency.equalsIgnoreCase("AED") || currency.equalsIgnoreCase("SAR"))
        {
            switch (day_of_week)
            {
                case GregorianCalendar.SUNDAY: 
                case GregorianCalendar.MONDAY:
                case GregorianCalendar.TUESDAY:
                case GregorianCalendar.WEDNESDAY:
                case GregorianCalendar.THURSDAY:
                    break;
                case GregorianCalendar.FRIDAY:
                    offset = 2; 
                    break;
                case GregorianCalendar.SATURDAY:
                    offset = 1; 
                    break;
                default:
                    throw new RuntimeException ("Error with DAY_OF_WEEK field: " + day_of_week);
            }
        }
        else
        {
            switch (day_of_week)
            {
                case GregorianCalendar.SUNDAY: 
                    offset = 1; 
                    break;
                case GregorianCalendar.MONDAY:
                case GregorianCalendar.TUESDAY:
                case GregorianCalendar.WEDNESDAY:
                case GregorianCalendar.THURSDAY:
                case GregorianCalendar.FRIDAY:
                    break;
                case GregorianCalendar.SATURDAY:
                    offset = 2; 
                    break;
                default:
                    throw new RuntimeException ("Error with DAY_OF_WEEK field: " + day_of_week);
            }
        }
        return offset;
    }    
    
    /** implements the rule:
     *   USD amount of a trade = Price per unit * Units * Agreed Fx
     * @param price_per_unit Price per unit
     * @param number_of_units number of units
     * @param agreed_fx agreed exchange rate
     * @return the equivalent price in US dollars
     */
    public static double calcPriceUSD (double price_per_unit, int number_of_units, double agreed_fx) {
        return price_per_unit * (double) number_of_units * agreed_fx;
    }
    
}
