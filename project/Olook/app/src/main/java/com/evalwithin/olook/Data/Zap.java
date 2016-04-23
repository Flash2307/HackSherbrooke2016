package com.evalwithin.olook.Data;

import java.util.ArrayList;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Zap extends Location
{

    public final static String URL_ZAP = "https://www.donneesquebec.ca/recherche/dataset/5cc3989e-442b-4f25-8049-d39d44421d6f/resource/0106c060-9559-4ce7-9987-41ec051a1df8/download/nodes.csv";

    private double locX;
    private double locY;
    private String zapName;

    public Zap(double locX, double locY, String name)
    {
        super(locX, locY, name);
    }

    public static ArrayList<Zap> parseCSV(String csvString)
    {
        ArrayList<Zap> zapList = new ArrayList<>();

        String[] lines = csvString.split("\n");

        //First line is header
        String[] header = lines[0].split(",");
        byte indexLocX = -1;
        byte indexLocY = -1;
        byte indexName = -1;

        for (byte i = 0; i < header.length; i++)
        {
            String curCol = header[i];
            if (curCol.toLowerCase().equals("latitude"))
                indexLocX = i;
            if (curCol.toLowerCase().equals("longitude"))
                indexLocY = i;
            if (curCol.toLowerCase().equals("name"))
                indexName = i;
        }


        for (int i = 1; i < lines.length; i++)
        {
            String curLine = lines[i];
            String[] data = curLine.split(",");

            String name = data[indexName];

            double locX = 0;
            double locY = 0;

            if (!data[indexLocX].toLowerCase().equals("null") && !data[indexLocY].toLowerCase().equals("null"))
            {
                locX = Double.parseDouble(data[indexLocX]);
                locY = Double.parseDouble(data[indexLocY]);
            }
            zapList.add(new Zap(locX, locY, name));
        }

        return zapList;
    }
}
