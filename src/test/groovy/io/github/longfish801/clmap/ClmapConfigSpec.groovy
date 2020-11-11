/*
 * ClmapConfigSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import spock.lang.Specification
import spock.lang.Unroll

/**
 * ClmapConfigのテスト。
 * @version 0.3.01 2020/10/28
 * @author io.github.longfish801
 */
class ClmapConfigSpec extends Specification {
	def 'config'(){
		given:
		ClmapConfig clConfig
		
		when:
		clConfig = new ClmapConfig(tag: 'config')
		clConfig._ = [ /map {/, /key = 'val'/, /}/ ]
		then:
		clConfig.config().map.key == 'val'
	}
}
