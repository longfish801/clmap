/*
 * ClmapMaker.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassSlurper;
import io.github.longfish801.tpac.TpacMaker;
import io.github.longfish801.tpac.parser.ParseException;
import org.apache.commons.lang3.StringUtils;

/**
 * Clmap記法の文字列の解析にともない、各要素を生成します。
 * @version 1.0.00 2017/07/11
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapMaker extends TpacMaker {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(ClmapMaker.class);
	
	/** {@inheritDoc} */
	void createRoot(String tag, String name, int lineNo){
		if (tag != constants.validTags.dec) throw new ParseException("宣言のタグ名が不正です。tag=${tag}");
		super.createRoot(tag, name, lineNo);
	}
	
	/** {@inheritDoc} */
	void createParent(String tag, String name, int lineNo){
		if (constants.validTags.parent.every { it != tag }) throw new ParseException("親要素のタグ名が不正です。tag=${tag}");
		if (!name.empty && constants.nonameTags.parent.any { it == tag }) throw new ParseException("この親要素には名前を指定できません。tag=${tag}, name=${name}");
		super.createParent(tag, name, lineNo);
	}
	
	/** {@inheritDoc} */
	void createChild(String tag, String name, int lineNo){
		if (constants.validTags.child.every { it != tag }) throw new ParseException("子要素のタグ名が不正です。tag=${tag}");
		if (!name.empty && constants.nonameTags.child.any { it == tag }) throw new ParseException("この親要素には名前を指定できません。tag=${tag}, name=${name}");
		super.createChild(tag, name, lineNo);
	}
	
	/** {@inheritDoc} */
	void createText(String text, int lineNo){
		List lines = text.split(/[\r\n]/);
		if (lines.any { StringUtils.trimToNull(it) != null }) super.createText(lines.join("\n"), lineNo);
	}
}
