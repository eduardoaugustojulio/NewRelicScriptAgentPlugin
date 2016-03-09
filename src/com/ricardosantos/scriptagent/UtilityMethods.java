/**
 * 
 */
package com.ricardosantos.scriptagent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility methods dump class.
 * @author Ricardo
 *
 */
public class UtilityMethods
{

	/**
	 * Gets the current date and time with the following format: yyyy/MM/dd HH:mm:ss"
	 * @return
	 */
	public static String getTimeDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//get current date time with Date()
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/**
	 * Prints an array of stuff, with the notation {x1,x2,x3...,xn}, or "" if the array is empty.
	 * Each passed object will be printed according to its arr[i].toString() output.
	 * @param arr The array of objects.
	 * @return The string representation of the passed array as {x1,x2,x3...,xn}, or "" is array empty.
	 */
	public static String printArray(Object[] arr)
	{
		String toPrint = "";
		
		if (arr.length>0)
		{
			toPrint = "{";
			for (int i=0 ; i<arr.length ; i++)
			{
				toPrint += arr[i].toString();
				if(i<arr.length-1)
					toPrint += ",";
			}
			toPrint += "}";
		}		
		return toPrint;
	}

}
