/*
 * ClmapMakerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.tpac.TeaServer;
import io.github.longfish801.tpac.element.TeaDec;
import io.github.longfish801.tpac.element.TeaHandle;
import io.github.longfish801.tpac.TeaServerParseException;
import spock.lang.Shared;
import spock.lang.Specification;

/**
 * ClmapMakerのテスト。
 * @version 1.0.00 2017/07/12
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapMakerSpec extends Specification {
	/** TeaServer */
	@Shared TeaServer server;
	
	def setup(){
		server = new ClmapServer();
	}
	
	def 'TeaDecインスタンスを生成します。'(){
		given:
		String source;
		TeaDec dec;
		
		when:
		source = '''\
			#! clmap
			'''.stripIndent();
		server.soak(source);
		dec = server['clmap:'];
		then:
		dec instanceof Clmap;
	}
	
	def 'TeaHandleインスタンスを生成します。'(){
		given:
		String source;
		TeaHandle closHndle;
		TeaServerParseException exc;
		
		when:
		source = '''\
			#! clmap
			#> map
			#>> closure
				println 'Hello World!';
			'''.stripIndent();
		server.soak(source);
		closHndle = server.path('/clmap:/map:/closure:');
		then:
		closHndle instanceof Clinfo;
		
		when:
		source = '''\
			#! clmap nosuch
			#> nosuchtag nosuchname
			'''.stripIndent();
		server.soak(source);
		then:
		exc = thrown(TeaServerParseException);
		exc.message == 'tpac文書の構築が記述誤りのため失敗しました。lineNo=2 line=#> nosuchtag nosuchname'
		
		when:
		source = '''\
			#! clmap invalid
			#> args
			'''.stripIndent();
		server.soak(source);
		then:
		exc = thrown(TeaServerParseException);
		exc.message == 'tpac文書の構築が記述誤りのため失敗しました。lineNo=2 line=#> args'
	}
}
