#!/bin/bash
# Script to add, commit, and push changes automatically

if [ -z "$1" ]; then
  echo "Usage: ./git-commit-push.sh \"commit message\""
  exit 1
fi

git add .
git commit -m "$1"
git push --set-upstream origin $(git rev-parse --abbrev-ref HEAD)
