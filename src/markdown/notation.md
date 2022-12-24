# clmap記法

[TOC levels=2-6]

## 概要

　clmap記法は複数のクロージャをまとめて定義するための記法です。
　インタフェースが同じクロージャをまとめて定義できます。

　clmap記法は tpacライブラリを利用したDSLです。tpacライブラリの詳細は [tpac](/maven/tpac/)を参照してください。
　以下、tpac記法と重複する説明は省いています。

## ハンドル
### 宣言

　宣言のタグ名は「clmap」です。
　指定できる子要素は map, closure, data, configハンドルです。
　いずれも省略あるいは複数個の指定が可能です。

```
#! clmap:sample
```

　指定できるキーは[mapハンドル](#mapハンドル)を参照してください。


### mapハンドル

　mapハンドルは複数のクロージャをまとめます。
　指定できる子要素は map, closure, data, configハンドルです。
　いずれも省略あるいは複数個の指定が可能です。

　以下のキーを指定できます。すべて省略可です。

* dec
    - クロージャの開始前の内容を記述します。
      たとえば import文の記述に利用してください。
      テキストで指定します。各行は改行コードで連結します。
* prefix
    - クロージャの引数定義後、処理開始前の内容を記述します。
      テキストで指定します。各行は改行コードで連結します。
* suffix
    - クロージャの処理終了後、return文の前の内容を記述します。
      テキストで指定します。各行は改行コードで連結します。
* args
    - 引数を定義します。
      テキストで指定します。各行は半角カンマ(,)で連結します。
* return
    - 戻り値を記述します。
      テキストで指定します。Groovyのクロージャは戻り値をひとつしか返さないため、二行目以降は無視します。
      指定の仕方は以下の二種類があります。
        - 変数の型と変数名を半角スペースで連結して記述します。
          この場合、変数の宣言とreturn文をクロージャのソースコードに反映します。
        - 変数名のみ記述します。
          これは引数を戻り値に使用する場合を想定しています。
          この場合、return文のみをクロージャのソースコードに反映します。

### closureハンドル

　closureハンドルはひとつのクロージャに相当します。
　デフォルトキーにテキストとしてソースコードを記述します。
　指定できる子要素はありません。

　指定できるキーは[mapハンドル](#mapハンドル)を参照してください。

### dataハンドル

　dataハンドルには、静的データをtpacの形式で記述します。
　指定できる子要素はありません。
　通常のハンドルと同じくテキスト、マップ、スカラー値などのデータを記述できます。

### configハンドル

　configハンドルには、静的データをConfigSlurperの形式で記述します。
　指定できる子要素はありません。
　テキストとして記述します。
　GroovyのConfigSlurperで解析可能な形式で記述してください。

## クロージャパス

　tpac文書ではハンドルを参照するとき、タグと名前を連結した文字列をパスに使います。
　clmap記法ではタグを省略したパスを使えます。便宜上、これをクロージャパスと呼びます。

　n個のマップが階層を構成し、その下位にある closureハンドルを参照したい場合、絶対クロージャパスのフォーマットは以下となります。

```
/${宣言の名前}/${mapハンドルの名前1}/ ... /${mapハンドルの名前n}#${closureハンドルの名前}
```

* 宣言および mapハンドルの名前は半角スラッシュ(/)を区切り文字としてください。
  末尾の closureハンドルは半角シャープ(#)を区切り文字としてください。
* ひとつ上位を参照するときは半角ドット二つ(..)を使用してください。
* 名前を省略したmapハンドルを指すには"dflt"を使用してください。
* 名前を省略したclosureハンドルを指すには空文字を使用してください。
    - 半角シャープは必要なことに注意してください。
      半角シャープがなければmapハンドルを指すと解釈されます。
* 宣言およびmapハンドルの名前に対応するハンドルが存在しないときはnullを返します。
  clousureハンドルの名前に対応するハンドルが存在しないときは、デフォルトキーで参照します。
    - デフォルトキーに対応するハンドルも無いときはnullを返します。

## 大域変数

　宣言（[Clmap](groovydoc/io/github/longfish801/clmap/Clmap.html)クラス）とmapハンドル（[ClmapMap](groovydoc/io/github/longfish801/clmap/ClmapMap.html)クラス）には大域変数を設定できます。
　メンバ変数であるpropertiesマップに格納したキーが大域変数名、値がその変数値となります。

* 宣言に設定した大域変数は、その配下にあるすべてのクロージャで使用できます。
* mapハンドルに設定した大域変数は、その mapハンドル配下のクロージャで使用できます。
  デフォルトの大域変数として "clmap"が格納されます。
  値はmapハンドルに相当する[ClmapMap](groovydoc/io/github/longfish801/clmap/ClmapMap.html)インスタンスです。
  大域変数名が同じならば、より下位のmapハンドルで設定されたものが優先されます。

## クロージャの生成

　引数、戻り値は以下の順番で探し **初めにみつかったもの** が使用されます。
　たとえば mapハンドルに argsキーを指定し、下位の closureハンドルはすべて argsキーを省略すると、生成されるクロージャはすべて同じ引数になります。

1. closureハンドルに指定された args, returnキー
2. closureハンドルが所属する mapハンドルあるいは宣言の args, returnキー
3. さらに上位の mapハンドルあるいは宣言の args, returnキー

　dec, prefix, suffixキーについては階層が上位のものから順番に連結します。
　たとえば宣言に prefixハンドルで処理を記述すると、この clmap文書で生成されるすべてのクロージャの先頭でその処理が実行されます。

　clmap文書のサンプルを以下に示します。

```
#! clmap:sample
#-dec
	import org.apache.commons.lang3.StringUtils
#-prefix
	println 'BGN HERE'
#-suffix
	println 'END HERE'

#> map:map1
#-args
	String yourName
#-return
	String result
#-dec
	String cmnString = 'This is'
#-prefix
	result = ''
#-suffix
	result += '!'
#>> closure:key1
	result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
```

　上記の clmap文書からクロージャパス「/sample/map1#key1」で参照されるクロージャのコードは以下のとおりです。
　読みやすいよう整形しています。

```
import org.apache.commons.lang3.StringUtils

String cmnString = 'This is'

{ String yourName ->
	String result
	println 'BGN HERE'
	result = ''
	result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
	println 'END HERE'
	result += '!'
	return result
}
```
