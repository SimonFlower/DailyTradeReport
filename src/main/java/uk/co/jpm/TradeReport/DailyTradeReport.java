package uk.co.jpm.TradeReport;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import uk.co.jpm.TradingLibrary.TradingData;
import uk.co.jpm.TradingLibrary.TradingDataList;

/**
 * A program that reads individual trading data transactions to create a daily
 * trading report. Input data is in CSV format. See {@link TradingDataList} for
 * details of the format. An example CSV file is in this project's test resources.
 * 
 * @author smf
 */
public class DailyTradeReport {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // check command line
            if (args.length != 1)
                handleError ("Missing command line argument: name of input CSV file", null);
            
            // load data from CSV file
            TradingDataList trading_data_list = new TradingDataList (new File (args[0]));
            
            // create SettlementsAndRankings from trading data - overall_sar holds the settlements and
            // rankings for the whole of the input data, daily_sar holds the daily settlements and rankings
            SettlementsAndRankings overall_sar = new SettlementsAndRankings(null);
            DailySettlementsAndRankings daily_sars = new DailySettlementsAndRankings();
            for (TradingData transaction : trading_data_list) {
                overall_sar.append(transaction);
                daily_sars.append(transaction);
            }
            
            // write reports to the console
            System.out.println ("Daily Trade Reports");
            for (Map.Entry<Date, SettlementsAndRankings> daily_entry : daily_sars.getDailySAR().entrySet())
                printSAR (System.out, daily_entry.getValue());
            printSAR (System.out, overall_sar);
        } catch (Exception e) {
            // It's generally not good style to catch Exception - instead we should
            // catch specific sub-classes. However it makes sense to do so here because
            // this enusres that any uncaught exceptions are handled gracefully for the 
            // whole program. The xception handler is capable of differentiating
            // exception types
            handleError (null, e);
        }
    }
    
    /** Handle any errors by printing an error messages and exiting with
     * a non-zero status code. Descendants of RuntimeException are handled
     * differently to other exceptions - they indicate an unexpected software
     * fault and so a stack trace is printed. Other exceptions only display
     * the error message associated with the exception.
     * 
     * @param errmsg an optional error message (may be null)
     * @param exception an optional exception (may be null)
     */
    public static void handleError (String errmsg, Exception exception) {
        // format error message
        System.err.println ("Error:");
        if (errmsg != null)
            System.err.println ("  " + errmsg);
        
        // format exception
        if (exception != null)
        {
            if (exception instanceof RuntimeException)
            {
                System.err.println ("  Unexpcted program fault:");
                exception.printStackTrace(System.err);
            }
            else
            {
                System.err.print ("  " + exception.getClass().getName());
                if (exception.getMessage() != null)
                    System.err.print (": " + exception.getMessage());
                System.err.println ();
            }
        }
        
        // inidicate error using non-zero exit status value
        System.exit (1);
    }
    
    public static void printSAR (PrintStream stream, SettlementsAndRankings sar) {
        
        // print title
        SimpleDateFormat date_format = new SimpleDateFormat ("dd-MMM-yyyy");
        date_format.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (sar.getAllowedDate() == null)
            stream.println ("  Overall total for " + date_format.format (sar.getEarlistActualSettlementDate()) +
                            " to " + date_format.format (sar.getLatestActualSettlementDate()) + ":");
        else
            stream.println ("  Total for " + date_format.format (sar.getAllowedDate()) + ":");
        
        // print total settlements for the period
        stream.printf  ("    Total incoming (USD): %.2f%n", sar.getIncomingTotalUSD());
        stream.printf  ("    Total outgoing (USD): %.2f%n", sar.getOutgoingTotalUSD());

        // print entity rankings for the period
        stream.println ("    Entity rankings, incoming:");
        List <Map.Entry<String, Double>> ranks = sar.createIncomingRankings();
        int rank_no = 1;
        for (Map.Entry<String, Double> entry : ranks)
            stream.printf ("      %d) %s (%.2f)%n", rank_no ++, entry.getKey(), entry.getValue());
        
        stream.println ("    Entity rankings, outgoing:");
        ranks = sar.createOutgoingRankings();
        rank_no = 1;
        for (Map.Entry<String, Double> entry : ranks)
            stream.printf ("      %d) %s (%.2f)%n", rank_no ++, entry.getKey(), entry.getValue());
    }
    
}
