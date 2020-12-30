# clmap

[TOC levels=2-6]

## 概要

　クロージャを管理します。
　インタフェースが同じクロージャをまとめて定義できます。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 複数のクロージャを clmap記法で定義します。
  clmap記法は [tpacライブラリ](/tpac/)を利用したDSLです。
* 共通する引数や import文などをまとめて定義できます。
  共通する前処理、後処理をまとめることで依存性注入ができます。

　このライブラリの名称はクロージャ（Closure）＋マップ（Map）に由来しています。

## サンプルコード

　以下に clmap文書のサンプルを示します（src/test/resources/sample.tpac）。

```
#! clmap
#> data
one
two
three
#> map
#>> args
	int idx
#>> closure
	return clmap.solvePath('/clmap/data').dflt[idx]
#> map:hello
#>> config:messages
hello {
	message = 'Hello, %s!'
}
#>> args
	String yourName
#>> closure:key1
	return String.format(clmap.solvePath('config:messages').config().hello.message, yourName)
#>> closure:key2
	config.msg = 'HELLO, WORLD!'
	return clmap.cl('#key1').call(yourName.toLowerCase())
#>> closure:key3
	return config.msg
```

　上記の clmap文書を読みこんでクロージャを実行し、期待する戻り値を得られるか assertで確認するスクリプトです（src/test/groovy/Sample.groovy）。

```
import io.github.longfish801.clmap.ClmapServer

def clmap
try {
	clmap = new ClmapServer().soak(new File('src/test/resources/sample.tpac')).cl('/_')
} catch (exc){
	exc.printStackTrace()
}

assert 'two' == clmap.call(1)

clmap.cl('hello').properties.config = new ConfigObject()
assert 'Hello, John!' == clmap.cl('hello#key1').call('John')
assert 'Hello, john!' == clmap.cl('hello#key2').call('John')
assert 'HELLO, WORLD!' == clmap.cl('hello#key3').call('John')
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

## 改版履歴

0.3.01
: config, dataハンドルを追加しました。
0.3.02
: args, dec, prefix, suffix, configハンドルのマップにキー指定を可能にしました。
