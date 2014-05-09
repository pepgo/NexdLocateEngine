objects = main.o POI.o
LIBXML = /usr/local/lib/
INCLUDEXML = /usr/include/libxml2/
CFLAGS = -I$(INCLUDEXML) -L$(LIBXML) 
NearestPOI : $(objects)
	g++  -o NearestPOI $(objects) -lxml2
POI.o : POI.cpp POI.h
	g++  $(CFLAGS) -c POI.cpp
main.o : main.cpp POI.h
	g++  $(CFLAGS) -c main.cpp
clean : 
	rm NearestPOI $(objects)
