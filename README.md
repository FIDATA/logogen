Logo Generator
==============

This tool takes a source of a logo in SVG format
and produces images and auxilliary files
for a variety of target operating systems, websites and social networks.

It is implemented as a plugin
for [Gradle](https://gradle.org) build tool
and is actually a wrapper
around [ImageMagick](https://www.imagemagick.org/).

The goal of this tool is to produce logos
with the least time and effort.
Its target audience includes open source projects without a budget,
and startup projects.
It can also be used to generate placeholder icons.
This tool doesn't replace a human designer
who can manually polish a logo for specific icon size
and other requirements. It is just a cheap alternative.

Usage
-----

1.  As it is a wrapper around ImageMagick,
    the latter should be installed

2.  Application and configuration of plugin:

    ```
    plugins {
      id 'org.fidata.logogen' version '0.1.0'
    }

    logogen {
      srcFile = file('src/main/svg/logo.svg')
      background = '#CCFFEE'
    }
    ```

    Both configuration options are required.

    `background` option is used in the cases
    when opaque background is required.

Custom Generators
-----------------

User can implement custom logo generator, extending
[LogoGenerator](src/main/groovy/org/fidata/logogen/generators/LogoGenerator.groovy)
class.

Custom generator could be registered in `LogoGenBasePlugin.generators`
collection, then the plugin
will create a task of this type automatically.


------------------------------------------------------------------------
Copyright ©  Basil Peace

This is part of Logo Generator.

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.  This file is offered as-is,
without any warranty.
