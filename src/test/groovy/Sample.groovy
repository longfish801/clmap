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
