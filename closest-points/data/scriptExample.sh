#!/bin/sh

rm output.txt

for FILE in *-tsp.txt

do
	echo $FILE
	java -cp "../closest-points-divide-conquer/out/production/closest-points-divide-conquer:" dk.itu.b.CP $FILE >> output.txt
done

diff output.txt closest-pair-out.txt
