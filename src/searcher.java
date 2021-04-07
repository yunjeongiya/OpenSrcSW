import jdk.internal.access.JavaNetHttpCookieAccess;
import org.jsoup.Jsoup;
import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
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

    //문서 아이디, 쿼리와의 유사도로 구성된 pair 들의 list 출력
    public PriorityQueue<indexer.Pair> CalcSim(String query, HashMap<String, List<indexer.Pair>> indexPOST) throws IOException, ClassNotFoundException {

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);
        HashMap<String, Double> querymap = new HashMap<>();
        for (int i=0; i<kl.size(); i++){
            Keyword kwrd = kl.get(i);
            querymap.put(kwrd.getString(), 1.0)
        }
        PriorityQueue<indexer.Pair> pq = new PriorityQueue();

        for (String key : indexPOST.keySet()) {
            List<indexer.Pair> pairList = indexPOST.get(key);
            double weight = 0;
            for (indexer.Pair pair : pairList) {
                String postId = pair.postId;
                double val = pair.val;
                if (!querymap.containsKey(postId))
                    continue;
                weight += val * querymap.get(postId);
            }
            indexer.Pair res = new indexer.Pair(key, weight);
            pq.add(res);
        }
        return pq;
    }

    //pair들의 list를 받아서 상위 3개 출력.
    //POSTPath를 받아와서 collectionPath 구한 뒤 title parse
    public void printResult(PriorityQueue<indexer.Pair> similarity, String POSTPath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(POSTPath.replace("index.post", "collection.xml"));
        String title = toString(doc.getElementsByTagName("title"));

        for(int i=0; i<3 && !similarity.isEmpty(); i++){

            indexer.Pair pair = similarity.pop();

            System.out.println("title: " +  bodyList(pair.postId) + ", result :"  + String.format("%.2f", pair.val));
        }
    }

}