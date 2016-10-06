package com.mark.prosper.invest.util;

import android.util.Log;
import android.view.View;

import com.mark.prosper.invest.R;
import com.mark.prosper.invest.constant.ApplicationConstants;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeVeX on 9/24/2015.
 */
public class UIUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT_NO_FRACTIONAL = new DecimalFormat("$#,##0");
    private static final DecimalFormat DECIMAL_FORMAT_CONDENSED_NO_FRACTIONAL = new DecimalFormat("##0");
    private static final DecimalFormat DECIMAL_FORMAT_CONDENSED = new DecimalFormat("##0.0");
    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
    private static final NumberFormat PERCENT_FORMAT_NO_FRACTIONAL = NumberFormat.getPercentInstance();

    private static final long ONE_WEEK_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(7);
    private static final long ONE_DAY_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(1);
    private static final long ONE_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1);

    private static final String WEEK = " Week";
    private static final String WEEKS = " Weeks";
    private static final String DAY = " Day";
    private static final String DAYS = " Days";
    private static final String HOUR = " Hour";
    private static final String HOURS = " Hours";


    static
    {
        PERCENT_FORMAT.setMinimumIntegerDigits(1);
        PERCENT_FORMAT.setMinimumFractionDigits(2);
        PERCENT_FORMAT_NO_FRACTIONAL.setMinimumIntegerDigits(1);
        PERCENT_FORMAT_NO_FRACTIONAL.setMinimumFractionDigits(0);
        PERCENT_FORMAT_NO_FRACTIONAL.setMaximumFractionDigits(0);

        DECIMAL_FORMAT_CONDENSED_NO_FRACTIONAL.setRoundingMode(RoundingMode.FLOOR);
        DECIMAL_FORMAT_CONDENSED_NO_FRACTIONAL.setMaximumFractionDigits(0);
        DECIMAL_FORMAT_CONDENSED.setRoundingMode(RoundingMode.FLOOR);
        DECIMAL_FORMAT_CONDENSED.setMaximumFractionDigits(1);

    }

    public static String getMoneyString(double d)
    {
        return getMoneyString(d, true);
    }

    public static String getMoneyString(double d, boolean includeFractional)
    {
        if ( includeFractional) {
            return DECIMAL_FORMAT.format(d);
        }
        return DECIMAL_FORMAT_NO_FRACTIONAL.format(d);
    }

    public static String getPercentageString(double d)
    {
        return getPercentageString(d, true);
    }

    public static String getPercentageString(double d, boolean includeFractional)
    {
        if ( includeFractional) {
            return PERCENT_FORMAT.format(d);
        }
        return PERCENT_FORMAT_NO_FRACTIONAL.format(d);
    }

    public static int tryGetInteger(String s)
    {
        try {
            return Integer.valueOf(s);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static double tryGetDouble(String s)
    {
        try {
            return Double.valueOf(s);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static Integer getResourceColorIdBasedOnProsperRating(String prosperRating) {
        if ( prosperRating != null )
        {
            switch (prosperRating)
            {
                case "AA": return R.color.prosper_rating_aa_bg;
                case "A":  return R.color.prosper_rating_a_bg;
                case "B":  return R.color.prosper_rating_b_bg;
                case "C":  return R.color.prosper_rating_c_bg;
                case "D":  return R.color.prosper_rating_d_bg;
                case "E":  return R.color.prosper_rating_e_bg;
                case "HR": return R.color.prosper_rating_hr_bg;
            }
         }
        return null;
    }


    public static String getPresentableTimeBetweenTwoDates(Date dateOne, Date dateTwO) {
        List<String> strings = new ArrayList<>();
        if ( dateOne != null && dateTwO != null)
        {
            long timeBetween = Math.abs(dateOne.getTime() - dateTwO.getTime());
            long weeksRemaining = timeBetween / ONE_WEEK_IN_MILLISECONDS;
            boolean weekAdded = false;
            if ( weeksRemaining > 0)
            {
                strings.add(weeksRemaining + ( weeksRemaining > 1 ? WEEKS : WEEK));
                timeBetween -= (weeksRemaining * ONE_WEEK_IN_MILLISECONDS);
                weekAdded = true;
            }
            long daysRemaining = timeBetween / ONE_DAY_IN_MILLISECONDS;
            if ( daysRemaining > 0)
            {
                strings.add(daysRemaining + ( daysRemaining > 1 ? DAYS : DAY));
                timeBetween -= (daysRemaining * ONE_DAY_IN_MILLISECONDS);
            }

            if ( !weekAdded ) {
                long hoursRemaining = timeBetween / ONE_HOUR_IN_MILLISECONDS;
                if (hoursRemaining > 0) {
                    strings.add(hoursRemaining + (hoursRemaining > 1 ? HOURS : HOUR));
                }

                if (strings.size() == 0) {
                    strings.add("Less than an hour");
                }
            }
        }
        String returnString = "";
        for ( String s : strings)
        {
            returnString += s + " ";
        }
        return returnString.trim();
    }

    public static String getVerificationStageText(int verificationStage) {
        switch (verificationStage)
        {
            case 1 : return "1st";
            case 2 : return "2nd";
            case 3 : return "3rd";
            default: return "N/A";
        }
    }

    public static int tryGetIntegerFromMoneyString(String moneyString) {
        return tryGetInteger(moneyString.replace("$", "").replace(",", ""));
    }

    public static double tryGetDoubleFromMoneyString(String moneyString) {
        return tryGetDouble(moneyString.replace("$", "").replace(",", ""));
    }

    public static int tryGetIntegerFromPercentString(String moneyString) {
        return tryGetInteger(moneyString.replace("%", ""));
    }

    public static double tryGetDoubleFromPercentString(String moneyString) {
        return tryGetDouble(moneyString.replace("%", ""));
    }

    public static String getCondensedMoneyString(double value) {

        double newValue;
        String amountSign = "";
        if ( value >= 1000000000)
        {
            newValue = ( value / 1000000000);
            amountSign = "B";
        }
        else if ( value >= 1000000)
        {
            newValue = ( value / 1000000);
            amountSign = "M";
        }
        else if ( value >= 1000)
        {
            newValue = ( value / 1000);
            amountSign = "K";
        }
        else
        {
            newValue = value;
        }

        String returnValue;
        if ( newValue > 99 || amountSign.length() == 0)
        {
            returnValue = DECIMAL_FORMAT_CONDENSED_NO_FRACTIONAL.format(newValue);
        }
        else
        {
            returnValue = DECIMAL_FORMAT_CONDENSED.format(newValue);
        }
        returnValue += amountSign;
        Log.d(ApplicationConstants.LOG_TAG, "["+newValue+"] became ["+returnValue+"]");
        return "$" + returnValue;
    }
}
