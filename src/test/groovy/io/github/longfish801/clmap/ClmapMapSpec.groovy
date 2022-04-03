/*
 * ClmapMapSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ClmapMapのテスト。
 * @author io.github.longfish801
 */
class ClmapMapSpec extends Specification {
	def 'ClmapMap'(){
		expect:
		new ClmapMap().shell instanceof GroovyShell
	}
	
	@Unroll
	def 'cl'(){
		given:
		ClmapServer server = new ClmapServer()
		Clmap clmap = new Clmap(tag: 'clmap', name: 'dec1')
		ClmapMap map1 = new ClmapMap(tag: 'map', name: 'map1')
		ClmapClosure cl0 = new ClmapClosure(tag: 'closure')
		ClmapClosure cl1 = new ClmapClosure(tag: 'closure', name: 'cl1')
		ClmapMap map10 = new ClmapMap(tag: 'map')
		ClmapMap map11 = new ClmapMap(tag: 'map', name: 'map11')
		ClmapClosure cl110 = new ClmapClosure(tag: 'closure')
		ClmapClosure cl111 = new ClmapClosure(tag: 'closure', name: 'cl111')
		ClmapMap map111 = new ClmapMap(tag: 'map', name: 'map111')
		server << clmap
		clmap << map1
		map1 << cl0
		map1 << cl1
		map1 << map10
		map1 << map11
		map11 << map111
		map11 << cl110
		map11 << cl111
		
		expect:
		map1.cl(clpath) == server.solve(path)
		
		where:
		clpath			|| path
		'/dec1'			|| '/clmap:dec1'
		'#dflt'			|| '/clmap:dec1/map:map1/closure'
		'#cl1'			|| '/clmap:dec1/map:map1/closure:cl1'
		'#nosuch'		|| '/clmap:dec1/map:map1/closure'
		'dflt'			|| '/clmap:dec1/map:map1/map'
		'map11'			|| '/clmap:dec1/map:map1/map:map11'
		'map11#dflt'	|| '/clmap:dec1/map:map1/map:map11/closure'
		'map11#cl111'	|| '/clmap:dec1/map:map1/map:map11/closure:cl111'
		'map11#nosuch'	|| '/clmap:dec1/map:map1/map:map11/closure'
		'map11/map111'	|| '/clmap:dec1/map:map1/map:map11/map:map111'
	}
	
	def 'cl - exception'(){
		given:
		TpacHandlingException exc
		
		when:
		new ClmapMap(tag: 'map', name: 'map1').cl('dec1:cl1')
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, 'dec1:cl1')
	}
}
