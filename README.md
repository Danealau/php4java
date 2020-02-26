# php4java
PHP4JAVA is a java-library, that integrates PHP-interpreter into Java and allows to run PHP code directly from JAVA.<br>
This repository is based on very old semi-working code from https://github.com/adsr/php4j<br>
Now it's a Gradle-project that can be used as subproject in any other Gradle-project.<br>

**WARNING: for now only Mac OS X is supported!**

## How to use in java
1. Create PHP-instance
`var php = php4java.PhpInstanceFactory.CreateInstance();` - this returns `IPhp` (PHP instance interface).<br>
Now you have separate PHP instance with it's own cURL, OpenSSL and other statically linked extensions.<br>
2. Execute any PHP-code
- `php.execString("include \"myScript.php\"");` - this includes and executes `myScript.php`.
- `var result = php.execString("return $a + $b;")` - this returns result of `$a + $b` in `IPhpVal` (PHP value interface).
3. Use returned value
- `String text = php.execString("return 10;").asString();` - returns String `"10"`.
- `Long value = php.execString("return 10;").asLong();` - returns Long value `10`.
- `Boolean value = php.execString("return 10;").asBoolean();` - returns Boolean value `true`.
- and others...

## Is it a single PHP instance for Java?
No. Each call `php4java.PhpInstanceFactory.CreateInstance();` creates new independent PHP copy with it's own variable pool and own extensions copies. If one PHP crashes, other instances still can work.<br>
**Even better**: each PHP-instance **is thread-safe**! You can use each instance in different threads simultaneously, it's ok.<br>

## How does `php.execString(...)` work?
It uses PHP's `eval(...)` to execute commands. It's not good when you use `eval(...)` in PHP-code, but for this project it's almost the only option.<br>
For example, if you write `php.execString("abcdefg")`, PHP executes this: `eval('abcdefg')`.<br>
**Warning**: it's. very dangerous, be careful! Do not use symbols like `'` or make escape sequences like this: `php.execString("return \\'abcdefg\\';")` => `eval('return \'abcdefg\';')`.<br>

## What if PHP crashes or throws exception (or we have syntax errors in our expression/php-file)?
PHP-instance's `execString(...)` throws `Php4JavaException` with message of your PHP-exception (including file name and line, where exception was thrown) or with info, that exception was unknown (for example, if PHP's `Zend` engine fails or anything else).<br>

**Example**:
```
var php = php4java.PhpInstanceFactory.CreateInstance();
try
{
    var result = php.execString("Here we do something wrong!");
}
catch (php4java.Php4JavaException exception)
{
    System.out.println(exception.getMessage());
}
```

## What do I need to build?
- Homebrew
- OpenJDK 11 or higher
- Gradle
- autoconf 2.68 or higher (`brew install autoconf`)
- automake 1.7 or higher (`brew install automake`)
- libtoolize 1.4.2 or higher (`brew install libtool`)
- Bison 3.0.0 or higher (`brew install bison`)
- re2c 0.13.4 or higher (`brew install re2c`)
- gxargs (`brew install findutils`)
- pkg-config (`brew install pkg-config`)
- libiconv (`brew install libiconv`)
- oniguruma (`brew install oniguruma`)
- libxml2 (`brew install libxml2`)
- nghttp2 (`brew install nghttp2`)
- libidn2 (`brew install libidn2`)
- rtmpdump (`brew install rtmpdump`)
- brotli (`brew install brotli`)

## How to build?
To build just run `gradle build` in base project directory