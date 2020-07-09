# clmap

[TOC levels=2-6]

## 概要

　クロージャを管理します。
　インタフェースが同じクロージャをまとめて定義できます。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 複数のクロージャを clmap記法で定義します。
  clmap記法は [tpac](/tpac/)を利用したDSLです。
* 共通する引数や import文などをまとめて定義できます。
  共通する前処理、後処理をまとめることで依存性注入ができます。

　このライブラリの名称はクロージャ（Closure）＋マップ（Map）に由来しています。

## サンプルコード

　以下に clmap文書のサンプルを示します（src/test/resources/sample.tpac）。

```
#! clmap
#> map
#>> args
	String yourName
#>> closure
	return "Hello, ${yourName}!"
#>> closure:key1
	return clmap.cl('#_').call(yourName.toLowerCase())
#>> closure:key2
	config.msg = 'HELLO, WORLD!'
	return clmap.cl('#_').call(yourName.toUpperCase())
#>> closure:key3
	return config.msg
```

　上記の clmap文書を読みこんでクロージャを実行し、期待する戻り値を得られるか assertで確認するスクリプトです（src/test/groovy/Sample.groovy）。

```
import io.github.longfish801.clmap.ClmapServer

def clmap
try {
	clmap = new ClmapServer().soak(new File('src/test/resources/sample.tpac')).cl('/_/_')
} catch (exc){
	exc.printStackTrace()
}

clmap.properties.config = new ConfigObject()

assert 'Hello, World!' == clmap.cl('#_').call('World')
assert 'Hello, world!' == clmap.cl('#key1').call('World')
assert 'Hello, WORLD!' == clmap.cl('#key2').call('World')
assert 'HELLO, WORLD!' == clmap.cl('#key3').call('DUMMY')
```

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

## ドキュメント

* [Groovydoc](groovydoc/)
* [clmap記法](notation.html)

## GitHubリポジトリ

* [clmap](https://github.com/longfish801/clmap)

## Mavenリポジトリ

　本ライブラリの JARファイルを [GitHub上の Mavenリポジトリ](https://github.com/longfish801/maven)で公開しています。
　build.gradleの記述例を以下に示します。

```
repositories {
	mavenCentral()
	maven { url 'https://longfish801.github.io/maven/' }
}

dependencies {
	implementation group: 'io.github.longfish801', name: 'clmap', version: '0.3.00'
}
```
