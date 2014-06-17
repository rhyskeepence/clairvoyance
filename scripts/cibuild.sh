#!/bin/sh

sbt test "+ publishSigned" sonatypeReleaseAll

sbt make-site
./scripts/pushsite.sh