### Using Ports
SSH         18000
JENKINS     18010
HTTP        18020

MariaDB     19000
PostgreSQL  19010
MongoDB     19020
Redis       19030



### SSH server
$ sudo apt update
$ sudo apt install net-tools
$ sudo apt install openssh-server

sudo vi /etc/ssh/sshd_config
------------------
PORT 18000
------------------

sudo vi /etc/hosts.allow
------------------
sshd: 192.168.
------------------

sudo vi /etc/hosts.deny
------------------
sshd: ALL
------------------



### VScode Without password
In Linux
cd ~
rm -rf .ssh
mkdir .ssh

In Windows
ssh-keygen -t rsa -b 4096
cd /d C:\Users\user\.ssh
copy id_rsa.pub authorized_keys
scp -P 22 authorized_keys user@192.168.200.124:/home/user/.ssh

In VS code setting - C:\Users\user\.ssh\config
Host HostName
  HostName 192.168.200.112
  User username
  Port 18000
  IdentityFile C:\Users\user\.ssh\id_rsa



### mariaDB
$ sudo apt update
$ sudo apt install mariadb-server mariadb-client
$ sudo mariadb-secure-installation

Switch to unix_socket authentication [Y/n] n

Change the root password? [Y/n] y
New password:
Re-enter new password:
Password updated successfully!
Reloading privilege tables..
 ... Success!

Remove anonymous users? [Y/n]
 ... Success!

Disallow root login remotely? [Y/n]
 ... Success!

Remove test database and access to it? [Y/n]
 - Dropping test database...
 ... Success!
 - Removing privileges on test database...
 ... Success!

Reload privilege tables now? [Y/n]
 ... Success!

$ sudo vi /etc/mysql/mariadb.conf.d/50-server.cnf
------------------
[mysqld]
------------------
[mysqld]
port           = 19000
------------------

------------------
bind-address            = 127.0.0.1
------------------
# bind-address            = 127.0.0.1
bind-address            = 0.0.0.0
------------------


$ sudo systemctl enable mariadb
$ sudo systemctl restart mariadb

$ sudo netstat -tulpn

Switch to unix_socket authentication [Y/n]
Y -> $ sudo mariadb
n -> $ sudo mariadb -u root -p

use mysql;
CREATE DATABASE databasename;
SHOW DATABASES;
DROP DATABASE databasename;

CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
CREATE USER 'username'@'%' IDENTIFIED BY 'password';

SELECT host, user, password FROM user;

DROP USER 'username'@'localhost';
DROP USER 'username'@'%';

GRANT ALL PRIVILEGES ON databasename.* TO 'username'@'localhost';
GRANT ALL PRIVILEGES ON databasename.* TO 'username'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO 'username'@'%';

FLUSH PRIVILEGES;



### postGreSQL
$ sudo apt update
$ sudo apt install postgresql postgresql-contrib

$ sudo -i -u postgres
$ psql

CREATE USER usename PASSWORD 'password' superuser;
\du

CREATE DATABASE databasename OWNER username;
\l

\q

$ sudo vi /etc/postgresql/14/main/pg_hba.conf
------------------
# "local" is for Unix domain socket connections only
local   all             all                                     peer
# IPv4 local connections:
host    all             all             127.0.0.1/32            scram-sha-256
------------------
# "local" is for Unix domain socket connections only
# local   all             all                                     peer
local   all             all                                     md5
# IPv4 local connections:
# host    all             all             127.0.0.1/32            scram-sha-256
host    all             all             0.0.0.0/0            scram-sha-256
------------------

$ sudo vi /etc/postgresql/14/main/postgresql.conf
------------------
#listen_addresses = 'localhost'         # what IP address(es) to listen on;
------------------
#listen_addresses = 'localhost'         # what IP address(es) to listen on;
listen_addresses = '*'                  # what IP address(es) to listen on;
------------------

------------------
port = 5432                           # (change requires restart)
------------------
# port = 5432                         # (change requires restart)
port = 19010                          # (change requires restart)
------------------


$ sudo systemctl enable postgresql
$ sudo systemctl restart postgresql

$ psql -U username -d databasename



### MongoDB
$ wget -qO - https://www.mongodb.org/static/pgp/server-6.0.asc | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/mongodb.gpg
$ echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list
$ sudo apt update
$ sudo apt install -y mongodb-org

$ sudo vi /etc/mongod.conf
# network interfaces
------------------
net:
  port: 27017
  bindIp: 127.0.0.1
------------------
net:
#  port: 27017
#  bindIp: 127.0.0.1
  port: 19020
  bindIp: 0.0.0.0
------------------

------------------
#security:
------------------
security:
  authorization: 'enabled'
------------------


$ sudo systemctl enable mongod
$ sudo systemctl restart mongod

$ mongosh --port 19020
use admin

db.createUser({ 
    user: 'username',
    pwd: 'password',
    roles: ['root']
});

$ mongosh --port 19020 -u username



### MongoDB Problem
$ mongosh
Current Mongosh Log ID: 656c5b0e587290789b322eda
Connecting to:          mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+2.1.0
MongoNetworkError: connect ECONNREFUSED 127.0.0.1:27017
$ sudo systemctl status mongod
...
Process: 6621 ExecStart=/usr/bin/mongod --config /etc/mongod.conf (code=exited, status=14)
...
### Solution
$ cd /tmp
$ sudo chown mongodb:mongodb mongodb-27017.sock


$ sudo systemctl status mongod
...
Process: 940 ExecStart=/usr/bin/mongod --config /etc/mongod.conf (code=dumped, signal=ILL)
...
### Solution


### docker
$ sudo apt update
$ sudo apt install -y ca-certificates curl software-properties-common apt-transport-https gnupg lsb-release
$ sudo mkdir -p /etc/apt/keyrings
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
$ echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
$ sudo apt update
$ sudo apt install docker-ce docker-ce-cli containerd.io

$ sudo curl -L https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
$ sudo chmod +x /usr/local/bin/docker-compose

$ sudo usermod -aG docker $USER



### git
sudo apt install git



### Java JDK
$ sudo apt install openjdk-17-jdk

$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java 91081
$ sudo update-alternatives --config java

~/.bashrc
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$PATH:$JAVA_HOME/bin

$ echo $JAVA_HOME
/usr/lib/jvm/java-17-openjdk-amd64



### Jenkins
Google "download jenkins .deb"
https://pkg.jenkins.io/debian-stable/direct/
wget https://pkg.jenkins.io/debian-stable/binary/jenkins_2.346.3_all.deb
sudo dpkg -i jenkins_2.346.3_all.deb
wget https://pkg.jenkins.io/debian-stable/direct/jenkins_2.426.2_all.deb
sudo dpkg -i jenkins_2.426.2_all.deb

### Problem
Waiting for cache lock: Could not get lock /var/lib/dpkg/lock-frontend. It is held by process 5572

### Solution
$ sudo rm /var/lib/apt/lists/lock
$ sudo rm /var/cache/apt/archives/lock
$ sudo rm /var/lib/dpkg/lcok*

$ sudo dpkg --configure -a
$ sudo apt update



$ sudo wget -O /usr/share/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
$ echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
$ sudo apt update
$ sudo apt install jenkins



$WORKSPACE
    /var/lib/jenkins/workspace/ProjectName

sudo vi /usr/lib/systemd/system/jenkins.service
search "PORT"
    /PORT
------------------
Environment="JENKINS_PORT=18010"
------------------

$ sudo vi /etc/sudoers
------------------
# User privilege specification
root    ALL=(ALL:ALL) ALL
jenkins ALL=(ALL) NOPASSWD: ALL
------------------

$ sudo systemctl daemon-reload
$ sudo systemctl restart jenkins

$ sudo vi /var/lib/jenkins/secrets/initialAdminPassword


Dashboard > Manage Jenkins > Tools
    JDK installations
        JDK
            Name : openjdk8
            JAVA_HOME : /usr/lib/jvm/java-8-openjdk-amd64
        JDK
            Name : openjdk11
            JAVA_HOME : /usr/lib/jvm/java-11-openjdk-amd64
        JDK
            Name : openjdk17
            JAVA_HOME : /usr/lib/jvm/java-17-openjdk-amd64

Dashboard > Manage Jenkins > Plugins
    Install Locale
Dashboard > Manage Jenkins > System >
    Locale
        Default Language : en
        Check : Ignore browser preference and force this language to all users
Dashboard > Manage Jenkins > System >
    Shell
        Shell executable : /usr/bin/bash


Forgetting Account
    sudo vi /var/lib/jenkins/config.xml



Freestyle Project
    Git
        Repositories
            Repository URL
                https://github.com/someone/sample.git
        Branches to build
            Branch Specifier (blank for any)
                */master
    build
        Invoke Gradle script
            Use Gradle Wrapper
                Check : Make gradlew executable
                Wrapper location
                    ${workspace}
        Tasks
            clean
            build
            -x test
            -x asciidoctor
            --info

        Execute shell
            Command
                java -version
                JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
                if [[ ${JAVA_HOME} == *"jre" ]]; then
                    JAVA_HOME=$(dirname ${JAVA_HOME})
                fi
                echo JAVA_HOME : ${JAVA_HOME}
                COMMAND_JAVA=$(readlink -f $(which java))
                echo COMMAND_JAVA : ${COMMAND_JAVA}

                sudo bash ./bash_deploy/deploy.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE}

            Command
                java -version
                JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
                if [[ ${JAVA_HOME} == *"jre" ]]; then
                    JAVA_HOME=$(dirname ${JAVA_HOME})
                fi
                echo JAVA_HOME : ${JAVA_HOME}
                COMMAND_JAVA=$(readlink -f $(which java))
                echo COMMAND_JAVA : ${COMMAND_JAVA}
                NAME_PROJECT="template"
                DIRECTORY="/home/JENKINS/template/api/"
                FILENAME="loginJwtRSA-0.0.1-SNAPSHOT.jar"
                PROFILE="develop"

                sudo bash ./bash_deploy/stop.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                sleep 1
                sudo bash ./bash_deploy/copy.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                sleep 1
                sudo bash ./bash_deploy/start.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                sleep 1

### Process leaked file descriptors. See https://www.jenkins.io/redirect/troubleshooting/process-leaked-file-descriptors for more information

### warning: unknown enum constant javax.annotation.meta.When.MAYBE
add in file build.gradle
    dependencies {
        implementation 'com.google.code.findbugs:jsr305:3.0.2'
    }


Pipeline Project
    Pipeline
        Definition
            SELECT : Pipeline script
            Script
                pipeline {
                    agent any

                    stages {
                        stage('Clone') {
                            steps {
                                git 'https://github.com/blank-popup/spring-boot-login-jwt-rsa-mybatis.git'
                            }
                        }
                        stage('Configure') {
                            steps{
                                sh 'chmod +x ./gradlew'
                                sh 'chmod +x ./bash_deploy/*'
                                sh '''
                                    APP_YML_NEW=/home/JENKINS/template/test_gradle/etc/application.yml
                                    APP_YML_OLD=/var/lib/jenkins/workspace/Template/src/main/resources/application.yml
                                    if [ -e ${APP_YML_NEW} ]; then
                                        if [ -e ${APP_YML_OLD} ]; then
                                            rm ${APP_YML_OLD}
                                        fi
                                        cp ${APP_YML_NEW} $(dirname ${APP_YML_OLD})
                                    fi
                                '''
                            }
                        }
                        stage('Gradle Build') {
                            tools {
                                jdk("openjdk8")
                            }
                            steps {
                                sh '''
                                    java -version
                                    ./gradlew clean build --warning-mode all
                                '''
                //                sh '''
                //                    java -version
                //                    bash ./gradlew clean build -Ptarget=develop -x test -x asciidoctor --debug
                //                '''
                            }
                        }
                        stage('Deploy') {
                            tools {
                                jdk("openjdk8")
                            }
                            steps {
                                sh '''
                                    export JENKINS_NODE_COOKIE=dontKillMe

                                    java -version
                                    if [[ ${JAVA_HOME} == *"jre" ]]; then
                                        JAVA_HOME=$(dirname ${JAVA_HOME})
                                    fi
                                    echo JAVA_HOME : ${JAVA_HOME}
                                    COMMAND_JAVA=$(readlink -f $(which java))
                                    echo COMMAND_JAVA : ${COMMAND_JAVA}

                                    sudo -u nova ./bash_deploy/deploy.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE}
                                '''
                //                sh '''
                //                    java -version
                //                    if [[ ${JAVA_HOME} == *"jre" ]]; then
                //                        JAVA_HOME=$(dirname ${JAVA_HOME})
                //                    fi
                //                    echo JAVA_HOME : ${JAVA_HOME}
                //                    COMMAND_JAVA=$(readlink -f $(which java))
                //                    echo COMMAND_JAVA : ${COMMAND_JAVA}
                //                    NAME_PROJECT="template"
                //                    DIRECTORY="/home/JENKINS/template/api/"
                //                    FILENAME="loginJwtRSA-0.0.1-SNAPSHOT.jar"
                //                    PROFILE="develop"
                //
                //                    sudo -u nova ./bash_deploy/stop.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                //                    sleep 1
                //                    sudo -u nova ./bash_deploy/copy.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                //                    sleep 1
                //                    sudo -u nova ./bash_deploy/start.sh ${JAVA_HOME} ${COMMAND_JAVA} ${WORKSPACE} ${NAME_PROJECT} ${DIRECTORY} ${FILENAME} ${PROFILE}
                //                    sleep 1
                //                '''
                            }
                        }
                //        stage('Send JAR File To Deploy Server'){
                //            steps{
                //                sh 'scp -P 00000 -i /var/lib/jenkins/.ssh/id_rsa.pem ./build/libs/mongo-log-0.0.1-SNAPSHOT.jar username@example.com:/var/www/mongo-log/mongo-log-0.0.1-SNAPSHOT.jar'
                //            }
                //        }
                //        stage('Deploy Using systemd'){
                //            steps{
                //                sh 'ssh -i /var/lib/jenkins/.ssh/id_rsa.pem username@example.com "sudo systemctl restart mongo-log.service"'
                //            }
                //        }
                    }
                }

            CHECK : Use Groovy Sandbox


vi ./bash_deployh/deploy.sh
------------------
#!/usr/bin/bash

JAVA_HOME=$1
COMMAND_JAVA=$2
WORKSPACE=$3
NAME_PROJECT="template"
DIRECTORY="/home/JENKINS/template/api/"
FILENAME="loginJwtRSA-0.0.1-SNAPSHOT.jar"
VALUE_PROFILE="develop"
FILEPATH=${DIRECTORY}${FILENAME}
KEY_PROFILE="spring.profiles.active"
OPTION="-D${KEY_PROFILE}=${VALUE_PROFILE}"

${COMMAND_JAVA} -version

echo USER : $USER
echo JAVA_HOME : ${JAVA_HOME}
echo COMMAND_JAVA : ${COMMAND_JAVA}
echo WORKSPACE : ${WORKSPACE}
echo NAME_PROJECT : ${NAME_PROJECT}
echo DIRECTORY : ${DIRECTORY}
echo FILENAME : ${FILENAME}
echo VALUE_PROFILE : ${VALUE_PROFILE}
echo FILEPATH : ${FILEPATH}
echo KEY_PROFILE : ${KEY_PROFILE}
echo OPTION : ${OPTION}

echo ========== Terminating ${NAME_PROJECT} process ==========

PID=`ps -ef | grep ${FILEPATH} | grep ${KEY_PROFILE}=${VALUE_PROFILE} | grep -v grep | awk '{print $2}'`
echo PID : ${PID}

if [ -n "${PID}" ]
then
    kill -9 ${PID}
    echo process is killed.
else
    echo running process not found.
fi

echo ========== Terminated ${NAME_PROJECT} process ==========

sleep 1

echo ========== copying ${NAME_PROJECT} file ==========

if [ -e ${FILEPATH} ]; then
    rm -rf ${FILEPATH}
    echo removed previous ${NAME_PROJECT} ${FILEPATH} file
fi

cp -r ${WORKSPACE}/build/libs/${FILENAME} ${FILEPATH}
echo copied new ${NAME_PROJECT} file

echo ========== copied ${NAME_PROJECT} file ==========

sleep 1

echo ========== Starting ${NAME_PROJECT} process ==========

${COMMAND_JAVA} -jar ${OPTION} "${FILEPATH}" &

echo ========== Started ${NAME_PROJECT} process ==========
------------------


vi ./bash_deployh/stop.sh
------------------
#!/usr/bin/bash

JAVA_HOME=$1
COMMAND_JAVA=$2
WORKSPACE=$3
NAME_PROJECT=$4
DIRECTORY=$5
FILENAME=$6
VALUE_PROFILE=$7
FILEPATH=${DIRECTORY}${FILENAME}
KEY_PROFILE="spring.profiles.active"
OPTION="-D${KEY_PROFILE}=${VALUE_PROFILE}"

${COMMAND_JAVA} -version

echo USER : $USER
echo JAVA_HOME : ${JAVA_HOME}
echo COMMAND_JAVA : ${COMMAND_JAVA}
echo WORKSPACE : ${WORKSPACE}
echo NAME_PROJECT : ${NAME_PROJECT}
echo DIRECTORY : ${DIRECTORY}
echo FILENAME : ${FILENAME}
echo VALUE_PROFILE : ${VALUE_PROFILE}
echo FILEPATH : ${FILEPATH}
echo KEY_PROFILE : ${KEY_PROFILE}
echo OPTION : ${OPTION}

echo ========== Terminating ${NAME_PROJECT} process ==========

PID=`ps -ef | grep ${FILEPATH} | grep ${KEY_PROFILE}=${VALUE_PROFILE} | grep -v grep | awk '{print $2}'`
echo PID : ${PID}

if [ -n "${PID}" ]
then
    kill -9 ${PID}
    echo process is killed.
else
    echo running process not found.
fi

echo ========== Terminated ${NAME_PROJECT} process ==========
------------------


vi ./bash_deployh/copy.sh
------------------
#!/usr/bin/bash

JAVA_HOME=$1
COMMAND_JAVA=$2
WORKSPACE=$3
NAME_PROJECT=$4
DIRECTORY=$5
FILENAME=$6
VALUE_PROFILE=$7
FILEPATH=${DIRECTORY}${FILENAME}
KEY_PROFILE="spring.profiles.active"
OPTION="-D${KEY_PROFILE}=${VALUE_PROFILE}"

${COMMAND_JAVA} -version

echo USER : $USER
echo JAVA_HOME : ${JAVA_HOME}
echo COMMAND_JAVA : ${COMMAND_JAVA}
echo WORKSPACE : ${WORKSPACE}
echo NAME_PROJECT : ${NAME_PROJECT}
echo DIRECTORY : ${DIRECTORY}
echo FILENAME : ${FILENAME}
echo VALUE_PROFILE : ${VALUE_PROFILE}
echo FILEPATH : ${FILEPATH}
echo KEY_PROFILE : ${KEY_PROFILE}
echo OPTION : ${OPTION}

echo ========== copying ${NAME_PROJECT} file ==========

if [ -e ${FILEPATH} ]; then
    rm -rf ${FILEPATH}
    echo removed previous ${NAME_PROJECT} ${FILEPATH} file
fi

cp -r ${WORKSPACE}/build/libs/${FILENAME} ${FILEPATH}
echo copied new ${NAME_PROJECT} file

echo ========== copied ${NAME_PROJECT} file ==========
------------------


vi ./bash_deployh/start.sh
------------------
#!/usr/bin/bash

JAVA_HOME=$1
COMMAND_JAVA=$2
WORKSPACE=$3
NAME_PROJECT=$4
DIRECTORY=$5
FILENAME=$6
VALUE_PROFILE=$7
FILEPATH=${DIRECTORY}${FILENAME}
KEY_PROFILE="spring.profiles.active"
OPTION="-D${KEY_PROFILE}=${VALUE_PROFILE}"

${COMMAND_JAVA} -version

echo USER : $USER
echo JAVA_HOME : ${JAVA_HOME}
echo COMMAND_JAVA : ${COMMAND_JAVA}
echo WORKSPACE : ${WORKSPACE}
echo NAME_PROJECT : ${NAME_PROJECT}
echo DIRECTORY : ${DIRECTORY}
echo FILENAME : ${FILENAME}
echo VALUE_PROFILE : ${VALUE_PROFILE}
echo FILEPATH : ${FILEPATH}
echo KEY_PROFILE : ${KEY_PROFILE}
echo OPTION : ${OPTION}

echo ========== Starting ${NAME_PROJECT} process ==========

${COMMAND_JAVA} -jar ${OPTION} "${FILEPATH}" &

echo ========== Started ${NAME_PROJECT} process ==========
------------------



### Path Data and Log
sudo mkdir -p /home/JENKINS/template/api
sudo mkdir -p /home/JENKINS/template/develop/data
sudo mkdir -p /home/JENKINS/template/develop/log
sudo mkdir -p /home/JENKINS/template/test_gradle/data/user/image
sudo mkdir -p /home/JENKINS/template/test_gradle/etc
sudo mkdir -p /home/JENKINS/template/test_gradle/log
sudo chown nova:nova -R /home/JENKINS

scp -P 18000 src\main\resources\application.yml nova@192.168.200.108:/home/JENKINS/template/test_gradle/etc
scp -P 18000 Document\01eddb85-d63f-1eb8-87c9-04529c92ee69 nova@192.168.200.108:/home/JENKINS/template/test_gradle/data/user/image

sudo chown jenkins:jenkins -R /home/JENKINS/template/test_gradle


drwxr-xr-x nova    nova    /home/JENKINS
drwxr-xr-x nova    nova    /home/JENKINS/template
drwxr-xr-x nova    nova    /home/JENKINS/template/api
drwxr-xr-x nova    nova    /home/JENKINS/template/develop
drwxr-xr-x nova    nova    /home/JENKINS/template/develop/data
drwxr-xr-x nova    nova    /home/JENKINS/template/develop/log
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle/data
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle/data/user
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle/data/user/image
-rw-r--r-- jenkins jenkins /home/JENKINS/template/test_gradle/data/user/image/01eddb85-d63f-1eb8-87c9-04529c92ee69
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle/etc
-rw-rw-r-- jenkins jenkins /home/JENKINS/template/test_gradle/etc/application.yml
drwxr-xr-x jenkins jenkins /home/JENKINS/template/test_gradle/log



### Insert Data(./Document/*.sql)



### Execute Template Server
nohup /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar -Dspring.profiles.active=develop /home/JENKINS/template/api/loginJwtRSA-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
nohup /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar -Ddefinition.db.url-jdbc=jdbc:log4jdbc:mariadb://192.168.45.195:19000 -Dspring.profiles.active=develop /home/JENKINS/template/api/loginJwtRSA-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &



### Nginx
$ sudo apt install nginx

$ sudo vi /etc/nginx/sites-available/default
------------------
# Please see /usr/share/doc/nginx-doc/examples/ for more detailed examples.

server {
    listen 80 default_server;
    listen [::]:80 default_server;

    root /var/www/html;
    index index.html index.htm index.nginx-debian.html;

    server_name _;

    location / {
        try_files $uri $uri/ =404;
    }

    location /template/docs {
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $host;
        proxy_pass http://template/template/docs/index.html;
    }

    location /template {
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $host;
        proxy_pass http://template;
    }
}


upstream template {
    server localhost:18020;
}
------------------


### .vscode/launch.json
    Add value of args
        "args": "--spring.profiles.active=local"


### Redis
$ sudo apt install redis-server
$ redis-server --version
$ sudo vi /etc/redis/redis.conf
bind 0.0.0.0 ::1
port 19030
maxmemory 1g
maxmemory-policy allkeys-lru
$ sudo systemctl restart redis-server


$ sudo apt install openjdk-8-jdk
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
The following additional packages will be installed:
  ca-certificates-java fonts-dejavu-extra java-common libatk-wrapper-java libatk-wrapper-java-jni libice-dev libpthread-stubs0-dev libsm-dev libx11-6
  libx11-dev libx11-xcb1 libxau-dev libxcb1-dev libxdmcp-dev libxt-dev openjdk-8-jdk-headless openjdk-8-jre openjdk-8-jre-headless x11proto-dev
  xorg-sgml-doctools xtrans-dev
Suggested packages:
  default-jre libice-doc libsm-doc libx11-doc libxcb-doc libxt-doc openjdk-8-demo openjdk-8-source visualvm fonts-nanum fonts-ipafont-gothic
  fonts-ipafont-mincho fonts-wqy-microhei fonts-wqy-zenhei
The following NEW packages will be installed:
  ca-certificates-java fonts-dejavu-extra java-common libatk-wrapper-java libatk-wrapper-java-jni libice-dev libpthread-stubs0-dev libsm-dev libx11-dev
  libxau-dev libxcb1-dev libxdmcp-dev libxt-dev openjdk-8-jdk openjdk-8-jdk-headless openjdk-8-jre openjdk-8-jre-headless x11proto-dev xorg-sgml-doctools
  xtrans-dev
The following packages will be upgraded:
  libx11-6 libx11-xcb1
2 upgraded, 20 newly installed, 0 to remove and 178 not upgraded.
Need to get 47.9 MB/48.6 MB of archives.
After this operation, 163 MB of additional disk space will be used.
Do you want to continue? [Y/n] 
Get:1 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 java-common all 0.72build2 [6,782 B]
Get:2 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-8-jre-headless amd64 8u392-ga-1~22.04 [30.8 MB]
Get:3 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 ca-certificates-java all 20190909ubuntu1.2 [12.1 kB]                                   
Get:4 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 fonts-dejavu-extra all 2.37-2build1 [2,041 kB]                                                 
Get:5 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libatk-wrapper-java all 0.38.0-5build1 [53.1 kB]                                               
Get:6 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libatk-wrapper-java-jni amd64 0.38.0-5build1 [49.0 kB]                                         
Get:7 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 xorg-sgml-doctools all 1:1.11-1.1 [10.9 kB]                                                    
Get:8 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 x11proto-dev all 2021.5-1 [604 kB]                                                             
Get:9 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libice-dev amd64 2:1.0.10-1build2 [51.4 kB]                                                    
Get:10 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libpthread-stubs0-dev amd64 0.4-1build2 [5,516 B]                                             
Get:11 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libsm-dev amd64 2:1.2.3-1build2 [18.1 kB]                                                     
Get:12 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libxau-dev amd64 1:1.0.9-1build5 [9,724 B]                                                    
Get:13 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libxdmcp-dev amd64 1:1.1.3-0ubuntu5 [26.5 kB]                                                 
Get:14 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 xtrans-dev all 1.4.0-1 [68.9 kB]                                                              
Get:15 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libxcb1-dev amd64 1.14-3ubuntu3 [86.5 kB]                                                     
Get:16 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 libx11-dev amd64 2:1.7.5-1ubuntu0.3 [744 kB]                                          
Get:17 http://kr.archive.ubuntu.com/ubuntu jammy/main amd64 libxt-dev amd64 1:1.2.1-1 [396 kB]                                                            
Get:18 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-8-jre amd64 8u392-ga-1~22.04 [75.5 kB]                                    
Get:19 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-8-jdk-headless amd64 8u392-ga-1~22.04 [8,863 kB]                          
Get:20 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-8-jdk amd64 8u392-ga-1~22.04 [4,006 kB]                                   
Fetched 47.9 MB in 10s (4,676 kB/s)                                                                                                                       
(Reading database ... 205951 files and directories currently installed.)
Preparing to unpack .../00-libx11-6_2%3a1.7.5-1ubuntu0.3_amd64.deb ...
Unpacking libx11-6:amd64 (2:1.7.5-1ubuntu0.3) over (2:1.7.5-1ubuntu0.2) ...
Preparing to unpack .../01-libx11-xcb1_2%3a1.7.5-1ubuntu0.3_amd64.deb ...
Unpacking libx11-xcb1:amd64 (2:1.7.5-1ubuntu0.3) over (2:1.7.5-1ubuntu0.2) ...
Selecting previously unselected package java-common.
Preparing to unpack .../02-java-common_0.72build2_all.deb ...
Unpacking java-common (0.72build2) ...
Selecting previously unselected package openjdk-8-jre-headless:amd64.
Preparing to unpack .../03-openjdk-8-jre-headless_8u392-ga-1~22.04_amd64.deb ...
Unpacking openjdk-8-jre-headless:amd64 (8u392-ga-1~22.04) ...
Selecting previously unselected package ca-certificates-java.
Preparing to unpack .../04-ca-certificates-java_20190909ubuntu1.2_all.deb ...
Unpacking ca-certificates-java (20190909ubuntu1.2) ...
Selecting previously unselected package fonts-dejavu-extra.
Preparing to unpack .../05-fonts-dejavu-extra_2.37-2build1_all.deb ...
Unpacking fonts-dejavu-extra (2.37-2build1) ...
Selecting previously unselected package libatk-wrapper-java.
Preparing to unpack .../06-libatk-wrapper-java_0.38.0-5build1_all.deb ...
Unpacking libatk-wrapper-java (0.38.0-5build1) ...
Selecting previously unselected package libatk-wrapper-java-jni:amd64.
Preparing to unpack .../07-libatk-wrapper-java-jni_0.38.0-5build1_amd64.deb ...
Unpacking libatk-wrapper-java-jni:amd64 (0.38.0-5build1) ...
Selecting previously unselected package xorg-sgml-doctools.
Preparing to unpack .../08-xorg-sgml-doctools_1%3a1.11-1.1_all.deb ...
Unpacking xorg-sgml-doctools (1:1.11-1.1) ...
Selecting previously unselected package x11proto-dev.
Preparing to unpack .../09-x11proto-dev_2021.5-1_all.deb ...
Unpacking x11proto-dev (2021.5-1) ...
Selecting previously unselected package libice-dev:amd64.
Preparing to unpack .../10-libice-dev_2%3a1.0.10-1build2_amd64.deb ...
Unpacking libice-dev:amd64 (2:1.0.10-1build2) ...
Selecting previously unselected package libpthread-stubs0-dev:amd64.
Preparing to unpack .../11-libpthread-stubs0-dev_0.4-1build2_amd64.deb ...
Unpacking libpthread-stubs0-dev:amd64 (0.4-1build2) ...
Selecting previously unselected package libsm-dev:amd64.
Preparing to unpack .../12-libsm-dev_2%3a1.2.3-1build2_amd64.deb ...
Unpacking libsm-dev:amd64 (2:1.2.3-1build2) ...
Selecting previously unselected package libxau-dev:amd64.
Preparing to unpack .../13-libxau-dev_1%3a1.0.9-1build5_amd64.deb ...
Unpacking libxau-dev:amd64 (1:1.0.9-1build5) ...
Selecting previously unselected package libxdmcp-dev:amd64.
Preparing to unpack .../14-libxdmcp-dev_1%3a1.1.3-0ubuntu5_amd64.deb ...
Unpacking libxdmcp-dev:amd64 (1:1.1.3-0ubuntu5) ...
Selecting previously unselected package xtrans-dev.
Preparing to unpack .../15-xtrans-dev_1.4.0-1_all.deb ...
Unpacking xtrans-dev (1.4.0-1) ...
Selecting previously unselected package libxcb1-dev:amd64.
Preparing to unpack .../16-libxcb1-dev_1.14-3ubuntu3_amd64.deb ...
Unpacking libxcb1-dev:amd64 (1.14-3ubuntu3) ...
Selecting previously unselected package libx11-dev:amd64.
Preparing to unpack .../17-libx11-dev_2%3a1.7.5-1ubuntu0.3_amd64.deb ...
Unpacking libx11-dev:amd64 (2:1.7.5-1ubuntu0.3) ...
Selecting previously unselected package libxt-dev:amd64.
Preparing to unpack .../18-libxt-dev_1%3a1.2.1-1_amd64.deb ...
Unpacking libxt-dev:amd64 (1:1.2.1-1) ...
Selecting previously unselected package openjdk-8-jre:amd64.
Preparing to unpack .../19-openjdk-8-jre_8u392-ga-1~22.04_amd64.deb ...
Unpacking openjdk-8-jre:amd64 (8u392-ga-1~22.04) ...
Selecting previously unselected package openjdk-8-jdk-headless:amd64.
Preparing to unpack .../20-openjdk-8-jdk-headless_8u392-ga-1~22.04_amd64.deb ...
Unpacking openjdk-8-jdk-headless:amd64 (8u392-ga-1~22.04) ...
Selecting previously unselected package openjdk-8-jdk:amd64.
Preparing to unpack .../21-openjdk-8-jdk_8u392-ga-1~22.04_amd64.deb ...
Unpacking openjdk-8-jdk:amd64 (8u392-ga-1~22.04) ...
Setting up java-common (0.72build2) ...
Setting up libpthread-stubs0-dev:amd64 (0.4-1build2) ...
Setting up xtrans-dev (1.4.0-1) ...
Setting up fonts-dejavu-extra (2.37-2build1) ...
Setting up libx11-6:amd64 (2:1.7.5-1ubuntu0.3) ...
Setting up xorg-sgml-doctools (1:1.11-1.1) ...
Setting up libatk-wrapper-java (0.38.0-5build1) ...
Setting up libx11-xcb1:amd64 (2:1.7.5-1ubuntu0.3) ...
Setting up libatk-wrapper-java-jni:amd64 (0.38.0-5build1) ...
Setting up openjdk-8-jre-headless:amd64 (8u392-ga-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java to provide /usr/bin/java (java) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/jjs to provide /usr/bin/jjs (jjs) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/keytool to provide /usr/bin/keytool (keytool) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/pack200 to provide /usr/bin/pack200 (pack200) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/rmid to provide /usr/bin/rmid (rmid) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/rmiregistry to provide /usr/bin/rmiregistry (rmiregistry) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/unpack200 to provide /usr/bin/unpack200 (unpack200) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/orbd to provide /usr/bin/orbd (orbd) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/servertool to provide /usr/bin/servertool (servertool) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/tnameserv to provide /usr/bin/tnameserv (tnameserv) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jexec to provide /usr/bin/jexec (jexec) in auto mode
Setting up ca-certificates-java (20190909ubuntu1.2) ...
head: cannot open '/etc/ssl/certs/java/cacerts' for reading: No such file or directory
Adding debian:GlobalSign_Root_R46.pem
Adding debian:Certainly_Root_R1.pem
Adding debian:DigiCert_Trusted_Root_G4.pem
Adding debian:QuoVadis_Root_CA_2_G3.pem
Adding debian:IdenTrust_Commercial_Root_CA_1.pem
Adding debian:emSign_ECC_Root_CA_-_G3.pem
Adding debian:TeliaSonera_Root_CA_v1.pem
Adding debian:DigiCert_TLS_RSA4096_Root_G5.pem
Adding debian:SecureTrust_CA.pem
Adding debian:Starfield_Services_Root_Certificate_Authority_-_G2.pem
Adding debian:QuoVadis_Root_CA_2.pem
Adding debian:Go_Daddy_Class_2_CA.pem
Adding debian:AffirmTrust_Networking.pem
Adding debian:HARICA_TLS_ECC_Root_CA_2021.pem
Adding debian:Microsec_e-Szigno_Root_CA_2009.pem
Adding debian:ISRG_Root_X2.pem
Adding debian:SZAFIR_ROOT_CA2.pem
Adding debian:T-TeleSec_GlobalRoot_Class_3.pem
Adding debian:Microsoft_ECC_Root_Certificate_Authority_2017.pem
Adding debian:SSL.com_EV_Root_Certification_Authority_RSA_R2.pem
Adding debian:Buypass_Class_3_Root_CA.pem
Adding debian:vTrus_Root_CA.pem
Adding debian:SSL.com_EV_Root_Certification_Authority_ECC.pem
Adding debian:CA_Disig_Root_R2.pem
Adding debian:Entrust_Root_Certification_Authority_-_EC1.pem
Adding debian:Actalis_Authentication_Root_CA.pem
Adding debian:UCA_Extended_Validation_Root.pem
Adding debian:DigiCert_Global_Root_G2.pem
Adding debian:ACCVRAIZ1.pem
Adding debian:AffirmTrust_Premium_ECC.pem
Adding debian:TWCA_Global_Root_CA.pem
Adding debian:GlobalSign_ECC_Root_CA_-_R4.pem
Adding debian:Security_Communication_ECC_RootCA1.pem
Adding debian:Certainly_Root_E1.pem
Adding debian:Amazon_Root_CA_4.pem
Adding debian:GTS_Root_R1.pem
Adding debian:NAVER_Global_Root_Certification_Authority.pem
Adding debian:USERTrust_ECC_Certification_Authority.pem
Adding debian:D-TRUST_BR_Root_CA_1_2020.pem
Adding debian:Atos_TrustedRoot_2011.pem
Adding debian:DigiCert_TLS_ECC_P384_Root_G5.pem
Adding debian:GTS_Root_R2.pem
Adding debian:Certum_Trusted_Root_CA.pem
Adding debian:Hellenic_Academic_and_Research_Institutions_RootCA_2015.pem
Adding debian:Baltimore_CyberTrust_Root.pem
Adding debian:emSign_Root_CA_-_C1.pem
Adding debian:Certum_Trusted_Network_CA.pem
Adding debian:Autoridad_de_Certificacion_Firmaprofesional_CIF_A62634068_2.pem
Adding debian:GTS_Root_R4.pem
Adding debian:GlobalSign_Root_CA_-_R3.pem
Adding debian:DigiCert_Assured_ID_Root_G3.pem
Adding debian:AffirmTrust_Commercial.pem
Adding debian:CFCA_EV_ROOT.pem
Adding debian:Go_Daddy_Root_Certificate_Authority_-_G2.pem
Adding debian:Buypass_Class_2_Root_CA.pem
Adding debian:HARICA_TLS_RSA_Root_CA_2021.pem
Adding debian:Security_Communication_RootCA2.pem
Adding debian:Amazon_Root_CA_3.pem
Adding debian:QuoVadis_Root_CA_3_G3.pem
Adding debian:E-Tugra_Global_Root_CA_ECC_v3.pem
Adding debian:SSL.com_Root_Certification_Authority_RSA.pem
Adding debian:e-Szigno_Root_CA_2017.pem
Adding debian:Starfield_Root_Certificate_Authority_-_G2.pem
Adding debian:OISTE_WISeKey_Global_Root_GB_CA.pem
Adding debian:D-TRUST_EV_Root_CA_1_2020.pem
Adding debian:QuoVadis_Root_CA_1_G3.pem
Adding debian:Secure_Global_CA.pem
Adding debian:AffirmTrust_Premium.pem
Adding debian:Trustwave_Global_Certification_Authority.pem
Adding debian:ePKI_Root_Certification_Authority.pem
Adding debian:emSign_Root_CA_-_G1.pem
Adding debian:Entrust_Root_Certification_Authority_-_G4.pem
Adding debian:GLOBALTRUST_2020.pem
Adding debian:Autoridad_de_Certificacion_Firmaprofesional_CIF_A62634068.pem
Adding debian:NetLock_Arany_=Class_Gold=_Főtanúsítvány.pem
Adding debian:ISRG_Root_X1.pem
Adding debian:Starfield_Class_2_CA.pem
Adding debian:SwissSign_Gold_CA_-_G2.pem
Adding debian:COMODO_Certification_Authority.pem
Adding debian:ANF_Secure_Server_Root_CA.pem
Adding debian:Comodo_AAA_Services_root.pem
Adding debian:GlobalSign_Root_CA_-_R6.pem
Adding debian:XRamp_Global_CA_Root.pem
Adding debian:Certum_EC-384_CA.pem
Adding debian:IdenTrust_Public_Sector_Root_CA_1.pem
Adding debian:D-TRUST_Root_Class_3_CA_2_2009.pem
Adding debian:Trustwave_Global_ECC_P384_Certification_Authority.pem
Adding debian:DigiCert_Assured_ID_Root_CA.pem
Adding debian:Hongkong_Post_Root_CA_3.pem
Adding debian:UCA_Global_G2_Root.pem
Adding debian:HiPKI_Root_CA_-_G1.pem
Adding debian:Entrust.net_Premium_2048_Secure_Server_CA.pem
Adding debian:AC_RAIZ_FNMT-RCM.pem
Adding debian:SecureSign_RootCA11.pem
Adding debian:TunTrust_Root_CA.pem
Adding debian:certSIGN_ROOT_CA.pem
Adding debian:Security_Communication_Root_CA.pem
Adding debian:COMODO_RSA_Certification_Authority.pem
Adding debian:DigiCert_Assured_ID_Root_G2.pem
Adding debian:T-TeleSec_GlobalRoot_Class_2.pem
Adding debian:Certigna_Root_CA.pem
Adding debian:Entrust_Root_Certification_Authority.pem
Adding debian:GlobalSign_Root_E46.pem
Adding debian:Microsoft_RSA_Root_Certificate_Authority_2017.pem
Adding debian:Security_Communication_RootCA3.pem
Adding debian:vTrus_ECC_Root_CA.pem
Adding debian:SwissSign_Silver_CA_-_G2.pem
Adding debian:Certum_Trusted_Network_CA_2.pem
Adding debian:Hongkong_Post_Root_CA_1.pem
Adding debian:Certigna.pem
Adding debian:Telia_Root_CA_v2.pem
Adding debian:DigiCert_Global_Root_G3.pem
Adding debian:GDCA_TrustAUTH_R5_ROOT.pem
Adding debian:COMODO_ECC_Certification_Authority.pem
Adding debian:DigiCert_High_Assurance_EV_Root_CA.pem
Adding debian:D-TRUST_Root_Class_3_CA_2_EV_2009.pem
Adding debian:E-Tugra_Global_Root_CA_RSA_v3.pem
Adding debian:GlobalSign_ECC_Root_CA_-_R5.pem
Adding debian:SSL.com_Root_Certification_Authority_ECC.pem
Adding debian:TWCA_Root_Certification_Authority.pem
Adding debian:GlobalSign_Root_CA.pem
Adding debian:AC_RAIZ_FNMT-RCM_SERVIDORES_SEGUROS.pem
Adding debian:certSIGN_Root_CA_G2.pem
Adding debian:USERTrust_RSA_Certification_Authority.pem
Adding debian:Entrust_Root_Certification_Authority_-_G2.pem
Adding debian:Trustwave_Global_ECC_P256_Certification_Authority.pem
Adding debian:Hellenic_Academic_and_Research_Institutions_ECC_RootCA_2015.pem
Adding debian:E-Tugra_Certification_Authority.pem
Adding debian:Amazon_Root_CA_1.pem
Adding debian:QuoVadis_Root_CA_3.pem
Adding debian:OISTE_WISeKey_Global_Root_GC_CA.pem
Adding debian:ssl-cert-snakeoil.pem
Adding debian:emSign_ECC_Root_CA_-_C3.pem
Adding debian:Amazon_Root_CA_2.pem
Adding debian:DigiCert_Global_Root_CA.pem
Adding debian:GTS_Root_R3.pem
Adding debian:TUBITAK_Kamu_SM_SSL_Kok_Sertifikasi_-_Surum_1.pem
Adding debian:Izenpe.com.pem
done.
Setting up openjdk-8-jre:amd64 (8u392-ga-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/policytool to provide /usr/bin/policytool (policytool) in auto mode
Setting up openjdk-8-jdk-headless:amd64 (8u392-ga-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/clhsdb to provide /usr/bin/clhsdb (clhsdb) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/extcheck to provide /usr/bin/extcheck (extcheck) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/hsdb to provide /usr/bin/hsdb (hsdb) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/idlj to provide /usr/bin/idlj (idlj) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jar to provide /usr/bin/jar (jar) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jarsigner to provide /usr/bin/jarsigner (jarsigner) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/javac to provide /usr/bin/javac (javac) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/javadoc to provide /usr/bin/javadoc (javadoc) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/javah to provide /usr/bin/javah (javah) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/javap to provide /usr/bin/javap (javap) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jcmd to provide /usr/bin/jcmd (jcmd) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jdb to provide /usr/bin/jdb (jdb) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jdeps to provide /usr/bin/jdeps (jdeps) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jfr to provide /usr/bin/jfr (jfr) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jhat to provide /usr/bin/jhat (jhat) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jinfo to provide /usr/bin/jinfo (jinfo) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jmap to provide /usr/bin/jmap (jmap) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jps to provide /usr/bin/jps (jps) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jrunscript to provide /usr/bin/jrunscript (jrunscript) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jsadebugd to provide /usr/bin/jsadebugd (jsadebugd) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jstack to provide /usr/bin/jstack (jstack) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jstat to provide /usr/bin/jstat (jstat) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jstatd to provide /usr/bin/jstatd (jstatd) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/native2ascii to provide /usr/bin/native2ascii (native2ascii) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/rmic to provide /usr/bin/rmic (rmic) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/schemagen to provide /usr/bin/schemagen (schemagen) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/serialver to provide /usr/bin/serialver (serialver) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/wsgen to provide /usr/bin/wsgen (wsgen) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/wsimport to provide /usr/bin/wsimport (wsimport) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/xjc to provide /usr/bin/xjc (xjc) in auto mode
Setting up openjdk-8-jdk:amd64 (8u392-ga-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/appletviewer to provide /usr/bin/appletviewer (appletviewer) in auto mode
update-alternatives: using /usr/lib/jvm/java-8-openjdk-amd64/bin/jconsole to provide /usr/bin/jconsole (jconsole) in auto mode
Processing triggers for mailcap (3.70+nmu1ubuntu1) ...
Processing triggers for fontconfig (2.13.1-4.2ubuntu5) ...
Processing triggers for desktop-file-utils (0.26-1ubuntu3) ...
Processing triggers for hicolor-icon-theme (0.17-2) ...
Processing triggers for gnome-menus (3.36.0-1ubuntu3) ...
Processing triggers for libc-bin (2.35-0ubuntu3.1) ...
Processing triggers for man-db (2.10.2-1) ...
Processing triggers for ca-certificates (20230311ubuntu0.22.04.1) ...
Updating certificates in /etc/ssl/certs...
0 added, 0 removed; done.
Running hooks in /etc/ca-certificates/update.d...

done.
done.
Processing triggers for sgml-base (1.30) ...
Setting up x11proto-dev (2021.5-1) ...
Setting up libxau-dev:amd64 (1:1.0.9-1build5) ...
Setting up libice-dev:amd64 (2:1.0.10-1build2) ...
Setting up libsm-dev:amd64 (2:1.2.3-1build2) ...
Setting up libxdmcp-dev:amd64 (1:1.1.3-0ubuntu5) ...
Setting up libxcb1-dev:amd64 (1.14-3ubuntu3) ...
Setting up libx11-dev:amd64 (2:1.7.5-1ubuntu0.3) ...
Setting up libxt-dev:amd64 (1:1.2.1-1) ...
$

$ sudo apt install openjdk-11-jdk
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
The following additional packages will be installed:
  openjdk-11-jdk-headless openjdk-11-jre openjdk-11-jre-headless
Suggested packages:
  openjdk-11-demo openjdk-11-source visualvm fonts-ipafont-gothic fonts-ipafont-mincho fonts-wqy-microhei | fonts-wqy-zenhei
The following NEW packages will be installed:
  openjdk-11-jdk openjdk-11-jdk-headless openjdk-11-jre openjdk-11-jre-headless
0 upgraded, 4 newly installed, 0 to remove and 178 not upgraded.
Need to get 118 MB of archives.
After this operation, 260 MB of additional disk space will be used.
Do you want to continue? [Y/n] 
Get:1 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 openjdk-11-jre-headless amd64 11.0.21+9-0ubuntu1~22.04 [42.5 MB]
Get:2 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 openjdk-11-jre amd64 11.0.21+9-0ubuntu1~22.04 [214 kB]                                 
Get:3 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 openjdk-11-jdk-headless amd64 11.0.21+9-0ubuntu1~22.04 [73.5 MB]                       
Get:4 http://kr.archive.ubuntu.com/ubuntu jammy-updates/main amd64 openjdk-11-jdk amd64 11.0.21+9-0ubuntu1~22.04 [1,327 kB]                               
Fetched 118 MB in 23s (5,136 kB/s)                                                                                                                        
Selecting previously unselected package openjdk-11-jre-headless:amd64.
(Reading database ... 207016 files and directories currently installed.)
Preparing to unpack .../openjdk-11-jre-headless_11.0.21+9-0ubuntu1~22.04_amd64.deb ...
Unpacking openjdk-11-jre-headless:amd64 (11.0.21+9-0ubuntu1~22.04) ...
Selecting previously unselected package openjdk-11-jre:amd64.
Preparing to unpack .../openjdk-11-jre_11.0.21+9-0ubuntu1~22.04_amd64.deb ...
Unpacking openjdk-11-jre:amd64 (11.0.21+9-0ubuntu1~22.04) ...
Selecting previously unselected package openjdk-11-jdk-headless:amd64.
Preparing to unpack .../openjdk-11-jdk-headless_11.0.21+9-0ubuntu1~22.04_amd64.deb ...
Unpacking openjdk-11-jdk-headless:amd64 (11.0.21+9-0ubuntu1~22.04) ...
Selecting previously unselected package openjdk-11-jdk:amd64.
Preparing to unpack .../openjdk-11-jdk_11.0.21+9-0ubuntu1~22.04_amd64.deb ...
Unpacking openjdk-11-jdk:amd64 (11.0.21+9-0ubuntu1~22.04) ...
Setting up openjdk-11-jre-headless:amd64 (11.0.21+9-0ubuntu1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/java to provide /usr/bin/java (java) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jjs to provide /usr/bin/jjs (jjs) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/keytool to provide /usr/bin/keytool (keytool) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/rmid to provide /usr/bin/rmid (rmid) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/rmiregistry to provide /usr/bin/rmiregistry (rmiregistry) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/pack200 to provide /usr/bin/pack200 (pack200) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/unpack200 to provide /usr/bin/unpack200 (unpack200) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/lib/jexec to provide /usr/bin/jexec (jexec) in auto mode
Setting up openjdk-11-jre:amd64 (11.0.21+9-0ubuntu1~22.04) ...
Setting up openjdk-11-jdk-headless:amd64 (11.0.21+9-0ubuntu1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jar to provide /usr/bin/jar (jar) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jarsigner to provide /usr/bin/jarsigner (jarsigner) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/javac to provide /usr/bin/javac (javac) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/javadoc to provide /usr/bin/javadoc (javadoc) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/javap to provide /usr/bin/javap (javap) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jcmd to provide /usr/bin/jcmd (jcmd) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jdb to provide /usr/bin/jdb (jdb) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jdeprscan to provide /usr/bin/jdeprscan (jdeprscan) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jdeps to provide /usr/bin/jdeps (jdeps) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jfr to provide /usr/bin/jfr (jfr) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jimage to provide /usr/bin/jimage (jimage) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jinfo to provide /usr/bin/jinfo (jinfo) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jlink to provide /usr/bin/jlink (jlink) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jmap to provide /usr/bin/jmap (jmap) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jmod to provide /usr/bin/jmod (jmod) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jps to provide /usr/bin/jps (jps) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jrunscript to provide /usr/bin/jrunscript (jrunscript) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jshell to provide /usr/bin/jshell (jshell) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jstack to provide /usr/bin/jstack (jstack) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jstat to provide /usr/bin/jstat (jstat) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jstatd to provide /usr/bin/jstatd (jstatd) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/rmic to provide /usr/bin/rmic (rmic) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/serialver to provide /usr/bin/serialver (serialver) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jaotc to provide /usr/bin/jaotc (jaotc) in auto mode
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jhsdb to provide /usr/bin/jhsdb (jhsdb) in auto mode
Setting up openjdk-11-jdk:amd64 (11.0.21+9-0ubuntu1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-11-openjdk-amd64/bin/jconsole to provide /usr/bin/jconsole (jconsole) in auto mode
Processing triggers for desktop-file-utils (0.26-1ubuntu3) ...
Processing triggers for hicolor-icon-theme (0.17-2) ...
Processing triggers for gnome-menus (3.36.0-1ubuntu3) ...
Processing triggers for mailcap (3.70+nmu1ubuntu1) ...
$

$ sudo apt install openjdk-17-jdk
Reading package lists... Done
Building dependency tree... Done
Reading state information... Done
The following additional packages will be installed:
  openjdk-17-jdk-headless openjdk-17-jre openjdk-17-jre-headless
Suggested packages:
  openjdk-17-demo openjdk-17-source visualvm fonts-ipafont-gothic fonts-ipafont-mincho fonts-wqy-microhei | fonts-wqy-zenhei
The following NEW packages will be installed:
  openjdk-17-jdk openjdk-17-jdk-headless openjdk-17-jre openjdk-17-jre-headless
0 upgraded, 4 newly installed, 0 to remove and 178 not upgraded.
Need to get 122 MB of archives.
After this operation, 274 MB of additional disk space will be used.
Do you want to continue? [Y/n] 
Get:1 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-17-jre-headless amd64 17.0.9+9-1~22.04 [48.2 MB]
Get:2 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-17-jre amd64 17.0.9+9-1~22.04 [203 kB]                                     
Get:3 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-17-jdk-headless amd64 17.0.9+9-1~22.04 [71.1 MB]                           
Get:4 http://kr.archive.ubuntu.com/ubuntu jammy-updates/universe amd64 openjdk-17-jdk amd64 17.0.9+9-1~22.04 [2,752 kB]                                   
Fetched 122 MB in 26s (4,767 kB/s)                                                                                                                        
Selecting previously unselected package openjdk-17-jre-headless:amd64.
(Reading database ... 207523 files and directories currently installed.)
Preparing to unpack .../openjdk-17-jre-headless_17.0.9+9-1~22.04_amd64.deb ...
Unpacking openjdk-17-jre-headless:amd64 (17.0.9+9-1~22.04) ...
Selecting previously unselected package openjdk-17-jre:amd64.
Preparing to unpack .../openjdk-17-jre_17.0.9+9-1~22.04_amd64.deb ...
Unpacking openjdk-17-jre:amd64 (17.0.9+9-1~22.04) ...
Selecting previously unselected package openjdk-17-jdk-headless:amd64.
Preparing to unpack .../openjdk-17-jdk-headless_17.0.9+9-1~22.04_amd64.deb ...
Unpacking openjdk-17-jdk-headless:amd64 (17.0.9+9-1~22.04) ...
Selecting previously unselected package openjdk-17-jdk:amd64.
Preparing to unpack .../openjdk-17-jdk_17.0.9+9-1~22.04_amd64.deb ...
Unpacking openjdk-17-jdk:amd64 (17.0.9+9-1~22.04) ...
Setting up openjdk-17-jre-headless:amd64 (17.0.9+9-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/java to provide /usr/bin/java (java) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jpackage to provide /usr/bin/jpackage (jpackage) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/keytool to provide /usr/bin/keytool (keytool) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/rmiregistry to provide /usr/bin/rmiregistry (rmiregistry) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/lib/jexec to provide /usr/bin/jexec (jexec) in auto mode
Setting up openjdk-17-jre:amd64 (17.0.9+9-1~22.04) ...
Setting up openjdk-17-jdk-headless:amd64 (17.0.9+9-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jar to provide /usr/bin/jar (jar) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jarsigner to provide /usr/bin/jarsigner (jarsigner) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/javac to provide /usr/bin/javac (javac) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/javadoc to provide /usr/bin/javadoc (javadoc) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/javap to provide /usr/bin/javap (javap) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jcmd to provide /usr/bin/jcmd (jcmd) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jdb to provide /usr/bin/jdb (jdb) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jdeprscan to provide /usr/bin/jdeprscan (jdeprscan) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jdeps to provide /usr/bin/jdeps (jdeps) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jfr to provide /usr/bin/jfr (jfr) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jimage to provide /usr/bin/jimage (jimage) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jinfo to provide /usr/bin/jinfo (jinfo) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jlink to provide /usr/bin/jlink (jlink) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jmap to provide /usr/bin/jmap (jmap) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jmod to provide /usr/bin/jmod (jmod) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jps to provide /usr/bin/jps (jps) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jrunscript to provide /usr/bin/jrunscript (jrunscript) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jshell to provide /usr/bin/jshell (jshell) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jstack to provide /usr/bin/jstack (jstack) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jstat to provide /usr/bin/jstat (jstat) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jstatd to provide /usr/bin/jstatd (jstatd) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/serialver to provide /usr/bin/serialver (serialver) in auto mode
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jhsdb to provide /usr/bin/jhsdb (jhsdb) in auto mode
Setting up openjdk-17-jdk:amd64 (17.0.9+9-1~22.04) ...
update-alternatives: using /usr/lib/jvm/java-17-openjdk-amd64/bin/jconsole to provide /usr/bin/jconsole (jconsole) in auto mode
Processing triggers for desktop-file-utils (0.26-1ubuntu3) ...
Processing triggers for hicolor-icon-theme (0.17-2) ...
Processing triggers for gnome-menus (3.36.0-1ubuntu3) ...
Processing triggers for mailcap (3.70+nmu1ubuntu1) ...
$

