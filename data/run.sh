#!/bin/sh

java -cp .:mysql-connector-java.jar:mybatis-3.2.2.jar:log4j-1.2.17.jar:sqljdbc4.jar DBTransfer 2> run.log 

