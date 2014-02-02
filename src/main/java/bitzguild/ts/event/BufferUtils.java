package bitzguild.ts.event;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

import bitzguild.ts.datetime.MutableDateTime;

public class BufferUtils {

    public static StringBuffer pairToBuffer(String name, String value, StringBuffer sb, boolean comma) {
        sb.append(name).append(":");
        sb.append("\"");
        sb.append(value);
        sb.append("\"");
        if(comma) sb.append(',');
        return sb;
    }

    public static StringBuffer pairToBuffer(String name, char value, StringBuffer sb, boolean comma) {
        sb.append(name).append(":");
        sb.append("\"");
        sb.append(value);
        sb.append("\"");
        if(comma) sb.append(',');
        return sb;
    }

    public static DecimalFormat DefaultFormat = new DecimalFormat("#.000");
    private static FieldPosition _FieldPos = new FieldPosition(NumberFormat.FRACTION_FIELD);
    
    public static StringBuffer pairToBuffer(String name, double value, String format, StringBuffer sb, boolean comma) {
        sb.append(name).append(":");
        DecimalFormat decimal = new DecimalFormat("#.000");
        decimal.format(value, sb, _FieldPos);
        sb.setLength(sb.length()-1);
        if(comma) sb.append(',');
        return sb;
    }

    public static StringBuffer pairToBuffer(String name, long value, StringBuffer sb, boolean comma) {
        sb.append(name).append(":");
        sb.append(value);
        if(comma) sb.append(',');
        return sb;
    }

    public static StringBuffer pairToBuffer(String name, int value, StringBuffer sb, boolean comma) {
        sb.append(name).append(":");
        sb.append(value);
        if(comma) sb.append(',');
        return sb;
    }

    public static StringBuffer datetimeToBuffer(String name, long datetime, StringBuffer sb) {
        sb.append(name).append(":");
        sb.append("\"");
        (new MutableDateTime(datetime)).toBuffer(sb);
        sb.append("\"").append(',');
        return sb;
    }
	
}
