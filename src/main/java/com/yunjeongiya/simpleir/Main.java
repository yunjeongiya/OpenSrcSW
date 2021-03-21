package com.yunjeongiya.simpleir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            BodyParser bodyParser = new BodyParser();
            TitleParser titleParser = new TitleParser();
            File directory = new File ("D:/구글드라이브/2021/1학기/오픈소스sw입문/SimpleIR/2주차 실습 html");

            Element docs = doc.createElement("docs");
            doc.appendChild(docs);

            int i = 0;
            for (File input : directory.listFiles()) {

                Element docElement = doc.createElement("doc");
                docs.appendChild(docElement);
                docElement.setAttribute("id", String.valueOf(i));

                Element title = doc.createElement("title");
                docElement.appendChild(title);
                title.appendChild(doc.createTextNode(titleParser.parse(input)));

                Element body = doc.createElement("body");
                body.appendChild(doc.createTextNode(bodyParser.parse(input)));
                docElement.appendChild(body);

                i++;
            }

            //xml 파일로 쓰기
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(new File("D:/구글드라이브/2021/1학기/오픈소스sw입문/SimpleIR/collection.xml")));

            transformer.transform(source, result);

        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
