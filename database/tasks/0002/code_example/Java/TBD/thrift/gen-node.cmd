thrift -r --gen java --gen js:node -o db  struct.thrift
thrift -r --gen java --gen js:node -o db  service.thrift

echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
echo Generating nodejs ...
cp ./db/gen-nodejs/* ../../NodeJs/thriftjs -rf
rm ./db/gen-nodejs/* -rf