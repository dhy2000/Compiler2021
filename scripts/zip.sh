echo "Zipping all java sources inside src/ ......"

# Working directory is root of project!
srcs=`find . -name "*.java"`

echo $srcs | xargs zip -q Compiler2021.zip