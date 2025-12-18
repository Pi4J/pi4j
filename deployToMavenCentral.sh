#!/bin/bash -e

# Your mvn settings.xml:
# 
# <settings>
#   <servers>
#     <server>
#       <id>oss.sonatype.org</id>
#       <username>...</username>
#       <password>...</password>
#     </server>
#   </servers>
# </settings>
# 

# usage
if [ $# != 1 ] ; then
  echo -e "Usage: ${0} <version>"
  exit 1
fi

gpgKeyName="$(git config --get user.signingkey)"

# Confirm
releaseVersion=${1}
echo -e "INFO: Do you want to deploy version ${releaseVersion} to Maven Central, signing with ${gpgKeyName}? y/n"
read a
if [[ "${a}" != "y" && "${a}" != "Y" ]] ; then
  exit 0;
fi


# Verify tag
echo -e "\nINFO: Verifying tag ${releaseVersion}\n\n"
if ! git tag --verify "${releaseVersion}" ; then
  echo -e "ERROR: Failed to verify tag ${releaseVersion}"
  exit 1
fi
echo
echo

current_branch=$(pwb)

# Checkout tag
echo -e "\nINFO: Checking out tag ${releaseVersion}"
if ! git checkout "${releaseVersion}" ; then
  echo -e "ERROR: Failed to checkout tag ${releaseVersion}"
  exit 1
fi

# cleanup trap
function cleanup {
  if ! git checkout "${current_branch}" ; then
    echo "ERROR: Failed to checkout previous branch ${current_branch}"
    exit 1
  fi
}
trap cleanup EXIT

# Build and deploy
echo -e "\nINFO: Building and deploying to Maven Central..."
export MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.text=ALL-UNNAMED --add-opens=java.desktop/java.awt.font=ALL-UNNAMED"
if ! mvn clean deploy -DskipTests "${MVN_PROFILES}" -Pdeploy "-Dgpg.keyname=${gpgKeyName}" ; then
  echo -e "ERROR: Failed to build and deploy to Maven Central!"
  exit 1
fi


echo -e "\nINFO: Pi4j release ${releaseVersion} deployed to Maven Central."
echo -e "INFO: Remember, it can take up to 10 minutes to be available, and 2 hours for all synching."

echo -e "\nRepository is at: https://oss.sonatype.org/service/local/repositories/releases/content/com/pi4j/"

exit 0
