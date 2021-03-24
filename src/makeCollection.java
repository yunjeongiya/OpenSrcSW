import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
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

public class makeCollection {

    public Document makeXml(String dirPath) {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            BodyParser bodyParser = new BodyParser();
            TitleParser titleParser = new TitleParser();
            File directory = new File (dirPath);

            Element docs = doc.createElement("docs");
            doc.appendChild(docs);

            int i = 0;
            for (File input : directory.listFiles()) {

                org.jsoup.nodes.Document htmlDoc = Jsoup.parse(input, "UTF-8");
                Element docElement = doc.createElement("doc");
                docs.appendChild(docElement);
                docElement.setAttribute("id", String.valueOf(i));

                Element title = doc.createElement("title");
                docElement.appendChild(title);

                title.appendChild(doc.createTextNode(titleParser.parse(htmlDoc)));

                Element body = doc.createElement("body");
                body.appendChild(doc.createTextNode(bodyParser.parse(htmlDoc)));
                docElement.appendChild(body);

                i++;
            }
            return doc;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    public static class BodyParser {
        public String parse (org.jsoup.nodes.Document doc) throws Exception{

            Elements foodDic = doc.select("p");
            StringBuilder resultBuilder = new StringBuilder();
            for( org.jsoup.nodes.Element food : foodDic )
            {
                resultBuilder.append(food.text());
                resultBuilder.append('\n');
            }
            return resultBuilder.toString();
        }
    }

    public static class TitleParser {
        public String parse (org.jsoup.nodes.Document doc) throws Exception{
            Elements foodDic = doc.select("title");

            return foodDic.text();
        }
    }
}