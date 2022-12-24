package com.wei.sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class TransSDK {
    //百度翻译 APPID  https://fanyi-api.baidu.com/api/trans/
    private static final String APP_ID = "";
    private static final String SECURITY_KEY = "";

    public static void main(String[] args) {
        //更改要翻译的文件夹
        String dir = "./API Reference/Utils";
        File file = new File(dir);
        File[] list = file.listFiles();
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                File[] list2 = list[i].listFiles();
                for (int j = 0; j < list2.length; j++) {
                    if (list2[j].isFile()) {
                        count++;
                        System.out.println("正在翻译第：" + count);
                        transOneFile(list2[j]);
                    }
                }
            }
            if (list[i].isFile()) {
                count++;
                System.out.println("正在翻译第：" + count);
                transOneFile(list[i]);
            }
        }
        System.out.println();
    }


    public static void transOneFile(File file) {
        try {
            System.out.println("当前被翻译文件路径："+file.getAbsolutePath());
            //读取.html文件为字符串
            String htmlStr = toHtmlString(file);
            //解析字符串为Document对象
            Document doc = Jsoup.parse(htmlStr);
            //获取div列表
            Elements div = doc.getElementsByClass("site-main");

//
//            List<Node> elements = div.get(0).childNodes().get(1).childNodes();
//            Node element = new Element("p");
//            element.wrap("<font color=\"#666\">这是中文 </font>");
//            elements.add(5, element);
            Elements elements = div.get(0).getElementsByAttributeValue("color", "#666");
            for (int i = 0; i < elements.size(); i++) {
                Element one = elements.get(i);
                String result = transStr(one.text());
                one.append("</br>" + result);
            }

//            System.out.println(doc);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(doc.toString());
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String transStr(String str) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);


        JSONObject jsonObject = JSON.parseObject(api.getTransResult(str, "auto", "zh"));
        if (jsonObject.containsKey("error_code")) {
            System.out.println("翻译调用错误，错误码：" + jsonObject.get("error_code")+"，错误信息："+jsonObject.get("error_msg"));
            return "";
        }
        JSONArray array = (JSONArray) jsonObject.get("trans_result");
        JSONObject a = (JSONObject) array.get(0);
        return a.get("dst").toString();

    }

    /**
     * 读取本地html文件里的html代码
     *
     * @return
     */
    public static String toHtmlString(File file) {
        // 获取HTML文件流
        StringBuffer htmlSb = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "utf-8"));
            while (br.ready()) {
                htmlSb.append(br.readLine());
            }
            br.close();
            // 删除临时文件
            //file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // HTML文件字符串
        String htmlStr = htmlSb.toString();
        // 返回经过清洁的html文本
        return htmlStr;
    }
}
