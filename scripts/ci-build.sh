#!/bin/sh

sbt test "+ publishSigned" sonatypeReleaseAll
