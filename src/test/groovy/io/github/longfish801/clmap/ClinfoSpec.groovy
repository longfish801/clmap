/*
 * ClinfoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * Clinfoのテスト。
 * 
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClinfoSpec extends Specification {
	def 'コンストラクタ'(){
		when:
		new Clinfo('some', "{ -> 'Hello!' }");
		then:
		noExceptionThrown();
		
		when:
		new Clinfo(null, "{ -> 'Hello!' }");
		then:
		thrown(IllegalArgumentException);
		
		when:
		new Clinfo('some', ' ');
		then:
		thrown(IllegalArgumentException);
	}
	
	def '可変長引数のクロージャを実行します'(){
		given:
		Clinfo clinfo = null;
		
		when:
		clinfo = new Clinfo('some', '{ -> "Hello!" }');
		then:
		clinfo.call() == 'Hello!';
		
		when:
		clinfo = new Clinfo('some', '{ String name -> "Hello, ${name}!!" }');
		then:
		clinfo.call('World') == 'Hello, World!!';
		
		when:
		clinfo = new Clinfo('some', '{ String name, int num -> "Hello, ${name}!!" * num }');
		then:
		clinfo.call('World', 2) == 'Hello, World!!Hello, World!!';
	}
}
