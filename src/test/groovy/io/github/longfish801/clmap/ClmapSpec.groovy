/*
 * ClmapSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Clmapのテスト。
 * @version 0.3.00 2020/06/11
 * @author io.github.longfish801
 */
class ClmapSpec extends Specification {
	@Unroll
	def 'cl'(){
		given:
		ClmapServer server = new ClmapServer()
		Clmap clmap = new Clmap(tag: 'clmap', name: 'dec1')
		ClmapMap map = new ClmapMap(tag: 'map')
		ClmapMap map1 = new ClmapMap(tag: 'map', name: 'map1')
		ClmapMap map11 = new ClmapMap(tag: 'map', name: 'map11')
		server << clmap
		clmap << map
		clmap << map1
		map1 << map11
		
		expect:
		clmap.cl(clpath) == server.solvePath(path)
		
		where:
		clpath			|| path
		'/dec1'			|| '/clmap:dec1'
		'dflt'			|| '/clmap:dec1/map'
		'map1'			|| '/clmap:dec1/map:map1'
		'map1/map11'	|| '/clmap:dec1/map:map1/map:map11'
	}
	
	def 'cl - exception'(){
		given:
		TpacHandlingException exc
		
		when:
		new Clmap(tag: 'clmap', name: 'dec1').cl('dec1#cl1/map1')
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, 'dec1#cl1/map1')
	}
}
