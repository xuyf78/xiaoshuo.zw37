package xyf.spider.xiaoshuo.zw37;

import com.geccocrawler.gecco.annotation.FieldRenderName;
import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.Html;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.RequestParameter;
import com.geccocrawler.gecco.spider.HtmlBean;

@Gecco(matchUrl={App.HTTP_WWW_37ZW_NET+"{group}/{book}/{chapter}.html"}, pipelines={"chapterPipeline"})
public class ChapterBean implements HtmlBean {

    private static final long serialVersionUID = 1;

    @RequestParameter("group")
    private String group;

    @RequestParameter("book")
    private String book;
    
    @RequestParameter("chapter")
    private String chapter;
    
    @FieldRenderName("printableTextFieldRender")
    @HtmlField(cssPath="#content")
    private String content;

    @Html(outer=true)
    @HtmlField(cssPath="#content")
    private String contentHtml;
    
    public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public String getBook() {
		return book;
	}


	public void setBook(String book) {
		this.book = book;
	}


	public String getChapter() {
		return chapter;
	}


	public void setChapter(String chapter) {
		this.chapter = chapter;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getContentHtml() {
		return contentHtml;
	}


	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}
	
}