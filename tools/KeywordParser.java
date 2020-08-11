// LSL Keyword File Parser v0.1 (c) 2020 Alex Pascal @ SL.
// =================================================================================
// Usage: java KeywordParser <inputFile.xml> <outputFile.txt>
// =================================================================================
// Given an LSL keywords file (you can find this in the root of your cache) and a
//  text file to write to, produces a readable list of keywords you can use to
//  create syntax highlighting rules, etc...
// ---------------------------------------------------------------------------------
// TODO: Modify parser to add --snippets command switch to auto-gen snippets?

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KeywordParser
{
    public static void main (String[] args)
    {
        try
        {
            if (args.length != 2)
            {
                System.out.println("Usage: KeywordParser <inputfile> <outputfile>");
            }
            else
            {
                File inputFile = new File(args[0]);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                KeywordListHandler handler = new KeywordListHandler(args[1]);
                parser.parse(inputFile, handler);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class KeywordListHandler extends DefaultHandler
{
    private PrintWriter outWriter;
    private int mapLevel;
    private boolean printKey;

    public KeywordListHandler (String outputFile) throws IOException
    {
        super();

        outWriter = new PrintWriter(outputFile);
        mapLevel = 0;
        printKey = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (qName.equalsIgnoreCase("map"))
        {
            mapLevel++;
        }
        else if (qName.equalsIgnoreCase("key"))
        {
            if (mapLevel > 0 && mapLevel <= 2)
            {
                printKey = true;
            }
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException
    {
        if (qName.equalsIgnoreCase("map"))
        {
            mapLevel--;
        }
        else if (qName.equalsIgnoreCase("llsd"))
        {
            outWriter.close();
        }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException
    {
        if (printKey)
        {
            if (mapLevel == 1)
            {
                String str = new String(ch, start, length);
                outWriter.println("\n" + str.toUpperCase() + ":\n");
            }
            else if (mapLevel == 2)
            {
                outWriter.println(new String(ch, start, length));
            }

            printKey = false;
        }
    }
}