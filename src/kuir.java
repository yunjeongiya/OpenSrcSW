import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;

public class kuir {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Not enough arguments");
            return;
        }

        String mode = args[0];
        String path = args[1];

        if (mode.equals("-c")) {
            makeCollection makeCollection = new makeCollection();
            Document res = makeCollection.makeXml(path);
            writeDocToFile(res, "../result/collection.xml");
        }
        else if (mode.equals("-k")) {
            makeKeyword keywordParser = new makeKeyword();
            Document converted = keywordParser.makeXml(path);
            writeDocToFile(converted, "../result/index.xml");
        }
    }

    private static void writeDocToFile(Document doc, String dir) {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(new File(dir)));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
