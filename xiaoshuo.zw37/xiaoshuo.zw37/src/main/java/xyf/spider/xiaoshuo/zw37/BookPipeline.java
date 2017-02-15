package xyf.spider.xiaoshuo.zw37;

import org.apache.commons.lang3.StringUtils;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.geccocrawler.gecco.utils.UrlUtils;

@PipelineName("bookPipeline")
public class BookPipeline implements Pipeline<BookBean> {

	public void process(BookBean book) {
		App.saveBook(book);
		final HttpRequest request = book.getRequest();
		for (Catalog item : book.getCatalog()) {
			String href = item.getHref();
			if (StringUtils.isNotBlank(href)) {
				if (App.isNeedDownload(book, href)){
					DeriveSchedulerContext.into(request.subRequest(UrlUtils.relative2Absolute(request.getUrl(), href)));
//					break;
				}
			}
		}
	}

}
