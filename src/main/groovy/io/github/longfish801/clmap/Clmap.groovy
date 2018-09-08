/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ArgmentChecker;
import io.github.longfish801.tpac.element.TeaDec;

/**
 * クロージャマップです。
 * @version 1.0.00 2016/11/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Clmap implements TeaDec {
	/**
	 * コンビキー文字列に対応するクロージャ情報を返します。<br/>
	 * コンビキーはマップ名とクロージャ名を半角シャープ(#)で連結した文字列です。<br/>
	 * 半角シャープが存在しない場合はマップ名のみ指定（クロージャ名は空文字）扱いとします。
	 * @param combiKey コンビキー文字列
	 * @return クロージャ情報（存在しない場合はnull）
	 */
	Clinfo cl(String combiKey){
		ArgmentChecker.checkMatchRex('コンビキー', combiKey, /[^ \/:]*/);
		int divIdx = combiKey.indexOf('#');
		String mapName = (divIdx < 0)? combiKey : combiKey.substring(0, divIdx);
		String clName = (divIdx < 0)? '' : combiKey.substring(divIdx + 1);
		return lowers["map:${mapName}"]?.lowers["closure:${clName}"];
	}
	
	/**
	 * マップ名の一覧を返します。
	 * @return マップ名の一覧
	 */
	List getMapNames(){
		return findAll(/map:.*/).collect { it.name };
	}
	
	/**
	 * 指定されたマップ配下のクロージャ名の一覧を返します。<br>
	 * 該当するマップが無い場合は空リストを返します。
	 * @param mapName マップ名
	 * @return クロージャ名の一覧
	 */
	List getClosureNames(String mapName){
		ArgmentChecker.checkNotNull('マップ名', mapName);
		return lowers["map:${mapName}"]?.findAll(/closure:.*/)?.collect { it.name } ?: [];
	}
}
