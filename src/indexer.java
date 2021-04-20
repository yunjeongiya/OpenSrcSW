import org.snu.ids.kkma.index.KeywordExtractor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class indexer {

    public HashMap<String, List<Pair>> indexXML(String path)  {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        HashMap<String, HashMap<String, Integer>> wordMap = new HashMap<>();
        HashMap<String, List<Pair>> result = new HashMap<>();
        Document doc = null;
        try {
            doc = docBuilder.parse(new File(path));
            NodeList bodyList = doc.getElementsByTagName("body");
            int n = bodyList.getLength();
            for (int i = 0; i < n; i++) {
                Node node = bodyList.item(i);
                String text = node.getTextContent();
                String[] words = text.split("#");
                for (String word : words) {
                    String sp[] = word.split(":");
                    int count = Integer.parseInt(sp[1]);
                    String id = node.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
                    if (!wordMap.containsKey(sp[0])) {
                        HashMap<String, Integer> localMap = new HashMap<>();
                        localMap.put(id, count);
                        wordMap.put(sp[0], localMap);
                    } else {
                        HashMap<String, Integer> localMap = wordMap.get(sp[0]);
                        if (!localMap.containsKey(id))
                            localMap.put(id, count);
                        else
                            localMap.put(id, localMap.get(id) + count);
                    }
                }
            }

            for (String key : wordMap.keySet()) {
                HashMap<String, Integer> localMap = wordMap.get(key);
                int totCount = localMap.keySet().size();
                List<Pair> list = new ArrayList<>();
                for (String id : localMap.keySet()) {
                    int f = localMap.get(id);
                    double val = Math.round(f  * Math.log((double) n / totCount)*100.0)/100.0;
                    Pair pair = new Pair(id , val);
                    list.add(pair);
                }
                result.put(key, list);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static class Pair implements Serializable, Comparable<Pair> {
        public double val;

        public String postId;

        public Pair(String postId, double val) {

            this.postId = postId;

            this.val = val;
        }

        @Override
        public int compareTo(Pair pair) {
            int flag = Double.compare(pair.val, this.val);
            if (flag==0)
                return this.postId.compareTo(pair.postId);
            return flag;
        }

        @Override
        public String toString() {
            return postId + " " + val+ " ";
        }
    }
    public void save(HashMap<String, List<Pair>> data, String path){

        try(FileOutputStream stream = new FileOutputStream(path); ObjectOutputStream out = new ObjectOutputStream(stream)){

            out.writeObject(data);

            out.flush();

        } catch(Exception e) {

            e.printStackTrace();
        }

    }

    public void readAndPrint(String path) {

        try( FileInputStream stream = new FileInputStream(path); ObjectInputStream input = new ObjectInputStream(stream)){

            Object obj = input.readObject();
            if (!(obj instanceof HashMap))
                return;
            HashMap map = (HashMap) obj;

            for (Object key : map.keySet()) {

                List pair = (List) map.get(key);

                System.out.print(key + " -> " );

                for (Object p : pair)
                    System.out.print(p.toString());

                System.out.println();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}