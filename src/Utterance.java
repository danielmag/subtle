import java.util.Date;


public class Utterance {

    private String subtitleFileId;
    private String id;
    private Date initTime;
    private Date endTime;
    private String finalContent;


    public Utterance(String subtitleFileId, String id, Date initTime, Date endTime, String finalContent) {
        this.subtitleFileId = subtitleFileId;
        this.id = id;
        this.initTime = initTime;
        this.endTime = endTime;
        this.finalContent = finalContent;
        this.initTime.setYear(1970);
        this.endTime.setYear(1970);
    }

    public boolean hasToBeContinued() {
        String temp = new String(finalContent);
        temp = temp.replaceAll("\\s", "");

        return temp.endsWith(",") ||
                temp.endsWith(":");
    }

    public boolean endsWithLetterOrEllipsisOrHyphen() { //if you change the function, change name accordingly
        if (finalContent.length() < 2) {
            return false;
        }
        String temp = new String(finalContent);
        temp = temp.replaceAll("\\s", "");
        return Character.isLetter(temp.charAt(temp.length()-1)) ||
                temp.endsWith("..") ||
                temp.endsWith("-");
    }

    public boolean isContinuation() {
        if (finalContent.length() < 2) {
            return false;
        }
        String temp = new String(finalContent);
        temp = temp.replaceAll("\\s", "");

        char firstChar = temp.charAt(0);
        return Character.isLowerCase(firstChar) ||
                temp.startsWith(",") ||
                temp.startsWith("..");
    }


    public String getFinalContent() {
        return finalContent;
    }

    public String getSubtitleFileId() {
        return subtitleFileId;
    }

    public void addContent(String content) {
        finalContent += " " + content;
    }

    public Date getInitTime() {
        return initTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date time) {
        endTime = time;
    }

    @Override
    public String toString() {
        return "Movie: " + subtitleFileId +
                "Line " + id +
                "Time " + initTime.getTime() +
                " --> " + endTime.getTime() +
                "Content" + (finalContent != null ? finalContent : "null");
    }

    public boolean startsWithHyphen() {
        return finalContent.startsWith("-");
    }

    public void removeFirstHyphen() {
        finalContent = finalContent.replaceFirst("-", "").trim();
    }
}




