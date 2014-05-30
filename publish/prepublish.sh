#!/bin/bash
openssl des3 -d -salt -in ./publish/sonatype.asc.enc -out ./publish/sonatype.asc -k "$SECRET"