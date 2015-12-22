package jp.ac.dendai.im.web;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;

/**
 *  RSS フィード中の 1つの item 要素、もしくは
 *  Atomフィード中の 1つの entry要素  を表すクラス
 */
public class Item {
    private String title;
    private String link;
    private String description;  // RSS のみ。Atom では summary。
    private String dcDate;       // RSS 1.0 のみ。Atom では updated。
    private String pubDate;      // RSS 2.0 のみ
    private String dcCreator;    // RSS 1.0 のみ
    private List<String> dcSubject;  // RSS 1.0 のみ。複数ある場合に対応。
    private Date date;           // Java の形式に変換した日付
    private String dateString;   // Java の形式に変換した日付の標準的な文字列表現
    public Item (Node node) {
        title = null;
        link = null;
        description = null;
        dcDate = null;
        pubDate = null;
        dcCreator = null;
        dcSubject = null;
        date = null;
        dateString = null;
        // 引数 node (= item要素) の子ノードを走査
        for(Node current = node.getFirstChild();
            current != null;
            current = current.getNextSibling()) { // 子ノードを先頭から
            if(current.getNodeType() == Node.ELEMENT_NODE) { // 要素だったら
                String nodeName = current.getNodeName();
                if(nodeName.equals("title"))
                    title = getContent(current);
                else if(nodeName.equals("link"))
                    if(current.hasChildNodes())  // RSS
                        link = getContent(current);
                    else  // Atom
                        link = current.getAttributes().getNamedItem("href").getNodeValue();
                else if(nodeName.equals("description") || nodeName.equals("summary"))
                    description = getContent(current);
                else if(nodeName.equals("dc:date") || nodeName.equals("updated"))
                    dcDate = getContent(current);
                else if(nodeName.equals("pubDate"))
                    pubDate = getContent(current);
                else if(nodeName.equals("dc:creator"))
                    dcCreator = getContent(current);
                else if(nodeName.equals("dc:subject")) {
                    if(dcSubject == null) {
                        dcSubject = new LinkedList<String>();
                    }
                    dcSubject.add(getContent(current));
                }
                else if(nodeName.equals("guid") ||
                        nodeName.equals("category") ||
                        nodeName.startsWith("dc:") ||
                        nodeName.startsWith("rdf:") ||
                        nodeName.startsWith("dcq:")){
                    ; // 処理しない
                }
                else {
                    ; // 処理しない
                }
            }
            // 要素 (Node.ELEMENT_NODE) でなかったら何もしない (改行など)
        }
    }
    /** 要素から(子孫の)内容 (TEXT か CDATA) を取り出す */
    private String getContent(Node node) {
        String content = "";
        // node の子ノードを走査
        for(Node current = node.getFirstChild();
            current != null;
            current = current.getNextSibling()) {
            if(current.getNodeType() == Node.TEXT_NODE)
                content += current.getNodeValue();
            else if(current.getNodeType() == Node.CDATA_SECTION_NODE)
                content += current.getNodeValue();  // HTMLタグなどを含む
            else if(current.getNodeType() == Node.ELEMENT_NODE)
                content += getContent(current); // 要素なら再帰
            else
                ; // 上記以外のタイプでは何もしない
        }
        return content;
    }
    public String getTitle() {
        return title;
    }
    public String getLink() {
        return link;
    }
    /** RSS の description 要素または Atom の summary 要素を返す */
    public String getDescription() {
        return description;
    }
    public String getSummary() {  // Atom
        return description;
    }
    public String getDCDate() {
        return dcDate;
    }
    public String getUpdated() {  // Atom
        return dcDate;
    }
    public String getPubDate() {  // RSS 2.0
        return pubDate;
    }
    /** RSS 1.0 の dc:date 要素、RSS 2.0 の pubDate 要素 または Atom の updated 要素を返す */
    public String getDateTime() {
        if(dcDate != null)
            return dcDate;
        return pubDate;
    }
    public String getDCCreator() {
        return dcCreator;
    }
    public List<String> getDCSubject() {
        return dcSubject;
    }
    /** 日時情報を Date クラスのインスタンスにして返す。日時情報がなければ null */
    public Date getDate() {
        if(date == null)
            if(dcDate != null)
                date = rfc3339toDate(dcDate);
            else if(pubDate != null)
                date = pubDate2Date(pubDate);
        return date;
    }
    /** 日時情報を文字列として返す。Date クラスを経由させるので元のフィードの種類によらず同じ形式になる */
    public String getDateString() {
        if(dateString == null) {
            Date dateDate = getDate();
            if(dateDate != null)
                dateString = DateFormat.getDateTimeInstance().format(dateDate);
        }
        return dateString;
    }
    /** RFC3339形式の日時 (dc:date 要素, updated 要素) を Date に変換 */
    private static Date rfc3339toDate(String rfc3339) {
        // 先頭が数字でなければ RFC3339 ではない
        if(rfc3339.charAt(0) - '0' > 9)
            return pubDate2Date(rfc3339);
        // 大文字にし、UTC の省略記法である Z を +00:00 に変換
        rfc3339 = rfc3339.toUpperCase(Locale.ENGLISH).replace("Z", "+00:00");
        // TimeZone の ':' を削除
        int colon = rfc3339.lastIndexOf(':');
        rfc3339 = rfc3339.substring(0, colon) + rfc3339.substring(colon + 1, rfc3339.length());
        // ミリ秒を削除
        if(rfc3339.indexOf('.') != -1)
            rfc3339 = rfc3339.replaceAll("\\.\\d+", "");
        // 秒がなかったら :00 を付加 (価格.com対策)
        if(rfc3339.indexOf('+') != 19)
            rfc3339 = rfc3339.replace("+", ":00+");
        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        return format.parse(rfc3339, new ParsePosition(0));
    }
    /** pubDate 要素の内容を Date に変換 */
    private static Date pubDate2Date(String pubDate) {
        String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        return format.parse(pubDate, new ParsePosition(0));
    }
    /** 各属性を文字列に変換(表示に利用) */
    public String toString() {
        String string = "";
        if(title != null)
            string += "title: " + title + "\n";
        if(link != null)
            string += "link: " + link + "\n";
        if(description != null)
            string += "description: " + description + "\n";
        if(date == null)
            getDate();
        if(date != null)
            string += "date: " + getDateString() + "\n";
        if(dcCreator != null)
            string += "creator: " + dcCreator + "\n";
        if(dcSubject != null) {
            string += "subject(s):";
            for(String subject: dcSubject)
                string += " \"" + subject + "\"";
            string += "\n";
        }
        return string;
    }
}
