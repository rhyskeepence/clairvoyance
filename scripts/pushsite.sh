#!/bin/sh
git clone -b gh-pages https://$USERNAME:$PASSWORD@github.com/rhyskeepence/clairvoyance.git ./target/gh-pages
cd ./target/gh-pages
git rm -r -f --ignore-unmatch *
cp -r ../site/* .
git add .
git commit -m "Updated site"
echo "Pushing site to gh-pages"
git push --quiet
cd ..
rm -rf gh-pages