@GrabResolver(name = 'longfish801 github repositry', root = 'https://longfish801.github.io/maven/')
@Grab('io.github.longfish801:clmap:0.1.00')
@GrabExclude('org.codehaus.groovy:groovy-all')

import io.github.longfish801.clmap.Clmap;

Clmap clmap = new Clmap(new File('sample.tpac'));

assert 'Hello, World!' == clmap.cl('map1').call('World');
assert 'Hello, world!' == clmap.cl('map1#key1').call('World');
assert 'Hello, WORLD!' == clmap.cl('map1#key2').call('World');
assert 'HELLO, WORLD!' == clmap.cl('map1#key3').call('DUMMY');
