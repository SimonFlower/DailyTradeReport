package uk.co.jpm.TradingLibrary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author smf
 */
public class TradingDataList extends ArrayList<TradingData> {
    
    /** Create an array of {@link TradingData} from a CSV file. The file should
     * have a header row followed by data rows. The order of cells in
     * data rows should be the same as that in the project specification 
     * example data table. Dates should be formatted as per the date_format 
     * object in the TradingData object.
     * 
     * @param csv_file The CSV file.
     * @throws IOException if there was an error in the CSV data
     */
    public TradingDataList (File csv_file) throws IOException {
        this (new FileReader (csv_file));
    }
    
    /** Create an array of {@link TradingData} from a reader. Allows class to be
     * tested with a CSV file on the classpath. The file should
     * have a header row followed by data rows. The order of cells in
     * data rows should be the same as that in the project specification 
     * example data table. Dates should be formatted as per the date_format 
     * object in the TradingData object.
     * 
     * @param reader The stream to read from.
     * @throws IOException if there was an error in the CSV data
     */
    public TradingDataList (Reader reader) throws IOException {
        super ();
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records)
            add (new TradingData (record));
    }
}
