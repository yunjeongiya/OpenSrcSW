package com.yunjeongiya.simpleir;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.File;

public class TitleParser {
    public String parse (File input) throws Exception{
        Document doc = Jsoup.parse(input, "UTF-8");

        Elements foodDic = doc.select("title");

        return foodDic.text();
    }
}
