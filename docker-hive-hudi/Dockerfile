FROM bde2020/hadoop-base:2.0.0-hadoop3.2.1-java8

MAINTAINER Yiannis Mouchakis <gmouchakis@iit.demokritos.gr>
MAINTAINER Ivan Ermilov <ivan.s.ermilov@gmail.com>

# Allow buildtime config of HIVE_VERSION
ARG HIVE_VERSION
ARG HUDI_VERSION
# Set HIVE_VERSION from arg if provided at build, env if provided at run, or default
# https://docs.docker.com/engine/reference/builder/#using-arg-variables
# https://docs.docker.com/engine/reference/builder/#environment-replacement
ENV HIVE_VERSION ${HIVE_VERSION:-3.1.3}
ENV HUDI_VERSION ${HUDI_VERSION:-0.13.0}
ENV POSTGRESQL_VER 42.5.4

ENV HIVE_HOME /opt/hive
ENV PATH $HIVE_HOME/bin:$PATH
ENV HADOOP_HOME /opt/hadoop-$HADOOP_VERSION
ENV HUDI_JARS /var/hoodie/ws/docker/hoodie/hadoop/hive_base/target

WORKDIR /opt

#Install Hive and PostgreSQL JDBC
RUN apt-get update && apt-get install -y wget procps ca-certificates && \
	wget https://archive.apache.org/dist/hive/hive-$HIVE_VERSION/apache-hive-$HIVE_VERSION-bin.tar.gz && \
	tar -xzvf apache-hive-$HIVE_VERSION-bin.tar.gz && \
	mv apache-hive-$HIVE_VERSION-bin hive

RUN mkdir -p $HUDI_JARS && \
  wget https://repo1.maven.org/maven2/org/apache/hudi/hudi-hadoop-mr-bundle/$HUDI_VERSION/hudi-hadoop-mr-bundle-$HUDI_VERSION.jar -O $HUDI_JARS/hudi-hadoop-mr-bundle-$HUDI_VERSION.jar && \
  wget https://repo1.maven.org/maven2/org/apache/hudi/hudi-spark-bundle_2.12/$HUDI_VERSION/hudi-spark-bundle_2.12-$HUDI_VERSION.jar -O $HUDI_JARS/hudi-spark-bundle_2.12-$HUDI_VERSION.jar && \
  wget https://repo1.maven.org/maven2/org/apache/hudi/hudi-hive-sync-bundle/$HUDI_VERSION/hudi-hive-sync-bundle-$HUDI_VERSION.jar -O $HUDI_JARS/hudi-hive-sync-bundle-$HUDI_VERSION.jar && \
  wget https://repo1.maven.org/maven2/org/apache/hudi/hudi-utilities-bundle_2.12/$HUDI_VERSION/hudi-utilities-bundle_2.12-$HUDI_VERSION.jar -O hudi-utilities-bundle_2.12-$HUDI_VERSION.jar

RUN	wget http://jdbc.postgresql.org/download/postgresql-$POSTGRESQL_VER.jar --no-check-certificate -O $HIVE_HOME/lib/postgresql-jdbc.jar && \
	rm apache-hive-$HIVE_VERSION-bin.tar.gz && \
  rm -f $HIVE_HOME/lib/guava-19.0.jar && \
  wget -P $HIVE_HOME/lib https://repo1.maven.org/maven2/com/google/guava/guava/27.0-jre/guava-27.0-jre.jar && \
	apt-get --purge remove -y wget && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/*



#Spark should be compiled with Hive to be able to use it
#hive-site.xml should be copied to $SPARK_HOME/conf folder

#Custom configuration goes here
ADD conf/hive-site.xml $HIVE_HOME/conf
ADD conf/beeline-log4j2.properties $HIVE_HOME/conf
ADD conf/hive-env.sh $HIVE_HOME/conf
ADD conf/hive-exec-log4j2.properties $HIVE_HOME/conf
ADD conf/hive-log4j2.properties $HIVE_HOME/conf
ADD conf/ivysettings.xml $HIVE_HOME/conf
ADD conf/llap-daemon-log4j2.properties $HIVE_HOME/conf

ENV HUDI_HADOOP_BUNDLE=$HUDI_JARS/hudi-hadoop-mr-bundle-$HUDI_VERSION.jar
ENV HUDI_SPARK_BUNDLE=$HUDI_JARS/hudi-spark-bundle_2.12-$HUDI_VERSION.jar
ENV HUDI_HIVE_SYNC_BUNDLE=$HUDI_JARS/hudi-hive-sync-bundle-$HUDI_VERSION.jar
ENV HUDI_UTILITIES_BUNDLE=$HUDI_JARS/hudi-utilities-bundle_2.12-$HUDI_VERSION.jar

COPY startup.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/startup.sh

COPY entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh

# NOTE: This is the only battle-proven method to inject jars into Hive CLI
ENV AUX_CLASSPATH=file://${HUDI_HADOOP_BUNDLE}

EXPOSE 10000
EXPOSE 10002

ENTRYPOINT ["entrypoint.sh"]
CMD startup.sh
