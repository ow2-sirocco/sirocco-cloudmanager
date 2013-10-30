/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 */
package org.ow2.sirocco.cloudmanager.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static Logger logger = LoggerFactory.getLogger(DateUtils.class.getName());

    private final static String ISO8601_DATE_FORMAT = "yyyy-MM-dd";

    private final static String DATE_REG_EXP = "((?:19|20)(?:\\d\\d))-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

    private final static String TIME_REG_EXP = "T([01][0-9]|2[0123]):([0-5][0-9]):([0-5][0-9])(.\\d\\d\\d)?";

    private final static String TIME_ZONE_REG_EXP = "(Z|((?:\\+|-)[0-5][0-9]:[0-5][0-9]))?";

    private final static String ISO8601_REG_EXP = "^" + DateUtils.DATE_REG_EXP + DateUtils.TIME_REG_EXP
        + DateUtils.TIME_ZONE_REG_EXP + "$";

    private final static Pattern ISO8601Pattern = Pattern.compile(DateUtils.ISO8601_REG_EXP);

    private final static Pattern ISO8601DatePattern = Pattern.compile(DateUtils.DATE_REG_EXP);

    public static String convertISO8601DateToSQLDate(String value) throws ParseException {
        Calendar date = null;

        try {
            Matcher dateTimeMatcher = DateUtils.ISO8601Pattern.matcher(value);

            if (dateTimeMatcher.matches()) {

                StringBuilder pattern = new StringBuilder("yyyy-MM-dd'T'HH:mm:ss");
                String timeZoneId = null;
                // We must decide which pattern to use
                // At this point this is the minimum

                // Group 7 corresponds to milli seconds
                if (dateTimeMatcher.groupCount() >= 7 && dateTimeMatcher.group(7) != null) {
                    pattern.append(".SSS");
                }
                if (dateTimeMatcher.groupCount() >= 8 && dateTimeMatcher.group(8) != null) {
                    if (!"Z".equals(dateTimeMatcher.group(8))) {
                        // Keep UTC info to look for time zone when Calendar object will be created,
                        // as SimpleDateformat which is used in DateUtils
                        // cannot handle time zone designator
                        timeZoneId = "GMT" + dateTimeMatcher.group(8);
                        value = value.replace(dateTimeMatcher.group(8), "");
                    } else {
                        timeZoneId = "GMT";
                        value = value.replace("Z", "");
                    }
                }

                date = DateUtils.fromString(value, pattern.toString());

                if (timeZoneId != null) {
                    // Now that date is found we should define its TimeZone
                    TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
                    if (!timeZone.getID().equals(timeZoneId)) {
                        // Time Zone is not valid
                        throw new ParseException("Invalid time zone in date value " + value);
                    }

                    date.setTimeZone(timeZone);
                }
            } else {

                // check for simple date
                Matcher dateMatcher = DateUtils.ISO8601DatePattern.matcher(value);
                if (dateMatcher.matches()) {
                    date = DateUtils.fromString(value, DateUtils.ISO8601_DATE_FORMAT);
                }
            }

        } catch (Exception e) {
            // Probably not a date value. Ignore exception
            throw new ParseException(e.getMessage());
        }

        if (date != null) {
            return new java.sql.Timestamp(date.getTime().getTime()).toString();
        } else {
            throw new ParseException("Wrong datetime format");
        }

    }

    public static Calendar clearTimeFromCalendar(final Calendar calendar) {
        if (calendar != null) {
            calendar.set(Calendar.HOUR, 0);
            calendar.clear(Calendar.AM_PM); // ALWAYS clear AM_PM before HOUR_OF_DAY
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.clear(Calendar.DST_OFFSET);
            calendar.clear(Calendar.ZONE_OFFSET);
        }

        return calendar;
    }

    public static Calendar addLocaleToCalendar(final Calendar date, final String locale) {
        if (date != null) {
            if (locale != null) {
                Calendar localizedDate = Calendar.getInstance(new Locale(locale));
                localizedDate.setTime(date.getTime());
                return localizedDate;
            } else {
                return date;
            }
        }

        return null;
    }

    private final static String Default_Pattern = "dd/MM/yyyy HH:mm:ss.SSS";

    public static Calendar fromString(final String date) {
        return DateUtils.fromString(date, DateUtils.Default_Pattern);
    }

    public static Calendar fromString(final String date, final String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);

        try {
            return DateUtils.toCalendar(df.parse(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static Calendar toCalendar(final Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;
    }

}
