/*
 * ClmapSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassDirectory;
import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * Clmapのテスト。
 * 
 * @version 1.0.00 2016/11/30
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	private static final File testDir = new ClassDirectory('src/test/resources').getDeepDir(ClmapSpec.class);
	
	def 'クロージャ情報を返します'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec01.tpac'));
		
		expect:
		clmap.cl('map1', 'key1').call('World') == 'This is World.';
		clmap.cl('map1#key1').call('World') == 'This is World.';
		clmap.cl(new CombiKey('map1#key1')).call('World') == 'This is World.';
	}
	
	def 'マップ名の一覧を返します'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec02.tpac'));
		
		expect:
		clmap.mapNames == [ 'map1', 'map2' ];
	}
	
	def '指定されたマップ配下のクロージャ名の一覧を返します'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec02.tpac'));
		
		expect:
		clmap.getClosureNames('map1') == [ '', 'key1', 'key2' ];
		clmap.getClosureNames('noSuchMap') == [ ];
	}
	
	def 'クロージャ情報インスタンスを新規に生成します'(){
		given:
		Clmap clmap = null;
		
		when: 'クロージャのソースコードが作成されること'
		clmap = new Clmap(new File(testDir, 'ClmapSpec01.tpac'));
		then:
		clmap.cl('map1#key1').code == new File(testDir, 'ClmapSpec01.txt').getText();
		
		when: '該当するクロージャ情報がない場合はnullを返すこと'
		clmap = new Clmap(new File(testDir, 'ClmapSpec01.tpac'));
		then:
		clmap.cl('noSuchMap#noSuchClosure') == null;
	}
	
	@Unroll
	def 'マップ名とキーに対応するクロージャが返ること'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec02.tpac'));
		
		expect:
		clmap.cl(combiKey).call(args) == expect;
		
		where:
		combiKey	| args	|| expect
		'map1#key1'	| '01'	|| 'This is 01 key1.'
		'map1#key2'	| '02'	|| 'This is 02 key2.'
	}
	
	@Unroll
	def 'キーの省略時や存在しないキーのときデフォルトのクロージャが返ること'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec02.tpac'));
		
		expect:
		clmap.cl(combiKey).call(args) == expect;
		
		where:
		combiKey			| args	|| expect
		'map1#noSuchKey'	| '03'	|| 'This is 03 default.'
		'map2'				| '04'	|| 'This is 04 default2.'
		'map2#noSuchKey'	| '05'	|| 'This is 05 default2.'
	}
	
	@Unroll
	def 'マージすることでクロージャマップが上書きされること'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec02.tpac'));
		Clmap clmapChild = new Clmap(new File(testDir, 'ClmapSpec03.tpac'));
		clmap.blend(clmapChild);
		
		expect:
		clmap.cl(combiKey).call(args) == expect;
		
		where:
		combiKey			| args	|| expect
		'map3#key3'			| '01'	|| 'THIS IS 01 key3.'
		'map3#noSuchKey'	| '02'	|| 'THIS IS 02 default2.'
		'map2#key3'			| '03'	|| 'This is 03 key3.'
		'map2'				| '04'	|| 'This is 04 default2.'
		'map1#key1'			| '05'	|| 'THIS IS 05 key1.'
		'map1#noSuchKey'	| '06'	|| 'THIS IS 06 default.'
		'map1#key2'			| '07'	|| 'This is 07 key2.'
	}
	
	@Unroll
	def 'デフォルトの引数を使用できること'(){
		given:
		Clmap clmap = new Clmap(new File(testDir, 'ClmapSpec04.tpac'));
		clmap.cl('map1#setter').call('TEST');
		
		expect:
		clmap.cl(combiKey).call(args) == expect;
		
		where:
		combiKey		| args				|| expect
		'map1#getter1'	| 'test'			|| 'TEST-1-test'
		'map2#getter2'	| 'test2'			|| 'TEST-2-test2'
		'map1#getter'	| 'map2'			|| 'map2-1-test'
		'map1#getter'	| 'map2#getter2'	|| 'map2#getter2-2-test'
	}
}
