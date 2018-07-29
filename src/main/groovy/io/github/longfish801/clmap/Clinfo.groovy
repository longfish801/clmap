/*
 * Clinfo.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.lang.ArgmentChecker;
import io.github.longfish801.shared.util.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * クロージャ情報です。<br>
 * クロージャとその付加情報を管理します。<br>
 * GroovyのClosureクラスからはソースコードを参照できないため、
 * 対応関係を把握しやすいよう作成しました。
 * @version 1.0.00 2016/11/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class Clinfo {
	/** GroovyShell */
	static GroovyShell shell = new GroovyShell(Clinfo.class.classLoader);
	/** 識別子 */
	String sign;
	/** クロージャのソースコード */
	String code;
	/** クロージャ */
	Closure closure;
	/** 暗黙の引数 */
	List implicitArgs = [];
	
	/**
	 * コンストラクタ。<br>
	 * クロージャは{@link GroovyShell#evaluate(String,String)}を用いて生成します。<br>
	 * このとき第二引数として"cl_${sign}.groovy"を渡します。<br>
	 * クロージャのコンパイル時にエラーもしくは例外が発生したならば WARNログを出力し、スローしなおします。<br>
	 * @param sign 識別子（空白文字は不可）
	 * @param code ソースコード（空白文字は不可）
	 * @exception Throwable ソースコードからのクロージャのコンパイルに失敗しました。
	 */
	Clinfo(String sign, String code){
		ArgmentChecker.checkNotBlank('sign', sign);
		ArgmentChecker.checkNotBlank('code', code);
		this.sign = sign;
		this.code = code;
		try {
			closure = shell.evaluate(code, "cl_${sign}.groovy");
		} catch (Throwable exc){
			LOG.warn("ソースコードからのクロージャのコンパイルに失敗しました。 sign={}, code=\n{}\n-----", sign, TextUtil.addLineNo(code));
			throw exc;
		}
	}
	
	/**
	 * 可変長引数のクロージャを実行します。<br>
	 * 暗黙の引数として、末尾に {@link Clmap}, {@link Clmap#config}を追加します。
	 * @param args 実行時可変長引数
	 * @return 実行結果
	 * @exception ClinfoCallException クロージャ実行時に例外が発生しました。
	 */
	Object call(Object... args) {
		Object result = null;
		try {
			result = closure.call(*args, *implicitArgs);
		} catch (ClinfoCallException exc){
			throw exc;
		} catch (Throwable exc){
			throw new ClinfoCallException("クロージャ実行時に例外が発生しました。sign=${sign}", exc, this);
		}
		return result;
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
			return "${super.getMessage()}, sign=${clinfo.sign}, code=\n${TextUtil.addLineNo(clinfo.code)}\n-----";
		}
	}
}
