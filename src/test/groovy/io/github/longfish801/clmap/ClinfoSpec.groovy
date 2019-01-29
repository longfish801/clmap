/*
 * ClinfoSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import io.github.longfish801.tpac.TeaServer;
import spock.lang.Shared;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * Clinfoのテスト。
 * 
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClinfoSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ClinfoSpec.class);
	/** TeaServer */
	@Shared TeaServer server;
	
	def setup(){
		server = new ClmapServer();
	}
	
	def 'このクロージャ情報のコンビキーを返します。'(){
		given:
		Clinfo clinfo;
		
		when:
		server.soak(new File(testDir, '01.tpac'));
		clinfo = server['clmap:テスト'].cl('map1#key1');
		then:
		clinfo.combiKey == 'map1#key1';
	}
	
	def '可変長引数のクロージャを実行します'(){
		given:
		Clinfo clinfo;
		
		when:
		clinfo = new Clinfo();
		clinfo.closure = { -> "Hello!" };
		then:
		clinfo.call() == 'Hello!';
		
		when:
		clinfo = new Clinfo();
		clinfo.closure = { String name -> "Hello, ${name}!!" };
		then:
		clinfo.call('World') == 'Hello, World!!';
		
		when:
		clinfo = new Clinfo();
		clinfo.closure = { String name, int num -> "Hello, ${name}!!" * num };
		then:
		clinfo.call('World', 2) == 'Hello, World!!Hello, World!!';
	}
	
	@Unroll
	def 'クロージャを生成します。'(){
		given:
		server.soak(new File(testDir, '03.tpac'));
		Clinfo clinfo = server['clmap:'].cl('#redirect');
		
		expect:
		clinfo.call(args) == expect;
		
		where:
		args			|| expect
		'#getter'		|| 'hello test'
		'some#'			|| 'bye test'
		'some#getter'	|| 'goodbye test'
	}
	
	def 'クロージャのソースコードを生成します。'(){
		given:
		String result;
		String expected;
		
		when:
		server.soak(new File(testDir, '01.tpac'));
		result = server['clmap:テスト'].cl('map1#key1').createCode();
		expected = new File(testDir, '01.txt').text;
		then:
		result == expected;
	}
}
