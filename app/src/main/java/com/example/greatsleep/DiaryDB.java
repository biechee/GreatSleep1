package com.example.greatsleep;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DiaryDB implements DiaryDB_I
{
    private String fpath;

    public DiaryDB(Context cont)
    {
        fpath = cont.getFilesDir().getPath() + "/torar.xml";
        System.out.println("fpath: " + fpath);
        init();
    }

    /**
     * Creates xml file with root element if data file does not exists
     */
    private void init()
    {
        try
        {
            File fl = new File(fpath);
            if(!fl.exists())
            {
                fl.createNewFile();

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("records");
                doc.appendChild(rootElement);

                saveXMLToFile(doc);
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Saves Document to xml file
     * @param doc Doc with data
     * @throws Exception
     */
    private void saveXMLToFile(Document doc) throws Exception
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fpath));
        transformer.transform(source, result);
    }

    /**
     * Returns root document from xml user data file
     * @return
     * @throws Exception Contains message for user.
     */
    private Document getXMLDoc() throws Exception
    {
        File fXmlFile = new File(fpath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        return dBuilder.parse(fXmlFile);
    }

    /**
     * Saves new data into file
     * @param data Contains new data to save
     * @throws Exception Contains message for user.
     */
    @Override
    public void addData(Diary data) throws Exception
    {
        try
        {
            Document doc = getXMLDoc();
            Element rootElement = doc.getDocumentElement();

            Attr attr = doc.createAttribute("date");
            attr.setValue(data.getId());

            Element record = doc.createElement("record");

            record.setAttributeNode(attr);

            Element text = (Element)record.appendChild(doc.createElement("text"));
            text.appendChild(doc.createTextNode(data.getText()));

            Element title = (Element)record.appendChild(doc.createElement("title"));
            Log.v("diary",title+"   "+text);

            title.appendChild(doc.createTextNode(data.getTitle()));

            rootElement.appendChild(record);

            saveXMLToFile(doc);
        }
        catch(Exception e)
        {
            System.err.println("Unable to add data" + e.getMessage());
        }
    }

    /**
     * Removes corresponding data from file according to unique identifier data.getId()
     * @param data
     * @throws Exception Contains message for user.
     */
    @Override
    public void removeData(Diary data) throws Exception
    {
        Document doc = getXMLDoc();
        Element rootElement = doc.getDocumentElement();
        NodeList list = doc.getElementsByTagName("record");

        for(int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                if(data.getId().equals(element.getAttribute("date")))
                {
                    rootElement.removeChild(element);
                    saveXMLToFile(doc);
                    break;
                }
            }
            else
            {
                System.err.println("Unable to delete data");
            }
        }
    }

    /**
     * Replace corresponding(according to data.getId()) text in file with new text from data.
     * @param data
     * @throws Exception Contains message for user.
     */
    @Override
    public void editData(Diary data) throws Exception
    {
        removeData(data);
        addData(data);
    }

    /**
     * Loads ArrayList with data from file and then returns it
     * @return ArrayList filled with data from file
     * @throws Exception Contains message for user.
     */
    @Override
    public ArrayList<Diary> getContent() throws Exception
    {
        ArrayList<Diary> it = new ArrayList<Diary>();

        Document doc = getXMLDoc();

        NodeList list = doc.getElementsByTagName("record");
        for(int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                String date = element.getAttribute("date");
                String title= element.getElementsByTagName("title").item(0).getTextContent();
                Log.v("diary",title+"  1");
                String text = element.getElementsByTagName("text").item(0).getTextContent();
                it.add(new Diary(text, date,title));
            }
            else
            {
                System.err.println("Unable to load data");
            }
        }

        return it;
    }
}
