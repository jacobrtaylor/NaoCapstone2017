#!/bin/bash

# Get the hash of the Git commit currently in use.
a=$(git branch -v |
    grep '^\*' |
    sed -e 's/  */ /g' \
        -e 's/(.*)/xxxx/' |
    cut -d ' ' -f 3)

# Get an asterisk if there are uncommited files in the current directory or
# below, or nothing if there are not.
b=$(git status --porcelain . |
    sed 's/.*/\*/' |
    uniq)

# Write the hash and possible asterisk to the BUILD_INFO file.
echo "$a$b" >BUILD_INFO
