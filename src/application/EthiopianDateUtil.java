package application;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.GregorianChronology;

/**
 * Utility class to convert Gregorian dates to Ethiopian calendar dates.
 *
 * @author Musab
 */
public class EthiopianDateUtil {

	/**
     * Converts a Gregorian date to an Ethiopian LocalDate.
     *
     * @param gregorianDate the date in Gregorian calendar
     * @return equivalent Ethiopian calendar date
     */
    public static LocalDate toEthiopianDate(java.time.LocalDate gregorianDate) {
        // Create DateTime with GregorianChronology
        DateTime gregDateTime = new DateTime(
                gregorianDate.getYear(),
                gregorianDate.getMonthValue(),
                gregorianDate.getDayOfMonth(),
                0, 0, GregorianChronology.getInstance()
        );

        // Convert to EthiopicChronology
        DateTime ethiopianDateTime = gregDateTime.withChronology(EthiopicChronology.getInstance());

        // Return as LocalDate
        return ethiopianDateTime.toLocalDate();
    }

    /**
     * Formats an Ethiopian date as a string.
     *
     * @param etDate the Ethiopian date
     * @return formatted Ethiopian date string
     */
    public static String formatEthiopianDate(LocalDate etDate) {
        return String.format("%02d - %02d - %04d",
                etDate.getDayOfMonth(),
                etDate.getMonthOfYear(),
                etDate.getYear());
    }
}
