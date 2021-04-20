import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

        if (args.length < 2) {

            System.out.println("Not enough arguments");
            return;
        }

        String mode = args[0];
        String path = args[1];

        if (mode.equals("-c")) {
            checkDir();
            makeCollection makeCollection = new makeCollection();
            Document res = makeCollection.makeXml(path);
            writeDocToFile(res, "../result/collection.xml");
        }
        else if (mode.equals("-k")) {
            makeKeyword keywordParser = new makeKeyword();
            Document converted = keywordParser.makeXml(path);
            writeDocToFile(converted, "../result/index.xml");
        }
        else if (mode.equals("-i")) {
            indexer indexer = new indexer();
            indexer.save(indexer.indexXML(path), "../result/index.post");
            indexer.readAndPrint("../result/index.post");
        }
        else if (mode.equals("-s")) {
            if (args[2].equals("-q")) {
                String query = args[3];
                searcher searcher = new searcher();
                searcher.printResult(searcher.CalcSim(query, searcher.getPOST(path)), path);
            }
        }
    }
    private static void checkDir(){
        File dir = new File("../result");
        if(!dir.exists())
            dir.mkdir();
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