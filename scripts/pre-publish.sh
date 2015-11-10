#!/bin/sh

openssl des3 -d -salt -in ./scripts/sonatype.asc.enc -out ./scripts/sonatype.asc -k "$SECRET"
