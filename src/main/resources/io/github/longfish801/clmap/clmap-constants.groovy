
// タグ
tags {
	clmap = 'clmap'
	map = 'map'
	closure = 'closure'
	args = 'args'
	dec = 'dec'
	prefix = 'prefix'
	suffix = 'suffix'
}

// 妥当なタグ
validtags = [ 'map', 'closure', 'args', 'dec', 'prefix', 'suffix' ]

// タグ間の親子関係（キーが親タグ、値は可能な子タグのリスト）
hierarchy {
	clmap = [ 'map', 'dec', 'prefix', 'suffix' ]
	map = [ 'map', 'closure', 'args', 'dec', 'prefix', 'suffix' ]
}

// クロージャパスに使用する文字と解析用の正規表現
clpath {
	level = '/'
	anchor = '#'
	upper = '..'
	noname = '_'
	forserver = [
		/\/([^#\/:]+)/,
		/\/([^#\/:]+)\/(.+)/
	]
	fordec = [
		/([^\/:]+)/,
		/([^#\/:]+)\/(.+)/
	]
	forhandle = [
		/([^\/:]+)/,
		/([^#\/:]+)\/(.+)/
	]
	closures = /([^#\/:]+)#([^#\/:]+)/
}

// クロージャ
closure {
	bgn = '{ '
	arg = ' ->'
	end = '}'
}
