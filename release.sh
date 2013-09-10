#!/bin/bash

OG=`/usr/bin/cut -d"\"" -f2 conf/version.conf`

if [ ! -z "$(/usr/bin/git status --porcelain)" ]; then
    echo "There are uncommited changes. Please commit and retry."
    /usr/bin/git status
    exit 1
fi

if [[ ! ${OG} =~ .*-SNAPSHOT$ ]]; then
    echo "${OG} is not a -SNAPSHOT version"
    exit 1
fi

echo "Original Version: ${OG}"

# remove the -SNAPSHOT to get the deploy version
VERSION=`echo ${OG} | cut -d'-' -f 1`

echo "Deploying Version: ${VERSION}"

# update the version info, check in, tag
echo "application.version=\"${VERSION}\"" > conf/version.conf
/usr/bin/git add conf/version.conf
/usr/bin/git commit -m "Creating Version ${VERSION}"
/usr/bin/git tag -a v${VERSION} -m 'Version ${VERSION}'

# push to heroku
/usr/bin/git push -f heroku v${VERSION}^{}:master

# create new -SNAPSHOT version
NEW=`expr ${VERSION} + 1`
NEW_SNAPSHOT=`echo "${NEW}-SNAPSHOT"`

echo "New Working Version: ${NEW_SNAPSHOT}"

# update the version info, check in, push to bitbucket
echo "application.version=\"${NEW_SNAPSHOT}\"" > conf/version.conf
/usr/bin/git add conf/version.conf
/usr/bin/git commit -m "Creating Working Version ${VERSION}"
/usr/bin/git push --tags



