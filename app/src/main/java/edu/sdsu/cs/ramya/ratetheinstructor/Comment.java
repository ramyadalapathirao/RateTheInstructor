package edu.sdsu.cs.ramya.ratetheinstructor;

/**
 * Created by sarathbollepalli on 2/28/15.
 */
public class Comment {
    private String comment;
    private String date;
    private int commentId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
