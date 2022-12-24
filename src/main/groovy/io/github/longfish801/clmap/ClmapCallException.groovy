/*
 * ClmapCallException.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs

/**
 * クロージャ実行時に発生した例外を保持するための例外クラスです。
 * @author io.github.longfish801
 */
class ClmapCallException extends Exception {
	/** クロージャ */
	ClmapClosure clcl = null
	
	/**
	 * コンストラクタ。
	 * @param message 詳細メッセージ
	 * @param cause 原因
	 * @param clcl クロージャ
	 */
	ClmapCallException(String message, Throwable cause, ClmapClosure clcl){
		super(message, cause)
		this.clcl = clcl
	}
	
	/**
	 * ローカライズされたメッセージを返します。
	 * @return ローカライズされたメッセージ
	 */
	@Override
	String getLocalizedMessage(){
		return String.format(msgs.exc.closureExc, message, clcl.clpath, addLineNo(clcl.code))
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br/>
	 * 行番号の先頭は０埋めします。行番号と各行の間は半角スペースで連結します。<br/>
	 * 末尾に改行コードの連続があっても無視します。<br/>
	 * 空文字や、改行コードの連続のみを渡された場合は、空文字を返します。
	 * NULLを渡された場合は文字列「<NULL_TEXT>」を返します。
	 * @param text 文字列
	 * @return 先頭に行番号を付与した文字列リスト
	 */
	static String addLineNo(String text) {
		if (text == null) return cnst.nulltext
		List lines = text.normalize().split(/\n/) as List
		// 文字列化することで桁数を得ます
		int digit = lines.size.toString().length()
		int idx = 0
		return lines.collect {
			String.format("%0${digit}d %s", ++ idx, it)
		}.join(System.lineSeparator())
	}
}
