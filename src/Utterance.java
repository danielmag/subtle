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

    public boolean endsWithLowerCaseOrDotsOrHyphen() {
        if (finalContent.length() < 2) {
            return false;
        }

        char lastChar = finalContent.charAt(finalContent.length() - 1);

        return Character.isLowerCase(lastChar) ||
                finalContent.endsWith(",") ||
                finalContent.endsWith("..") ||
                finalContent.endsWith("-");
    }

    public boolean beginsWithLowerCaseOrDots() {
        if (finalContent.length() < 2) {
            return false;
        }

        char firstChar = finalContent.charAt(0);

        return Character.isLowerCase(firstChar) ||
                finalContent.startsWith(",") ||
                finalContent.startsWith("..");
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




