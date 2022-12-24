
// 指定可能なハンドル名
handles = [ 'map', 'closure', 'data', 'config' ]

// 親子関係（キーが親ハンドル、値は可能な子ハンドルのリスト）
hierarchy {
	clmap = [ 'map', 'closure', 'data', 'config' ]
	map = [ 'map', 'closure', 'data', 'config' ]
	closure = []
	data = []
	config = []
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
	noname {
		map = 'dflt'
		cl = ''
	}
	forserver = [
		/\/([^\/:]+)/,
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
	closures = /([^#\/:]+)#([^#\/:]*)/
}

// クロージャ
closure {
	bgn = '{ '
	arg = ' ->'
	argdiv = ', '
	end = '}'
	ret = '	return '
	retdiv = ' '
	lsep = System.lineSeparator()
}

// NULLを意味する文字列
nulltext = '<NULL_TEXT>'
