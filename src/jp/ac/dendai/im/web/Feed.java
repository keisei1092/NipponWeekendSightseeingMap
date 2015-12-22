package jp.ac.dendai.im.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**  1つのフィードを表すクラス。
 *  setURL メソッドで指定された URL のフィードを取得し、Item のリストを生成
 */
public class Feed implements Runnable {
    private URL url;
    private String encoding;
    private Document document;
    private ArrayList<Item> itemList;
    public Feed() {
        url = null;
        encoding = "utf-8";
        document = null;
        itemList = new ArrayList<Item>();
    }
    public void setURL(String url) {
        try {
            this.url = new URL(url);
        }
        catch(MalformedURLException e) {
            System.err.println("間違ったURL: " + url);
        }
    }
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /** Feed の内容を Item オブジェクトのリストとして返す */
    public ArrayList<Item> getItemList() {
        return itemList;
    }
    /** URLで指示されたフィードを取得し DOM tree を構築、itemList を生成 */
    public void run() {
        BufferedReader in = null;
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream,
                    encoding);
            in = new BufferedReader(reader);
        }
        catch (IOException e) {
            System.err.println("接続エラー: " + e);
            System.exit(1);
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(in));
        }
        catch (ParserConfigurationException e) {
            System.err.println("DocumentBuilderFactory生成エラー:" + e);
        }
        catch (SAXException e) {
            System.err.println("構文エラー:" + e);
        }
        catch (IOException e) {
            System.err.println("入出力エラー:" + e);
        }

        try {
            in.close();
        }
        catch (IOException e) {
            System.err.println("接続終了エラー: " + e);
        }

        try {
            // root要素 (rdf:RDF または rss または feed ) を findItems に渡す
            findItems(document.getDocumentElement());
        }
        catch (DOMException e) {
            System.err.println("DOMエラー:" + e);
        }
    }
    /** node の下位(子孫)から item 要素を探し Item オブジェクトを得る(深さ優先探索) */
    private void findItems(Node node) {
        // node の子ノードについて繰り返す
        for(Node current = node.getFirstChild();
            current != null;
            current = current.getNextSibling()) {
            // 着目している子ノード current は要素か
            if(current.getNodeType() == Node.ELEMENT_NODE) {
                // 要素なら要素名をチェック
                String nodeName = current.getNodeName();
                if(nodeName.equals("item") || nodeName.equals("entry"))  // item または entry 要素を発見
                    // item 要素または entry 要素から Item オブジェクトを生成しリストに追加
                    itemList.add(new Item(current));
                else
                    // item 要素または entry 要素でなければ、さらにその要素の子ノードから探す
                    // (channel, items など)
                    findItems(current); // 再帰呼び出し
            }
        }
    }
}