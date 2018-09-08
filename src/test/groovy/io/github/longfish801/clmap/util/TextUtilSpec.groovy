/*
 * TextUtilSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap.util;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * TextUtilのテスト。
 * @version 1.0.00 2017/07/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class TextUtilSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', TextUtilSpec.class);
	
	def 'addLineNo'(){
		given:
		String result = null;
		String expected = null;
		
		when:
		result = TextUtil.addLineNo(new File(testDir, 'input.txt').getText());
		expected = new File(testDir, 'output.txt').getText();
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('');
		then:
		result == '1 ';
		
		when:
		result = TextUtil.addLineNo('a');
		then:
		result == '1 a';
		
		when:
		result = TextUtil.addLineNo('''\
			あ
			い
			う'''.stripIndent());
		expected = '''\
			1 あ
			2 い
			3 う'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			
			2
			'''.stripIndent());
		expected = '''\
			1 
			2 2'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			
			
			3
			4'''.stripIndent());
		expected = '''\
			1 
			2 
			3 3
			4 4'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			1
			
			
			4'''.stripIndent());
		expected = '''\
			1 1
			2 
			3 
			4 4'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
		
		when:
		result = TextUtil.addLineNo('''\
			|
			|
			|
			|'''.stripMargin());
		then:
		result == '';
		
		when:
		result = TextUtil.addLineNo('''\
			|1
			|
			|
			| 
			|
			|
			|
			|
			|
			|10'''.stripMargin());
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
			10 10'''.stripIndent().replaceAll("\n", System.getProperty('line.separator'));
		then:
		result == expected;
	}
}
