import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Daniel on 10-02-2014.
 */
public class XmlParser {

    private String subtitles;
    private long diff;
    private boolean toTag;
    private String ner;
    private String filePath;

    public XmlParser(String filePath) {
        this.filePath = filePath;
        parse();
    }

    private void parse() {
        try {
            File xml = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("subtitles");
            subtitles = nodeList.item(0).getTextContent();

            nodeList = doc.getElementsByTagName("timeDiff");
            diff = Long.parseLong(nodeList.item(0).getTextContent());

            nodeList = doc.getElementsByTagName("toTag");
            toTag = Boolean.parseBoolean(nodeList.item(0).getTextContent());

            if (toTag) {
                nodeList = doc.getElementsByTagName("serializedClassifier");
                ner = nodeList.item(0).getTextContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSubtitles() {
        return subtitles;
    }

    public long getDiff() {
        return diff;
    }

    public boolean isToTag() {
        return toTag;
    }

    public String getNer() {
        return ner;
    }

    @Override
    public String toString() {
        return "XmlParser{" +
                "subtitles='" + subtitles + '\'' +
                ", diff=" + diff +
                ", toTag=" + toTag +
                ", ner='" + ner + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
