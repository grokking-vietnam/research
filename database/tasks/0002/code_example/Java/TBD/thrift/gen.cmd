thrift -r --gen java --gen java -o db  struct.thrift
thrift -r --gen java --gen java -o db  service.thrift

echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
echo Generating Java ...
cp ./db/gen-java/* ../src -rf
rm ./db/gen-java/* -rf