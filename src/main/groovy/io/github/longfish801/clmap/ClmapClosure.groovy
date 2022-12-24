/*
 * ClmapClosure.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaHandle
import org.apache.commons.lang3.StringUtils

/**
 * クロージャです。<br/>
 * GroovyのClosureインスタンスやソースコードを保持します。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapClosure implements TeaHandle {
	/** クロージャのソースコード */
	String code
	/** クロージャ */
	Closure closure
	
	/**
	 * 再帰的に下位のハンドルも含めてクローンします。<br/>
	 * ソースコード、クロージャはnullを格納します。
	 * @return クローン
	 */
	@Override
	ClmapClosure cloneRecursive(){
		ClmapClosure cloned = (ClmapClosure) TeaHandle.super.cloneRecursive()
		cloned.code = null
		cloned.closure = null
		return cloned
	}
	
	/**
	 * このハンドルを指す絶対クロージャパスを返します。
	 * @return 絶対クロージャパス
	 */
	String getClpath(){
		Closure getClpathRecursive
		getClpathRecursive = { TeaHandle hndl ->
			return (hndl.upper == null)
				? "${cnst.clpath.level}${hndl.name}"
				: "${getClpathRecursive(hndl.upper)}${cnst.clpath.level}${hndl.name}"
		}
		String clname = (name == 'dflt')? cnst.clpath.noname.cl : name
		return "${getClpathRecursive(upper)}${cnst.clpath.anchor}${clname}"
	}
	
	/**
	 * クロージャを実行します。<br/>
	 * クロージャを未作成であれば、作成してメンバ変数 closureに格納します。<br/>
	 * Throwableをキャッチしたならば ClmapCallExceptionに置き換えます。<br/>
	 * ただし ClmapCallException自身を置き換えることはしません。
	 * @param args 実行時可変長引数
	 * @return 実行結果
	 * @throws ClmapCallException クロージャ呼出時に例外あるいはエラーが投げられました。
	 * @see #createClosure()
	 */
	Object call(Object... args){
		Object result = null
		try {
			if (closure == null) closure = createClosure()
			result = closure.call(*args)
		} catch (ClmapCallException exc){
			throw exc
		} catch (Throwable exc){
			throw new ClmapCallException(msgs.exc.throwedClosure, exc, this)
		}
		return result
	}
	
	/**
	 * クロージャを生成します。<br/>
	 * クロージャのソースコードを未作成であれば、作成してメンバ変数 codeに格納します。<br/>
	 * クロージャ生成時、{@link Clmap}, {@link ClmapMap}クラスのメンバ変数 propertiesを
	 * 大域変数として利用できるよう設定（delegateに設定）します。<br/>
	 * 同じ大域変数名がある場合はより下位の {@link ClmapMap}クラスでの値で上書きされます。<br/>
	 * コンパイル時に Throwableをキャッチしたならば WARNログを出力します。
	 * @return クロージャ
	 * @see #createCode()
	 */
	Closure createClosure(){
		if (code == null){
			code = createCode()
			LOG.trace("--- closure code: clpath={} ---\n{}\n---", clpath, code)
		}
		Closure getProperties
		getProperties = { def hndl ->
			return (hndl.level == 0)
				? (hndl as Clmap).properties
				: getProperties.call(hndl.upper) + (hndl as ClmapMap).properties
		}
		Closure closure
		try {
			closure = (dec as Clmap).shell.evaluate(code, clpath)
			closure.delegate = getProperties.call(upper)
		} catch (Throwable exc){
			LOG.warn(String.format(msgs.logmsg.failedCompile, clpath, ClmapCallException.addLineNo(code)))
			throw exc
		}
		return closure
	}
	
	/**
	 * クロージャのソースコードを生成します。
	 * @return クロージャのソースコード
	 * @exception TpacSemanticException returnハンドルにテキストが指定されていません。
	 * @exception TpacSemanticException returnハンドルの指定が不正です。
	 */
	String createCode(){
		// 指定したキーが存在すればその値を最上位のハンドルから順に文字列連結するクロージャです
		Closure appendKey
		appendKey = { List lines, String key, TeaHandle hndl ->
			if (hndl.level > 0) appendKey(lines, key, hndl.upper)
			if (hndl[key] == null) return
			if (!(hndl[key] instanceof List)){
				throw new TpacSemanticException(String.format(msgs.exc.notText, key, hndl.tag))
			}
			lines.addAll(hndl[key])
		}
		// 指定されたキーの値を自ハンドル、親、さらに上位へ探していくクロージャです
		Closure getKey
		getKey = { String key, TeaHandle hndl ->
			if (hndl[key] == null){
				return (hndl.level > 0)? getKey(key, hndl.upper) : null
			}
			if (!(hndl[key] instanceof List)){
				throw new TpacSemanticException(String.format(msgs.exc.notText, key, hndl.tag))
			}
			return hndl[key]
		}
		// 引数の文字列を作成します
		String argStr = ''
		List args = getKey('args', this)
		if (args != null){
			argStr = args.collect {
				StringUtils.trim(it)
			}.join(cnst.closure.argdiv)
		}
		// 戻り値の文字列を作成します
		String retDef = ''
		String retVar = ''
		List rets = getKey('return', this)
		if (rets != null){
			retDef = StringUtils.trim(rets.first())
			if (retDef.indexOf(cnst.closure.retdiv) < 0){
				retVar = retDef
				retDef = ''
			} else {
				retVar = retDef.split(cnst.closure.retdiv).last()
			}
		}
		// クロージャのソースコードを生成します
		List lines = []
		appendKey(lines, 'dec', this)
		lines << "${cnst.closure.bgn}${argStr}${cnst.closure.arg}"
		if (!retDef.empty) lines << retDef
		appendKey(lines, 'prefix', this)
		if (getAt('dflt') == null || !(getAt('dflt') instanceof List)){
			throw new TpacSemanticException(String.format(msgs.exc.notText, 'dflt', tag))
		}
		lines.addAll(getAt('dflt'))
		appendKey(lines, 'suffix', this)
		if (!retVar.empty) lines << "${cnst.closure.ret}${retVar}"
		lines << cnst.closure.end
		return lines.join(cnst.closure.lsep)
	}
}
