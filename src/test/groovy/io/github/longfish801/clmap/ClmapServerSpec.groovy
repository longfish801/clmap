/*
 * ClmapServerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandlingException
import io.github.longfish801.tpac.TpacMaker
import io.github.longfish801.tpac.tea.TeaMaker
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ClmapServerのテスト。
 * @author io.github.longfish801
 */
class ClmapServerSpec extends Specification {
	/** ClmapServer */
	@Shared ClmapServer server
	
	def setup(){
		server = new ClmapServer()
	}
	
	def 'ClmapServer'(){
		when:
		server = new ClmapServer(ClmapServerSpec.class.classLoader)
		then:
		ClmapMap.loader == ClmapServerSpec.class.classLoader
	}
	
	def 'newMaker'(){
		given:
		TeaMaker maker
		
		when:
		maker = server.newMaker('clmap')
		then:
		maker instanceof ClmapMaker
		
		when:
		maker = server.newMaker('some')
		then:
		maker instanceof TpacMaker
	}
	
	@Unroll
	def 'cl'(){
		given:
		Clmap clmap = new Clmap(tag: 'clmap')
		Clmap clmap1 = new Clmap(tag: 'clmap', name: 'dec1')
		ClmapMap map1 = new ClmapMap(tag: 'map', name: 'map1')
		server << clmap
		server << clmap1
		clmap1 << map1
		
		expect:
		server.cl(clpath) == server.solve(path)
		
		where:
		clpath			|| path
		'/dflt'			|| '/clmap'
		'/dec1'			|| '/clmap:dec1'
		'/dec1/map1'	|| '/clmap:dec1/map:map1'
	}
	
	def 'cl - exception'(){
		given:
		TpacHandlingException exc
		
		when:
		server.cl('x')
		then:
		exc = thrown(TpacHandlingException)
		exc.message == String.format(msgs.exc.invalidClpath, 'x')
	}
}
