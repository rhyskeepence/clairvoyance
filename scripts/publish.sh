#!/bin/sh

sbt coveralls clean compile "+ publishSigned" sonatypeReleaseAll
