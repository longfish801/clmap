/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;

/**
 * コンビキーです。<br>
 * コンビキーは、マップとクロージャの名前をつなげた文字列です。<br>
 * 区切り文字として半角シャープ(#)を使用します。
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class CombiKey {
	/** マップの名前 */
	String mapName;
	/** クロージャの名前 */
	String clName;
	
	/**
	 * コンストラクタ。
	 * @param mapName マップ名
	 * @param clName クロージャ名
	 */
	CombiKey(String mapName, String clName){
		ArgmentChecker.checkMatchRex('マップ名', mapName, /[^\r\n #]*/);
		ArgmentChecker.checkMatchRex('クロージャ名', clName, /[^\r\n #]*/);
		this.mapName = mapName;
		this.clName = clName;
	}
	
	/**
	 * コンストラクタ。<br>
	 * もし半角シャープを含まないコンビキーを指定された場合は、マップ名のみを指定されたとみなします。
	 * @param combiStr コンビキー文字列
	 */
	CombiKey(String combiStr){
		ArgmentChecker.checkMatchRex('コンビキー文字列', combiStr, /[^\r\n ]*/);
		combiStr = (combiStr.indexOf('#') >= 0)? combiStr : combiStr + '#';
		this.mapName = combiStr.substring(0, combiStr.indexOf('#'));
		this.clName = combiStr.substring(combiStr.indexOf('#') + 1);
		ArgmentChecker.checkMatchRex('マップ名', this.mapName, /[^\r\n #]*/);
		ArgmentChecker.checkMatchRex('クロージャ名', this.clName, /[^\r\n #]*/);
	}
	
	/**
	 * 文字列表現を返します。<br>
	 * マップ名とクロージャ名を半角シャープでつなげた文字列を返します。
	 * @return コンビキー文字列
	 */
	String toString(){
		return "${mapName}#${clName}";
	}
}
