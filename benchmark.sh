#!/bin/bash
pattern=(
    "S(a|r|g)+on"
    "(miss|call)(ed)*"
    "what"
    "power(ful|less)"
)
N=20
j=0

for file in ressources/*; do
    total_java1=0
    total_egrep=0
    total_java2=0
    CMD1="java -jar build/jar/EGrep.jar \"${pattern[j]}\" $file"
    CMD2="egrep -n \"${pattern[j]}\" $file"
    CMD3="java -jar build/jar/egrepWithPattern.jar \"${pattern[j]}\" $file"
    for ((i=1; i<=N; i++)); do
        start=$(date +%s%3N)
        eval $CMD1 > /dev/null 2>&1
        end=$(date +%s%3N)
        duration_java1=$((end - start))
        total_java1=$((total_java1 + duration_java1))

        start=$(date +%s%3N)
        eval $CMD2 > /dev/null 2>&1
        end=$(date +%s%3N)
        duration_egrep=$((end - start))
        total_egrep=$((total_egrep + duration_egrep))

        start=$(date +%s%3N)
        eval $CMD3 > /dev/null 2>&1
        end=$(date +%s%3N)
        duration_java2=$((end - start))
        total_java2=$((total_java2 + duration_java2))
    done
    average_java1=$((total_java1 / N))
    average_egrep=$((total_egrep / N))
    average_java2=$((total_java2 / N))
    echo "$file ${pattern[j]}"
    echo "Egrep $average_java1"
    echo "egrep $average_egrep"
    echo "EgrepJavaPatternMatcher $average_java2"
    j=$(($j + 1))
done
