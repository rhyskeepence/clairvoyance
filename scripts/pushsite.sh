#!/bin/sh
git clone -b gh-pages https://github.com/rhyskeepence/clairvoyance.git ./target/gh-pages
cd ./target/gh-pages
git rm -r -f --ignore-unmatch *
cp -r ../site/* .
git add .
git commit -m "Updated site"
git push