import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class makeKeyword {
    public Document makeXml(String collectionPath){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(collectionPath));
            NodeList bodyList = doc.getElementsByTagName("body");
            KeywordExtractor ke = new KeywordExtractor();
            for (int i = 0; i < bodyList.getLength(); i++) {

                Node node = bodyList.item(i);
                String bodyString = node.getTextContent();
                KeywordList kl = ke.extractKeyword(bodyString, true);
                StringBuilder builder = new StringBuilder();
                for (Keyword kwrd : kl)
                    builder.append(kwrd.getString()).append(":").append(kwrd.getCnt()).append("#");
                node.setTextContent(builder.toString());
            }
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}