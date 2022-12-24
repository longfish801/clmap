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
 * @author io.github.longfish801
 */
class ClmapSpec extends Specification {
	def 'Clmap'(){
		expect:
		new Clmap().shell instanceof GroovyShell
	}
	
	@Unroll
	def 'cl'(){
		given:
		ClmapServer server = new ClmapServer()
		Clmap clmap = new Clmap(tag: 'clmap', name: 'dec1')
		ClmapMap map = new ClmapMap(tag: 'map')
		ClmapMap map1 = new ClmapMap(tag: 'map', name: 'map1')
		ClmapMap map11 = new ClmapMap(tag: 'map', name: 'map11')
		ClmapClosure cl1 = new ClmapClosure(tag: 'closure', name: 'cl1')
		ClmapClosure cl2 = new ClmapClosure(tag: 'closure')
		server << clmap
		clmap << map
		clmap << map1
		map1 << map11
		clmap << cl1
		clmap << cl2
		
		expect:
		clmap.cl(clpath) == server.solve(path)
		
		where:
		clpath			|| path
		'/dec1'			|| '/clmap:dec1'
		'dflt'			|| '/clmap:dec1/map'
		'map1'			|| '/clmap:dec1/map:map1'
		'map1/map11'	|| '/clmap:dec1/map:map1/map:map11'
		'#cl1'			|| '/clmap:dec1/closure:cl1'
		'#'				|| '/clmap:dec1/closure'
		'/dec1#cl1'		|| '/clmap:dec1/closure:cl1'
		'/dec1#'		|| '/clmap:dec1/closure'
	}
	
	def 'cl - exception'(){
		given:
		TpacHandlingException exc
		
		when:
		new Clmap(tag: 'clmap', name: 'dec1').cl('dec1#cl1/map1')
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, 'dec1#cl1/map1')
		
		when:
		new Clmap(tag: 'clmap', name: 'dec1').cl('..')
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, '..')
	}
	
	def 'call'(){
		given:
		ClmapServer server
		Clmap clmap
		
		when:
		server = new ClmapServer()
		clmap = server.soak('''\
			#! clmap:call
			#> closure
				return 'Hello'
			'''.stripIndent())['clmap:call']
		then:
		clmap.call() == 'Hello'
	}
	
	def 'call - exception'(){
		given:
		ClmapServer server
		Clmap clmap
		TpacHandlingException exc
		
		when:
		server = new ClmapServer()
		clmap = server.soak('''\
				#! clmap:call
				#> closure:name
					return 'Hello'
			'''.stripIndent())['clmap:call']
		clmap.call() == 'Hello'
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, '#')
	}
}
