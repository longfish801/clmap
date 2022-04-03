# clmap

## Overview

Manage closures.  
You can define closures with the same interface collectively.

This is individual development, for self-learning.  
No support such as troubleshooting, answering inquiries, and so on.

## Features

* Define closures using the clmap notation.  
  The clmap notation is a DSL using [tpac](/maven/tpac/).
* You can define common arguments, import statements, etc. together.  
  You can do dependency injection with common pre-processing and post-processing together.

The name of this library comes from Closure + Map.

## Sample Code

Here is a sample clmap document (src/test/resources/sample.tpac).

```
#! clmap
#> map
#>> args
	String yourName
#>> closure
	return "Hello, ${yourName}!"
#>> closure:key1
	return clmap.cl('#dflt').call(yourName.toLowerCase())
#>> closure:key2
	config.msg = 'HELLO, WORLD!'
	return clmap.cl('#dflt').call(yourName.toUpperCase())
#>> closure:key3
	return config.msg
```

A script that reads the above clmap document, executes the closure and checks with assert to see if it gives the expected return value (src/test/groovy/Sample.groovy).

```
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
```

This sample code is executed in the execSamples task, see build.gradle.

## Next Step

Please see the [documents](https://longfish801.github.io/maven/clmap/) for more detail.
