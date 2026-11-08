#!/usr/bin/env bash
# Compiles Sample.cs and SampleFacade.cs to the assemblies of the summary test, against a .NET reference pack:
# build.sh <directory of System.Runtime.dll>
set -euo pipefail
here="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
csc="$(ls -d "$(dirname "$(readlink -f "$(command -v dotnet)")")"/sdk/*/Roslyn/bincore/csc.dll | tail -1)"
references=()
for library in "$1"/*.dll; do
    references+=("-reference:$library")
done
dotnet exec "$csc" -nologo -target:library -nostdlib -noconfig -deterministic "${references[@]}" -out:"$here/Sample.dll" "$here/Sample.cs"
dotnet exec "$csc" -nologo -target:library -nostdlib -noconfig -deterministic "${references[@]}" -reference:"$here/Sample.dll" -out:"$here/SampleFacade.dll" "$here/SampleFacade.cs"
