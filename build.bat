@echo off
echo Setting JAVA_HOME
set JAVA_HOME=C:\Users\RJ\Documents\Development\tools\Java\JDK
echo setting PATH
set PATH=C:\Users\RJ\Documents\Development\tools\Java\JDK\bin;%PATH%
echo Display java version
javadoc -d "./javadoc" -sourcepath "./_source" -classpath "lib\apache-tika\tika-app-1.5.jar;lib\commons\commons-codec-.3.jar;lib\commons\commons-collections-3.1.jar;lib\commons\commons-dbcp-1.4.jar;lib\commons\commons-io-1.3.1.jar;lib\commons\commons-lang-2.1.jar;lib\commons\commons-lang3-3.1.jar;lib\commons\commons-logging-1.1.1.jar;lib\commons\commons-pool-1.6.jar;lib\html\cobra.jar;lib\html\js.jar;lib\jacksum\jacksum.jar;lib\jaudiotagger\jaudiotagger-2.2.0.jar;lib\jebml\jebml.jar;lib\log4j\log4j-1.2.14.jar;lib\metadata\metadata-extractor-2.3.1.jar;lib\pdf\bcmail-jdk14-132.jar;lib\pdf\bcprov-jdk14-132.jar;lib\pdf\fontbox-1.8.2.jar;lib\pdf\ISBNExtractor-1.0.jar;lib\pdf\pdfbox-1.8.2.jar;lib\pdf\PDFRenderer.jar;lib\sun\rowset.jar;lib\xml\avalon-framework-4.2.0.jar;lib\xml\batik-all-1.7.jar;lib\xml\fop.jar;lib\xml\serializer-2.7.0.jar;lib\xml\xalan-2.7.0.jar;lib\xml\xercesImpl-2.7.1.jar;lib\xml\xml-apis-1.3.04.jar;lib\xml\xml-apis-ext-1.3.04.jar;lib\xml\xmlgraphics-commons-1.4.jar;lib\xml\xsltc.jar;lib\xml-rpc\ws-commons-util-1.0.2.jar;lib\xml-rpc\xmlrpc-client-3.1.3.jar;lib\xml-rpc\xmlrpc-common-3.1.3.jar;lib\xml-rpc\xmlrpc-server-3.1.3.jar;lib\zip\truezip_7_7_1.jar;" net.datacrow -subpackages net -protected -version -author
java -version
rd _classes /S /Q
del datacrow-core.jar
call ant
copy datacrow-core.jar ..\datacrow-server\lib
copy datacrow-core.jar ..\datacrow-client\lib
copy datacrow-core.jar ..\datacrow-services\lib
del datacrow-core.jar
