package xyf.spider.xiaoshuo.zw37;

import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;

@PipelineName("chapterPipeline")
public class ChapterPipeline implements Pipeline<ChapterBean> {

	public void process(ChapterBean bean) {
		App.saveChapter(bean);
	}

}
