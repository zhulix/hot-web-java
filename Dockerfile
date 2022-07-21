#指定环境
FROM openjdk:11-jre
#对内暴漏端口
EXPOSE 8080
#campushoy-0.0.1-SNAPSHOT.jar 复制到根目录下，并改名为app.jar
ADD hot-java-0.0.1-SNAPSHOT.jar /app.jar
#根目录创建app.jar文件，上面一行命令已经创建了文件（这个起到修改app.jar创建时间的作用）
RUN bash -c 'touch /app.jar'
#运行命令java -jar /app.jar       也可指定spring开发环境 --srping.profiles.activie=pro
ENTRYPOINT ["java", "-jar", "-Xmx1024m", "-Duser.timezone=GMT+08", "--spring.profiles.active=prod", "/app.jar"]