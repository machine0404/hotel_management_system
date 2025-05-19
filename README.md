# hotel_management_system

## 后端运行

首次运行：
mvn install:install-file `
  "-Dfile=D:\DM8\drivers\jdbc\DmJdbcDriver8.jar" `
  "-DgroupId=com.dameng" `
  "-DartifactId=DmJdbcDriver8" `
  "-Dversion=8.1.4.6" `
  "-Dpackaging=jar"
后续运行：
.\mvnw spring-boot:run

##前端运行

首次运行：
npm i
后续运行：
npm run dev