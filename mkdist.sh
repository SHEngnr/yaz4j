#!/bin/sh
V=1.5
git archive --format=tar --prefix=yaz4j-$V/ v$V |gzip >yaz4j-$V.tar.gz
