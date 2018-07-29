/*
 * ClmapMakerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.tpac.Tpac.TpacParseException;
import spock.lang.Specification;

/**
 * ClmapMakerのテスト。
 * 
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapMakerSpec extends Specification {
	def '可変長引数のクロージャを実行します'(){
		given:
		Clmap clmap = null;
		
		when:
		clmap = new Clmap('''\
			#! nosuchtag
			## map
			# closure
			'Hello!'
			'''.stripIndent());
		then:
		thrown(TpacParseException);
		
		when:
		clmap = new Clmap('''\
			#! clmap
			## nosuchtag
			# closure
			'Hello!'
			'''.stripIndent());
		then:
		thrown(TpacParseException);
		
		when:
		clmap = new Clmap('''\
			#! clmap
			## dec 名前
			# closure
			'Hello!'
			'''.stripIndent());
		then:
		thrown(TpacParseException);
		
		when:
		clmap = new Clmap('''\
			#! clmap
			## map
			# nosuchtag
			'Hello!'
			'''.stripIndent());
		then:
		thrown(TpacParseException);
		
		when:
		clmap = new Clmap('''\
			#! clmap
			## map
			# dec 名前
			'Hello!'
			'''.stripIndent());
		then:
		thrown(TpacParseException);
	}
}
