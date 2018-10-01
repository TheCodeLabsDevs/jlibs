#!/usr/bin/env bash
if hash codesign 2>/dev/null; then
    codesign --remove-signature src/main/resources/libraries/libUtilsNative.dylib
    echo Remove code siging
else
    (>&2 echo "codesign image not found")
fi