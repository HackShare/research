package HADS.XML;

import java.io.*;             // For reading the input file
import org.w3c.dom.*;         // W3C DOM classes for traversing the document
import org.xml.sax.*;         // SAX classes used for error handling by JAXP
import javax.xml.parsers.*;   // JAXP classes for parsing
import javax.xml.transform.*; // For transforming a DOM tree to an XML file.

/**
 * A ParseHADS object is a wrapper around a DOM tree for a hads.xml
 * file.  The methods of the class use the DOM API to work with the
 * tree in various ways.
 **/
public class ParseHADS {
   /** The main method creates and demonstrates a ParseHADS object */
   public static void main(String[] args)
      throws IOException, SAXException, ParserConfigurationException,
             TransformerConfigurationException, TransformerException {
      // Create a new ParseHADS object that represents the hads.xml
      // file specified by the first command-line argument
      ParseHADS parsed = new ParseHADS(new File(args[0]));
      // Write out an XML version of the DOM tree to standard out
      System.out.println("==================output======================");
      parsed.output(new PrintWriter(System.out));
      // And query the tree for all node names and values
      System.out.println("==================getAllNodes=================");
      parsed.getAllNodes();
   }

   org.w3c.dom.Document document;  // This field holds the parsed DOM tree

   /**
    * This constructor method is passed an XML file.  It uses the JAXP API to
    * obtain a DOM parser, and to parse the file into a DOM Document object,
    * which is used by the remaining methods of the class.
    **/
   public ParseHADS(File parsedfile)
      throws IOException {
      // Get a JAXP parser factory object
      try
      {
        javax.xml.parsers.DocumentBuilderFactory dbf =
          DocumentBuilderFactory.newInstance();
      // Tell the factory what kind of parser we want
      dbf.setValidating(false);
      // Use the factory to get a JAXP parser object
      javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();

      // Tell the parser how to handle errors.  Note that in the JAXP API,
      // DOM parsers rely on the SAX API for error handling
      parser.setErrorHandler(new org.xml.sax.ErrorHandler() {
         public void warning(SAXParseException e) {
            System.err.println("WARNING: " + e.getMessage());
         }
         public void error(SAXParseException e) {
            System.err.println("ERROR: " + e.getMessage());
         }
         public void fatalError(SAXParseException e) throws SAXException {
            System.err.println("FATAL: " + e.getMessage());
            throw e;   // re-throw the error
         }
      });

     // Finally, use the JAXP parser to parse the file.  This call returns
     // A Document object.  Now that we have this object, the rest of this
     // class uses the DOM API to work with it; JAXP is no longer required.
     document = parser.parse(parsedfile);
     }
     catch (Exception e)
     {
             e.printStackTrace();
     }
   }

   /***********************************************************************
    * Method:      getNumOfNodes
    * Description: Gets the number of nodes in an element
    * Parameters:  String name
    * Returns:     String num
    * By:          David Ivins
    **********************************************************************/
   public String getNumOfNodes(String name)
   {
     NodeList nodes = document.getElementsByTagName(name);
     String num = (new Integer(nodes.getLength())).toString();

     return num;
   }


   /***********************************************************************
    * Method:      getSubNodeByName
    * Description: Gets a subnode of another node by its name
    * Parameters:  String parent, String child, int index
    * Returns:     String node
    * By:          David Ivins
    *********************************************************************/
   public String getSubNodeByName(String parent, String child, int index)
   {
     if (index < 0)
             index = 0;

     String node = null;
     NodeList nodes = document.getElementsByTagName(parent);
     Element tag = (Element)nodes.item(index);

     NodeList subNodes = tag.getElementsByTagName("*");

     for (int i = 0; i < subNodes.getLength(); i++)
     {
       Element subTag = (Element)subNodes.item(i);

       if ((subTag.getTagName()).equals(child))
       {
         node = ((Text)subTag.getFirstChild()).getData().trim();
         break;
       }
     }

     return node;
   }


   /***********************************************************************
    * Method:      getNodeByName
    * Description: Gets a node by its name
    * Parameters:  String name
    * Returns:     String node
    * By:          David Ivins
    **********************************************************************/
   public String getNodeByName(String name)
   {
     String node = null;
     NodeList nodes = document.getElementsByTagName("*");

     for (int i = 0; i < nodes.getLength(); i++)
     {
       Element tag = (Element)nodes.item(i);

       if ((tag.getTagName()).equals(name))
       {
         node = ((Text)tag.getFirstChild()).getData().trim();
         break;
       }
     }

     return node;
   }

   /**
    * This method looks for all Element nodes in the DOM tree in order
    * to print the name and value
    **/
   public void getAllNodes() {
      // Find all elements and loop through them.
      NodeList nodes = document.getElementsByTagName("*");
      int num = nodes.getLength();
      System.out.println("num nodes=" + num);
      for(int i = 0; i < (num>0 ? num : 1); i++) {
         Element tag = (Element) nodes.item(i);
         System.out.println("i=" + i + ": tag.getTagName=" + tag.getTagName());
         System.out.println("i=" + i + ": tag 1st child="
            + ((Text)tag.getFirstChild()).getData().trim());
         NodeList subNodes = tag.getElementsByTagName("*");
         int numSubNodes = subNodes.getLength();
         System.out.println("i=" + i + ": num subNodes=" + numSubNodes);
         // Get the tags within this tag
         for(int j = 0; j < (numSubNodes>0 ? numSubNodes : 1); j++) {
            Element nameTag = (Element) subNodes.item(j);
            if (nameTag == null) {
               System.out.println("j=" + j + ": null nameTag, tag 1st child="
                  + ((Text)tag.getFirstChild()).getData().trim());
            } else {
               // The <server-name> tag should have a single child of type
               // Text.  Get that child, and extract its text.  Use trim()
               // to strip whitespace from the beginning and end of it.
               System.out.println("j=" + j + ": nameTag=" + nameTag
                  + ", nameTag 1st child="
                  + ((Text)nameTag.getFirstChild()).getData().trim());
            }
         }
      }
   }

   /**
    * Output the DOM tree to the specified stream as an XML document.
    * See the XMLDocumentWriter example for the details.
    **/
   public void output(PrintWriter out)
      throws TransformerConfigurationException, TransformerException {
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer();

      transformer.transform(new javax.xml.transform.dom.DOMSource(document),
                            new javax.xml.transform.stream.StreamResult(out));
  }
}
