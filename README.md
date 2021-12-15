Abstract Game Library
=====================

This software is available under the MIT license. See LICENSE.md.

About
-----

This software was originally created for my thesis to test the UCT
algorithm in various domains.

Domains:
- [backgammon](https://en.wikipedia.org/wiki/Backgammon)
- [biniax](https://en.wikipedia.org/wiki/Biniax)
- [connect4](https://en.wikipedia.org/wiki/Connect4)
- [havannah](https://en.wikipedia.org/wiki/Havannah)
- [hex](https://en.wikipedia.org/wiki/Hex_%28board_game%29)
- [yahtzee](https://en.wikipedia.org/wiki/Yahtzee)

Agents:
- random - agent randomly selects an action

Setup
-----

1. Install gradle
2. Install java sdk 1.8

Build
-----

Create a jar file with no main so it can be used as library.

    ./gradlew clean build

Run Domain Test
---------------

Build jar with com.castlefrog.agl.DomainTest.java as main to test software from console.

1. run `java -jar agl.jar <test_filepath> [output_filepath]`

Some example test files are located in domainTests/resources.
