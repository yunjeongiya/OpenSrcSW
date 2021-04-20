import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class genSnippet {

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

        if (args.length < 3) {
            System.out.println("Not enough arguments");
            return;
        }

        String mode = args[0];
        String path = args[1];
        String mode2 = args[2];
        String query = args[3];

        if (mode.equals("-f")) {

            FileInputStream fileInputStream = new FileInputStream(path);

            //파일스트림에서 들어오는 단어들을 엔터 단위로 잘라 리스트로 저장

            String key;
            while(key != null)

            List lineList = new List();

            if (mode2.equals("-q")){

                //쿼리에서 텍스트들을 항목으로 갖는 리스트로 저장

                //파일 전체 리스트 순회해 쿼리리스트랑 비교

                //찾아서 출력하기
                System.out.println(matchLine);
            }
        }
}
