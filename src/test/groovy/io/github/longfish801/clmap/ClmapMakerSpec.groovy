/*
 * ClmapMakerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacSemanticException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ClmapMakerのテスト。
 * @version 0.3.00 2020/06/11
 * @author io.github.longfish801
 */
class ClmapMakerSpec extends Specification {
	/** ClmapMaker */
	@Shared ClmapMaker maker
	
	def setup(){
		maker = new ClmapMaker()
	}
	
	def 'newTeaDec'(){
		expect:
		maker.newTeaDec('tag', 'name') instanceof Clmap
	}
	
	@Unroll
	def 'newTeaHandle'(){
		given:
		TpacHandle upper = new TpacHandle(tag: 'map')
		
		expect:
		maker.newTeaHandle(tag, '', upper).class.simpleName == classname
		
		where:
		tag			|| classname
		'closure'	|| 'ClmapClosure'
		'map'		|| 'ClmapMap'
		'args'		|| 'TpacHandle'
	}
	
	def 'newTeaHandle - exception'(){
		given:
		TpacSemanticException exc
		
		when:
		maker.newTeaHandle('nosuch', '', new TpacHandle(tag: 'clmap'))
		then:
		exc = thrown(TpacSemanticException)
		exc.message == String.format(msgs.exc.invalidTag, 'nosuch')
		
		when:
		maker.newTeaHandle('closure', '', new TpacHandle(tag: 'clmap'))
		then:
		exc = thrown(TpacSemanticException)
		exc.message == String.format(msgs.exc.invalidHierarchy, 'closure', 'clmap')
	}
}
