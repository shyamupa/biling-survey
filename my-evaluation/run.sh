#mvn -q dependency:copy-dependencies
#mvn -q compile
# module load sun-jdk/1.8.0
CP="./config/:./target/classes/:./target/dependency/*"

MEMORY="-Xmx30g"
# OPTIONS="$MEMORY -Xss40m -ea -cp $CP"
# OPTIONS="$MEMORY -Xss40m -ea -cp $CP"
OPTIONS="$MEMORY -Xss40m -cp $CP"
#PACKAGE_PREFIX="edu.illinois.cs.cogcomp"

MAIN="evaluation.MainClass"
time nice java $OPTIONS $MAIN $CONFIG_STR $*
