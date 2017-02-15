package xyf.spider.xiaoshuo.zw37;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.listener.EventListener;
import com.geccocrawler.gecco.request.HttpGetRequest;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger logger = Logger.getLogger(App.class);
	private static final Properties properties = new Properties();
	static File rootPathFile;

	private static void init() {
		try {
			properties.load(App.class.getResourceAsStream("/config.properties"));
			String rootPath = properties.getProperty("root_path", "./zw37_book");
			rootPathFile = new File(rootPath);
			if (!rootPathFile.isDirectory()) {
				if (rootPathFile.exists())
					rootPathFile.delete();
				rootPathFile.mkdirs();
			}
		} catch (IOException e) {
			logger.error("App.init", e);
		}
	}

	final static List<BookBean> books = new ArrayList<BookBean>();

	public static void main(String[] args) {
		logger.info("begin");
		init();
		String books = properties.getProperty("books");
		if (StringUtils.isNotBlank(books)) {
			GeccoEngine engine = GeccoEngine.create()
					// 工程的包路径
					.classpath("xyf.spider.xiaoshuo.zw37")
					// 开启几个爬虫线程
					.thread(1)
					// 单个爬虫每次抓取完一个请求后的间隔时间
					.interval(1000)
					// 使用pc端userAgent
					.mobile(false);
//			engine.getSpiderBeanFactory().getRenderFactory()
			for (String item : books.split(",")) {
				// 开始抓取的页面地址
				HttpGetRequest request = new HttpGetRequest("http://www.37zw.com/" + item);
				request.setCharset("GBK");
				engine.start(request);
			}
			engine.setEventListener(new EventListener() {

				public void onStop(GeccoEngine ge) {
					assembleBooks();
				}

				public void onStart(GeccoEngine ge) {

				}

				public void onRestart(GeccoEngine ge) {

				}

				public void onPause(GeccoEngine ge) {

				}
			});
			logger.info("start");
			engine.start();
		}
	}

	protected static void assembleBooks() {
		for (BookBean book : books) {
			File bookPath = getBookPath(book);
			logger.info("assemble book " + book.getTitle());
			File f = new File(bookPath, "book.txt");
			try {
				OutputStream os = new FileOutputStream(f);
				try {
					Writer out = new OutputStreamWriter(os, "UTF-8");
					out.write(book.getTitle());
					out.write("\r\n");
					out.write("\r\n");
					out.write("作者：");
					out.write(book.getAuthor());
					out.write("\r\n");
					out.write("简介：");
					out.write(book.getDescription());
					out.write("\r\n");
					out.write("\r\n");

					for (Catalog item : book.getCatalog()) {
						String title = item.getText();
						out.write(title);
						out.write("\r\n");
						out.flush();
						File chapterFile = getChapterPath(book, item.getHref());
						if (chapterFile != null && chapterFile.exists() && chapterFile.isFile()) {
							InputStream is = new FileInputStream(chapterFile);
							IOUtils.copy(is, os);
							os.flush();
							IOUtils.closeQuietly(is);
						}
						out.write("\r\n");
						out.write("\r\n");
					}
					IOUtils.closeQuietly(os);
					os = null;
					FileUtils.copyFile(f, new File(bookPath, book.getTitle() + ".txt"));
				} finally {
					IOUtils.closeQuietly(os);
				}
				logger.info("assemble book finished");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static File getBookPath(BookBean bean) {
		File ret = new File(new File(rootPathFile, bean.getGroup()), bean.getBook());
		ret.mkdirs();
		return ret;
	}

	private static File getChapterPath(BookBean book, String href) {
		File bookPath = getBookPath(book);
		final File parent = new File(bookPath, "content");
		parent.mkdirs();
		int pos = href.lastIndexOf('.');
		if (pos >= 0)
			href = href.substring(0, pos);
		File ret = new File(parent, href + ".txt");
		return ret;
	}

	public static void saveBook(BookBean bean) {
		books.add(bean);
		File path = getBookPath(bean);
		File jsonFile = new File(path, "book.json");
		try {
			FileUtils.write(jsonFile, JSON.toJSONString(bean));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BookBean findBook(ChapterBean chapter) {
		for (BookBean book : books) {
			if (StringUtils.equals(book.getGroup(), chapter.getGroup())
					&& StringUtils.equals(book.getBook(), chapter.getBook()))
				return book;
		}
		return null;
	}

	public static void saveChapter(ChapterBean bean) {
		try {
			final BookBean book = findBook(bean);
			logger.info("save chapter: " + bean.getChapter());
			File chapterFile = getChapterPath(book, bean.getChapter() + ".html");
			FileUtils.write(chapterFile, bean.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNeedDownload(BookBean book, String href) {
		return !App.getChapterPath(book, href).exists();
	}
}
