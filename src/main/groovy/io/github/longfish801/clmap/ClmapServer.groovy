/*
 * ClmapServer.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.util.logging.Slf4j;
import io.github.longfish801.tpac.TeaServer;
import io.github.longfish801.tpac.parser.TeaMaker;

/**
 * クロージャマップのサーバーです。
 * @version 1.0.00 2018/09/03
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class ClmapServer implements TeaServer {
	/**
	 * 宣言のタグに対応する TeaMakerを返します。
	 * @param tag 宣言のタグ
	 * @return TeaMaker
	 */
	TeaMaker maker(String tag){
		return (tag == 'clmap')? new ClmapMaker() : TeaServer.super.maker(tag);
	}
}
