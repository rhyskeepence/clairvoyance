#!/bin/sh

pip install --user codecov && codecov
sbt coveralls clean compile "+ publishSigned" sonatypeReleaseAll
