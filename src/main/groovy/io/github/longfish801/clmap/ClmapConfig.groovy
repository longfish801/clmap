/*
 * ClmapConfig.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.tpac.tea.TeaHandle

/**
 * 設定値です。
 * @author io.github.longfish801
 */
class ClmapConfig implements TeaHandle {
	/** ConfigSlurper */
	static ConfigSlurper slurper = new ConfigSlurper()
	
	/**
	 * 設定値を返します。<br/>
	 * 各キーにテキストで指定した値があれば改行コードで連結します。<br/>
	 * それらを改行コードで連結して{@link ConfigSlurper}で解析します。<br/>
	 * テキストの指定が無ければnullを返します。
	 * @return ConfigObject
	 */
	ConfigObject config(){
		String data = map.values().findAll {
			it.value instanceof List
		}.collect {
			it.join(System.lineSeparator())
		}.join(System.lineSeparator())
		return (data.length() == 0)? null : slurper.parse(data)
	}
	
	/**
	 * キーに対応する設定値を返します。<br/>
	 * テキストで指定した値があれば改行コードで連結して{@link ConfigSlurper}で解析します。<br/>
	 * キーに対応するテキストの指定が無ければnullを返します。
	 * @return ConfigObject
	 */
	ConfigObject config(String key){
		String data = (map.get(key) instanceof List)? map.get(key).join(System.lineSeparator()) : ''
		return (data.length() == 0)? null : slurper.parse(data)
	}
}
