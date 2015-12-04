package web.model;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import web.vo.Comment;
import web.vo.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by alxev on 27.11.2015.
 */
public class IXBTStrategy implements Strategy
{
    private static final String URL_FORMAT = "http://www.ixbt.com/news/%d/%s/%s/";

    @Override
    public List<News> getNews()
    {
        List<News> allNews = new ArrayList<News>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE, -2);
        Calendar cutoffCalendar = Calendar.getInstance();
        cutoffCalendar.add(calendar.DATE, -3);

        WebClient webClient = new WebClient();
        CookieManager cookieManager = new CookieManager();
        Cookie cookie = new Cookie("http://www.ixbt.com/", "news_comments", "show_all%3A1%3B");
        cookieManager.addCookie(cookie);
        webClient.setCookieManager(cookieManager);

        try{
            while (true){
                int year = calendar.get(calendar.YEAR);
                int month = calendar.get(calendar.MONTH) + 1;
                int day = calendar.get(calendar.DAY_OF_MONTH);
                Document newsDocument = getDocument(year, month, day);
                List<Element> newsItems = newsDocument.getElementsByAttributeValue("class", "news-block _clr");
                if (!newsItems.isEmpty()){
                    for (Element newsItem : newsItems){
                        News news = new News();
                        news.setDate(dateFormat.format(calendar.getTime()) + " " + newsItem.getElementsByAttributeValue("class", "date").first().text());
                        news.setNewsId(newsItem.getElementsByAttributeValue("class", "comment").first().getElementsByTag("span").first().attr("id").replace("spubid:", ""));
                        String rawCommentsBlock = getCommentsBlock(webClient, "http://www.ixbt.com/cgi/news_comments/comments.pl?action=ajax&sub_option=full_comments_list&option=news&category=news3&id=" + news.getNewsId());
                        List<Comment> commentList= parseCommentBlock(rawCommentsBlock);
                        news.setCommentsList(commentList);
                        news.setComments(commentList.size());
                        news.setHeader(newsItem.getElementsByClass("title").first().text());
                        String newsURL = "http://www.ixbt.com" + newsItem.getElementsByClass("title").first().getElementsByTag("a").first().attr("href");
                        news.setNewsText(getNewsText(newsURL));
                        newsURL = newsURL.substring(newsURL.lastIndexOf('/'));
                        news.setUrl(newsURL);
                        allNews.add(news);
                    }
                }
                calendar.add(calendar.DATE, -1);
                if (calendar.before(cutoffCalendar)){
                    break;
                }

            }
        } catch (IOException ignored){

        }
        return allNews;
    }

    protected Document getDocument(int year, int month, int day) throws IOException{
        String mon = "0" + month;
        String da = "0" + day;
        String url = String.format(URL_FORMAT, year, mon.substring(mon.length() - 2), da.substring(da.length() - 2));
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        String referrer = "http://www.ixbt.com/news/2015/";
        Document document = Jsoup.connect(url).userAgent(userAgent).referrer(referrer).cookie("news_comments", "show_all%3A1%3B").get();
        return document;
    }

    protected String getCommentsBlock(WebClient webClient, String url) throws IOException{
        WebRequest webRequest = new WebRequest(new URL(url));
        webRequest.setCharset("utf-8");
        Page page = webClient.getPage(webRequest);
        String commentsBlock = page.getWebResponse().getContentAsString();
        return wrapCommentsBlock(commentsBlock);
    }

    protected String wrapCommentsBlock(String commentsBlock){
        String toRemove = "{\"status\":\"ok\",\"content\":\"<!-- main START -->\\n";
        commentsBlock = commentsBlock.replace(toRemove, "");
        //commentsBlock.replace("{\"status\":\"ok\",\"content\":\"<!-- main START -->\\n", "");
        commentsBlock = commentsBlock.replace("\\n<!-- main END -->\",\n" +
                "\"page\":1,\n" +
                "\"dev\":\"3\"}", "");
        commentsBlock = commentsBlock.replace("\\n", "");
        commentsBlock = commentsBlock.replace("\\/", "/");
        commentsBlock = commentsBlock.replace("\\\"", "\"");
        commentsBlock = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head lang=\"ru\">\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title></title>\n" +
                "</head>\n" +
                "<body>" + commentsBlock;
        commentsBlock = commentsBlock + "</body>\n" +
                "</html>";
        return commentsBlock;
    }

    protected List<Comment> parseCommentBlock(String commentBlock){
        List<Comment> commentList = new ArrayList<Comment>();
        Document document = Jsoup.parse(commentBlock);
        List<Element> commentElements = document.getElementsByAttributeValue("class", "comment_item");
        if (!commentElements.isEmpty()){
            for (Element element : commentElements){
                Comment comment = new Comment();
                comment.setCommentDate(element.getElementsByAttributeValue("class", "newsTime").first().text());
                comment.setUserName(element.getElementsByAttributeValue("class", "comments_userName").first().text());
                comment.setCommentText(element.getElementsByAttributeValue("class", "comment_user_text").first().toString());
                commentList.add(comment);
            }
        }
        return commentList;
    }

    protected String getNewsText(String url) throws IOException{
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        String referrer = "http://www.ixbt.com/news/2015/";
        Document document = Jsoup.connect(url).userAgent(userAgent).referrer(referrer).cookie("news_comments", "show_all%3A1%3B").get();
        return document.getElementsByAttributeValue("class", "news_body").first().toString();
    }
}
