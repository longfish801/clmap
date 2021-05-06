import io.github.longfish801.clmap.ClmapServer

def clmap
try {
	clmap = new ClmapServer().soak(new File('src/test/resources/sample.tpac'))
} catch (exc){
	exc.printStackTrace()
}

clmap.cl('/_/const').properties.titleMap = [
	'Kennedy': 0,
	'Thatcher': 1,
	'Windsor': 2
]

assert 'Good morning, Mr.Kennedy.' == clmap.cl('/_/_#morning').call('Kennedy')
assert 'HELLO, MRS.THATCHER.' == clmap.cl('/_/_#noon').call('Thatcher')
assert 'Good night, Ms.Windsor.' == clmap.cl('/_/_#night').call('Windsor')
