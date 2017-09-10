package uk.co.jpm.TradingLibrary;

import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author smf
 */
public class BusinessRulesTest {
    
    public static final double TEST_USD_TOLERANCE = 0.0001;
    
    public BusinessRulesTest() {
    }

    /**
     * Test of findActualSettlementDate method, of class BusinessRules.
     */
    @Test
    public void testFindActualSettlementDate() {
        System.out.println("findActualSettlementDate");
        
        // 2017-1-1 was a Sunday
        GregorianCalendar test_date = new GregorianCalendar (2017, 0, 1, 12, 0, 0);
        test_date.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        // use a date format to generate dates that we can compare against literal strings
        SimpleDateFormat date_format = new SimpleDateFormat ("yyyy-MM-dd");
        date_format.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Sunday
        assertEquals ("2017-01-02", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-01", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Monday
        assertEquals ("2017-01-02", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-02", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Tuesday
        assertEquals ("2017-01-03", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-03", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Wednesday
        assertEquals ("2017-01-04", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-04", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Thursday
        assertEquals ("2017-01-05", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-05", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Friday
        assertEquals ("2017-01-06", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-08", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
        test_date.add (GregorianCalendar.DAY_OF_MONTH, 1);
        // Saturday
        assertEquals ("2017-01-09", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "SGP")));
        assertEquals ("2017-01-08", date_format.format (BusinessRules.findActualSettlementDate (test_date.getTime(), "AED")));
    }

    /**
     * Test of findActualSettlementOffset method, of class BusinessRules.
     */
    @Test
    public void testFindActualSettlementOffset() {
        System.out.println("findActualSettlementOffset");
        
        assertEquals (1, BusinessRules.findActualSettlementOffset(GregorianCalendar.SUNDAY, "SGP"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.SUNDAY, "AED"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.MONDAY, "SGP"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.MONDAY, "AED"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.TUESDAY, "SGP"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.TUESDAY, "AED"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.WEDNESDAY, "SGP"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.WEDNESDAY, "AED"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.THURSDAY, "SGP"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.THURSDAY, "AED"));
        assertEquals (0, BusinessRules.findActualSettlementOffset(GregorianCalendar.FRIDAY, "SGP"));
        assertEquals (2, BusinessRules.findActualSettlementOffset(GregorianCalendar.FRIDAY, "AED"));
        assertEquals (2, BusinessRules.findActualSettlementOffset(GregorianCalendar.SATURDAY, "SGP"));
        assertEquals (1, BusinessRules.findActualSettlementOffset(GregorianCalendar.SATURDAY, "AED"));
    }

    /**
     * Test of calcPriceUSD method, of class BusinessRules.
     */
    @Test
    public void testCalcPriceUSD() {
        System.out.println("calcPriceUSD");
        
        double price_per_unit = 100.25;
        int number_of_units = 200;
        double agreed_fx = 0.50;
        double expResult = price_per_unit * (double) number_of_units * agreed_fx;
        double result = BusinessRules.calcPriceUSD(price_per_unit, number_of_units, agreed_fx);
        assertEquals(expResult, result, TEST_USD_TOLERANCE);
        
        price_per_unit = 150.50;
        number_of_units = 450;
        agreed_fx = 0.22;
        expResult = price_per_unit * (double) number_of_units * agreed_fx;
        result = BusinessRules.calcPriceUSD(price_per_unit, number_of_units, agreed_fx);
        assertEquals(expResult, result, TEST_USD_TOLERANCE);
    }
    
}
