/*
 * TextUtil.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;

/**
 * 文字列に対する汎用的な処理です。
 * @version 1.0.00 2017/07/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TextUtil {
	/**
	 * 各行の先頭に行番号を付与して返します。<br>
	 * 改行コードはシステム固有の値を使用します。
	 * @param text 文字列
	 * @return 先頭に行番号を付与された文字列
	 * @see #addLineNo(List<String>)
	 */
	static String addLineNo(String text) {
		ArgmentChecker.checkNotNull('text', text);
		return addLineNo(text.split(/\r\n|[\n\r]/) as List).join(System.lineSeparator());
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br>
	 * 行番号の先頭は０埋めします。行番号と各行の間は半角スペースで連結します。<br>
	 * 末尾に改行コードの連続があっても無視します。<br>
	 * 空文字や、改行コードの連続のみを渡された場合は、空文字を返します。
	 * @param lines 文字列リスト
	 * @return 先頭に行番号を付与した文字列リスト
	 */
	static List<String> addLineNo(List<String> lines) {
		ArgmentChecker.checkNotNull('lines', lines);
		int digitNum = lines.size.toString().length();
		int lineNo = 0;
		return lines.collect { String.format("%0${digitNum}d %s", ++ lineNo, it) };
	}
}
