package web.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alxev on 27.11.2015.
 */
public class News
{
    private String date;
    private int comments;
    private String header;
    private String newsId;

    private List<Comment> commentsList;

    private String newsText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        if (comments != news.comments) return false;
        if (commentsList != null ? !commentsList.equals(news.commentsList) : news.commentsList != null) return false;
        if (!date.equals(news.date)) return false;
        if (!header.equals(news.header)) return false;
        if (!newsId.equals(news.newsId)) return false;
        if (!newsText.equals(news.newsText)) return false;
        if (!url.equals(news.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + comments;
        result = 31 * result + header.hashCode();
        result = 31 * result + newsId.hashCode();
        result = 31 * result + (commentsList != null ? commentsList.hashCode() : 0);
        result = 31 * result + newsText.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    public String getNewsText() {

        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comment> commentsList) {

        this.commentsList = commentsList;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getComments()
    {
        return comments;
    }

    public void setComments(int comments)
    {
        this.comments = comments;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    private String url;

    public void filterComments(){
        List<Comment> retList = new ArrayList<Comment>();
        retList.addAll(commentsList);

        for (Comment comment : commentsList){
            if (comment.getUserName().equals("Vyacheslav") || comment.getUserName().equals("PersonaGrata") || comment.getUserName().equals("The Best Of")){
                retList.remove(comment);
            }
        }

        commentsList = retList;
    }

}
