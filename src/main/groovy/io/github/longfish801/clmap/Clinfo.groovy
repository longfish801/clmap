/*
 * Clinfo.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.clmap.util.TextUtil;
import io.github.longfish801.shared.ExchangeResource;
import io.github.longfish801.tpac.element.TeaDec;
import io.github.longfish801.tpac.element.TeaHandle;

/**
 * クロージャ情報です。<br>
 * クロージャとその付加情報を管理します。<br>
 * GroovyのClosureクラスからはソースコードを参照できないため、
 * 対応関係を把握しやすくなるよう作成しました。
 * @version 1.0.00 2016/11/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Clinfo implements TeaHandle {
	/** ConfigObject */
	static final ConfigObject cnstClinfo = ExchangeResource.config(Clinfo.class);
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(Clinfo.class.classLoader);
	/** クロージャのソースコード */
	String code;
	/** クロージャ */
	Closure closure;
	
	/**
	 * このクロージャ情報のコンビキーを返します。
	 * @return コンビキー
	 */
	String getCombiKey(){
		return (name.empty)? upper.name : String.format(cnstClinfo.format.combiKey, upper.name, name);
	}
	
	/**
	 * 可変長引数のクロージャを実行します。<br>
	 * 暗黙の引数として、末尾に {@link Clmap}, {@link Clmap#config}を追加します。
	 * @param args 実行時可変長引数
	 * @return 実行結果
	 * @throws ClinfoCallException クロージャ実行時に例外が発生しました。
	 */
	Object call(Object... args) {
		Object result = null;
		try {
			if (closure == null) closure = createClosure();
			result = closure.call(*args);
		} catch (ClinfoCallException exc){
			throw exc;
		} catch (Throwable exc){
			throw new ClinfoCallException("クロージャ実行時に例外が発生しました。", exc, this);
		}
		return result;
	}
	
	/**
	 * クロージャを生成します。<br/>
	 * ソースコードを初めて生成したときは DEBUGログに出力します。<br/>
	 * コンパイル時に Throwableをキャッチしたならば WARNログを出力します。
	 * @return クロージャ
	 */
	Closure createClosure(){
		if (code == null){
			code = createCode();
			LOG.debug("--- closure code: combiKey={} ---\n{}---", combiKey, code);
		}
		try {
			closure = shell.evaluate(code, String.format(cnstClinfo.format.clname, combiKey));
			Clmap clmap = upper.upper as Clmap;
			clmap.properties.each { closure.setProperty(it.key, it.value) }
		} catch (Throwable exc){
			LOG.warn("クロージャのコンパイルに失敗しました。 combiKey={}, code=\n{}\n-----", combiKey, TextUtil.addLineNo(code));
			throw exc;
		}
		return closure;
	}
	
	/**
	 * クロージャのソースコードを生成します。<br/>
	 * スクリプト文字列は以下となります。</p>
	 * <blockquote>
	 * clmap.dec + map.dec + '{' + args + ' ->' + map.prefix + clmap.prefix + closure + clmap.suffix + map.suffix + '}'
	 * </blockquote>
	 * <p>clmapは clmapタグ直下（すべてのマップに共通）、mapは mapタグ直下、closureは closureタグの内容を指します。<br>
	 * dec、prefix、suffixはそれぞれ同じ名前のタグの内容です。<br>
	 * argsは、argsタグの指定があれば「 ->」を末尾に付与した文字列、なければ空文字です。<br>
	 * デフォルトで引数の最後には Clmap, ConfigObjectを渡します。</p>
	 * @return クロージャのソースコード
	 */
	String createCode(){
		StringBuilder builder = new StringBuilder();
		TeaDec dec = upper.upper;
		if (dec.lowers['dec:'] != null) builder << dec.lowers['dec:'].text.toString();
		if (upper.lowers['dec:'] != null) builder << upper.lowers['dec:'].text.toString();
		builder << '{ ';
		if (upper.lowers['args:'] != null) builder << upper.lowers['args:'].text.join(System.lineSeparator());
		builder << " ->${System.lineSeparator()}";
		if (dec.lowers['prefix:'] != null) builder << dec.lowers['prefix:'].text.toString();
		if (upper.lowers['prefix:'] != null) builder << upper.lowers['prefix:'].text.toString();
		builder << text.toString();
		if (dec.lowers['suffix:'] != null) builder << dec.lowers['suffix:'].text.toString();
		if (upper.lowers['suffix:'] != null) builder << upper.lowers['suffix:'].text.toString();
		builder << '}';
		return builder.toString();
	}
	
	/**
	 * クロージャ実行時に発生した例外を保持するための例外クラスです。
	 */
	class ClinfoCallException extends Exception {
		/** クロージャ情報 */
		Clinfo clinfo = null;
		
		/**
		 * コンストラクタです。
		 * @param message 詳細メッセージ
		 * @param cause 原因
		 * @param clinfo クロージャ情報
		 */
		ClinfoCallException(String message, Throwable cause, Clinfo clinfo){
			super(message, cause);
			this.clinfo = clinfo;
		}
		
		/** {@inheritDoc} */
		@Override
		String getMessage(){
			return super.getMessage();
//			return "${super.getMessage()} combiKey=${clinfo.combiKey}, code=\n${TextUtil.addLineNo(clinfo.code)}\n-----";
		}
	}
}
