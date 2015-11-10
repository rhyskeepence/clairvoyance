#!/bin/sh

sbt clean compile "+ publishSigned" sonatypeReleaseAll
