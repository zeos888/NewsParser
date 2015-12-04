package web.view;

import web.Controller;
import web.vo.Comment;
import web.vo.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by alxev on 28.11.2015.
 */
public class HtmlView implements View
{
    private final String filePath = "./src/" + (this.getClass().getPackage().getName()).replaceAll("\\.", "/") + "/html/NewsList.html";
    private final String newsTemplate = "./src/" + (this.getClass().getPackage().getName()).replaceAll("\\.", "/") + "/html/NewsTemplate.html";
    private final String newsFolder = "./src/" + (this.getClass().getPackage().getName()).replaceAll("\\.", "/") + "/html/";
    private Controller controller;
    @Override
    public void update(List<News> newsList)
    {
        try{
            String html = getUpdatedFileContent(newsList);
            updateFile(html);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    public void userRunModel(){
        controller.onViewUpdate();
    }

    private String getUpdatedFileContent(List<News> newsList){
        try{
            Document document = getDocument();
            Element elementTemplate = document.getElementsByClass("template").get(0);
            Element template = elementTemplate.clone();
            template.removeAttr("style");
            template.removeClass("template");

            document.getElementsByAttributeValue("class", "news").remove();

            for (News news : newsList){
                Element element = template.clone();
                element.getElementsByClass("date").first().text(news.getDate());
                //Element comments = element.getElementsByClass("comments").first();
                Element comments = element.getElementsByClass("comments").first().getElementsByTag("a").first();
                comments.text(String.valueOf(news.getComments()));
                comments.attr("href", "./" + news.getUrl());
                //Element header = element.getElementsByClass("header").first();
                Element header = element.getElementsByClass("header").first().getElementsByTag("a").first();
                header.text(String.valueOf(news.getHeader()));
                header.attr("href", "./" + news.getUrl());

                elementTemplate.before(element);

                news.filterComments();

                writeNewsContent(news.getNewsText(), news.getCommentsList(), news.getUrl());
            }
            return document.html();
        } catch (IOException ignored){
            ignored.printStackTrace();
            return "Some exception occurred";
        }
    }

    private void updateFile(String html) throws IOException{
        FileWriter writer = new FileWriter(filePath);
        writer.write(html);
        writer.close();
    }

    protected Document getDocument() throws IOException{
        File htmlFile = new File(filePath);
        Document doc = Jsoup.parse(htmlFile, "UTF-8");
        return doc;
    }

    protected void writeNewsContent(String newsText, List<Comment> commentList, String newsURL) throws IOException{
        FilesHelper.copyFile(newsTemplate, newsFolder + newsURL);
        File htmlFile = new File(newsFolder + newsURL);
        Document doc = Jsoup.parse(htmlFile, "UTF-8");
        Element newsTextElement = doc.getElementsByAttributeValue("class", "center_col news_center_col").first();
        newsTextElement.html(newsText);
        Element elementTemplate = doc.getElementsByClass("template").get(0);
        Element template = elementTemplate.clone();
        template.removeAttr("style");
        template.removeClass("template");

        doc.getElementsByAttributeValue("class", "comment").remove();
        for (Comment comment : commentList){
            Element element = template.clone();
            element.getElementsByClass("commentDate").first().text(comment.getCommentDate());
            element.getElementsByClass("userName").first().text(comment.getUserName());
            element.getElementsByClass("commentText").first().html(comment.getCommentText());

            elementTemplate.before(element);
        }
        FileWriter writer = new FileWriter(newsFolder + newsURL);
        writer.write(doc.html());
        writer.close();
    }
}
