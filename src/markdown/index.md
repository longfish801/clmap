# clmap

[TOC levels=2-6]

## 概要

　クロージャを管理します。
　インタフェースが同じクロージャをまとめて定義できます。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 複数のクロージャを clmap記法で定義します。
  clmap記法は [tpac](/maven/tpac/)を利用したDSLです。
* 共通する引数や import文などをまとめて定義できます。
  共通する前処理、後処理をまとめることで依存性注入ができます。

　このライブラリの名称はクロージャ（Closure）＋マップ（Map）に由来しています。

## サンプルコード

　以下に clmap文書のサンプルを示します（src/test/resources/sample.tpac）。

```
#! clmap
#> map:const
#-args
	String time
	String name
#>> closure
	greet = clmap.solve('config:messages').config().greeting[time]
	title = clmap.solve('data:title').dflt[titleMap[name]]
	return "${greet}, ${title}${name}."
#>> data:title
Mr.
Mrs.
Ms.
#>> config:messages
greeting {
	morning = 'Good morning'
	noon = 'Hello'
	night = 'Good night'
}
#> map
#-args
	String name
#-return
	String message
#>> closure:morning
	message = clmap.cl('/dflt/const#dflt').call('morning', name)
#>> closure:noon
	message = clmap.cl('/dflt/const#dflt').call('noon', name)
#-suffix
	message = message.toUpperCase()
#>> closure:night
	message = clmap.cl('/dflt/const#dflt').call('night', name)
```

　上記の clmap文書を読みこんでクロージャを実行し、期待する戻り値を得られるか assertで確認するスクリプトです（src/test/groovy/Sample.groovy）。

```
import io.github.longfish801.clmap.ClmapServer

def clmap
try {
	clmap = new ClmapServer().soak(new File('src/test/resources/sample.tpac'))
} catch (exc){
	println "Failed to soak: ${exc.message}"
	throw exc
}

clmap.cl('/dflt/const').properties.titleMap = [
	'Kennedy': 0,
	'Thatcher': 1,
	'Windsor': 2
]

assert 'Good morning, Mr.Kennedy.' == clmap.cl('/dflt/dflt#morning').call('Kennedy')
assert 'HELLO, MRS.THATCHER.' == clmap.cl('/dflt/dflt#noon').call('Thatcher')
assert 'Good night, Ms.Windsor.' == clmap.cl('/dflt/dflt#night').call('Windsor')
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

0.3.03
: args, dec, prefix, suffix, configハンドルのマップのキー指定はclosureハンドルの名前と一致時のみにしました。
: returnハンドルを追加しました。

0.3.04
: ドキュメントはmavenリポジトリに出力するよう修正しました。

0.3.05
: 宣言に大域変数を設定できるよう対応しました。

0.3.06
: returnハンドルに変数の型も記述するよう変更しました。

0.3.07
: tpac 0.3.13に対応しました。

0.3.08
: clメソッドでクロージャ名に対応するハンドルがないときはデフォルトキーで参照するよう変更しました。

1.1.00
: gradle 7.4の記法に対応しました。

1.1.01
: Clmap、ClmapMap、ClmapClosureクラスについてクローン作成に対応しました。

1.1.02
: GroovyShellはClmapMapではなくClmapで保持するよう修正しました。

1.1.03
: TeaHandle#asStringを利用するよう処理を見直しました。

2.0.00
: args, return, decs, prefix, suffixはハンドルではなくキーで指定するよう修正しました。
: 宣言やclosureハンドルにargs, return, decs, prefix, suffixを指定できるようにしました。

2.0.01
: mapハンドル名がクラスとみなされないようクロージャの名前を修正しました。

2.0.02
: Clmapクラスの clone生成時に propertiesの clmapキーについて複製漏れを修正しました。

