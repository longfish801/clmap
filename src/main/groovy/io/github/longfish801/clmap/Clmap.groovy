/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle
import java.util.regex.Matcher

/**
 * 宣言です。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Clmap implements TeaDec {
	/** ClassLoader */
	static ClassLoader loader
	/** GroovyShell */
	GroovyShell shell
	/** この宣言配下のすべてのクロージャの大域変数として使用するプロパティ */
	Map properties = [(cnst.map.dflt): this]
	
	/**
	 * コンストラクタ。<br/>
	 * メンバ変数 shellを初期化します。GroovyShellのコンストラクタには
	 * 引数としてメンバ変数loaderを渡します。<br/>
	 * メンバ変数 loaderが nullのときは
	 * Clmap.class.classLoaderで初期化します。
	 */
	Clmap(){
		shell = new GroovyShell(loader ?: Clmap.class.classLoader)
	}
	
	/**
	 * 再帰的に下位のハンドルも含めてクローンします。<br/>
	 * shellはシャローコピーします。<br/>
	 * 大域変数のプロパティは各値をシャローコピーします。
	 * @return クローン
	 */
	@Override
	Clmap cloneRecursive(){
		Clmap cloned = (Clmap) TeaDec.super.cloneRecursive()
		cloned.properties = properties.collectEntries { String key, def val -> [key, val] }
		cloned.properties[(cnst.map.dflt)] = cloned
		return cloned
	}
	
	/**
	 * クロージャパスの参照対象を返します。<br/>
	 * 絶対パスの場合はサーバーに解決を依頼します。<br/>
	 * クロージャの名前の区切り文字で開始する場合はクロージャを返します。<br/>
	 * 相対パスの場合、パス区切り文字で分割した先頭の名前と、残りのパスに分割します。<br/>
	 * このとき、統語的にありえないパスの場合は例外を投げます。<br/>
	 * 先頭の要素が上位のパスの場合は例外を投げます。<br/>
	 * 先頭の要素にマップ名とクロージャ名がある場合、マップ名を先頭の要素に、クロージャ名を残りのパスに分割します。<br/>
	 * 残りのパスがなければ、名前が一致する下位のマップを返します。<br/>
	 * 残りのパスがあれば、それを新たなパスとして名前が一致する下位のマップに依頼します。<br/>
	 * 上記で該当するハンドルがなければ nullを返します。
	 * @param clpath クロージャパス
	 * @return クロージャパスの参照対象（存在しない場合はnull）
	 * @exception TpacHandlingException 統語的にありえないクロージャパスです
	 */
	def cl(String clpath){
		// 絶対パスの場合はサーバーに解決を依頼します
		if (clpath.startsWith(cnst.clpath.level)) return server.cl(clpath)
		// クロージャの名前の区切り文字で開始する場合はクロージャを返します
		if (clpath.startsWith(cnst.clpath.anchor)){
			String clname = clpath.substring(cnst.clpath.anchor.length())
			return solve("closure:${clname}") ?: solve('closure:dflt')
		}
		// パス区切り文字で分割した先頭の要素を解決します
		if (cnst.clpath.fordec.every { !(clpath ==~ it) }){
			throw new TpacHandlingException(String.format(msgs.exc.invalidClpath, clpath))
		}
		Matcher matcher = Matcher.lastMatcher
		String firstPath = matcher.group(1)
		String otherPath = (matcher.groupCount() >= 2)? matcher.group(2) : ''
		// 先頭の要素にマップ名とクロージャ名がある場合、マップ名を先頭の要素に、クロージャ名を残りのパスに分割します
		if (otherPath.empty && firstPath ==~ cnst.clpath.closures){
			matcher = Matcher.lastMatcher
			firstPath = matcher.group(1)
			otherPath = cnst.clpath.anchor + matcher.group(2)
		}
		if (firstPath == cnst.clpath.upper){	// 上位のパスの場合
			throw new TpacHandlingException(String.format(msgs.exc.invalidClpath, clpath))
		}
		def lower = solve("map:${firstPath}")
		return (otherPath.empty)? lower : lower?.cl(otherPath)
	}
	
	/**
	 * 宣言の直下にある、名前を省略したclosureハンドルのクロージャを実行します。<br/>
	 * 当該クロージャが存在しない場合はnullを返します。
	 * @param args 可変長の引数
	 * @return 名前を省略したハンドルのクロージャの実行結果
	 */
	Object call(Object... args){
		String clpath = "${cnst.clpath.anchor}${cnst.clpath.noname.cl}"
		def cl = cl(clpath)
		if (cl == null){
			throw new TpacHandlingException(String.format(msgs.exc.invalidClpath, clpath))
		}
		return cl.call(*args);
	}
}
