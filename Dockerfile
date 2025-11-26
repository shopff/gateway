FROM registry.cn-guangzhou.aliyuncs.com/szmengran/szmengran-docker-base:jdk17.0.12
MAINTAINER Joe <android_li@sina.cn>
ENV JAR_FILE gateway.jar
#Set Beijing time zone
RUN echo 'Asia/Shanghai'>/etc/timezone
ADD ./target/$JAR_FILE /app/
CMD java -Xmx400m -jar /app/$JAR_FILE
EXPOSE 8701
