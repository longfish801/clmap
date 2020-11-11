/*
 * ClmapConfig.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.tpac.tea.TeaHandle

/**
 * 設定値です。
 * @version 0.3.01 2020/10/28
 * @author io.github.longfish801
 */
class ClmapConfig implements TeaHandle {
	/** ConfigSlurper */
	static ConfigSlurper slurper = new ConfigSlurper()
	
	/**
	 * デフォルトキーに対応するテキストを{@link ConfigSlurper}で解析した結果を返します。<br/>
	 * デフォルトキーに対応する値がテキスト以外のときはnullを返します。
	 * @return ConfigObject
	 */
	ConfigObject config(){
		return (dflt instanceof List)? slurper.parse(dflt?.join(System.lineSeparator())) : null
	}
}
