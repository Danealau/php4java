# php4java

PHP4JAVA is a java-library, that integrates PHP-interpreter into Java and allows to run PHP code directly from JAVA.

This repository is based on very old semi-working code from https://github.com/adsr/php4j

# How to use
This repo is a Gradle-project that can be used as subproject in any other Gradle-project

### WARNING: Now only Mac OS X is allowed!

# What do I need to build?

- libxml2 (`brew install libxml2`)
- coreutils (`brew install coreutils`)
- findutils (`brew install findutils`)
- libiconv (`brew install libiconv`)
- OpenJDK 11

# How to build?
To build just run `gradle build` in base project directory
