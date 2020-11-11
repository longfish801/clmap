
// 妥当なタグ
validtags = [ 'map', 'closure', 'args', 'dec', 'prefix', 'suffix', 'data', 'config' ]

// タグ間の親子関係（キーが親タグ、値は可能な子タグのリスト）
hierarchy {
	clmap = [ 'map', 'dec', 'prefix', 'suffix', 'data', 'config' ]
	map = [ 'map', 'closure', 'args', 'dec', 'prefix', 'suffix', 'data', 'config' ]
}

// マップ
map {
	// 大域変数のデフォルト変数名
	dflt = 'clmap'
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