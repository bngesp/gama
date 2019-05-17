cd ummisco.gama.annotations
mvn clean install $1 $2
cd - 
cd msi.gama.processor 
mvn clean install $1 $2
cd - 
cd msi.gama.parent 
mvn clean install $1 $2
cd -