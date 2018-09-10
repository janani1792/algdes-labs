#!/bin/sh
for FILE in *-in.txt

do
	echo $FILE
	base=${FILE%-in.txt}
    java -cp '../stable-matching/out/production/stable-matching:' GS $FILE > $base.B.out.txt # replace with your command!
    diff $base.B.out.txt $base-out.txt
done
