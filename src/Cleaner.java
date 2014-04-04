import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cleaner {

    private boolean tag = false;
    AbstractSequenceClassifier<CoreLabel> classifier;


    public Cleaner(boolean tag, AbstractSequenceClassifier<CoreLabel> classifier) {
        this.tag = tag;
        this.classifier = classifier;
    }


    public String clean(String str) {

        // Now create a new pattern and matcher to replace whitespace with tabs
        Pattern replace = Pattern.compile("(<i>)(.*[^<])(</i>)");
        Pattern replace1 = Pattern.compile("[ ][ ]+");
        Pattern replace2 = Pattern.compile("\\{\\p{L}*\\}");
        Pattern replace3 = Pattern.compile("\\- \\-");
        Pattern replace4 = Pattern.compile("<[/]?[ib]>");
        Pattern replace5 = Pattern.compile("^\\p{Lu}\\p{L}*:");
        Pattern replace6 = Pattern.compile("^\"(.*)\"$");
        Pattern replace7 = Pattern.compile("^'(.*)'$");
        Pattern replace8 = Pattern.compile("^[(\\[]\\p{L}*[)\\]]$");
        Pattern replace9 = Pattern.compile(".*[\\p{L}].*");

        Matcher matcher2 = replace.matcher(str);
        String newString = matcher2.replaceAll("$2");

        Matcher matcher5 = replace1.matcher(newString);
        String newString3 = matcher5.replaceAll(" ");

        Matcher matcher3 = replace2.matcher(newString3);
        String newString2 = matcher3.replaceAll("");

        Matcher matcher1 = replace4.matcher(newString2);
        String newString4 = matcher1.replaceAll("");

        Matcher matcher6 = replace3.matcher(newString4);
        String newString5 = matcher6.replaceAll("-");

        Matcher matcher7 = replace5.matcher(newString5);
        String newString6 = matcher7.replaceAll("");

        Matcher matcher8 = replace6.matcher(newString6);
        String newString7 = matcher8.replaceAll("$1");

        Matcher matcher9 = replace7.matcher(newString7);
        String newString8 = matcher9.replaceAll("$1");

        Matcher matcher10 = replace8.matcher(newString8);
        String newString9 = matcher10.replaceAll("");

        Matcher matcher11 = replace9.matcher(newString9);
        if (!matcher11.matches()) {
            return "";
        }

        if (tag) {
            String taggedStr = classifier.classifyToString(newString8);
            return taggedStr;
        }

        return newString8;

    }

}
