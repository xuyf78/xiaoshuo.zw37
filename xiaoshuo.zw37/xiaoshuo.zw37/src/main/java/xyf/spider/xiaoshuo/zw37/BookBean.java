package xyf.spider.xiaoshuo.zw37;

import java.util.List;

import com.geccocrawler.gecco.annotation.Attr;
import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Request;
import com.geccocrawler.gecco.annotation.RequestParameter;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.HtmlBean;

@Gecco(matchUrl = { "http://www.37zw.com/{group}/{book}", "http://www.37zw.com/{group}/{book}/" }, pipelines = {
		"bookPipeline" })
public class BookBean implements HtmlBean {
	private static final long serialVersionUID = 1L;

	@RequestParameter("group")
	private String group;
	@RequestParameter("book")
	private String book;

	@Request
	private HttpRequest request;

	@Attr("content")
	@HtmlField(cssPath = "head meta[property='og:title'][content]")
	private String title;

	@Attr("content")
	@HtmlField(cssPath = "head meta[property='og:novel:author'][content]")
	private String author;

	@Attr("content")
	@HtmlField(cssPath = "head meta[property='og:description'][content]")
	private String description;

	@Attr("content")
	@HtmlField(cssPath = "head meta[property='og:novel:category'][content]")
	private String category;

	@HtmlField(cssPath = "#list > dl > dd")
	private List<Catalog> catalog;

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

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Catalog> getCatalog() {
		return catalog;
	}

	public void setCatalog(List<Catalog> catalog) {
		this.catalog = catalog;
	}
}
