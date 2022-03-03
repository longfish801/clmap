/*
 * ClmapConfigSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import spock.lang.Shared
import spock.lang.Specification

/**
 * ClmapConfigのテスト。
 * @version 0.3.01 2020/10/28
 * @author io.github.longfish801
 */
class ClmapConfigSpec extends Specification {
	/** ClmapConfig */
	@Shared ClmapConfig clConfig
	
	def setup(){
		clConfig = new ClmapConfig(tag: 'config')
	}
	
	def 'config'(){
		when:
		clConfig.dflt = [ /map {/, /key = 'val'/, /}/ ]
		then:
		clConfig.config().map.key == 'val'
		
		when:
		clConfig.dflt = [ /some.bar = 1/ ]
		clConfig.x = [ /some.foo = 2/ ]
		then:
		clConfig.config().some.bar == 1
		clConfig.config().some.foo == 2
		
		when:
		clConfig.dflt = [ /some.bar = 1/ ]
		clConfig.x = [ /some.foo = 2/ ]
		then:
		clConfig.config('dflt').some.bar == 1
		clConfig.config('x').some.foo == 2
	}
}
