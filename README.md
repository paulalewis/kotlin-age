Abstract Game Library
=====================

This sofware is available under the MIT license. See LICENSE.txt.

About
-----

Library for creating abstract games and agents to play those games.
This software can be used to test the strength and speed of various
algorithms in various domains.

List of working domains:
- backgammon
- biniax
- havannah
- hex

Liste of working agents:
- console
- external
- random
- uct

Setup
-----

1. Install ant
2. clean the project `ant clean`
3. build the project `ant install`

Run
---

Run DomainTest.java

    java -jar agl.jar <test_filepath> [output_filepath]

Some example test files are located in res/domainTests/.

Create Library
--------------

Creates a jar file with no main so it can be used as library.
    
    ant dist
