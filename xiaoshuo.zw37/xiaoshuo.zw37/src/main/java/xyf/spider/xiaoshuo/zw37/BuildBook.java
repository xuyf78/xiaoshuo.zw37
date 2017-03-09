package xyf.spider.xiaoshuo.zw37;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class BuildBook {
	private static final Logger logger = Logger.getLogger(App.class);

	public static void main(String[] args) {
		App.init();
		List<String> a = new ArrayList<String>(args.length);
		if (args.length > 0) {
			for (int i = (args[0].equals(BuildBook.class.getName())) ? 1 : 0; i < args.length; i++) {
				a.add(args[i]);
			}
		}
		String books;
		if (a.isEmpty()) {
			books = App.properties.getProperty("books");
		} else {
			books = String.join(",", a);
		}

		for (String bookStr : books.split(",")) {
			buildBook(bookStr);
		}
	}

	private static void buildBook(String bookStr) {
		String[] a = bookStr.split("/", 2);
		BookBean book;
		book = new BookBean();
		book.setGroup(a[0]);
		book.setBook(a[1]);

		File bookPath = App.getBookPath(book);
		if (!bookPath.isDirectory()) {
			return;
		}

		try {
			File jsonFile = new File(bookPath, App.BOOK_JSON);
			book = JSON.parseObject(FileUtils.readFileToString(jsonFile), BookBean.class);
			File lastHrefFile = new File(bookPath, App.LAST_HREF_TXT);
			String lastHref = null;
			if (lastHrefFile.exists()) {
				lastHref = FileUtils.readFileToString(lastHrefFile);
			}
			if (StringUtils.isNotBlank(lastHref)) {
				int suffix = 2;
				File lastSuffixFile = new File(bookPath, App.LAST_SUFFIX_TXT);
				if (lastSuffixFile.exists()) {
					try {
						suffix = Integer.parseInt(FileUtils.readFileToString(lastSuffixFile)) + 1;
					} catch (Exception e) {
						// nothing to do
					}
				}
				App.assembleBookPart(book, String.valueOf(suffix), lastHref);
			} else {
				App.assembleBook(book);
			}
		} catch (IOException e) {
			logger.error("buildBook -->", e);
		}
	}
}
