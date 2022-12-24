/*
 * ClmapServer.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import io.github.longfish801.tpac.tea.TeaServer
import io.github.longfish801.tpac.tea.TeaMaker
import java.util.regex.Matcher

/**
 * clmap文書を保持します。
 * @author io.github.longfish801
 */
class ClmapServer implements TeaServer {
	/**
	 * コンストラクタ。
	 * @return 自インスタンス
	 */
	ClmapServer(){
	}
	
	/**
	 * コンストラクタ。<br/>
	 * 引数loaderを {@link Clmap}のメンバ変数 loaderに設定します。
	 * @param loader ClassLoader
	 * @return 自インスタンス
	 */
	ClmapServer(ClassLoader loader){
		Clmap.loader = loader
	}
	
	/**
	 * 宣言のタグに対応する生成器を返します。<br/>
	 * clmapタグに対し {@link ClmapMaker}のインスタンスを生成して返します。<br/>
	 * それ以外はオーバーライド元のメソッドの戻り値を返します。
	 * @param tag 宣言のタグ
	 * @return TeaMaker
	 */
	@Override
	TeaMaker newMaker(String tag){
		if (tag == 'clmap') return new ClmapMaker()
		return TeaServer.super.newMaker(tag)
	}
	
	/**
	 * クロージャパスの参照対象を返します。<br/>
	 * パス区切り文字で分割した先頭の名前と、残りのパスに分割します。<br/>
	 * このとき、統語的にありえないパスの場合は例外を投げます。<br/>
	 * 残りのパスがなければ、名前が一致する宣言を返します。<br/>
	 * 残りのパスがあれば、それを新たなパスとして名前が一致する宣言に依頼します。<br/>
	 * 上記で該当するハンドルがなければ nullを返します。
	 * @param clpath クロージャパス
	 * @return クロージャパスの参照対象（存在しない場合はnull）
	 * @exception TpacHandlingException 統語的にありえないクロージャパスです
	 */
	def cl(String clpath){
		// パス区切り文字で分割した先頭の要素を解決します
		if (cnst.clpath.forserver.every { !(clpath ==~ it) }){
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
		String clname = (firstPath == cnst.clpath.noname.map)? '' : ":${firstPath}"
		def dec = getAt("clmap${clname}")
		return (otherPath.empty)? dec : dec?.cl(otherPath)
	}
}
