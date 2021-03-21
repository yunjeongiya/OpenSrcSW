package com.yunjeongiya.simpleir;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class BodyParser {
    public String parse (File input) throws Exception{
        Document doc = Jsoup.parse(input, "UTF-8");

        Elements foodDic = doc.select("p");
        StringBuilder resultBuilder = new StringBuilder();
        for( Element food : foodDic )
        {
            resultBuilder.append(food.text());
            resultBuilder.append('\n');
        }
        return resultBuilder.toString();
    }
}
