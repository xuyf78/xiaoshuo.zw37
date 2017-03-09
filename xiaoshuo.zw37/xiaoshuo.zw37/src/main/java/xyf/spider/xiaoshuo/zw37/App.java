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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.listener.EventListener;
import com.geccocrawler.gecco.request.HttpGetRequest;

/**
 * Hello world!
 *
 */
public class App {
	public static final String LAST_HREF_TXT = "lastHref.txt";
	public static final String LAST_SUFFIX_TXT = "lastSuffix.txt";
	public static final String BOOK_JSON = "book.json";
	private static final Logger logger = Logger.getLogger(App.class);
	public static final Properties properties = new Properties();
	static File rootPathFile;

	public static void init() {
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
		String books;
		if(args.length > 0){
			books = String.join(",", args);
		}else{
			books = properties.getProperty("books");
		}
		if (StringUtils.isNotBlank(books)) {
			logger.info("下载："+books);
			GeccoEngine engine = GeccoEngine.create()
					// 工程的包路径
					.classpath("xyf.spider.xiaoshuo.zw37")
					// 开启几个爬虫线程
					.thread(1)
					// 单个爬虫每次抓取完一个请求后的间隔时间
					.interval(1000)
					// 使用pc端userAgent
					.mobile(false);
			for (String item : books.split(",")) {
				// 开始抓取的页面地址
				HttpGetRequest request = new HttpGetRequest("http://www.37zw.com/" + item);
				request.setCharset("GBK");
				engine.start(request);
			}
			engine.setEventListener(new EventListener() {

				public void onStop(GeccoEngine ge) {
					// assembleBooks();
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
			assembleBook(book);
		}
	}

	public static void assembleBook(BookBean book) {
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

				String lastHref = null;
				for (Catalog item : book.getCatalog()) {
					if (StringUtils.isNotBlank(item.getHref()))
						lastHref = item.getHref();
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
				saveLastHref(book, lastHref);
			} finally {
				IOUtils.closeQuietly(os);
			}
			logger.info("assemble book finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveLastHref(BookBean book, String lastHref) throws IOException {
		if (StringUtils.isNotBlank(lastHref)) {
			File bookPath = getBookPath(book);
			FileUtils.write(new File(bookPath, LAST_HREF_TXT), lastHref);
		}
	}

	public static void assembleBookPart(BookBean book, String suffix, String beginHref) {
		File bookPath = getBookPath(book);
		logger.info("assemble book part " + book.getTitle() + " suffix: " + suffix + " beginHref: " + beginHref);
		File f = new File(bookPath, "book.txt");
		boolean found = false;
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

				String lastHref = null;
				for (Catalog item : book.getCatalog()) {
					if (!found) {
						if (StringUtils.equals(item.getHref(), beginHref))
							found = true;
						else
							continue;
					}
					if (StringUtils.isNotBlank(item.getHref()))
						lastHref = item.getHref();
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
				FileUtils.copyFile(f, new File(bookPath, book.getTitle() + suffix + ".txt"));
				saveLastHref(book, lastHref);
				saveLastSuffix(book, suffix);
			} finally {
				IOUtils.closeQuietly(os);
			}
			logger.info("assemble book part finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveLastSuffix(BookBean book, String suffix) throws IOException {
		if (StringUtils.isNotBlank(suffix)) {
			File bookPath = getBookPath(book);
			FileUtils.write(new File(bookPath, LAST_SUFFIX_TXT), suffix);
		}
	}

	public static File getBookPath(BookBean book) {
		File ret = new File(new File(rootPathFile, book.getGroup()), book.getBook());
		ret.mkdirs();
		return ret;
	}

	public static File getChapterPath(BookBean book, String href) {
		File bookPath = getBookPath(book);
		final File parent = new File(bookPath, "content");
		parent.mkdirs();
		int pos = href.lastIndexOf('.');
		if (pos >= 0)
			href = href.substring(0, pos);
		File ret = new File(parent, href + ".txt");
		return ret;
	}

	public static void saveBook(BookBean book) {
		books.add(book);
		File path = getBookPath(book);
		File jsonFile = new File(path, BOOK_JSON);
		try {
			FileUtils.write(jsonFile, JSON.toJSONString(book,SerializerFeature.BrowserCompatible));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BookBean findBook(ChapterBean chapter) {
		final String group = chapter.getGroup();
		final String book = chapter.getBook();
		return findBook(group, book);
	}

	public static BookBean findBook(final String group, final String bookId) {
		for (BookBean book : books) {
			if (StringUtils.equals(book.getGroup(), group) && StringUtils.equals(book.getBook(), bookId))
				return book;
		}
		return null;
	}

	public static void saveChapter(ChapterBean chapter) {
		try {
			final BookBean book = findBook(chapter);
			String chapterTitle = null;
			Catalog catalog = findChapterCatalog(book, chapter);
			if (catalog != null)
				chapterTitle = catalog.getText();
			logger.info("save chapter: " + chapter.getChapter() + " " + chapterTitle);
			File chapterFile = getChapterPath(book, chapter.getChapter() + ".html");
			FileUtils.write(chapterFile, chapter.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Catalog findChapterCatalog(BookBean book, ChapterBean chapter) {
		for (Catalog item : book.getCatalog()) {
			if (StringUtils.equals(item.getHref(), chapter.getChapter() + ".html"))
				return item;
		}
		return null;
	}

	public static boolean isNeedDownload(BookBean book, String href) {
		return !App.getChapterPath(book, href).exists();
	}
}
