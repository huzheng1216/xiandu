package com.inveno.xiandu.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author zheng.hu
 * @ClassName: DateTimeUtils
 * @Description: 时间日期工具类
 * @date 2015年8月10日 上午9:58:53
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeUtils
{
    // private static LogUtil logger = LogUtil.jLog();
    public static String mYear;
    public static String mMonth;
    public static String mDay;
    private static String mWay;
    
    // public static String getNowTime()
    // {
    // return getLongToString(new Date().getTime(), Constant.dateFormat3);
    // }
    
    /**
     * 格式化时间，将毫秒转换为时：分:秒格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time)
    {
        String hour = time / (1000 * 60 * 60) + "";
        String min = (int) (time % (1000 * 60 * 60)) / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (hour.length() < 2)
        {
            hour = "0" + hour;
        }
        if (min.length() < 2)
        {
            min = "0" + min + "";
        }
        else
        {
            min = min + "";
        }
        if (sec.length() == 4)
        {
            sec = "0" + sec + "";
        }
        else if (sec.length() == 3)
        {
            sec = "00" + sec + "";
        }
        else if (sec.length() == 2)
        {
            sec = "000" + sec + "";
        }
        else if (sec.length() == 1)
        {
            sec = "0000" + sec + "";
        }
        return hour + ":" + min + ":" + sec.trim().substring(0, 2);
    }
    
    /**
     * 格式化时间，将毫秒转换为分'秒''格式
     *
     * @param time
     * @return
     */
    public static String NewformatTime(long time)
    {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2)
        {
            min = "0" + time / (1000 * 60) + "";
        }
        else
        {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4)
        {
            sec = "0" + (time % (1000 * 60)) + "";
        }
        else if (sec.length() == 3)
        {
            sec = "00" + (time % (1000 * 60)) + "";
        }
        else if (sec.length() == 2)
        {
            sec = "000" + (time % (1000 * 60)) + "";
        }
        else if (sec.length() == 1)
        {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + "'" + sec.trim().substring(0, 2) + "''";
    }
    
    /**
     * 得到现在时间后几天的时间.如days=20,结果20070725093258656
     *
     * @param days
     * @return
     */
    public static String getRelativeDate(int days)
    {
        Calendar c = Calendar.getInstance();
        c.set(5, c.get(5) + days);
        StringBuffer sb = new StringBuffer(17);
        sb.append(c.get(1));
        int tmp[] = {
                c.get(2) + 1, c.get(5), c.get(11), c.get(12), c.get(13),
                c.get(14)
        };
        for (int i = 0; i < tmp.length - 1; i++)
            sb.append(tmp[i] >= 10 ? "" : "0").append(tmp[i]);
        
        if (tmp[tmp.length - 1] < 10)
            sb.append("0");
        if (tmp[tmp.length - 1] < 100)
            sb.append("0");
        sb.append(tmp[tmp.length - 1]);
        return sb.toString();
    }
    
    /**
     * t1和t2的差
     *
     * @param t1
     * @param t2
     * @return
     */
    public static long compare(String t1, String t2)
    {
        return Long.valueOf(t1).longValue() - Long.valueOf(t2).longValue();
    }
    
    /**
     * 得到Year，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getYear(String time)
    {
        return Integer.valueOf(time.substring(0, 4)).intValue();
    }
    
    /**
     * 得到Month，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getMonth(String time)
    {
        return Integer.valueOf(time.substring(4, 6)).intValue();
    }
    
    /**
     * 得到Date，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getDate(String time)
    {
        return Integer.valueOf(time.substring(6, 8)).intValue();
    }
    
    /**
     * 得到Hour，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getHour(String time)
    {
        return Integer.valueOf(time.substring(8, 10)).intValue();
    }
    
    /**
     * 得到Minute，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getMinute(String time)
    {
        return Integer.valueOf(time.substring(10, 12)).intValue();
    }
    
    /**
     * 得到Second，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getSecond(String time)
    {
        return Integer.valueOf(time.substring(12, 14)).intValue();
    }
    
    /**
     * 得到MilliSencond，time是Date类型
     *
     * @param time
     * @return
     */
    public static int getMilliSencond(String time)
    {
        return Integer.valueOf(time.substring(14, 17)).intValue();
    }
    
    public static String secondToTime(String second)
    {
        return "";
    }
    
    /**
     * 将time(13位)格式化成指定string样式;如：1183605002016l,"yyyy年MM月dd日 HH时mm分ss秒"
     *
     * @param time
     * @param string
     * @return
     */
    public static String getLongToString(long time, String string)
    {
        // if (StringUtils.isNotEmpty(time + "") &&
        // StringUtils.isNotEmpty(string))
        // {
        // return (new SimpleDateFormat(string)).format(new Date(time));
        // }
        // else
        // {
        return null;
        // }
    }
    
    /**
     * 把myDate(Thu Jul 05 10:06:37 CST 2007格式)转化成指定string如(yyyy年MM月dd日HH时mm分ss秒)
     *
     * @param myDate
     * @param string
     * @return
     */
    public static String formatDate(Date myDate, String string)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(string);
        String time = formatter.format(myDate);
        return time;
    }
    
    /**
     * 将time指定格式string转化成long类型
     *
     * @param time
     * @param string
     * @return
     */
    public static long getStrToLong(String time, String string)
    {
        try
        {
            return (new SimpleDateFormat(string)).parse(time).getTime();
        }
        catch (ParseException ex)
        {
            ex.getStackTrace();
            return 0L;
        }
    }
    
    /**
     * 将time转成"yyyy-MM-dd HH:mm:ss"
     *
     * @param time
     * @return
     */
    public static String getShowFormat(long time)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
        // 初始化Formatter的转换格式。
        
        String hms = formatter.format(time);
        return hms;
    }
    /**
     * 将time转成"yyyy-MM-dd HH:mm:ss"
     *
     * @param time
     * @return
     */
    public static String getShowFormatNotSecond(long time)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");//
        // 初始化Formatter的转换格式。
        
        String hms = formatter.format(time);
        return hms;
    }
    
    /**
     * 将time指定string样式输出,两参数要匹配.如"2007-07-25 09:26:57","yyyy-MM-dd HH:mm:ss"
     *
     * @param time
     * @param string
     * @return
     */
    public static String getShowFormat(String time, String string)
    {
        SimpleDateFormat temp = new SimpleDateFormat(string);
        try
        {
            if (time == null || time.equals(""))
                time = temp.format(new Date());
            else
            {
                time = temp.format(temp.parse(time));
            }
        }
        catch (ParseException e)
        {
        }
        return time;
    }
    
    /**
     * 设置时间转换的方法
     *
     * @param paramInt
     * @return
     */
    public static String secondToTime(int paramInt)
    {
        int i = paramInt / 3600;
        int j = paramInt / 60;
        int k = paramInt - j * 60;
        String str = "%d" + "时" + "%d" + "分" + "%d" + "秒";
        Object[] arrayOfObject = new Object[3];
        arrayOfObject[0] = Integer.valueOf(i);
        arrayOfObject[1] = Integer.valueOf(j);
        arrayOfObject[2] = Integer.valueOf(k);
        return String.format(str, arrayOfObject);
    }
    
    /**
     * 设置时间转换的方法 -天-时-分-秒
     *
     * @param paramInt
     * @return
     */
    public static String secondToTimeDay(int paramInt)
    {
        String str;
        Object[] arrayOfObject;
        int d = paramInt / 86400;
        int i;
        int j;
        int k;
        if (d != 0)
        {
            d = paramInt / 86400;
            int time = paramInt % 86400;
            i = time / 3600;
            j = (time - (i * 3600)) / 60;
            k = time % 60;
            str = "%d" + "天" + "%d" + "时" + "%d" + "分" + "%d" + "秒";
            arrayOfObject = new Object[4];
            arrayOfObject[0] = Integer.valueOf(d);
            arrayOfObject[1] = Integer.valueOf(i);
            arrayOfObject[2] = Integer.valueOf(j);
            arrayOfObject[3] = Integer.valueOf(k);
        }
        else
        {
            i = paramInt / 3600;
            j = (paramInt - (i * 3600)) / 60;
            k = paramInt % 60;
            str = "%d" + "时" + "%d" + "分" + "%d" + "秒";
            arrayOfObject = new Object[3];
            arrayOfObject[0] = Integer.valueOf(i);
            arrayOfObject[1] = Integer.valueOf(j);
            arrayOfObject[2] = Integer.valueOf(k);
        }
        return String.format(str, arrayOfObject);
    }
    
    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(long timeStr)
    {
        StringBuffer sb = new StringBuffer();
        long time = System.currentTimeMillis() - timeStr;
        long mill = (long) Math.ceil(time / 1000);// 秒前
        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前
        if (day - 1 > 0)
        {
            if (day > 3 && day < 7)
            {
                sb.append("一周内");
            }
            else if (day == 3)
            {
                sb.append("前天");
            }
            else
            {
            }
        }
        else if (hour - 1 > 0)
        {
            if (hour >= 24)
            {
                sb.append("昨天");
            }
            else
            {
                sb.append(hour + "小时前");
            }
        }
        else if (minute - 1 > 0)
        {
            if (minute == 60)
            {
                sb.append("1小时前");
            }
            else
            {
                sb.append(minute + "分钟前");
            }
        }
        else if (mill - 1 > 0)
        {
            if (mill == 60)
            {
                sb.append("1分钟前");
            }
            else
            {
                sb.append(mill + "秒前");
            }
        }
        return sb.toString();
    }
    
    @SuppressLint("SimpleDateFormat")
    public static String getCharacterAndNumber()
    {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }
    
    /**
     * 将时间转换成秒
     *
     * @param string
     * @return
     */
    public static int dateToSecond(String string)
    {
        return Integer.valueOf(string.split(":")[0]) * 60
                + Integer.valueOf(string.split(":")[1]);
    }
    
    /**
     * 转成时：分：秒 格式
     */
    public static String secondToDate(int second)
    {
        String time;
        if (second % 60 > 9)
        {
            if (second - (second % 60) * 60 > 9)
            {
                time = String.valueOf(second % 60) + ":"
                        + String.valueOf(second - (second % 60) * 60);
            }
            else
            {
                time = String.valueOf(second % 60) + ":0"
                        + String.valueOf(second - (second % 60) * 60);
            }
        }
        else
        {
            if (second - (second % 60) * 60 > 9)
            {
                time = "0" + String.valueOf(second % 60) + ":"
                        + String.valueOf(second - (second % 60) * 60);
            }
            else
            {
                time = "0" + String.valueOf(second % 60) + ":0"
                        + String.valueOf(second - (second % 60) * 60);
            }
        }
        return time;
    }
    
    /**
     * 获取当前年月 yyyy/mm
     *
     * @return
     */
    public static String getYear_Mouth()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if (Integer.valueOf(mMonth) < 10)
        {
            return mYear + "-0" + mMonth;
        }
        else
        {
            return mYear + "-" + mMonth;
        }
        
    }
    
    /**
     * 获取当前年月 yyyymm
     *
     * @return
     */
    public static String getYearMouth()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if (Integer.valueOf(mMonth) < 10)
        {
            return mYear + "0" + mMonth;
        }
        else
        {
            return mYear + "" + mMonth;
        }
        
    }
    
    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getMouth()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        return mMonth;
    }
    
    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getYear()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        return mYear;
    }
    
    /**
     * 获取当前日期
     *
     * @return
     */
    public static int getDay()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
        return mDay;
    }
    
    /**
     * 获取昨天日期
     *
     * @return
     */
    public static String getYesterday()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal
                .getTime());
        return yesterday;
    }
    
    /**
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: StringData
     * @Description: 获取星期几
     */
    public static String getWeekInWeekends()
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay))
        {
            mWay = "Sunday";
        }
        else if ("2".equals(mWay))
        {
            mWay = "Monday";
        }
        else if ("3".equals(mWay))
        {
            mWay = "Tuesday";
        }
        else if ("4".equals(mWay))
        {
            mWay = "Wednesday";
        }
        else if ("5".equals(mWay))
        {
            mWay = "Thursday";
        }
        else if ("6".equals(mWay))
        {
            mWay = "Friday";
        }
        else if ("7".equals(mWay))
        {
            mWay = "Saturday";
        }
        // return mYear + "年" + mMonth + "月" + mDay + "日" + "/星期" + mWay;
        return mWay;
    }
    
    /**
     * <pre>
     * 根据指定的日期字符串获取星期几
     * </pre>
     *
     * @param strDate 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return week
     * 星期几(MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY)
     */
    public static int getWeekByDateStr(String strDate)
    {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));
        
        Calendar c = Calendar.getInstance();
        
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        
        String week = "";
        int weekIndex = c.get(Calendar.DAY_OF_WEEK);
        
        switch (weekIndex)
        {
            case 1:
                week = "SUNDAY";
                break;
            case 2:
                week = "MONDAY";
                break;
            case 3:
                week = "TUESDAY";
                break;
            case 4:
                week = "WEDNESDAY";
                break;
            case 5:
                week = "THURSDAY";
                break;
            case 6:
                week = "FRIDAY";
                break;
            case 7:
                week = "SATURDAY";
                break;
        }
        return weekIndex;
    }
    
    /**
     * 判断某天是那年的第几周
     *
     * @param strDate
     * @return
     */
    public static int getWeekOfYear(String strDate)
    {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));
        
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
        
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        
        return c.get(Calendar.WEEK_OF_YEAR);
    }
    
    /**
     * 判断今天是今年的第几周
     *
     * @return
     */
    public static int getWeekOfYear()
    {
        
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
        
    }
    
    /**
     * 判断月份是否与本月相等 yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameMonth(String data)
    {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        
        int year = Integer.parseInt(data.substring(0, 4));
        int month = Integer.parseInt(data.substring(5, 7));
        
        if (mYear == year && mMonth == month)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 判断月份是否与上月月相等 yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameLastMonth(String data)
    {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        
        if (mMonth == 1)
        {
            mYear = mYear - 1;
            mMonth = 12;
        }
        else
        {
            mMonth = mMonth - 1;
        }
        int year = Integer.parseInt(data.substring(0, 4));
        int month = Integer.parseInt(data.substring(5, 7));
        
        if (mYear == year && mMonth == month)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 判断周数是否与本周相等（周一开始） yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameWeek(String data)
    {
        final Calendar c1 = Calendar.getInstance();
        c1.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c1.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
        int mWeek = c1.get(Calendar.WEEK_OF_YEAR);
        
        int year = Integer.parseInt(data.substring(0, 4));
        int month = Integer.parseInt(data.substring(5, 7));
        int day = Integer.parseInt(data.substring(8, 10));
        
        Calendar c2 = Calendar.getInstance();
        c2.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c2.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
        
        c2.set(Calendar.YEAR, year);
        c2.set(Calendar.MONTH, month - 1);
        c2.set(Calendar.DAY_OF_MONTH, day);
        int week = c2.get(Calendar.WEEK_OF_YEAR);
        if (c1.YEAR != c2.YEAR)
        {
            return false;
        }
        if (mWeek == week - 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 判断周数是否与上周相等 yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameLastWeek(String data)
    {
        Calendar c1 = Calendar.getInstance();
        c1.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c1.setMinimalDaysInFirstWeek(7); // 设置每周最少为1天
        int mWeek = c1.get(Calendar.WEEK_OF_YEAR);
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c2 = Calendar.getInstance();
        c2.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天为星期一
        c2.setMinimalDaysInFirstWeek(7); // 设置每周最少为1天
        
        try
        {
            c2.setTime(df.parse(data));
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int week = c2.get(Calendar.WEEK_OF_YEAR);
        
        if (c1.YEAR != c2.YEAR)
        {
            return false;
        }
        if (mWeek - 1 == week)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 判断今天是否与昨天相等 yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameYesterday(String data)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date d = cal.getTime();
        
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = sp.format(d);// 获取昨天日期
        if (data.equals(yesterday))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 判断日期是否是今天 yyyy-mm-dd 或者yyyy/mm/dd
     *
     * @return
     */
    public static boolean isSameDay(String data)
    {
        Calendar cal = Calendar.getInstance();
        Date d = cal.getTime();
        
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = sp.format(d);// 获取昨天日期
        if (data.equals(yesterday))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 获取某年的第几周的开始日期
     *
     * @param date
     * @param week
     * @return
     */
    public static String getFirstDayOfWeek(String date, int week)
    {
        
        int year = Integer.parseInt(date.substring(0, 4));
        
        Calendar c = new GregorianCalendar();
        
        c.setFirstDayOfWeek(Calendar.MONDAY);
        
        c.set(Calendar.YEAR, year);
        
        c.set(Calendar.MONTH, Calendar.JANUARY);
        
        c.set(Calendar.DATE, 1);
        
        Calendar cal = (GregorianCalendar) c.clone();
        
        cal.add(Calendar.DATE, week * 7);
        
        return getFirstDayOfWeek(cal.getTime());
        
    }
    
    /**
     * 获取某年的第几周的结束日期
     *
     * @param date
     * @param week
     * @return
     */
    
    public static String getLastDayOfWeek(String date, int week)
    {
        
        int year = Integer.parseInt(date.substring(0, 4));
        
        Calendar c = new GregorianCalendar();
        
        c.setFirstDayOfWeek(Calendar.MONDAY);
        
        c.set(Calendar.YEAR, year);
        
        c.set(Calendar.MONTH, Calendar.JANUARY);
        
        c.set(Calendar.DATE, 1);
        
        Calendar cal = (GregorianCalendar) c.clone();
        
        cal.add(Calendar.DATE, week * 7);
        
        return getLastDayOfWeek(cal.getTime());
        
    }
    
    /**
     * 获取当前时间所在周的开始日期
     *
     * @param date
     * @return
     */
    public static String getFirstDayOfWeek(Date date)
    {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        SimpleDateFormat sp = new SimpleDateFormat("MM/dd");
        String firstDayOfWeek = sp.format(c.getTime());// 获取昨天日期
        return firstDayOfWeek;
    }
    
    /**
     * 获取当前时间所在周的结束日期
     *
     * @param date
     * @return
     */
    public static String getLastDayOfWeek(Date date)
    {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        SimpleDateFormat sp = new SimpleDateFormat("MM/dd");
        String lastDayOfWeek = sp.format(c.getTime());// 获取昨天日期
        return lastDayOfWeek;
        
    }
    
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;
    
    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";
    
    public static String a5 = "2017";
    
    // 几秒前，几分钟前，几小时前，几天前，几月前，几年前
    public static String formatDate(Context context, Date date)
    {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE)
        {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds)
                    + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE)
        {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes)
                    + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR)
        {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours)
                    + ONE_HOUR_AGO;
        }
//		if (delta < 48L * ONE_HOUR) {
//			return context.getResources().getString(R.string.Yesterday);
//		}
        if (delta < 30L * ONE_DAY)
        {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days)
                    + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK)
        {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months)
                    + ONE_MONTH_AGO;
        }
        else
        {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years)
                    + ONE_YEAR_AGO;
        }
    }
    
    private static long toSeconds(long date)
    {
        return date / 1000L;
    }
    
    private static long toMinutes(long date)
    {
        return toSeconds(date) / 60L;
    }
    
    private static long toHours(long date)
    {
        return toMinutes(date) / 60L;
    }
    
    private static long toDays(long date)
    {
        return toHours(date) / 24L;
    }
    
    private static long toMonths(long date)
    {
        return toDays(date) / 30L;
    }
    
    private static long toYears(long date)
    {
        return toMonths(date) / 365L;
    }
    
    // 获取时间戳
    public static String getTime()
    {
        
        long time = System.currentTimeMillis() / 1000;// 获取系统时间的10位的时间戳
        
        String str = String.valueOf(time);
        
        return str;
        
    }
    
    // 计算年龄
    public static int getAge(String date)
    {
        
        try
        {
            int age = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dbDate = (Date) dateFormat.parse(date);
            Calendar born = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            if (dbDate != null)
            {
                now.setTime(new Date());
                born.setTime(dbDate);
                if (born.after(now))
                {
                    throw new IllegalArgumentException(
                            "Can't be born in the future");
                }
                age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) < born
                        .get(Calendar.DAY_OF_YEAR))
                {
                    age -= 1;
                }
            }
            return age;
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
            
        }
    }
    
    // 计算生日
    public static String getBirthDay(String age)
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        return (Integer.valueOf(mYear) - Integer.valueOf(age)) + "";
    }
    
    // 两日期相减 date1 date2 yyyy-MM-dd HH:mm:ss
    public static Double getDateSubtractiveDays(String date1, String date2)
    {
        date1 = date1.split(" ")[0];
        date2 = date2.split(" ")[0];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int days = 0;
        try
        {
            Date date = sdf.parse(date1);// 通过日期格式的parse()方法将字符串转换成日期
            Date dateBegin = sdf.parse(date2);
            long betweenTime = date.getTime() - dateBegin.getTime();
            days = (int) (betweenTime / 1000 / 60 / 60 / 24);
        }
        catch (Exception e)
        {
        }
        return (double) days;
    }
    
    /**
     * 根据day_of_week得到汉字星期
     *
     * @return
     */
//	public static String getDayOfWeekCN(Context mContext, int day_of_week) {
//		String result = null;
//		switch (day_of_week) {
//		case 1:
//			result = mContext.getResources().getString(R.string.plan_Sunday);
//			break;
//		case 2:
//			result = mContext.getResources().getString(R.string.plan_Monday);
//			break;
//		case 3:
//			result = mContext.getResources().getString(R.string.plan_Tuesday);
//			break;
//		case 4:
//			result = mContext.getResources().getString(R.string.plan_Wednesday);
//			break;
//		case 5:
//			result = mContext.getResources().getString(R.string.plan_Thursday);
//			break;
//		case 6:
//			result = mContext.getResources().getString(R.string.plan_Friday);
//			break;
//		case 7:
//			result = mContext.getResources().getString(R.string.plan_Saturday);
//			break;
//		default:
//			break;
//		}
//		return result;
//	}
    
    /**
     * 得到年月日，星期几
     *
     * 2017年03月13日  周一
     *
     * @return
     */
//	public static String DateObject_string(Context mContext, int year2, int month2,
//			int day2, int week2) {
//		int maxDayOfMonth = Calendar.getInstance().getActualMaximum(
//				Calendar.DAY_OF_MONTH);
//		if (day2 > maxDayOfMonth) {
//			month2 = month2 + 1;
//			day2 = day2 % maxDayOfMonth;
//		}
//		week2 = week2 % 7 == 0 ? 7 : week2 % 7;
//
//		if (day2 == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
//			return year2
//					+ mContext.getResources().getString(R.string.plan_year)
//					+ String.format("%02d", month2)
//					+ mContext.getResources().getString(R.string.plan_month)
//					+ String.format("%02d", day2)
//					+ mContext.getResources().getString(R.string.plan_day)
//					+ "  "
//					+ mContext.getResources().getString(R.string.plan_today);
//		} else {
//			return year2
//					+ mContext.getResources().getString(R.string.plan_year)
//					+ String.format("%02d", month2)
//					+ mContext.getResources().getString(R.string.plan_month)
//					+ String.format("%02d", day2)
//					+ mContext.getResources().getString(R.string.plan_day)
//					+ "  " + DateTimeUtils.getDayOfWeekCN(mContext, week2);
//		}
//
//	}
    
    /**
     * 得到年月日，星期几
     * <p>
     * 2017-03-13 周一
     *
     * @return
     */
//	public static String DateObject_string2(Context mContext, int year2, int month2,
//			int day2, int week2) {
//		int maxDayOfMonth = Calendar.getInstance().getActualMaximum(
//				Calendar.DAY_OF_MONTH);
//		if (day2 > maxDayOfMonth) {
//			month2 = month2 + 1;
//			day2 = day2 % maxDayOfMonth;
//		}
//		week2 = week2 % 7 == 0 ? 7 : week2 % 7;
//
//		if (day2 == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
//			return year2
//					+"-"
//					+ String.format("%02d", month2)
//					+ "-"
//					+ String.format("%02d", day2)
//					+ "  "
//					+ mContext.getResources().getString(R.string.plan_today);
//		} else {
//			return year2
//					+ "-"
//					+ String.format("%02d", month2)
//					+ "-"
//					+ String.format("%02d", day2)
//					+ "  " + DateTimeUtils.getDayOfWeekCN(mContext, week2);
//		}
//
//	}
    public static String getLastModified(long longMS)
    {
        Date date = new Date(longMS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        String monthStr, dayStr;
        if (month < 10)
        {
            monthStr = "0" + month;
        }
        else
        {
            monthStr = month + "";
        }
        if (day < 10)
        {
            dayStr = "0" + day;
        }
        else
        {
            dayStr = "" + day;
        }
        
        if (getShowFormat(longMS).split(" ").length > 1)
        {
            String mDate = getShowFormat(longMS).split(" ")[0];
            String mTime = getShowFormatNotSecond(longMS).split(" ")[1];
            if (year == getYear())
            {
                if (isSameYesterday(getShowFormat(longMS).split(" ")[0]))
                {
                    return "昨天" + mTime;
                }
                else if (isSameDay(getShowFormat(longMS).split(" ")[0]))
                {
                    return "今天" + mTime;
                }
                else
                {
                    if (month < 10)
                    {
                        return monthStr + "-" + dayStr + " " + mTime;
                    }
                    else
                    {
                        return monthStr + "-" + dayStr + " " + mTime;
                    }
                    
                }
            }
            else
            {
                return getShowFormat(longMS);
            }
        }
        return "";
    }
}