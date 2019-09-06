version: '2'

services:
    wildfly:
      image: 'bitnami/wildfly:17'
      ports:
        - '8080:8080'
        - '9990:9990'
      volumes:
        - ./wildfly:/bitnami
    gradle:
      user: root
      image: 'gradle:4.2.1'
      volumes:
        - .:/home/gradle/project
      command:
        sh -c 'cd /home/gradle/project && ./build-redeploy.sh'
