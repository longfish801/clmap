/*
 * ClmapClosureCallException.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs

/**
 * クロージャ実行時に発生した例外を保持するための例外クラスです。
 * @author io.github.longfish801
 */
class ClmapClosureCallException extends Exception {
	/** クロージャ */
	ClmapClosure clclosure = null
	
	/**
	 * コンストラクタ。
	 * @param message 詳細メッセージ
	 * @param cause 原因
	 * @param clclosure クロージャ
	 */
	ClmapClosureCallException(String message, Throwable cause, ClmapClosure clclosure){
		super(message, cause)
		this.clclosure = clclosure
	}
	
	/**
	 * ローカライズされたメッセージを返します。
	 * @return ローカライズされたメッセージ
	 */
	@Override
	String getLocalizedMessage(){
		return String.format(msgs.exc.closureExc, message, clclosure.clpath, ClmapClosure.addLineNo(clclosure.code))
	}
}
