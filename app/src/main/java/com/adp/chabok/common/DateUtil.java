package com.adp.chabok.common;

import android.content.Context;

import com.adp.chabok.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtil {

    private Context context;

    public DateUtil(Context context) {
        this.context = context;
    }

    private class SolarCalendar {

        public String strWeekDay = "";
        public String strMonth = "";

        int date;
        int month;
        int year;
        int hour;
        int minute;
        int second;

        public SolarCalendar() {
            Date MiladiDate = new Date();
            calcSolarCalendar(MiladiDate, false);
        }

        public SolarCalendar(Date MiladiDate, boolean timeNeeded) {
            calcSolarCalendar(MiladiDate, timeNeeded);
        }

        private void calcSolarCalendar(Date MiladiDate, boolean timeNeeded) {

            int ld;

            int miladiYear = MiladiDate.getYear() + 1900;
            int miladiMonth = MiladiDate.getMonth() + 1;
            int miladiDate = MiladiDate.getDate();
            int WeekDay = MiladiDate.getDay();

            if (timeNeeded) {
                hour = MiladiDate.getHours();
                minute = MiladiDate.getMinutes();
                second = MiladiDate.getSeconds();
            }

            int[] buf1 = new int[12];
            int[] buf2 = new int[12];

            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;

            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;

            if ((miladiYear % 4) != 0) {
                date = buf1[miladiMonth - 1] + miladiDate;

                if (date > 79) {
                    date = date - 79;
                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = date / 31;
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    if ((miladiYear > 1996) && (miladiYear % 4) == 1) {
                        ld = 11;
                    } else {
                        ld = 10;
                    }
                    date = date + ld;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }
            } else {
                date = buf2[miladiMonth - 1] + miladiDate;

                if (miladiYear >= 1996) {
                    ld = 79;
                } else {
                    ld = 80;
                }
                if (date > ld) {
                    date = date - ld;

                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = (date / 31);
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    date = date + 10;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }

            }

            switch (month) {
                case 1:
                    strMonth = context.getString(R.string.monthFarvardin);
                    break;
                case 2:
                    strMonth = context.getString(R.string.monthOrdibehesht);
                    break;
                case 3:
                    strMonth = context.getString(R.string.monthKhordad);
                    break;
                case 4:
                    strMonth = context.getString(R.string.monthTir);
                    break;
                case 5:
                    strMonth = context.getString(R.string.monthMordad);
                    break;
                case 6:
                    strMonth = context.getString(R.string.monthShahrivar);
                    break;
                case 7:
                    strMonth = context.getString(R.string.monthMehr);
                    break;
                case 8:
                    strMonth = context.getString(R.string.monthAban);
                    break;
                case 9:
                    strMonth = context.getString(R.string.monthAzar);
                    break;
                case 10:
                    strMonth = context.getString(R.string.monthDay);
                    break;
                case 11:
                    strMonth = context.getString(R.string.monthBahman);
                    break;
                case 12:
                    strMonth = context.getString(R.string.monthEsfand);
                    break;
            }

            switch (WeekDay) {

                case 0:
                    strWeekDay = context.getString(R.string.weekSunday);
                    break;
                case 1:
                    strWeekDay = context.getString(R.string.weekMonday);
                    break;
                case 2:
                    strWeekDay = context.getString(R.string.weekTuesday);
                    break;
                case 3:
                    strWeekDay = context.getString(R.string.weekWednesday);
                    break;
                case 4:
                    strWeekDay = context.getString(R.string.weekThursday);
                    break;
                case 5:
                    strWeekDay = context.getString(R.string.weekFriday);
                    break;
                case 6:
                    strWeekDay = context.getString(R.string.weekSaturday);
                    break;
            }

        }

    }

    public static String getSolarDate(Context context, Date date, boolean weekNeeded, boolean timeNeeded) {
        String result = "";
        Locale loc = new Locale("en_US");
        DateUtil util = new DateUtil(context);
        SolarCalendar sc = util.new SolarCalendar(date, timeNeeded);
        if (weekNeeded) {
            result = sc.strWeekDay + " " + String.valueOf(sc.year) + "/" + String.format(loc, "%02d",
                    sc.month) + "/" + String.format(loc, "%02d", sc.date);
        } else {
            result = String.valueOf(sc.year) + "/" + String.format(loc, "%02d",
                    sc.month) + "/" + String.format(loc, "%02d", sc.date);
        }


        if (timeNeeded) {
            result += " " + String.format(loc, "%02d", sc.hour)
                    + ":" + String.format(loc, "%02d", sc.minute)
                    + ":" + String.format(loc, "%02d", sc.second);
        }

        return result;
    }

    public static int getCurrentSolarYear(Context context) {
        DateUtil util = new DateUtil(context);
        SolarCalendar sc = util.new SolarCalendar(new Date(), false);
        return sc.year;
    }

    public static int getCurrentSolarMonth(Context context) {
        DateUtil util = new DateUtil(context);
        SolarCalendar sc = util.new SolarCalendar(new Date(), false);
        return sc.month;
    }

    public static Date differDate(Date date, int differ) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, differ);
        return c.getTime();
    }

    public static Date differDate(Date date, Integer days, Integer hours, Integer minutes, Integer seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        if (days != null)
            c.add(Calendar.DATE, days);
        if (hours != null)
            c.add(Calendar.HOUR_OF_DAY, hours);
        if (minutes != null)
            c.add(Calendar.MINUTE, minutes);
        if (seconds != null)
            c.add(Calendar.SECOND, seconds);

        return c.getTime();
    }

    public static Date getDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date getDateNoTime(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Timestamp getDateNoTime(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new Timestamp(cal.getTimeInMillis());
    }

    public static Date getTimeNoDate(String strDate) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getTimeNoDate(Timestamp timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        return dateFormat.format(new Date(timestamp.getTime()));
    }

    public static String getTimeNoDateNoSecond(Timestamp timestamp, boolean hasSecond) {
        DateFormat dateFormat;
        if (hasSecond)
            dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        else
            dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        return dateFormat.format(new Date(timestamp.getTime()));
    }
}
