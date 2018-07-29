/*
 * Clmap.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.clmap;

import groovy.transform.InheritConstructors;
import groovy.util.logging.Slf4j;
import io.github.longfish801.shared.util.ClassSlurper;
import io.github.longfish801.tpac.Tpac;
import io.github.longfish801.tpac.TpacMaker;
import io.github.longfish801.tpac.element.TpacChild;
import io.github.longfish801.tpac.element.TpacParent;

/**
 * クロージャマップです。
 * @version 1.0.00 2016/11/29
 * @author io.github.longfish801
 */
@Slf4j('LOG')
@InheritConstructors
class Clmap extends Tpac {
	/** ConfigObject */
	protected static final ConfigObject constants = ClassSlurper.getConfig(Clmap.class);
	/** ConfigObject */
	ConfigObject config = new ConfigObject();
	/** コンビキーとクロージャ情報のキャッシュ */
	Map<CombiKey, Clinfo> cache = [:];
	
	/**
	 * TPAC文書を解析するためのインスタンスとしてClmapMakerを返します。
	 * @return TPAC文書を解析するためのインスタンス
	 */
	TpacMaker getMaker(){
		return new ClmapMaker();
	}
	
	/**
	 * マップ名、クロージャ名に対応するクロージャ情報を返します。
	 * @param mapName マップ名
	 * @param clName クロージャ名
	 * @return クロージャ情報（存在しない場合はnull）
	 * @see #cl(CombiKey)
	 */
	Clinfo cl(String mapName, String clName){
		return cl(new CombiKey(mapName, clName));
	}
	
	/**
	 * コンビキー文字列に対応するクロージャ情報を返します。
	 * @param combiStr コンビキー文字列
	 * @return クロージャ情報（存在しない場合はnull）
	 * @see #cl(CombiKey)
	 */
	Clinfo cl(String combiStr){
		return cl(new CombiKey(combiStr));
	}
	
	/**
	 * コンビキーに対応するクロージャ情報を返します。
	 * コンビキーに対応する Clinfoをキャッシュから参照して返します。<br>
	 * キャッシュに無い場合は新規にインスタンスを生成します。
	 * @param combiKey コンビキー
	 * @return クロージャ情報（存在しない場合はnull）
	 * @see #createClinfo(CombiKey)
	 */
	Clinfo cl(CombiKey combiKey){
		if (!cache.containsKey(combiKey)) cache[combiKey] = createClinfo(combiKey);
		return cache[combiKey];
	}
	
	/**
	 * マップ名の一覧を返します。
	 * @return マップ名の一覧
	 */
	List getMapNames(){
		return dec.findAll(/map#.*/).collect { it.name };
	}
	
	/**
	 * 指定されたマップ配下のクロージャ名の一覧を返します。<br>
	 * 該当するマップが無い場合は空リストを返します。
	 * @param mapName マップ名
	 * @return クロージャ名の一覧
	 */
	List getClosureNames(String mapName){
		return dec.lowers["map#${mapName}"]?.findAll(/closure#.*/)?.collect { it.name } ?: [];
	}
	
	/**
	 * 暗黙的引数の引数名と値を格納したマップを生成します。
	 * @return 暗黙的引数の引数名と値を格納したマップ
	 */
	protected Map getImplicitArgs(){
		Map map = [:];
		map[constants.implicitArgNames.clmap] = this;
		map[constants.implicitArgNames.config] = config;
		return map;
	}
	
	/**
	 * クロージャ情報インスタンスを新規に生成します。<br>
	 * コンビキーに対応するマップ、クロージャが存在しない場合は nullを返します。<br>
	 * スクリプト文字列は以下となります。</p>
	 * <blockquote>
	 * clmap.dec + map.dec + '{' + args + ', Clmap clmap, ConfigObject config ->' + map.prefix + clmap.prefix + closure + clmap.suffix + map.suffix + '}'
	 * </blockquote>
	 * <p>clmapは clmapタグ直下（すべてのマップに共通）、mapは mapタグ直下、closureは closureタグの内容を指します。<br>
	 * dec、prefix、suffixはそれぞれ同じ名前のタグの内容です。<br>
	 * argsは、argsタグの指定があれば「 ->」を末尾に付与した文字列、なければ空文字です。<br>
	 * デフォルトで引数の最後には Clmap, ConfigObjectを渡します。</p>
	 * @param combiKey コンビキー
	 * @return クロージャ情報（存在しない場合はnull）
	 */
	Clinfo createClinfo(CombiKey combiKey){
		// クロージャ名に対応する map要素と closure要素を参照します
		TpacParent map = dec.lowers["map#${combiKey.mapName}"] ?: dec.lowers['map#'];
		if (map == null) return null;
		TpacChild clos = map.lowers["closure#${combiKey.clName}"] ?: map.lowers['closure#'];
		if (clos == null) return null;
		
		// クロージャのソースコードを生成します
		List lines = [];
		if (dec.lowers.containsKey('dec#')) lines << dec.lowers['dec#'].text;
		if (map.lowers.containsKey('dec#'))  lines << map.lowers['dec#'].text;
		String argStr = (map.lowers.containsKey('args#'))? "${map.lowers['args#'].text}, " : '';
		String implicitArgStr = implicitArgs.collect { "${it.value.getClass().name} ${it.key}" }.join(', ');
		lines << "{ ${argStr}${implicitArgStr} ->";
		if (dec.lowers.containsKey('prefix#')) lines << dec.lowers['prefix#'].text;
		if (map.lowers.containsKey('prefix#'))  lines << map.lowers['prefix#'].text;
		lines << clos.text;
		if (dec.lowers.containsKey('suffix#')) lines << dec.lowers['suffix#'].text;
		if (map.lowers.containsKey('suffix#'))  lines << map.lowers['suffix#'].text;
		lines << '}';
		
		// クロージャ情報を生成します
		Clinfo clinfo = new Clinfo(combiKey.toString(), lines.join("\n"));
		clinfo.implicitArgs << this;
		clinfo.implicitArgs << config;
		return clinfo;
	}
}
