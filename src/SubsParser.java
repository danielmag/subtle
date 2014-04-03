import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

public class SubsParser {
    private final long diff;
    private AbstractSequenceClassifier<CoreLabel> classifier;
    private String stats;
    private File stat;
    private String dialogues;
    private File file;
    private int SubsParsed;
    private int badlyFormedSubs;
    private boolean toTag;
    private int totalInteractions = 0;
    private String subsLocation;

    public SubsParser() {
        XmlParser parser = new XmlParser("./resources/config.xml");
        System.out.println(parser);
        this.diff = parser.getDiff();
        toTag = parser.isToTag();
        classifier = null;
        if (toTag) {
            classifier = CRFClassifier.getClassifierNoExceptions(parser.getNer());
        }
        stats = "corpus" + "Statistics.txt";
        stat = new File(stats);
        dialogues = "corpus" + this.diff + "s" + "Dialogues.txt";
        file = new File(dialogues);
        SubsParsed = 0;
        badlyFormedSubs = 0;
        this.subsLocation = parser.getSubtitles();
    }

    public static void main(String[] args) {
        SubsParser t = new SubsParser();
        t.createsAllSubs();
        String statsFinal = "statisticsFinal.txt";

        try {
            FileWriter stat = new FileWriter(statsFinal, true);

            stat.write("###########################################" + "\n");
            stat.write("Elapsed time between utterances (ms) - " + t.getDiff() + "\n");
            stat.write("Number of subtitle of this genre - " + (t.SubsParsed + t.badlyFormedSubs) + "\n");
            stat.write("Number of well formed subtitles - " + t.SubsParsed + "\n");
            stat.write("Number of badly formed subtitles - " + t.badlyFormedSubs + "\n");
            stat.write("Total number of interactions - " + t.totalInteractions + "\n");
            int averageInteractions = t.totalInteractions / t.SubsParsed;
            stat.write("Average number of interactions per file - " + averageInteractions + "\n");
            stat.write("###########################################" + "\n" + "\n");
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createsAllSubs() {
        try {
            stat.delete();
            file.delete();
            File f = new File(subsLocation);
            File[] files = f.listFiles();
            int i = 0;
            for (File file : files) {
                i++;
                if (file.isDirectory()) {
                    continue;
                }
                System.out.println(file.getName());
                String line;
                String id;
                Date initTime;
                Date endTime;
                BufferedReader reader;
                if (file.getName().endsWith(".gz")) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
                } else {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS", Locale.UK);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                //Contains all subtitles from a movie
                ArrayList<Utterance> sub = new ArrayList<>();

                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        id = line;
                        while (((line = reader.readLine()) != null) && line.trim().length() == 0) {
                            //do nothing
                        }
                        String[] times = line.split(" --> ");
                        initTime = dateFormat.parse(times[0]);
                        endTime = dateFormat.parse(times[1]);

                        ArrayList<String> content = new ArrayList<>();
                        //catch whitespace line at the beginning
                        if (((line = reader.readLine()) != null) && !line.trim().isEmpty()) {
                            content.add(line);
                        }

                        //while its not a blank line
                        while (((line = reader.readLine()) != null) && !line.trim().isEmpty()) {
                            content.add(line);
                        }
                        ArrayList<Utterance> utts = extractUtterances(file.getName(), id, initTime, endTime, content);
                        sub.addAll(utts);
                    }
                    sub = joinContents(sub);
                    dialoguesToTxt(createDialogues(sub));

                } catch (Exception e) {
                    badlyFormedSubs++;
                    continue;
                }
                reader.close();
            }
        } catch (Exception e) {}
    }

    private ArrayList<Utterance> joinContents(ArrayList<Utterance> sub) {
        for (int i = 0; i < sub.size(); i++) {
            Utterance uter1 = sub.get(i);
            Utterance uter2;

            if (i + 1 < sub.size()) {
                uter2 = sub.get(i + 1);
            } else {
                break;
            }

            if (uter1.hasToBeContinued() || (uter1.endsWithLetterOrEllipsisOrHyphen() && uter2.isContinuation())) {
                uter1.setEndTime(uter2.getEndTime());
                uter1.addContent(uter2.getFinalContent());
                sub.remove(i + 1);
                i--;
            }
            if (uter2.startsWithHyphen() && i > 0) {
                Utterance uter0 = sub.get(i - 1);
                uter0.addContent(uter1.getFinalContent());
                uter0.setEndTime(uter1.getEndTime());
                uter2.removeFirstHyphen();
                sub.remove(i);
                i--;
            }
        }
        return sub;
    }

    private ArrayList<SimpleDialogue> createDialogues(ArrayList<Utterance> sub) {
        ArrayList<SimpleDialogue> SimpleDialogues = new ArrayList<>();
        Cleaner cleaner = new Cleaner(toTag, classifier);
        int conversationCount = 0;
        for (int i = 0; i < sub.size(); i++) {
            Utterance uter1 = sub.get(i);
            Utterance uter2;
            if (i + 1 < sub.size()) {
                uter2 = sub.get(i + 1);
            } else {
                break;
            }

            long timeBetweenUtterances = uter2.getInitTime().getTime() - uter1.getEndTime().getTime();

            String cleanedU1 = cleaner.clean(uter1.getFinalContent());
            String cleanedU2 = cleaner.clean(uter2.getFinalContent());

            if (!cleanedU1.trim().isEmpty() &&
                    !cleanedU2.trim().isEmpty() &&
                    (this.diff == 0 ? true : timeBetweenUtterances < this.diff)) {
                SimpleDialogue sd = new SimpleDialogue(uter1.getSubtitleFileId(),
                        cleanedU1,
                        cleanedU2,
                        conversationCount,
                        timeBetweenUtterances);
                SimpleDialogues.add(sd);
                conversationCount++;
            } else {
                conversationCount = 0;
            }
        }
        return SimpleDialogues;
    }

    private void dialoguesToTxt(ArrayList<SimpleDialogue> SimpleDialog) {
        try {
            FileWriter x = new FileWriter(dialogues, true);
            FileWriter st = new FileWriter(stats, true);

            String content;

            for (SimpleDialogue sd : SimpleDialog) {
                content = "\n" + "SubId - " + sd.getSubtitleFileId() + "\n" +
                        "DialogId - " + sd.getDialogueId() + '\n' +
                        "Diff - " + sd.getTimeBetweenUtterances() + '\n' +
                        "I - " + sd.getQuestion() + "\n" +
                        "R - " + sd.getAnswer() + "\n";
                x.write(content);

            }

            SubsParsed++;
            st.write("############################" + "\n");
            st.write("ID - " + SimpleDialog.get(0).getSubtitleFileId() + "\n");
            st.write("TimeDiff - " + SimpleDialog.get(0).getTimeBetweenUtterances() + "\n");
            st.write("Interactions - " + SimpleDialog.size() + "\n");
            st.write("############################" + "\n" + "\n");

            totalInteractions += SimpleDialog.size();

            x.close();
            st.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Utterance> extractUtterances(String name, String id, Date initTime, Date endTime, ArrayList<String> content) {
        String finalContent = "";
        ArrayList<Utterance> utts = new ArrayList<>();
        //2 lines
        if (content.size() == 2) {
            /*
            Checks if the 2nd phrase does not belong to another character, by verifying that the first letter is "-"
            If it is not, it joins them in the same sentence
            */
            String line1 = content.get(0);
            String line2 = content.get(1);

            if (!line2.startsWith("-")) {
                finalContent = line1 + " " + line2;
                utts.add(new Utterance(name, id, initTime, endTime, finalContent));
            } else {
                if (line1.startsWith("-")) {
                    utts.add(new Utterance(name, id, initTime, endTime, line1.replaceFirst("-", "").trim()));
                    utts.add(new Utterance(name, id, endTime, endTime, line2.replaceFirst("-", "").trim()));
                } else {
                    utts.add(new Utterance(name, id, initTime, endTime, line1));
                    utts.add(new Utterance(name, id, endTime, endTime, line2));
                }
            }
        }
        // 1 line
        else if (content.size() == 1) {
            finalContent = content.get(0);
            utts.add(new Utterance(name, id, initTime, endTime, finalContent));
        } else if (content.size() > 2) { //more than 2 lines
            for (String str : content) {
                finalContent += " " + str;
            }
            utts.add(new Utterance(name, id, initTime, endTime, finalContent));
        }
        return utts;
    }

    public long getDiff() {
        return diff;
    }
}

  