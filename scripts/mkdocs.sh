#!/bin/bash
# Clone gh-pages branch
git clone --branch=gh-pages --depth=1 "git@github.com:${GITHUB_REPOSITORY}" gh-pages || exit 1

# If its to the master branch we build to 'latest'
if [ "${BRANCH_NAME}" = "master" ]; then
  echo "$0: Deploying Documents to gh-pages/en/latest"
  mkdir -p "gh-pages/en/latest" || exit 1
  mkdocs build --clean --strict --site-dir="gh-pages/en/latest" || exit 1
fi

# If its a tagged build, build to the tag version and link stable
if [ "${GITHUB_REF}" =~ ^refs/tags/v.+ ]; then
  echo "$0: Deploying Documents to gh-pages/en/${VERSION}"
  mkdir -p "gh-pages/en/${VERSION}" || exit 1
  mkdocs build --clean --strict --site-dir="gh-pages/en/${VERSION}" || exit 1

  echo "$0: Linking gh-pages/stable to gh-pages/en/${VERSION}"
  (cd "gh-pages/en" && ln -sfn "${VERSION}" stable)
fi

# Build versions.json from english
cat <<EOF > gh-pages/versions.json
[
  {"version": "latest", "title": "latest", "aliases": []}
EOF
for i in $(ls -1 gh-pages/en/ | grep -v latest | grep -v stable | sort -n); do
  echo -n "  ,{\"version\": \"${i}\", \"title\": \"${i}\", \"aliases\": [" >> gh-pages/versions.json
  if [ "${i}" == "${VERSION}" ];then
    echo -n "\"stable\"" >> gh-pages/versions.json
  fi

  echo "]}" >> gh-pages/versions.json
done
echo "]" >> gh-pages/versions.json

# Commit
(
  cd gh-pages || exit 1
  git add -f .
  git commit -m "Deploy Documents: Build ${VERSION}"
  git push -fq origin gh-pages > /dev/null
)
