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
    public HashMap<String, List<indexer.Pair>> getPOST (String POSTPath) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(POSTPath);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return (HashMap) object;
    }

    public HashMap<String, Double> getQueryMap (String query){
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);
        HashMap<String, Double> queryMap = new HashMap<>();
        for (int i=0; i<kl.size(); i++){
            Keyword kwrd = kl.get(i);
            queryMap.put(kwrd.getString(), 1.0);
        }
        return queryMap;
    }

    public HashMap<String, Double> InnerProduct(HashMap<String, Double> queryMap, HashMap<String, List<indexer.Pair>> indexPOST) throws IOException, ClassNotFoundException {
        HashMap<String, Double> map  =  new HashMap<>();
        for (String key : queryMap.keySet()) {
            if (!indexPOST.containsKey(key))
                continue;
            double queryWeight = queryMap.get(key);
            List<indexer.Pair> pairList = indexPOST.get(key);
            for (indexer.Pair pair : pairList) {
                String postId = pair.postId;
                double docWeight = pair.val;
                if (!map.containsKey(postId))
                    map.put(postId, 0.0);
                map.put(postId, map.get(postId) + docWeight * queryWeight);
            }
        }
        return map;
    }

    public PriorityQueue<indexer.Pair> CalcSim(HashMap<String, Double> queryMap, HashMap<String, List<indexer.Pair>> indexPOST) {

        PriorityQueue<indexer.Pair> pq = new PriorityQueue();

        Double querySize = 0.0;
        for (String key : queryMap.keySet())
            querySize += Math.pow(queryMap.get(key), 2);
        querySize = Math.pow(querySize, 0.5);

        HashMap<String, Double> map  =  new HashMap<>();
        try {
            map = InnerProduct(queryMap, indexPOST);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, Double> docSize  =  new HashMap<>();
        for (String key : queryMap.keySet()) {
            List<indexer.Pair> pairList = indexPOST.get(key);
            for (indexer.Pair pair : pairList) {
                String postId = pair.postId;
                double docWeight = pair.val;
                if (!docSize.containsKey(postId))
                    docSize.put(postId, 0.0);
                docSize.put(postId, docSize.get(postId) + Math.pow(docWeight,2));
            }
        }

        for (String key : map.keySet()) {
            docSize.put(key, Math.pow(docSize.get(key), 0.5));
            map.put(key, map.get(key) / (querySize * docSize.get(key)));
            pq.add(new indexer.Pair(key, map.get(key)));
        }

        return pq;
    }

    public void printResult(PriorityQueue<indexer.Pair> similarity, String POSTPath) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document docs = docBuilder.parse(new File(POSTPath.replace("index.post", "collection.xml")));

        for(int i=0; i<3 && !similarity.isEmpty(); i++){

            indexer.Pair pair = similarity.poll();
            NodeList doc = docs.getElementsByTagName("doc");
            int n =  doc.getLength();

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