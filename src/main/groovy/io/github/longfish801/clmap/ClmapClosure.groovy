/*
 * ClmapClosure.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacConst as tpacCnst
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * クロージャです。<br/>
 * GroovyのClosureインスタンスやソースコードを保持します。
 * @version 0.3.00 2020/06/11
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapClosure implements TeaHandle {
	/** クロージャのソースコード */
	String code
	/** クロージャ */
	Closure closure
	
	/**
	 * 絶対クロージャパスを返します。
	 * @return 絶対クロージャパス
	 */
	String getClpath(){
		Closure getClpathRecursive
		getClpathRecursive = { TeaHandle hndl ->
			return (hndl.upper == null)
				? "${cnst.clpath.level}${hndl.name}"
				: "${getClpathRecursive(hndl.upper)}${cnst.clpath.level}${hndl.name}"
		}
		return "${getClpathRecursive(upper)}${cnst.clpath.anchor}${name}"
	}
	
	/**
	 * クロージャを実行します。<br/>
	 * クロージャを未作成であれば、作成してメンバ変数 closureに格納します。<br/>
	 * Throwableをキャッチしたならば ClmapClosureCallExceptionに置き換えます。<br/>
	 * ただし ClmapClosureCallException自身を置き換えることはしません。
	 * @param args 実行時可変長引数
	 * @return 実行結果
	 * @throws ClmapClosureCallException クロージャ呼出時に例外あるいはエラーが投げられました。
	 * @see #createClosure()
	 */
	Object call(Object... args){
		Object result = null
		try {
			if (closure == null) closure = createClosure()
			result = closure.call(*args)
		} catch (ClmapClosureCallException exc){
			throw exc
		} catch (Throwable exc){
			throw new ClmapClosureCallException(msgs.exc.throwedClosure, exc, this)
		}
		return result
	}
	
	/**
	 * クロージャを生成します。<br/>
	 * クロージャのソースコードを未作成であれば、作成してメンバ変数 codeに格納します。<br/>
	 * クロージャ生成時、{@link ClmapMap}クラスのメンバ変数 propertiesを
	 * 大域変数として利用できるよう設定（delegateに設定）します。<br/>
	 * コンパイル時に Throwableをキャッチしたならば WARNログを出力します。
	 * @return クロージャ
	 * @see #createCode()
	 */
	Closure createClosure(){
		if (code == null){
			code = createCode()
			LOG.debug("--- closure code: clpath={} ---\n{}\n---", clpath, code)
		}
		Closure closure
		try {
			closure = (upper as ClmapMap).shell.evaluate(code, clpath)
			closure.delegate = (upper as ClmapMap).properties
		} catch (Throwable exc){
			LOG.warn(msgs.exc.failedCompile, clpath, addLineNo(code))
			throw exc
		}
		return closure
	}
	
	/**
	 * クロージャのソースコードを生成します。
	 * @return クロージャのソースコード
	 */
	String createCode(){
		Closure writeCode
		writeCode = { StringBuilder builder, String tag, TeaHandle hndl ->
			if (hndl.upper != null) writeCode(builder, tag, hndl.upper)
			if (hndl.solvePath(tag) != null){
				builder << hndl.solvePath(tag).getAt(tpacCnst.dflt.mapKey).join(System.lineSeparator())
				builder << System.lineSeparator()
			}
		}
		StringBuilder builder = new StringBuilder()
		writeCode(builder, cnst.tags.dec, this)
		builder << cnst.closure.bgn
		if (upper.solvePath(cnst.tags.args) != null){
			builder << upper.solvePath(cnst.tags.args).getAt(tpacCnst.dflt.mapKey).join(System.lineSeparator())
		}
		builder << cnst.closure.arg
		builder << System.lineSeparator()
		writeCode(builder, cnst.tags.prefix, this)
		builder << getAt(tpacCnst.dflt.mapKey).join(System.lineSeparator())
		builder << System.lineSeparator()
		writeCode(builder, cnst.tags.suffix, this)
		builder << cnst.closure.end
		builder << System.lineSeparator()
		return builder.toString()
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br/>
	 * 改行コードはシステム固有の値を使用します。
	 * @param text 文字列
	 * @return 先頭に行番号を付与した文字列
	 * @see #addLineNo(List<String>)
	 */
	static String addLineNo(String text) {
		return addLineNo(text.normalize().split(/\n/) as List).join(System.lineSeparator())
	}
	
	/**
	 * 各行の先頭に行番号を付与して返します。<br/>
	 * 行番号の先頭は０埋めします。行番号と各行の間は半角スペースで連結します。<br/>
	 * 末尾に改行コードの連続があっても無視します。<br/>
	 * 空文字や、改行コードの連続のみを渡された場合は、空文字を返します。
	 * @param lines 文字列リスト
	 * @return 先頭に行番号を付与した文字列リスト
	 */
	static List<String> addLineNo(List<String> lines) {
		int digitNum = lines.size.toString().length()
		int idx = 0
		return lines.collect { String.format("%0${digitNum}d %s", ++ idx, it) }
	}
}
