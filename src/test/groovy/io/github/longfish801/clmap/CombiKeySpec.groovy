/*
 * CombiKeySpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import spock.lang.Specification;

/**
 * CombiKeyのテスト。
 * 
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class CombiKeySpec extends Specification {
	def 'コンストラクタ'(){
		given:
		CombiKey combiKey = null;
		
		when:
		new CombiKey('マップ', 'クロージャ');
		then:
		noExceptionThrown();
		
		when:
		new CombiKey('マ#プ', 'クロージャ');
		then:
		thrown(IllegalArgumentException);
		
		when:
		new CombiKey('マップ', '');
		then:
		noExceptionThrown();
		
		when:
		combiKey = new CombiKey('マップ#クロージャ');
		then:
		combiKey.mapName == 'マップ';
		combiKey.clName == 'クロージャ';
		
		when:
		combiKey = new CombiKey('#クロージャ');
		then:
		combiKey.mapName == '';
		combiKey.clName == 'クロージャ';
		
		when:
		combiKey = new CombiKey('マップ#');
		then:
		combiKey.mapName == 'マップ';
		combiKey.clName == '';
		
		when:
		combiKey = new CombiKey('マップ');
		then:
		combiKey.mapName == 'マップ';
		combiKey.clName == '';
		
		when:
		combiKey = new CombiKey('');
		then:
		combiKey.mapName == '';
		combiKey.clName == '';
		
		when:
		new CombiKey('マ プ');
		then:
		thrown(IllegalArgumentException);
	}
	
	def '文字列表現を返します'(){
		given:
		CombiKey combiKey = null;
		
		when:
		combiKey = new CombiKey('マップ', 'クロージャ');
		then:
		combiKey.toString() == 'マップ#クロージャ';
		
		when:
		combiKey = new CombiKey('マップ', '');
		then:
		combiKey.toString() == 'マップ#';
		
		when:
		combiKey = new CombiKey('', 'クロージャ');
		then:
		combiKey.toString() == '#クロージャ';
	}
}
