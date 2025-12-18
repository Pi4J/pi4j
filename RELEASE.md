# Releasing pi4j

## Create a new release

To release pi4j use the following:

    MVN_PROFILES=-P\!default,native,docker ./autoReleaseBranch minor develop release/<version>

this merges the ``develop`` branch into ``release/<version>``, increments the current tag on the ``release/<version>``
branch and then builds it locally. It uses ``mvn versions:set``
to set the version, and after the version is set, does a test build to make sure all dependencies are really there.

The tag is signed by the configured git signing key. The key used can be checked with:

    git config --get user.signingkey

## Deploy to Maven central

To deploy to maven, use the following commands:

    MVN_PROFILES=-P\!default,native,docker ./deployToMavenCentral.sh <version>

This checks out the given tag and used the ``deploy`` profile to build and push the artifacts to maven central.

## Update snapshot version
To create a new snapshot version on ``develop`` branch, use the following two commands:

    mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${newVersion}"
    mvn clean install -Pnative,docker

Then commit and push the changed ``pom.xml`` files.
