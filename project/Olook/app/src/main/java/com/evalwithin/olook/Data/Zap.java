package com.evalwithin.olook.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Zap extends Location implements Serializable
{
    public final static String ZAP_FILENAME = "ZapData.dat";
    public final static String URL_ZAP = "https://www.donneesquebec.ca/recherche/dataset/5cc3989e-442b-4f25-8049-d39d44421d6f/resource/0106c060-9559-4ce7-9987-41ec051a1df8/download/nodes.csv";

    public Zap(double locX, double locY, String name)
    {
        super(locX, locY, name);
    }

    public static ArrayList<Location> parseCSV(String csvString)
    {
        ArrayList<Location> zapList = new ArrayList<>();

        String[] lines = csvString.split("\n");

        //First line is header
        String[] header = lines[0].split(",");
        int nbrCol = header.length;
        byte indexLocX = -1;
        byte indexLocY = -1;
        byte indexName = -1;

        for (byte i = 0; i < header.length; i++)
        {
            String curCol = header[i];
            if (curCol.toLowerCase().equals("\"latitude\""))
                indexLocY = i;
            if (curCol.toLowerCase().equals("\"longitude\""))
                indexLocX = i;
            if (curCol.toLowerCase().equals("\"name\""))
                indexName = i;
        }


        for (int i = 1; i < lines.length; i++)
        {
            String curLine = lines[i];
            String[] data = new String[nbrCol];

            int dataIndex = 0;
            String[] splitted = curLine.split(",");
            for (int j = 0; j < splitted.length; j++)
            {
                String curStr = splitted[j];
                if (!curStr.isEmpty() && !curStr.equals("\"\"") && !curStr.toLowerCase().equals("null"))
                {
                    if (curStr.startsWith("\""))
                    {
                        data[dataIndex] = curStr.substring(1);
                    }
                    else
                    {
                        data[dataIndex] += ", " + curStr.substring(1);
                    }
                    if (curStr.endsWith("\""))
                    {
                        data[dataIndex] = data[dataIndex].substring(0, data[dataIndex].length()-1);
                        dataIndex++;
                    }
                }
                else
                {
                    data[dataIndex] = "";
                    dataIndex++;
                }
            }

            String name = data[indexName];

            double locX = 0;
            double locY = 0;

            if (!data[indexLocX].isEmpty() && !data[indexLocY].isEmpty())
            {
                locX = Double.parseDouble(data[indexLocX].replaceAll("\"", ""));
                locY = Double.parseDouble(data[indexLocY].replaceAll("\"", ""));
            }
            zapList.add(new Zap(locX, locY, name));
        }

        return zapList;
    }
}
