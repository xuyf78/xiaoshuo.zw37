package xyf.spider.xiaoshuo.zw37;

import java.lang.reflect.Field;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.geccocrawler.gecco.annotation.FieldRenderName;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.spider.SpiderThreadLocal;
import com.geccocrawler.gecco.spider.render.CustomFieldRender;

import net.sf.cglib.beans.BeanMap;

@FieldRenderName("printableTextFieldRender")
public class PrintableTextFieldRender implements CustomFieldRender {

	private String content;
	private String baseUrl;

	public void render(HttpRequest request, HttpResponse response, BeanMap beanMap, SpiderBean bean, Field field) {
		HtmlField htmlField = field.getAnnotation(HtmlField.class);
		baseUrl = request.getUrl();
		content = response.getContent();
		String html;
		if(htmlField != null){
			html = $element(htmlField.cssPath()).html();
		}else
			html = content;
		String newContent = Jsoup.clean(html, baseUrl, Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
		newContent = newContent.replace("&nbsp;", " ");
		beanMap.put(field.getName(), newContent);
	}

	public Elements $(String selector) {
		Document document = Jsoup.parse(content, baseUrl);
		Elements elements = document.select(selector);
		if (SpiderThreadLocal.get().getEngine().isDebug()) {
			if (!selector.equalsIgnoreCase("script")) {
				// log.debug("["+selector+"]--->["+elements+"]");
				System.out.println("[" + selector + "]--->[" + elements + "]");
			}
		}
		return elements;
	}

	public Element $element(String selector) {
		Elements elements = $(selector);
		if (elements != null && elements.size() > 0) {
			return elements.first();
		}
		return null;
	}
}
