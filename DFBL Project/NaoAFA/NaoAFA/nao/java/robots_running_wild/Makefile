JAVA_INSTALL_PATH = $(shell ls -d /opt/jdk1.7* | tail -1)

JAVAC = ${JAVA_INSTALL_PATH}/bin/javac
JAR = ${JAVA_INSTALL_PATH}/bin/jar
JAVA = ${JAVA_INSTALL_PATH}/bin/java

CP = cp
RM = rm -f

#COMMON_DIR = ../../../../common/java

#TIMESTAMP = $(shell date +%Y%m%d%H%M%S)

CLEAN = find . -name '*.class' | xargs rm -f

#JAVAC_FLAGS = -classpath .:../lib:../lib/absolutelayout/AbsoluteLayout.jar

#JAVAC_FLAGS = $(shell grep '^Class-Path: ' Manifest.reschu | sed -e 's/^Class-Path: */ /' -e 's/ /:..\//g' -e 's/^/-classpath ./'):${COMMON_DIR}
#
#JAVA_ARGS = -Djava.util.logging.config.file=reschu-logging.properties
#
#JAR_ARGS = -databaseEngine-MySQL \
#           -database-bailey \
#           -time-120 \
#           -infodat-info_two_images.dat \
#           -noeyelink \
#           -messageBoardMode-active \
#           -debug
#JAR_ARGS = run

default :
	${CLEAN}
	./get_build_info.sh
#	${RM}			$(find common -type f)
	${JAVAC} ${JAVAC_FLAGS}	Test.java
#	${CP}			${COMMON_DIR}/common/Debug*.class \
#				${COMMON_DIR}/common/GraphicsUtil*.class \
#				${COMMON_DIR}/common/ThreadUtil*.class \
#				common
#	${CP}			${COMMON_DIR}/common/graphics/MessageArea*.class \
#				${COMMON_DIR}/common/graphics/MessageDisplay*.class \
#				${COMMON_DIR}/common/graphics/MessageList*.class \
#				common/graphics
#	${JAR} cfm		../convoy_leader.jar \
#				ConvoyLeader.manifest \
#				BUILD_INFO \
#				$$(find . -name '*.class') \
#				$$(find . -name '*.xml')
#	${CLEAN}

test :
	java Test rufus
#	(cd ..; ${JAVA} ${JAVA_ARGS} -jar convoy_leader.jar ${JAR_ARGS} 2>&1) | tee RESULTS
