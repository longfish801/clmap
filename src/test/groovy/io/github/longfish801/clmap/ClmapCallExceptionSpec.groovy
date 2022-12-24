/*
 * ClmapCallExceptionSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapConst as cnst
import spock.lang.Specification

/**
 * ClmapCallExceptionのテスト。
 * @author io.github.longfish801
 */
class ClmapCallExceptionSpec extends Specification {
	def 'addLineNo'(){
		given:
		String result
		String expected
		
		when:
		result = ClmapCallException.addLineNo(null)
		then:
		result == cnst.nulltext
		
		when:
		result = ClmapCallException.addLineNo('')
		then:
		result == '1 '
		
		when:
		result = ClmapCallException.addLineNo('a')
		then:
		result == '1 a'
		
		when:
		result = ClmapCallException.addLineNo('''\
			あ
			い
			う'''.stripIndent())
		expected = '''\
			1 あ
			2 い
			3 う'''.stripIndent().denormalize()
		then:
		result == expected
		
		when:
		result = ClmapCallException.addLineNo('''\
			
			2
			'''.stripIndent())
		expected = '''\
			1 
			2 2'''.stripIndent().denormalize()
		then:
		result == expected
		
		when:
		result = ClmapCallException.addLineNo('''\
			
			
			3
			4'''.stripIndent())
		expected = '''\
			1 
			2 
			3 3
			4 4'''.stripIndent().denormalize()
		then:
		result == expected
		
		when:
		result = ClmapCallException.addLineNo('''\
			1
			
			
			4'''.stripIndent())
		expected = '''\
			1 1
			2 
			3 
			4 4'''.stripIndent().denormalize()
		then:
		result == expected
		
		when:
		result = ClmapCallException.addLineNo('''\
			|
			|
			|
			|'''.stripMargin())
		then:
		result == ''
		
		when:
		result = ClmapCallException.addLineNo('''\
			|1
			|
			|
			| 
			|
			|
			|
			|
			|
			|10'''.stripMargin())
		expected = '''\
			01 1
			02 
			03 
			04  
			05 
			06 
			07 
			08 
			09 
			10 10'''.stripIndent().denormalize()
		then:
		result == expected
	}
}
