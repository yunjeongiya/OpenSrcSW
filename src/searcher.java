import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class searcher {
    public PriorityQueue<indexer.Pair> CalcCosSim(String query, HashMap<String, List<indexer.Pair>> indexPOST) {
        PriorityQueue cosSim = new PriorityQueue();

        return cosSim;
    }
    public HashMap<String, List<indexer.Pair>> getPOST (String POSTPath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(POSTPath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return (HashMap) object;
    }

    public PriorityQueue<indexer.Pair> CalcSim(String query, HashMap<String, List<indexer.Pair>> indexPOST) throws IOException, ClassNotFoundException {

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);
        HashMap<String, Double> querymap = new HashMap<>();
        for (int i=0; i<kl.size(); i++){
            Keyword kwrd = kl.get(i);
            querymap.put(kwrd.getString(), 1.0);
        }
        PriorityQueue<indexer.Pair> pq = new PriorityQueue();
        HashMap<String, Double> map  =  new HashMap<>();
        for (String key : querymap.keySet()) {
            if (!indexPOST.containsKey(key))
                continue;
            double queryWeight = querymap.get(key);
            List<indexer.Pair> pairList = indexPOST.get(key);
            for (indexer.Pair pair : pairList) {
                String postId = pair.postId;
                double val = pair.val;
                if (!map.containsKey(postId))
                    map.put(postId, 0.0);
                map.put(postId, map.get(postId) + val * queryWeight);
            }
        }
        for (String key : map.keySet())
            pq.add(new indexer.Pair(key, map.get(key)));
        return pq;
    }

    public void printResult(PriorityQueue<indexer.Pair> similarity, String POSTPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document docs = docBuilder.parse(new File(POSTPath.replace("index.post", "collection.xml")));

        for(int i=0; i<3 && !similarity.isEmpty(); i++){

            indexer.Pair pair = similarity.poll();

            NodeList doc = docs.getElementsByTagName("doc");

            for(int j=0;j<doc.getLength();j++) {
                Node node = doc.item(j);
                if (node.getAttributes().getNamedItem("id").getNodeValue().equals(pair.postId)) {
                    String title = node.getFirstChild().getTextContent();
                    System.out.println("title: " + title + ", result :" + String.format("%.2f", pair.val));
                    break;
                }
            }
        }
    }
}