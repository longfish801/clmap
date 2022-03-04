/*
 * ClmapClosureSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap

import io.github.longfish801.clmap.ClmapMsg as msgs
import io.github.longfish801.tpac.TpacSemanticException
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * ClmapClosureのテスト。
 * @version 0.3.00 2020/06/11
 * @author io.github.longfish801
 */
class ClmapClosureSpec extends Specification {
	/** ClmapServer */
	@Shared ClmapServer server
	
	def setup(){
		server = new ClmapServer()
	}
	
	def 'getClpath'(){
		given:
		Clmap clmap
		ClmapMap map1, map11
		ClmapClosure cl111
		
		when:
		clmap = new Clmap(tag: 'clmap', name: 'dec1')
		map1 = new ClmapMap(tag: 'map', name: 'map1')
		map11 = new ClmapMap(tag: 'map', name: 'map11')
		cl111 = new ClmapClosure(tag: 'closure', name: 'cl1')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << cl111
		then:
		cl111.clpath == '/dec1/map1/map11#cl1'
		
		when:
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		map11 = new ClmapMap(tag: 'map')
		cl111 = new ClmapClosure(tag: 'closure')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << cl111
		then:
		cl111.clpath == '/dflt/dflt/dflt#dflt'
	}
	
	def 'call'(){
		given:
		ClmapClosure clclosure
		
		when:
		clclosure = new ClmapClosure()
		clclosure.closure = { -> 'Hello!' }
		then:
		clclosure.call() == 'Hello!'
		
		when:
		clclosure = new ClmapClosure()
		clclosure.closure = { String name -> "Hello, ${name}!!" }
		then:
		clclosure.call('World') == 'Hello, World!!'
		
		when:
		clclosure = new ClmapClosure()
		clclosure.closure = { String name, int num -> "Hello, ${name}!!" * num }
		then:
		clclosure.call('World', 2) == 'Hello, World!!Hello, World!!'
	}
	
	def 'call - exception'(){
		given:
		Clmap clmap = new Clmap(tag: 'clmap', name: 'dec1')
		ClmapMap map1 = new ClmapMap(tag: 'map', name: 'map1')
		ClmapClosure clclosure
		ClmapClosureCallException exc
		server << clmap
		clmap << map1
		
		when: 'クロージャ実行時に例外'
		clclosure = new ClmapClosure()
		map1 << clclosure
		clclosure.code = '{ -> 1 / 0 }'
		clclosure.call()
		then:
		exc = thrown(ClmapClosureCallException)
		exc.localizedMessage == String.format(
			msgs.exc.closureExc,
			msgs.exc.throwedClosure,
			clclosure.clpath,
			ClmapClosure.addLineNo(clclosure.code))
		
		when: 'クロージャのコンパイル時に例外'
		clclosure = new ClmapClosure()
		map1 << clclosure
		clclosure.code = '{ -> '
		clclosure.call()
		then:
		exc = thrown(ClmapClosureCallException)
		exc.localizedMessage == String.format(
			msgs.exc.closureExc,
			msgs.exc.throwedClosure,
			clclosure.clpath,
			ClmapClosure.addLineNo(clclosure.code))
	}
	
	def 'createClosure'(){
		given:
		Clmap clmap
		ClmapMap map1, map11
		ClmapClosure clclosure11, clclosure111
		
		when:
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		clclosure11 = new ClmapClosure(tag: 'closure', name: 'cl11')
		server << clmap
		clmap << map1
		map1 << clclosure11
		clclosure11.code = '{ -> clmap }'
		then:
		clclosure11.createClosure().call() == map1
		
		when:
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		map11 = new ClmapMap(tag: 'map')
		clclosure111 = new ClmapClosure(tag: 'closure', name: 'cl111')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << clclosure111
		clclosure111.code = '{ -> clmap }'
		then:
		clclosure111.createClosure().call() == map11
		
		when: '大域変数を利用できること'
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		map11 = new ClmapMap(tag: 'map')
		clclosure111 = new ClmapClosure(tag: 'closure', name: 'cl111')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << clclosure111
		clmap.properties['prop1'] = 'val1'
		map1.properties['prop2'] = 'val2'
		map11.properties['prop3'] = 'val3'
		clclosure111.code = '{ -> [ prop1, prop2, prop3 ].join("-") }'
		then:
		clclosure111.createClosure().call() == 'val1-val2-val3'
		
		when: '宣言の大域変数を下位のmapハンドルで上書きできること'
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		map11 = new ClmapMap(tag: 'map')
		clclosure111 = new ClmapClosure(tag: 'closure', name: 'cl111')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << clclosure111
		clmap.properties['prop1'] = 'val1'
		clmap.properties['prop2'] = 'val2'
		map11.properties['prop2'] = 'val2overwrite'
		clclosure111.code = '{ -> [ prop1, prop2 ].join("-") }'
		then:
		clclosure111.createClosure().call() == 'val1-val2overwrite'
		
		when: 'mapハンドルの大域変数を下位のmapハンドルで上書きできること'
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		map11 = new ClmapMap(tag: 'map')
		clclosure111 = new ClmapClosure(tag: 'closure', name: 'cl111')
		server << clmap
		clmap << map1
		map1 << map11
		map11 << clclosure111
		map1.properties['prop1'] = 'val1'
		map1.properties['prop2'] = 'val2'
		map11.properties['prop2'] = 'val2overwrite'
		clclosure111.code = '{ -> [ prop1, prop2 ].join("-") }'
		then:
		clclosure111.createClosure().call() == 'val1-val2overwrite'
	}
	
	def 'createClosure - exception'(){
		given:
		Clmap clmap
		ClmapMap map1, map11
		ClmapClosure clclosure11, clclosure111
		MultipleCompilationErrorsException exc
		
		when:
		clmap = new Clmap(tag: 'clmap')
		map1 = new ClmapMap(tag: 'map')
		clclosure11 = new ClmapClosure(tag: 'closure', name: 'cl11')
		server << clmap
		clmap << map1
		map1 << clclosure11
		clclosure11.code = '{ -> } }'
		clclosure11.createClosure()
		then:
		exc = thrown(MultipleCompilationErrorsException)
		exc.message.startsWith('startup failed:') == true
	}
	
	def 'createCode'(){
		given:
		String source
		String code, code2
		String expected, expected2
		
		when:
		source = '''\
			#! clmap:test1
			#> dec
				import org.apache.commons.lang3.StringUtils
			#> prefix
				println 'BGN HERE'
			#> suffix
				println 'END HERE'
			#> map:map1
			#>> args
				String yourName
			#>> return
				String result
			#>> dec
				String cmnString = 'This is'
			#>> suffix
				result += '!'
			#>> closure:key1
				result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
			'''.stripIndent()
		expected = '''\
				import org.apache.commons.lang3.StringUtils
				String cmnString = 'This is'
			{ 	String yourName ->
			String result
				println 'BGN HERE'
				result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
				println 'END HERE'
				result += '!'
				return result
			}
			'''.stripIndent().denormalize()
		server.soak(source)
		code = server.cl('/test1/map1#key1').createCode()
		then:
		code == expected
		
		when:
		source = '''\
			#! clmap:test2
			#> map:map1
			#>> args
				String greet
			#-key
				String target
			#>> dec
				import org.apache.commons.lang3.StringUtils
			#-key
				// some dec
			#>> prefix
				String div = ', '
			#-key
				div = '-'
			#>> suffix
				return result
			#-key
				// some suffix
			#>> closure
				String result = StringUtils.trim("   ${greet}${div}.   ")
			#>> closure:key
				String result = StringUtils.trim("   ${greet}${div}${target}.   ")
			'''.stripIndent()
		expected = '''\
				import org.apache.commons.lang3.StringUtils
			{ 	String greet ->
				String div = ', '
				String result = StringUtils.trim("   ${greet}${div}.   ")
				return result
			}
			'''.stripIndent().denormalize()
		expected2 = '''\
				import org.apache.commons.lang3.StringUtils
				// some dec
			{ 	String greet,	String target ->
				String div = ', '
				div = '-'
				String result = StringUtils.trim("   ${greet}${div}${target}.   ")
				return result
				// some suffix
			}
			'''.stripIndent().denormalize()
		server.soak(source)
		code = server.cl('/test2/map1#dflt').createCode()
		code2 = server.cl('/test2/map1#key').createCode()
		then:
		code == expected
		code2 == expected2
		
		when:
		source = '''\
			#! clmap:test3
			#> map:map1
			#>> args
				String yourName
			#>> return
				yourName
			#>> closure:key1
				yourName = "Hello, ${yourName}"
			'''.stripIndent()
		expected = '''\
			{ 	String yourName ->
				yourName = "Hello, ${yourName}"
				return yourName
			}
			'''.stripIndent().denormalize()
		server.soak(source)
		code = server.cl('/test3/map1#key1').createCode()
		then:
		code == expected
	}
	
	def 'createCode - exception'(){
		given:
		String source
		TpacSemanticException exc
		
		when:
		source = '''\
			#! clmap:test1
			#> map:map1
			#>> args
				String yourName
			#>> return String message
			#>> closure:key1
				message = 'Hello!'
			'''.stripIndent().denormalize()
		server.soak(source)
		server.cl('/test1/map1#key1').createCode()
		then:
		exc = thrown(TpacSemanticException)
		exc.message == msgs.exc.noReturnText
	}
	
	def 'addLineNo'(){
		given:
		String result
		String expected
		
		when:
		result = ClmapClosure.addLineNo('')
		then:
		result == '1 '
		
		when:
		result = ClmapClosure.addLineNo('a')
		then:
		result == '1 a'
		
		when:
		result = ClmapClosure.addLineNo('''\
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
		result = ClmapClosure.addLineNo('''\
			
			2
			'''.stripIndent())
		expected = '''\
			1 
			2 2'''.stripIndent().denormalize()
		then:
		result == expected
		
		when:
		result = ClmapClosure.addLineNo('''\
			
			
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
		result = ClmapClosure.addLineNo('''\
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
		result = ClmapClosure.addLineNo('''\
			|
			|
			|
			|'''.stripMargin())
		then:
		result == ''
		
		when:
		result = ClmapClosure.addLineNo('''\
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
