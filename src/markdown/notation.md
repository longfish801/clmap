# clmap記法

[TOC levels=2-6]

## 概要

　clmap記法は複数のクロージャをまとめて定義するための記法です。
　インタフェースが同じクロージャをまとめて定義できます。

　clmap記法は tpacを利用したDSLです。tpacの詳細は [tpac](/tpac/)を参照してください。
　以下、tpac記法と重複する説明は省いています。

## 宣言

　宣言のタグ名は「clmap」です。
　可能な子要素は map, dec, prefix, suffixハンドルです。

```
#! clmap:sample
```

## mapハンドル

　mapハンドルは複数のクロージャをまとめます。
　可能な子要素は map, closure, args, dec, prefix, suffixハンドルです。
　子要素のうち、map, closureハンドルは複数記述できます。省略も可能です。
　他は最大ひとつです。

## closureハンドル

　closureハンドルはひとつのクロージャに相当します。
　引数が無い場合は省略できます。
　デフォルトキーにテキストとしてソースコードを記述します。
　可能な子要素はありません。

## argsハンドル

　argsハンドルには引数を定義します。
　同じ mapハンドルに属す closureハンドルに定義されたクロージャすべてに共通する引数を定義します。
　可能な子要素はありません。

## decハンドル

　decハンドルには、クロージャの開始前の内容を記述します。
　たとえば import文の記述に利用してください。
　同じ mapハンドルに属す closureハンドル、および下位の階層にあるすべての closureハンドルに定義されたクロージャすべてに有効です。
　decハンドルが複数ある場合、階層が上位のものから順番に連結します。
　可能な子要素はありません。

## prefixハンドル

　prefixハンドルには、クロージャの引数定義後、処理開始前の内容を記述します。
　同じ mapハンドルに属す closureハンドル、および下位の階層にあるすべての closureハンドルに定義されたクロージャすべてに有効です。
　prefixハンドルが複数ある場合、階層が上位のものから順番に連結します。
　可能な子要素はありません。

## suffixハンドル

　suffixハンドルには、クロージャの処理終了後の内容を記述します。
　同じ mapハンドルに属す closureハンドル、および下位の階層にあるすべての closureハンドルに定義されたクロージャすべてに有効です。
　suffixハンドルが複数ある場合、階層が上位のものから順番に連結します。
　可能な子要素はありません。

## クロージャパス

　tpac文書ではハンドルを参照するとき、タグと名前を連結した文字列をパスに使います。
　clmap記法ではタグを省略したパスを使えます。便宜上、これをクロージャパスと呼びます。
　clmap宣言および mapハンドルの名前は半角スラッシュ(/)を、末尾の closureハンドルは半角シャープ(#)を区切り文字としてください。
　ひとつ上位を参照するときは半角ドット二つ(..)を使用してください。名前を省略したハンドルを指すには半角アンダーバー(_)を使用してください。
　n個のマップが階層を構成し、その下位にある closureハンドルを参照したい場合、絶対クロージャパスのフォーマットは以下となります。

```
/${clmap宣言の名前}/${mapハンドルの名前1}/ ... /${mapハンドルの名前n}#${closureハンドルの名前}
```

## 生成されるコード

　clmap文書のサンプルを以下に示します。

```
#! clmap:sample
#> dec
	import org.apache.commons.lang3.StringUtils
#> prefix
	println 'BGN HERE'
#> suffix
	println 'END HERE'

#> map:map1
#>> args
	String yourName
#>> dec
	String cmnString = 'This is'
#>> prefix
	String result = ''
#>> suffix
	return result
#>> closure:key1
	result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
```

　上記の clmap文書からクロージャパス「/sample/map1#key1」で参照されるクロージャのコードは以下のとおりです。
　読みやすいよう整形しています。

```
import org.apache.commons.lang3.StringUtils

String cmnString = 'This is'

{ String yourName ->
	println 'BGN HERE'
	String result = ''
	result = StringUtils.trim("   ${cmnString} ${yourName}.   ")
	println 'END HERE'
	return result
}
```

以上
