 #!/bin/bash

CMD1='java -jar build/jar/EGrep.jar "S(a|r|g)*on" ressources/56667-0.txt'
CMD2='egrep "S(a|r|g)*on" ressources/56667-0.txt'
CMD3='java -jar build/jar/Test.jar "S(a|r|g)*on" ressources/56667-0.txt'

N=20

total_java1=0
total_egrep=0
total_java2=0

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

echo "Egrep $average_java1"
echo "egrep $average_egrep"
echo "EgrepJavaPatternMatcher $average_java2"
