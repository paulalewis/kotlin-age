Abstract Game Library
=====================

This sofware is available under the MIT license. See LICENSE.txt.

About
-----

This library was created for my thesis to test the UCT algorithm in various
domains, but it could be expanded to include other AI algorithms and domains.

List of working domains:
- backgammon
- biniax
- havannah
- hex

List of working agents:
- random - basic agent randomly selects an action
- uct - agent based on UCT algorithm with some additions
- console - agent for human input from console
- external - agent for input from external interfaces such as a gui

Setup
-----

1. Install ant
2. clean the project `ant clean`
3. build the project `ant install`

Build
-----

Create a jar file with no main so it can be used as library.

1. clean the project `ant clean`
2. build the project `ant dist`

Run Domain Test
---------------

Build jar with DomainTest.java as main to test software from console.

1. clean the project `ant clean`
2. build the project `ant install`
3. run `java -jar agl.jar <test_filepath> [output_filepath]`

Some example test files are located in res/domainTests/.
