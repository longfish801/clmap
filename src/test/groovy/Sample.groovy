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
