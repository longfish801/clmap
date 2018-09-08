/*
 * ClmapSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.PackageDirectory;
import io.github.longfish801.tpac.TeaServer;
import spock.lang.Shared;
import spock.lang.Specification;

/**
 * Clmapのテスト。
 * @version 1.0.00 2016/11/30
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapSpec extends Specification {
	/** ファイル入出力のテスト用フォルダ */
	static final File testDir = PackageDirectory.deepDir('src/test/resources', ClmapSpec.class);
	/** TeaServer */
	@Shared TeaServer server;
	
	def setup(){
		server = new ClmapServer();
	}
	
	def 'コンビキー文字列に対応するクロージャ情報を返します。'(){
		given:
		Clmap clmap;
		
		when:
		server.soak(new File(testDir, '01.tpac'));
		clmap = server['clmap:テスト'];
		then:
		clmap.cl('map1#key1').call('World') == 'This is World.';
	}
	
	def 'マップ名の一覧を返します。'(){
		given:
		Clmap clmap;
		
		when:
		server.soak(new File(testDir, '02.tpac'));
		clmap = server['clmap:'];
		then:
		clmap.mapNames == [ 'map1', 'map2' ];
	}
	
	def '指定されたマップ配下のクロージャ名の一覧を返します'(){
		given:
		Clmap clmap;
		
		when:
		server.soak(new File(testDir, '02.tpac'));
		clmap = server['clmap:'];
		then:
		clmap.getClosureNames('map1') == [ '', 'key1', 'key2' ];
		clmap.getClosureNames('map2') == [ '', 'key3' ];
		clmap.getClosureNames('noSuchMap') == [ ];
	}
}
