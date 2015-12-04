package web.vo;

/**
 * Created by alxev on 01.12.2015.
 */
public class Comment {
    private String commentDate;
    private String userName;
    private String commentText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!commentDate.equals(comment.commentDate)) return false;
        if (!commentText.equals(comment.commentText)) return false;
        if (!userName.equals(comment.userName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commentDate.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + commentText.hashCode();
        return result;
    }

    public String getCommentDate() {

        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
