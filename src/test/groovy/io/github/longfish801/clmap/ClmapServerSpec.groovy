/*
 * ClmapServerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.tpac.parser.TeaMaker;
import io.github.longfish801.tpac.parser.TpacMaker;
import spock.lang.Specification;

/**
 * ClmapServerのテスト。
 * 
 * @version 1.0.00 2018/09/06
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapServerSpec extends Specification {
	def '宣言のタグに対応する TeaMakerを返します。'(){
		given:
		ClmapServer server = new ClmapServer();
		TeaMaker maker;
		
		when:
		maker = server.maker('clmap');
		then:
		maker instanceof ClmapMaker;
		
		when:
		maker = server.maker('tpac');
		then:
		maker instanceof TpacMaker;
	}
}
