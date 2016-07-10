#!/bin/sh
 
#cd bin
APPPATH=".";
 
echo $PWD
for k in $PWD/lib/*.jar
do
 APPPATH=$APPPATH:$k
 echo "current dir is $k."
done
APPPATH=$APPPATH:$CLASSPATH
#export $APPPATH

cd bin
java  -cp $APPPATH  com.axon.icloud.person.PersonSearchLuncher /root/com.axon.icloud.cdc/

