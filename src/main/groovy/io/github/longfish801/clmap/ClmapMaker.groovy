/*
 * ClmapMaker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.tpac.element.TeaDec;
import io.github.longfish801.tpac.element.TeaHandle;
import io.github.longfish801.tpac.parser.TeaMaker;
import io.github.longfish801.tpac.parser.TeaMakerMakeException;

/**
 * clmap記法の文字列の解析にともない、各要素を生成します。
 * @version 1.0.00 2017/07/11
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapMaker implements TeaMaker {
	/** ConfigObject */
	static final ConfigObject cnstClmapMaker = ExchangeResource.config(ClmapMaker.class);
	
	/**
	 * TeaDecインスタンスを生成します。
	 * @param tag タグ
	 * @param name 名前
	 * @return TeaDec
	 */
	TeaDec newTeaDec(String tag, String name){
		return new Clmap();
	}
	
	/**
	 * TeaHandleインスタンスを生成します。
	 * @param tag タグ
	 * @param name 名前
	 * @param upper 上位ハンドル
	 * @return TeaHandle
	 */
	TeaHandle newTeaHandle(String tag, String name, TeaHandle upper){
		if (!cnstClmapMaker.check.valid.contains(tag)) throw new TeaMakerMakeException("不正なタグ名です。tag=${tag}, name=${name}");
		if (!cnstClmapMaker.check.hierarchy[upper.tag].contains(tag)) throw new TeaMakerMakeException("タグの親子関係が不正です。tag=${tag}, name=${name}, upper=${upper.tag}");
		return (tag == 'closure')? new Clinfo() : TeaMaker.super.newTeaHandle(tag, name, upper);
	}
}
