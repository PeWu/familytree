familytree
==========

A scala library for generating family trees grom GEDCOM files.

This has been started as a rewrite of the [graphical genealogy tree report](http://genj.sourceforge.net/wiki/en/reports/graphicaltree) in [GenealogyJ](http://genj.sourceforge.net/). Having a separate independent library will make it possible not only to include it in GenealogyJ as a report but also use it in other projects such as [Ancestris](http://www.ancestris.org/). The library can also be used on the server side in genealogy web applications.

My personal use of the library will be as a command line utility for generating a set of PDF files that I share with my family.

[![Build Status](https://secure.travis-ci.org/PeWu/familytree.png)](http://travis-ci.org/PeWu/familytree)

Examples
========

See the [Wiki](https://github.com/PeWu/familytree/wiki/Family-tree-examples) for [example outputs](https://github.com/PeWu/familytree/wiki/Family-tree-examples) of this library.

[
![karol_wojtyla](http://imageshack.com/a/img577/3361/f89c.png)]
(https://github.com/PeWu/familytree/wiki/Family-tree-examples)

Usage
=====

Standalone application
----------------------

1. Download [familytree-assembly-20140205.jar](http://dl.bintray.com/pewu/maven/familytree-assembly-20140205.jar)
2. Run:
```sh
$ java -jar familytree-assembly-20140205.jar input.ged output.pdf
```

Library
-------

TODO

License
=======

Copyright (c) 2014 Przemek Wiech

Published under Apache License, Version 2.0
