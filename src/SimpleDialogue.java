public class SimpleDialogue {
    private String question;
    private String answer;
    private String subtitleFileId;
    private long dialogueId;
    private long timeBetweenUtterances;


    public SimpleDialogue(String subtitleFileId, String question, String answer, long dialogueId, long timeBetweenUtterances) {
        this.subtitleFileId = subtitleFileId;
        this.answer = answer;
        this.question = question;
        this.dialogueId = dialogueId;
        this.timeBetweenUtterances = timeBetweenUtterances;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getSubtitleFileId() {
        return subtitleFileId;
    }

    public long getTimeBetweenUtterances() {
        return timeBetweenUtterances;
    }

    @Override
    public String toString() {
        return "########  DIALOGUE  #########" + "\n" +
                "MOVIE - " + subtitleFileId + "\n" +
                "QUESTION - " + question + "\n" +
                "ANSWER - " + answer + "\n" +
                "\n";
    }

    public long getDialogueId() {
        return dialogueId;
    }
}