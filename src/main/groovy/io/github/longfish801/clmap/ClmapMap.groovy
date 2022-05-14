/*
 * ClmapMap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import groovy.util.logging.Slf4j
import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import io.github.longfish801.tpac.tea.TeaHandle
import java.util.regex.Matcher

/**
 * マップです。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapMap implements TeaHandle {
	/** ClassLoader */
	static ClassLoader loader
	/** GroovyShell */
	GroovyShell shell
	/**
	 * 各クロージャの大域変数として使用するプロパティ。<br/>
	 * デフォルトでキー'clmap'に対し値として自インスタンスを保持します。
	 */
	Map properties = [(cnst.map.dflt): this]
	
	/**
	 * 再帰的に下位のハンドルも含めてクローンします。<br/>
	 * 大域変数のプロパティの各値はシャローコピーします。
	 * @return クローン
	 */
	@Override
	ClmapMap cloneRecursive(){
		ClmapMap cloned = (ClmapMap) TeaHandle.super.cloneRecursive()
		cloned.properties = properties.collectEntries { String key, def val -> [key, val] }
		return cloned
	}
	
	/**
	 * コンストラクタ。<br/>
	 * メンバ変数 loaderが nullのときは
	 * ClmapMap.class.classLoaderで初期化します。<br/>
	 * メンバ変数 shellを初期化します。<br/>
	 * GroovyShellのコンストラクタには引数としてメンバ変数loaderを渡します。
	 * @return 自インスタンス
	 */
	ClmapMap(){
		if (loader == null) loader = ClmapMap.class.classLoader
		shell = new GroovyShell(loader)
	}
	
	/**
	 * クロージャパスの参照対象を返します。<br/>
	 * クロージャ名に相当する対象が無い場合はデフォルトキーに対応する値を返します。<br/>
	 * 絶対パスの場合は宣言に解決を依頼します。<br/>
	 * クロージャの名前の区切り文字で開始する場合はクロージャを返します。<br/>
	 * 相対パスの場合、パス区切り文字で分割した先頭の名前と、残りのパスに分割します。<br/>
	 * このとき、統語的にありえないパスの場合は例外を投げます。<br/>
	 * 先頭の要素にマップ名とクロージャ名がある場合、マップ名を先頭の要素に、クロージャ名を残りのパスに分割します。<br/>
	 * 先頭の名前に応じて以下のとおり処理します。</p>
	 * <dl>
	 * <dt>先頭の名前が上位のパスを意味する場合</dt>
	 * 	<dd>残りのパスがなければ上位ハンドルを返します。</dd>
	 * 	<dd>残りのパスがあれば、それを新たなパスとして上位ハンドルに依頼します。</dd>
	 * <dt>上記以外の場合</dt>
	 * 	<dd>残りのパスがなければ、名前が一致するマップを返します。</dd>
	 * 	<dd>残りのパスがあれば、それを新たなパスとして名前が一致するマップに依頼します。</dd>
	 * </dl>
	 * <p>上記で該当するハンドルがなければ nullを返します。
	 * @param clpath クロージャパス
	 * @return クロージャパスの参照対象（存在しない場合はnull）
	 * @exception TpacHandlingException 統語的にありえないクロージャパスです
	 */
	def cl(String clpath){
		// 絶対パスの場合は宣言に解決を依頼します
		if (clpath.startsWith(cnst.clpath.level)) return dec.cl(clpath)
		// クロージャの名前の区切り文字で開始する場合はクロージャを返します
		if (clpath.startsWith(cnst.clpath.anchor)){
			String clname = clpath.substring(cnst.clpath.anchor.length())
			return solve("closure:${clname}") ?: solve('closure:dflt')
		}
		// パス区切り文字で分割した先頭の要素を解決します
		if (cnst.clpath.forhandle.every { !(clpath ==~ it) }){
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
		switch (firstPath){
			case cnst.clpath.upper: // 上位のパスの場合
				return (otherPath.empty)? upper : upper.cl(otherPath)
			default: // 下位ハンドルの場合
				def lower = solve("map:${firstPath}")
				return (otherPath.empty)? lower : lower?.cl(otherPath)
		}
	}
}
