/*
 * ClmapMaker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapConst as cnst
import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacSemanticException
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle
import io.github.longfish801.tpac.tea.TeaMaker

/**
 * clmap記法の文字列の解析にともない、各要素を生成します。
 * @version 0.3.00 2020/06/11
 * @author io.github.longfish801
 */
class ClmapMaker implements TeaMaker {
	/**
	 * 宣言を生成します。<br/>
	 * {@link Clmap}インスタンスを生成して返します。
	 * @param tag タグ
	 * @param name 名前
	 * @return 宣言
	 */
	@Override
	TeaDec newTeaDec(String tag, String name){
		return new Clmap()
	}
	
	/**
	 * ハンドルを生成します。<br/>
	 * closureタグに対し {@link ClmapClosure}のインスタンスを生成して返します。<br/>
	 * mapタグに対し {@link ClmapMap}のインスタンスを生成して返します。<br/>
	 * それ以外はオーバーライド元のメソッドの戻り値を返します。
	 * @param tag タグ
	 * @param name 名前
	 * @param upper 上位ハンドル
	 * @return ハンドル
	 * @exception TpacSemanticException 統語的にありえないタグです。
	 * @exception TpacSemanticException タグの親子関係が不正です。
	 */
	@Override
	TeaHandle newTeaHandle(String tag, String name, TeaHandle upper){
		if (!cnst.validtags.contains(tag)){
			throw new TpacSemanticException(String.format(msgs.exc.invalidTag, tag))
		}
		if (cnst.hierarchy.get(upper.tag) != null
		 && !cnst.hierarchy.get(upper.tag).contains(tag)){
			throw new TpacSemanticException(String.format(msgs.exc.invalidHierarchy, tag, upper.tag))
		}
		switch (tag) {
			case 'closure': return new ClmapClosure()
			case 'map': return new ClmapMap()
			case 'config': return new ClmapConfig()
		}
		return TeaMaker.super.newTeaHandle(tag, name, upper)
	}
}
