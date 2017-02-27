package com.cardvlaue.sys.data;

import java.util.List;

public class MidsResponse extends ErrorResponse {

    private List<MidsItemResponse> results;

    private String resCode;

    private String resMsg;

    private List<String> answers;

    private String correctAnswers;

    private String question;

    private String verifyId;

    private boolean verifyResult;

    private int leftVerifyTimes;

    public List<MidsItemResponse> getResults() {
        return results;
    }

    public void setResults(List<MidsItemResponse> results) {
        this.results = results;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public boolean isVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(boolean verifyResult) {
        this.verifyResult = verifyResult;
    }

    public int getLeftVerifyTimes() {
        return leftVerifyTimes;
    }

    public void setLeftVerifyTimes(int leftVerifyTimes) {
        this.leftVerifyTimes = leftVerifyTimes;
    }

}
