CFLAGS=-std=c99

all: printtable

printtable: printtable.o s57obj.o s57att.o s57val.o
	cc -o printtable -g printtable.o s57obj.o s57att.o s57val.o 

clean:
	rm printtable printtable.o s57obj.o s57att.o s57val.o
	
printtable.o: printtable.c
	cc -c -g $(CFLAGS) printtable.c
s57obj.o: s57obj.c s57obj.h
	cc -c -g $(CFLAGS) s57obj.c
s57att.o: s57att.c s57obj.h s57att.h
	cc -c -g $(CFLAGS) s57att.c
s57val.o: s57val.c s57obj.h s57att.h s57val.h
	cc -c -g $(CFLAGS) s57val.c

