import io.github.longfish801.clmap.ClmapServer

def clmap
try {
	clmap = new ClmapServer().soak(new File('src/test/resources/sample.tpac')).cl('/_/_')
} catch (exc){
	exc.printStackTrace()
}

clmap.properties.config = new ConfigObject()

assert 'Hello, World!' == clmap.cl('#_').call('World')
assert 'Hello, world!' == clmap.cl('#key1').call('World')
assert 'Hello, WORLD!' == clmap.cl('#key2').call('World')
assert 'HELLO, WORLD!' == clmap.cl('#key3').call('DUMMY')
