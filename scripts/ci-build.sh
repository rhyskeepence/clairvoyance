#!/bin/sh

sbt clean coverage test coverageReport
sbt coverageAggregate
